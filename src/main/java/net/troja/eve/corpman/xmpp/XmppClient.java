package net.troja.eve.corpman.xmpp;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import javax.annotation.PreDestroy;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.Builder;
import org.jivesoftware.smack.util.TLSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class XmppClient implements ChatManagerListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmppClient.class);

    private AbstractXMPPConnection connection = null;
    private Chat broadcast;
    private boolean enabled = true;

    private final Map<String, BiConsumer<Chat, String>> commands = new HashMap<>();
    private final Map<String, String> commandsAndDescription = new HashMap<>();
    private final Map<Integer, Long> chatTimeouts = new HashMap<>();

    @Autowired
    public XmppClient(final Environment env) {
        try {
            final String server = env.getRequiredProperty("xmpp.server");
            final int port = Integer.parseInt(env.getProperty("xmpp.port", "5222"));
            final String user = env.getRequiredProperty("xmpp.user");
            final String password = env.getRequiredProperty("xmpp.password");
            final String broadcast = env.getProperty("xmpp.broadcast");
            final boolean insecure = Boolean.parseBoolean(env.getProperty("xmpp.insecure", "false"));
            final String message = env.getProperty("xmpp.statusmessage", "Working for my corp");
            init(server, port, user, password, broadcast, insecure, message);
        } catch (final IllegalStateException e) {
            LOGGER.warn("XMPP disabled as configuration was not given");
            enabled = false;
        }
    }

    private void init(final String server, final int port, final String user, final String password, final String broadcastAddress,
            final boolean insecure, final String message) {
        LOGGER.info("Logging in " + server + " " + port + " " + user + " " + broadcastAddress);
        final Builder builder = XMPPTCPConnectionConfiguration.builder().setCompressionEnabled(true).setUsernameAndPassword(user, password)
                .setServiceName(server).setHost(server).setPort(port);
        if (insecure) {
            try {
                TLSUtils.acceptAllCertificates(builder);
            } catch (KeyManagementException | NoSuchAlgorithmException e1) {
                LOGGER.warn("Could not disable SSL");
            }
        }

        connection = new XMPPTCPConnection(builder.build());
        try {
            connection.connect();
            connection.login();

            final Presence presence = new Presence(Presence.Type.available, message, 0, Presence.Mode.away);
            connection.sendStanza(presence);

            final ChatManager chatmanager = ChatManager.getInstanceFor(connection);
            if (broadcastAddress != null) {
                broadcast = chatmanager.createChat(broadcastAddress, (arg0, arg1) -> {
                });
                LOGGER.info("Ready to broadcast");
            }
            chatmanager.addChatListener(this);
            LOGGER.info("Ready to receive");
        } catch (final XMPPException | SmackException | IOException e) {
            LOGGER.error("Error during init " + e.getMessage(), e);
        }
    }

    public void broadcast(final String text) {
        LOGGER.info("Send Broadcast " + text);
        if (broadcast != null) {
            try {
                broadcast.sendMessage(text);
                LOGGER.info("done");
            } catch (final NotConnectedException e) {
                LOGGER.error("Couldn't broadcast " + e.getMessage(), e);
            }
        }
    }

    public void addCommand(final String command, final String description, final BiConsumer<Chat, String> consumer) {
        commandsAndDescription.put(command, description);
        commands.put(command, consumer);
    }

    @Override
    public void chatCreated(final Chat chat, final boolean createdLocally) {
        if (!createdLocally) {
            chat.addMessageListener((chat2, arg1) -> {
                if ((arg1.getBody() != null) && (arg1.getBody().length() <= 10)) {
                    LOGGER.info("Queried by " + arg1.getFrom() + " to " + arg1.getTo() + ": " + arg1.getType() + " - " + arg1.getSubject() + " - "
                            + arg1.getBody());
                    final Long timeout = chatTimeouts.get(chat2.hashCode());
                    if ((timeout != null) && ((timeout + 500) > System.currentTimeMillis())) {
                        LOGGER.info("waiting...");
                        return;
                    }
                    final String command = arg1.getBody().trim().toLowerCase();
                    if (commands.containsKey(command)) {
                        final String from = arg1.getFrom();
                        commands.get(command).accept(chat, from.substring(0, from.indexOf("@")));
                    } else {
                        try {
                            chat.sendMessage("Unknown command, sir, please try one of those:");
                            for (final Entry<String, String> entry : commandsAndDescription.entrySet()) {
                                chat.sendMessage(entry.getKey() + " -> " + entry.getValue());
                            }
                        } catch (final Exception e) {
                            LOGGER.warn("Couldn't send message", e);
                        }

                    }
                    chatTimeouts.put(chat2.hashCode(), System.currentTimeMillis());
                }
            });
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    @PreDestroy
    public void shutdown() {
        LOGGER.info("Shutting down");
        connection.disconnect();
    }
}

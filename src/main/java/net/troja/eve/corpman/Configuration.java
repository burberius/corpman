package net.troja.eve.corpman;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Configuration {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "eveapi_id")
    private long eveApiId = 0;
    @Column(name = "evaapi_vcode")
    private String evaApiVCode;
    private String xmppServer;
    private int xmppPort = 5222;
    private String xmppUsername;
    private String xmppPassword;
    private String xmppBroadcast;
    private boolean xmppInsecure = false;
    @Column(name = "xmpp_statusmessage")
    private String xmppStatusMessage;
    @Column(name = "pos_alerthours")
    private final int posAlertHours = 48;
    @Column(name = "pos_broadcasthours")
    private final int posBroadcastHours = 24;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public long getEveApiId() {
        return eveApiId;
    }

    public void setEveApiId(final long eveApiId) {
        this.eveApiId = eveApiId;
    }

    public String getEvaApiVCode() {
        return evaApiVCode;
    }

    public void setEvaApiVCode(final String evaApiVCode) {
        this.evaApiVCode = evaApiVCode;
    }

    public String getXmppServer() {
        return xmppServer;
    }

    public void setXmppServer(final String xmppServer) {
        this.xmppServer = xmppServer;
    }

    public int getXmppPort() {
        return xmppPort;
    }

    public void setXmppPort(final int xmppPort) {
        this.xmppPort = xmppPort;
    }

    public String getXmppUsername() {
        return xmppUsername;
    }

    public void setXmppUsername(final String xmppUsername) {
        this.xmppUsername = xmppUsername;
    }

    public String getXmppPassword() {
        return xmppPassword;
    }

    public void setXmppPassword(final String xmppPassword) {
        this.xmppPassword = xmppPassword;
    }

    public String getXmppBroadcast() {
        return xmppBroadcast;
    }

    public void setXmppBroadcast(final String xmppBroadcast) {
        this.xmppBroadcast = xmppBroadcast;
    }

    public boolean isXmppInsecure() {
        return xmppInsecure;
    }

    public void setXmppInsecure(final boolean xmppInsecure) {
        this.xmppInsecure = xmppInsecure;
    }

    public String getXmppStatusMessage() {
        return xmppStatusMessage;
    }

    public void setXmppStatusMessage(final String xmppStatusMessage) {
        this.xmppStatusMessage = xmppStatusMessage;
    }

    public int getPosAlertHours() {
        return posAlertHours;
    }

    public int getPosBroadcastHours() {
        return posBroadcastHours;
    }
}

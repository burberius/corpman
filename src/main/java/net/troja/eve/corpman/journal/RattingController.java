package net.troja.eve.corpman.journal;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import net.troja.eve.corpman.journal.db.RattingDao;
import net.troja.eve.corpman.xmpp.XmppClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ratting")
public class RattingController {
    private static final Logger LOGGER = LogManager.getLogger(RattingController.class);
    @Autowired
    private RattingDao dao;
    @Autowired
    private XmppClient xmppClient;

    private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

    @PostConstruct
    public void initChecks() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (!xmppClient.isEnabled()) {
            return;
        }

        xmppClient.addCommand("ticks", "Get the top 5 ratting ticks in the last month", (chat, who) -> {
            final List<Map<String, Object>> ticks = dao.getHighesTicks();
            try {
                chat.sendMessage("Top 5 ratting ticks in the last month");
                for (int count = 0; count < Math.min(5, ticks.size()); count++) {
                    final Map<String, Object> map = ticks.get(count);
                    final Date date = (Date) map.get("date");
                    final String name = (String) map.get("name");
                    final Double value = (Double) map.get("value");
                    chat.sendMessage(dateFormat.format(date) + " " + name + " " + String.format("%,.2f", value) + " ISK");
                }
            } catch (final NotConnectedException e) {
                LOGGER.error("Couldn't send message " + e.getMessage(), e);
            }
        });

        xmppClient.addCommand("fancy", "Get the latest 5 fancy rats in the last month", (chat, who) -> {
            final List<Map<String, Object>> ticks = dao.getFancyRats();
            try {
                chat.sendMessage("Latest 5 fancy rats in the last month");
                for (int count = 0; count < Math.min(5, ticks.size()); count++) {
                    final Map<String, Object> map = ticks.get(count);
                    final Date date = (Date) map.get("date");
                    final String name = (String) map.get("name");
                    final String killer = (String) map.get("killer");
                    chat.sendMessage(dateFormat.format(date) + " " + killer + " " + name);
                }
            } catch (final NotConnectedException e) {
                LOGGER.error("Couldn't send message " + e.getMessage(), e);
            }
        });

        xmppClient.addCommand("revenue", "Get your ratting revenue in the last month", (chat, who) -> {
            final List<Map<String, Object>> ticks = dao.getRevenueOfMembers(false);
            try {
                boolean found = false;
                for(final Map<String, Object> data : ticks) {
                    final String name = (String) data.get("name");
                    if (cleanName(name).equals(cleanName(who))) {
                        final Double value = (Double) data.get("value");
                        chat.sendMessage("Your revenue for this month is " + String.format("%,.2f", value) + " ISK.");
                        found = true;
                    }
                }
                if (!found) {
                    chat.sendMessage("Couldn't find any revenue data for you!");
                }
            } catch (final NotConnectedException e) {
                LOGGER.error("Couldn't send message " + e.getMessage(), e);
            }
        });
    }

    private String cleanName(final String name) {
        return name.toLowerCase().replaceAll("[ -_]", "");
    }

    @RequestMapping("/revenue")
    public List<Map<String, Object>> getRevenue() {
        return dao.getCorpRevenue();
    }

    @RequestMapping("/monthlymemberrevenue")
    public List<Map<String, Object>> getPlayerRevenueMonth() {
        return dao.getRevenueOfMembers(false);
    }

    @RequestMapping("/weeklymemberrevenue")
    public List<Map<String, Object>> getPlayerRevenueWeek() {
        return dao.getRevenueOfMembers(true);
    }

    @RequestMapping("/rats")
    public List<Map<String, Object>> getRatsTypes() {
        return dao.getRatsByType();
    }

    @RequestMapping("/ratgroups")
    public List<Map<String, Object>> getRatsGroup() {
        return dao.getRatsByGroup();
    }

    @RequestMapping("/fancyrats")
    public List<Map<String, Object>> getFancyRats() {
        return dao.getFancyRats();
    }

    @RequestMapping("/systems")
    public List<Map<String, Object>> getSystems() {
        return dao.getSystems();
    }

    @RequestMapping("/perday")
    public List<Map<String, Object>> getPerDay() {
        return dao.getPerDay();
    }

    @RequestMapping("/perhour")
    public List<Map<String, Object>> getPerHour() {
        return dao.getPerHour();
    }

    @RequestMapping("/highestticks")
    public List<Map<String, Object>> getHighestTicks() {
        return dao.getHighesTicks();
    }
}

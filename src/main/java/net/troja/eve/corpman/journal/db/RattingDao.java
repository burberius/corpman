package net.troja.eve.corpman.journal.db;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RattingDao {
    @Autowired
    private JdbcTemplate template;

    /**
     * The first value is the revenue of the last week, the second of the last month.
     */
    public List<Map<String, Object>> getCorpRevenue() {
        return template.queryForList(
                "SELECT year(`date`) AS year, month(`date`) AS month, SUM(amount) AS value FROM wallet_journal WHERE ref_typeid = 85 "
                        + "GROUP BY Year(`date`), Month(`date`) ORDER BY Year(`date`), Month(`date`) DESC");
    }

    /**
     * Get the revenue of the members, ordered by amount.
     *
     * @param week
     *            true means per week, false per month
     * @return
     */
    public List<Map<String, Object>> getRevenueOfMembers(final boolean week) {
        String time = "MONTH";
        if (week) {
            time = "WEEK";
        }
        return template
                .queryForList("SELECT owner_name2 AS name, SUM(amount / tax_rate * (100 - tax_rate)) AS value FROM wallet_journal WHERE ref_typeid = 85 "
                        + "AND date > DATE_SUB(now(), INTERVAL 1 "
                        + time
                        + ") GROUP BY owner_name2 ORDER BY SUM(amount / tax_rate * (100 - tax_rate)) desc");
    }

    public List<Map<String, Object>> getRatsByType() {
        return template
                .queryForList("SELECT name, COUNT(id) AS count FROM rat JOIN wallet_journal ON journal_id = refid WHERE ref_typeid = 85 AND date > DATE_SUB(now(), INTERVAL 1 MONTH) GROUP BY name ORDER BY COUNT(id) DESC");
    }

    public List<Map<String, Object>> getRatsByGroup() {
        return template
                .queryForList("SELECT group_name AS name, COUNT(id) AS count FROM rat JOIN wallet_journal ON journal_id = refid WHERE ref_typeid = 85 AND date > DATE_SUB(now(), INTERVAL 1 MONTH) GROUP BY groupid ORDER BY COUNT(id) DESC");
    }

    public List<Map<String, Object>> getSystems() {
        return template
                .queryForList("SELECT arg_name AS system, SUM(amount) AS value FROM wallet_journal WHERE ref_typeid = 85 AND date > DATE_SUB(now(), INTERVAL 1 MONTH) GROUP BY arg_name ORDER By SUM(amount) DESC");
    }

    public List<Map<String, Object>> getFancyRats() {
        return template
                .queryForList("SELECT date, name, owner_name2 AS killer FROM rat JOIN wallet_journal ON journal_id = refid WHERE groupid IN (553, 554, 558, 559, 563, 564, 568, 569, 573, 574, 760, 789, 790, 791, 792, 793, 794, 795, 796, 797, 798, 799, 800, 807, 808, 809, 810, 811, 812, 813, 814, 819, 820, 821, 843, 844, 845, 846, 847, 848, 849, 850, 851, 852, 1174, 1285, 1286, 1287) ORDER BY date DESC LIMIT 30");
    }

    public List<Map<String, Object>> getPerDay() {
        return template
                .queryForList("SELECT DAYOFWEEK(date) AS day, SUM(amount) AS value FROM wallet_journal WHERE ref_typeid = 85 AND date > DATE_SUB(now(), INTERVAL 1 MONTH) GROUP BY DAYOFWEEK(date)");
    }

    public List<Map<String, Object>> getPerHour() {
        return template
                .queryForList("SELECT HOUR(date) AS hour, SUM(amount) AS value FROM wallet_journal WHERE ref_typeid = 85 AND date > DATE_SUB(now(), INTERVAL 1 MONTH) GROUP BY HOUR(date)");
    }

    public List<Map<String, Object>> getHighesTicks() {
        return template
                .queryForList("SELECT owner_name2 AS name, (amount / tax_rate * (100 - tax_rate)) value, date FROM wallet_journal WHERE ref_typeid = 85 AND date > DATE_SUB(now(), INTERVAL 1 MONTH) ORDER BY (amount / tax_rate * (100 - tax_rate)) DESC LIMIT 25");
    }
}

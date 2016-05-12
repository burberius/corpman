package net.troja.eve.corpman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Configuration
@EnableScheduling
public class CorpManApplication {
    public static final String POS_ALERT_HOURS = "pos.alerthours";
    public static final String POS_BROADCAST_HOURS = "pos.broadcasthours";

    public static final int DELAY_CORPSHEET = 1000;
    public static final int DELAY_SOV = 10000;
    public static final int DELAY_KILLLOG = 15000;
    public static final int DELAY_ASSETS = 30000;
    public static final int DELAY_POS = 45000;
    public static final int DELAY_POSMODULE = 60000;

    public static final int RATE_CORPSHEET = 6000000;
    public static final int RATE_SOV = 6000000;
    public static final int RATE_KILLLOG = 6000000;
    public static final int RATE_ASSETS = 600000;
    public static final int RATE_POS = 600000;
    public static final int RATE_POSMODULE = 3600000;

    private CorpManApplication() {
    }

    public static void main(final String... args) {
        SpringApplication.run(CorpManApplication.class, args);
    }
}

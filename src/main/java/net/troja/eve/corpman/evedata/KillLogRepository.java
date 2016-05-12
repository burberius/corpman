package net.troja.eve.corpman.evedata;

import java.util.Date;
import java.util.Set;

import net.troja.eve.corpman.EveApiRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.beimin.eveapi.model.shared.Kill;
import com.beimin.eveapi.response.shared.KillLogResponse;

@Repository
public class KillLogRepository {
    private static final Logger LOGGER = LogManager.getLogger(KillLogRepository.class);

    @Autowired
    private EveApiRepository eveApiRepository;

    private Date lastLoss;
    private Date lastKill;
    private Date oldestKill;
    private int kills;
    private int losses;

    // @Scheduled(initialDelay = CorpManApplication.DELAY_KILLLOG, fixedRate = CorpManApplication.RATE_KILLLOG)
    public void update() {
        LOGGER.info("Update");
        final KillLogResponse response = eveApiRepository.getKillLog();
        final Set<Kill> all = response.getAll();
        final long corpId = eveApiRepository.getCorpId();
        lastLoss = null;
        lastKill = null;
        LOGGER.info("Size: " + all.size() + " " + response.getCurrentTime() + " " + response.getCachedUntil());
        for(final Kill kill : all) {
            LOGGER.info(kill.getVictim().getCharacterName() + " " + kill.getSolarSystemID() + " " + kill.getKillTime());
            if(kill.getVictim().getCorporationID() == corpId) {
                // loss
                losses++;
                if((lastLoss == null) || lastLoss.after(kill.getKillTime())) {
                    lastLoss = kill.getKillTime();
                }
            } else {
                // kill
                kills++;
                if((lastKill == null) || lastKill.after(kill.getKillTime())) {
                    lastKill = kill.getKillTime();
                }
            }
            if ((oldestKill == null) || oldestKill.before(kill.getKillTime())) {
                oldestKill = kill.getKillTime();
            }
        }
        LOGGER.info(lastLoss + " " + lastKill + " " + oldestKill + " " + kills + " " + losses);
    }
}

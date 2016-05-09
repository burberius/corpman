package net.troja.eve.corpman.evedata;

import java.util.Map;

import net.troja.eve.corpman.CorpManApplication;
import net.troja.eve.corpman.EveApiRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import com.beimin.eveapi.model.map.SystemSovereignty;
import com.beimin.eveapi.response.map.SovereigntyResponse;

@Repository
public class SovereigntyRepository {
    private static final Logger LOGGER = LogManager.getLogger(SovereigntyRepository.class);

    @Autowired
    private EveApiRepository eveApiRepository;
    private Map<Integer, SystemSovereignty> sovereignties;

    public SovereigntyRepository() {
    }

    @Scheduled(initialDelay = CorpManApplication.DELAY_SOV, fixedRate = 6000000)
    public void update() {
        final SovereigntyResponse sovereigntyResponse = eveApiRepository.getSovereigntyInformation();
        if (sovereigntyResponse != null) {
            sovereignties = sovereigntyResponse.getSystemSovereignties();
            LOGGER.info("sovereignties " + sovereignties.size());
        }
    }

    public boolean isUnderOwnSovereignty(final int systemId) {
        boolean result = false;
        if (sovereignties != null) {
            final SystemSovereignty systemSovereignty = sovereignties.get(systemId);
            if (systemSovereignty != null) {
                result = systemSovereignty.getAllianceID() == eveApiRepository.getAllianceId();
            }
        }
        return result;
    }
}

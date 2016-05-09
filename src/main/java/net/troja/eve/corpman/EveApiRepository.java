package net.troja.eve.corpman;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.corporation.Starbase;
import com.beimin.eveapi.model.shared.Location;
import com.beimin.eveapi.parser.ApiAuthorization;
import com.beimin.eveapi.parser.corporation.AssetListParser;
import com.beimin.eveapi.parser.corporation.CorpSheetParser;
import com.beimin.eveapi.parser.corporation.LocationsParser;
import com.beimin.eveapi.parser.corporation.StarbaseDetailParser;
import com.beimin.eveapi.parser.corporation.StarbaseListParser;
import com.beimin.eveapi.parser.corporation.WalletJournalParser;
import com.beimin.eveapi.parser.map.SovereigntyParser;
import com.beimin.eveapi.parser.pilot.KillLogParser;
import com.beimin.eveapi.response.corporation.CorpSheetResponse;
import com.beimin.eveapi.response.corporation.StarbaseDetailResponse;
import com.beimin.eveapi.response.corporation.StarbaseListResponse;
import com.beimin.eveapi.response.map.SovereigntyResponse;
import com.beimin.eveapi.response.shared.AssetListResponse;
import com.beimin.eveapi.response.shared.KillLogResponse;
import com.beimin.eveapi.response.shared.LocationsResponse;
import com.beimin.eveapi.response.shared.WalletJournalResponse;

@Repository
public class EveApiRepository {
    private static final Logger LOGGER = LogManager.getLogger(EveApiRepository.class);

    private final ApiAuthorization auth;
    private long allianceId;
    private long corpId;
    private double taxRate;

    @Autowired
    public EveApiRepository(final Environment env) {
        final int eveApiId = Integer.parseInt(env.getRequiredProperty("eveapi.id"));
        final String eveApiVCode = env.getRequiredProperty("eveapi.vcode");
        auth = new ApiAuthorization(eveApiId, eveApiVCode);
    }

    @Scheduled(initialDelay = CorpManApplication.DELAY_CORPSHEET, fixedRate = 6000000)
    public void update() {
        final CorpSheetParser parser = new CorpSheetParser();
        try {
            final CorpSheetResponse corpSheetResponse = parser.getResponse(auth);
            if (corpSheetResponse != null) {
                allianceId = corpSheetResponse.getAllianceID();
                corpId = corpSheetResponse.getCorporationID();
                taxRate = corpSheetResponse.getTaxRate();
            }
        } catch (final ApiException e) {
            LOGGER.warn("Could not read corp sheet", e);
        }
    }

    public Set<Starbase> getPosList() {
        final StarbaseListParser parser = new StarbaseListParser();
        Set<Starbase> starbases = null;
        try {
            final StarbaseListResponse response = parser.getResponse(auth);
            if (!response.hasError()) {
                starbases = response.getAll();
            }
        } catch (final ApiException e) {
            LOGGER.warn("Could not access starbase list", e);
        }
        return starbases;
    }

    public StarbaseDetailResponse getPosDetails(final long posId) {
        final StarbaseDetailParser parser = new StarbaseDetailParser();
        StarbaseDetailResponse response = null;
        try {
            response = parser.getResponse(auth, posId);
            if (response.hasError()) {
                response = null;
            }
        } catch (final ApiException e) {
            LOGGER.warn("Could not access starbase details", e);
        }
        return response;
    }

    public Set<Location> getLocationNames(final List<Long> locationIds) {
        final LocationsParser parser = new LocationsParser();
        Set<Location> locations = null;
        try {
            final LocationsResponse response = parser.getResponse(auth, locationIds);
            if (!response.hasError()) {
                locations = response.getAll();
            }
        } catch (final ApiException e) {
            LOGGER.warn("Could not access starbase details", e);
        }
        return locations;
    }

    public AssetListResponse getAssets() {
        final AssetListParser parser = new AssetListParser();
        AssetListResponse response = null;
        try {
            response = parser.getResponse(auth);
        } catch (final ApiException e) {
            LOGGER.warn("Could not access assets", e);
        }
        return response;
    }

    public SovereigntyResponse getSovereigntyInformation() {
        final SovereigntyParser parser = new SovereigntyParser();
        SovereigntyResponse response = null;
        try {
            response = parser.getResponse();
        } catch (final ApiException e) {
            LOGGER.warn("Could not get sovereignty information", e);
        }
        return response;
    }

    public WalletJournalResponse getWalletJournal(final long startRef, final int count) {
        final WalletJournalParser journalParser = new WalletJournalParser();
        WalletJournalResponse response = null;
        try {
            response = journalParser.getResponse(auth, 1000, startRef, count);
        } catch (final ApiException e) {
            LOGGER.warn("Could not get wallet journal", e);
        }
        return response;
    }

    public KillLogResponse getKillLog() {
        final KillLogParser killLogParser = new KillLogParser();
        KillLogResponse response = null;
        try {
            response = killLogParser.getResponse(auth);
        } catch (final ApiException e) {
            LOGGER.warn("Could not get kill log", e);
        }
        return response;
    }

    public long getAllianceId() {
        return allianceId;
    }

    public long getCorpId() {
        return corpId;
    }

    public double getTaxRate() {
        return taxRate;
    }
}

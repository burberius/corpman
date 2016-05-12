package net.troja.eve.corpman.assets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.troja.eve.corpman.CorpManApplication;
import net.troja.eve.corpman.EveApiRepository;
import net.troja.eve.corpman.evedata.FlagsRepository;
import net.troja.eve.corpman.evedata.InvType;
import net.troja.eve.corpman.evedata.InvTypesRepository;
import net.troja.eve.corpman.evedata.SystemLocationsRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.response.shared.AssetListResponse;

@Repository
public class AssetRepository {
    private static final Logger LOGGER = LogManager.getLogger(AssetRepository.class);
    private static final List<Integer> GROUP_IDS_CONTAINERS = Arrays.asList(448, 340, 649);
    private static final int CATEGORY_ID_SHIP = 6;

    @Autowired
    private EveApiRepository eveApiRepository;
    @Autowired
    private SystemLocationsRepository locationRepository;
    @Autowired
    private InvTypesRepository invTypesRepository;
    @Autowired
    private FlagsRepository flagsRepository;

    private Date cachedUntil;
    private Date currentTime;
    private List<Asset> assets;

    @Scheduled(initialDelay = CorpManApplication.DELAY_ASSETS, fixedRate = CorpManApplication.RATE_ASSETS)
    public void update() {
        if ((cachedUntil == null) || cachedUntil.before(new Date())) {
            LOGGER.info("updating Assets");
            final AssetListResponse response = eveApiRepository.getAssets();
            if (response == null) {
                return;
            }
            assets = response.getAll();
            fixAssetsLocations(assets, null);
            cachedUntil = response.getCachedUntil();
            currentTime = response.getCurrentTime();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("done, cachedUntil: " + cachedUntil + " currentTime: " + currentTime);
            }
        }
    }

    private void fixAssetsLocations(final List<Asset> assetsToFix, final Long locationID) {
        for (final Asset asset : assetsToFix) {
            if ((asset.getLocationID() == null) && (locationID != null)) {
                asset.setLocationID(locationID);
            }
            final List<Asset> subAssets = asset.getAssets();
            if ((subAssets != null) && (!subAssets.isEmpty())) {
                fixAssetsLocations(subAssets, asset.getLocationID());
            }
        }
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public boolean isLabledAsset(final Asset asset) {
        final InvType type = invTypesRepository.getInvType(asset.getTypeID());
        boolean result = false;
        if (asset.getSingleton()) {
            result = GROUP_IDS_CONTAINERS.contains(type.getGroupID()) || (type.getCategoryID() == CATEGORY_ID_SHIP);
        }
        return result;
    }

    public List<Asset> filterAssets(final Filter filter) {
        final List<Asset> list = new ArrayList<Asset>();
        if (assets != null) {
            filterSubAssets(assets, list, filter, false);
        }
        return list;
    }

    public Date getCachedUntil() {
        return cachedUntil;
    }

    public Date getCurrentTime() {
        return currentTime;
    }

    private boolean filterSubAssets(final List<Asset> assets2Filter, final List<Asset> list, final Filter filter, final boolean addAll) {
        for (final Asset asset : assets2Filter) {
            if ((filter.getItemID() != null) && (asset.getItemID() == filter.getItemID().longValue())) {
                list.clear();
                if (filter.isContainerContentOnly()) {
                    filterSubAssets(asset.getAssets(), list, filter, true);
                } else {
                    filterSubAssets(asset.getAssets(), list, filter, addAll);
                }
                return true;
            } else if (addAll || matches(filter, asset)) {
                list.add(asset);
            }
            if ((asset.getAssets() != null) && (!asset.getAssets().isEmpty()) && filterSubAssets(asset.getAssets(), list, filter, addAll)) {
                return true;
            }
        }
        return false;
    }

    private boolean matches(final Filter filter, final Asset asset) {
        boolean result = true;
        if (matchesNotType(filter, asset) || matchesNotFlag(filter, asset) || matchesNotLocation(filter, asset)) {
            result = false;
        } else {
            final InvType type = invTypesRepository.getInvType(asset.getTypeID());
            if (type == null) {
                LOGGER.error("Could not find type: " + asset.getTypeID());
                result = false;
            } else if (matchesNotCategory(filter, type) || matchesNotGroup(filter, type)) {
                result = false;
            }
        }
        return result;
    }

    private boolean matchesNotGroup(final Filter filter, final InvType type) {
        return (filter.getGroupID() != null) && (filter.getGroupID() != type.getGroupID());
    }

    private boolean matchesNotCategory(final Filter filter, final InvType type) {
        return (filter.getCategoryID() != null) && (filter.getCategoryID() != type.getCategoryID());
    }

    private boolean matchesNotLocation(final Filter filter, final Asset asset) {
        return (filter.getLocationID() != null) && (filter.getLocationID().longValue() != asset.getLocationID());
    }

    private boolean matchesNotFlag(final Filter filter, final Asset asset) {
        return (filter.getFlagID() != null) && (filter.getFlagID() != asset.getFlag());
    }

    private boolean matchesNotType(final Filter filter, final Asset asset) {
        return (filter.getTypeID() != null) && (filter.getTypeID() != asset.getTypeID());
    }

    public String getSimpleAssetString(final Asset asset) {
        final InvType type = invTypesRepository.getInvType(asset.getTypeID());
        final StringBuilder retString = new StringBuilder(60);
        retString.append(locationRepository.getLocationName(asset.getLocationID())).append(" (").append(asset.getLocationID()).append(") - ")
        .append(invTypesRepository.getName(asset.getTypeID())).append(" (").append(asset.getTypeID()).append(") - ")
        .append(asset.getQuantity()).append(" ").append(asset.getRawQuantity()).append(" - ").append(type.getGroupName()).append(" (")
        .append(type.getGroupID()).append(") - ").append(type.getCategoryName()).append(" (").append(type.getCategoryID()).append(") - ")
        .append(flagsRepository.getName(asset.getFlag())).append(" (").append(asset.getFlag()).append(")");
        return retString.toString();
    }

    public void setEveApiRepository(final EveApiRepository eveApiRepository) {
        this.eveApiRepository = eveApiRepository;
    }

    public void setLocationRepository(final SystemLocationsRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public void setInvTypesRepository(final InvTypesRepository invTypesRepository) {
        this.invTypesRepository = invTypesRepository;
    }

    public void setFlagsRepository(final FlagsRepository flagsRepository) {
        this.flagsRepository = flagsRepository;
    }
}

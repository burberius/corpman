package net.troja.eve.corpman.pos;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.troja.eve.corpman.CorpManApplication;
import net.troja.eve.corpman.EveApiRepository;
import net.troja.eve.corpman.assets.AssetRepository;
import net.troja.eve.corpman.assets.Filter;
import net.troja.eve.corpman.evedata.InvTypesRepository;
import net.troja.eve.corpman.evedata.ReactionsRepository;
import net.troja.eve.corpman.evedata.SystemLocationsRepository;
import net.troja.eve.corpman.pos.db.Pos;
import net.troja.eve.corpman.pos.db.PosModule;
import net.troja.eve.corpman.pos.db.PosModuleRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.model.shared.Location;

@Repository
public class PosModuleController {
    private static final Logger LOGGER = LogManager.getLogger(PosModuleController.class);
    private static final int CATEGORY_STARBASE = 23;

    @Autowired
    private EveApiRepository eveApiRepository;
    @Autowired
    private PosModuleRepository posModuleRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private SystemLocationsRepository locationRepository;
    @Autowired
    private InvTypesRepository invTypesRepository;
    @Autowired
    private PosManagingController posManagingController;
    @Autowired
    private ReactionsRepository reactionsRepository;
    @Autowired
    private PosMerger posMerger;
    @Autowired
    private PosStructurizer posStructurizer;
    @Autowired
    private PosHelper posHelper;
    @Autowired
    private PosCalculator posCalculator;

    private Date cachedUntil;
    private Date currentTime;
    private Runnable broadcastTrigger;

    private final Filter starbaseFilter = new Filter(null, null, CATEGORY_STARBASE, null, null);

    @Scheduled(initialDelay = CorpManApplication.DELAY_POSMODULE, fixedRate = CorpManApplication.RATE_POSMODULE)
    public void update() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loadPosModuleData start");
        }
        List<PosModule> moduleList = (List<PosModule>) posModuleRepository.findAll();
        boolean updateStructure = false;
        if (moduleList.isEmpty()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Download pos module list");
            }
            final Map<Long, PosModule> downloadAssets = getAssets();
            if ((downloadAssets == null) || downloadAssets.isEmpty()) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("No pos module data!");
                }
                return;
            }
            moduleList = new ArrayList<PosModule>(downloadAssets.values());
            posModuleRepository.save(moduleList);
            updateStructure = true;
        } else {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Update pos module list");
            }
            updateStructure = updatePosModuleData(moduleList);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Updating names of modules");
        }
        addNamesToModules(moduleList);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Matching modules to poses");
        }
        if (updateStructure) {
            final List<Pos> posList = posManagingController.getPosList();
            posHelper.mergeModuleToPos(posList, moduleList);
            for (final Pos pos : posList) {
                final List<PosModule> modulesForPos = posHelper.getModulesForPos(moduleList, pos);
                posMerger.matchModules(modulesForPos);
                posStructurizer.calculateStructure(modulesForPos);

            }
        }
        posCalculator.calculateChangeQuantities(moduleList);
        posModuleRepository.save(moduleList);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loadPosModuleData done");
        }
    }

    private Map<Long, PosModule> getAssets() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("downloadAssets");
        }
        final Map<Long, PosModule> posModuleList = new HashMap<Long, PosModule>();
        final List<Asset> assets = assetRepository.filterAssets(starbaseFilter);
        cachedUntil = assetRepository.getCachedUntil();
        currentTime = assetRepository.getCurrentTime();
        for (final Asset asset : assets) {
            if (!locationRepository.isInSpace(asset.getLocationID())) {
                continue;
            }
            final long typeId = asset.getTypeID();
            if ((typeId == PosTypeIds.TYPEID_SILO) || (typeId == PosTypeIds.TYPEID_REACTOR_COMPLEX) || (typeId == PosTypeIds.TYPEID_REACTOR_SIMPLE)
                    || (typeId == PosTypeIds.TYPEID_COUPLING_ARRAY) || (typeId == PosTypeIds.TYPEID_MOON_HARVESTER)) {
                final String container = invTypesRepository.getName(asset.getTypeID());
                final String system = locationRepository.getName(asset.getLocationID());
                final PosModule module = new PosModule(asset.getItemID(), container, asset.getTypeID(), system, currentTime, cachedUntil);
                for (final Asset ass : asset.getAssets()) {
                    final String type = invTypesRepository.getName(ass.getTypeID());
                    module.setContent(type);
                    module.setContentTypeID(ass.getTypeID());
                    module.setQuantity(ass.getQuantity());
                }
                posModuleList.put(module.getItemID(), module);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("downloadAssets done");
        }
        return posModuleList;
    }

    private boolean updatePosModuleData(final List<PosModule> moduleList) {
        boolean update = false;
        final Map<Long, PosModule> moduleData = getAssets();
        if ((moduleData != null) && (moduleData.size() > 0)) {
            for (final PosModule module : moduleList) {
                final PosModule modNew = moduleData.get(module.getItemID());
                if (modNew == null) {
                    // deleted
                    for (final PosModule inputMod : getInputModulesOf(moduleList, module)) {
                        inputMod.setOutputModule(null);
                        posModuleRepository.save(inputMod);
                    }
                    posModuleRepository.delete(module);
                    update = true;
                    continue;
                }
                if (PosHelper.isContainer(module.getTypeID())) {
                    // check siphon
                    final int changeQuantity = module.getChangeQuantity();
                    if (changeQuantity != 0) {
                        final int fixedQuantity = posCalculator.getFixedQuantity(module, changeQuantity);
                        if (modNew.getQuantity() < fixedQuantity) {
                            module.setDiviation(fixedQuantity - modNew.getQuantity());
                            LOGGER.info("Diviation in " + module.getName());
                        }
                    }
                }
                // update
                module.setCachedUntil(modNew.getCachedUntil());
                module.setQuantity(modNew.getQuantity());
                module.setUpdateTime(modNew.getUpdateTime());
                if (module.getContentTypeID() == 0) {
                    module.setContentTypeID(modNew.getContentTypeID());
                    module.setContent(modNew.getContent());
                }
                posModuleRepository.save(module);
                moduleData.remove(module.getItemID());
            }
            update = update || !moduleData.isEmpty();
            for (final Entry<Long, PosModule> entry : moduleData.entrySet()) {
                // rest is new
                posModuleRepository.save(entry.getValue());
            }
        }
        if (broadcastTrigger != null) {
            broadcastTrigger.run();
        }
        return update;
    }

    private List<PosModule> getInputModulesOf(final Iterable<PosModule> allModules, final PosModule posModule) {
        final List<PosModule> posModules = new ArrayList<PosModule>();
        for (final PosModule mod : allModules) {
            if ((mod.getOutputModule() != null) && mod.getOutputModule().equals(posModule)) {
                posModules.add(mod);
            }
        }
        return posModules;
    }

    private void addNamesToModules(final List<PosModule> moduleList) {
        final List<Long> locationIds = new ArrayList<Long>();
        for (final PosModule module : moduleList) {
            locationIds.add(module.getItemID());
        }
        final Set<Location> locationNames = eveApiRepository.getLocationNames(locationIds);
        if (locationNames == null) {
            return;
        }
        for (final Location loc : locationNames) {
            final long itemID = loc.getItemID();
            final String name = loc.getItemName();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(itemID + " " + name);
            }
            for (final PosModule module : moduleList) {
                if (module.getItemID() == itemID) {
                    module.setName(name);
                    break;
                }
            }
        }
    }

    public List<PosModule> getModules() {
        return (List<PosModule>) posModuleRepository.findAll();
    }

    public PosModule getModule(final long itemId) {
        return posModuleRepository.findOne(itemId);
    }

    public void updateModule(final PosModule module) {
        posModuleRepository.save(module);
    }

    public void updateModules(final List<PosModule> modules) {
        posModuleRepository.save(modules);
    }

    public Date getCachedUntil() {
        return cachedUntil;
    }

    public Date getCurrentTime() {
        return currentTime;
    }

    public void setBroadcastTrigger(final Runnable broadcastTrigger) {
        this.broadcastTrigger = broadcastTrigger;
    }
}

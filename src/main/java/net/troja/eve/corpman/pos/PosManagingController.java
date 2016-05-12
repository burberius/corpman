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
import net.troja.eve.corpman.evedata.InvTypesRepository;
import net.troja.eve.corpman.evedata.SystemLocationsRepository;
import net.troja.eve.corpman.pos.db.Pos;
import net.troja.eve.corpman.pos.db.PosModule;
import net.troja.eve.corpman.pos.db.PosModuleRepository;
import net.troja.eve.corpman.pos.db.PosRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import com.beimin.eveapi.model.corporation.Starbase;
import com.beimin.eveapi.model.shared.Location;
import com.beimin.eveapi.response.corporation.StarbaseDetailResponse;

@Repository
public class PosManagingController {
    private static final Logger LOGGER = LogManager.getLogger(PosManagingController.class);

    @Autowired
    private EveApiRepository eveApiRepository;
    @Autowired
    private PosRepository posRepository;
    @Autowired
    private PosModuleRepository posModuleRepository;
    @Autowired
    private SystemLocationsRepository locationRepository;
    @Autowired
    private InvTypesRepository invTypesRepository;

    private long lastUpdate;
    private Runnable broadcastTrigger;

    @Scheduled(initialDelay = CorpManApplication.DELAY_POS, fixedRate = CorpManApplication.RATE_POS)
    public void update() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("update start");
        }
        final List<Pos> list = posRepository.findAll();
        if ((list == null) || list.isEmpty()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Download pos list");
            }
            final List<Pos> listNew = downloadPosData();
            if ((listNew == null) || listNew.isEmpty()) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("No pos data!");
                }
                return;
            }
            posRepository.save(listNew);
        } else if (isUpdatePosList()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Update pos list");
            }
            updatePosData(list);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("update done");
        }
        if (broadcastTrigger != null) {
            broadcastTrigger.run();
        }
        lastUpdate = System.currentTimeMillis();
    }

    private void updatePosData(final List<Pos> oldList) {
        final List<Pos> newList = downloadPosData();
        if (newList == null) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updatePosData 1 " + oldList.size() + " " + newList.size());
        }
        final Map<Long, Pos> posMap = new HashMap<>();
        for (final Pos pos : newList) {
            posMap.put(pos.getItemID(), pos);
        }

        for (final Pos pos : oldList) {
            final Pos posNew = posMap.get(pos.getItemID());
            if (posNew == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("delete " + pos);
                }
                removePosFromModules(pos);
                posRepository.delete(pos);
                continue;
            }
            pos.update(posNew);
            posRepository.save(pos);
            posMap.remove(pos.getItemID());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updatePosData 2 " + posMap.size());
        }
        for (final Entry<Long, Pos> entry : posMap.entrySet()) {
            posRepository.save(entry.getValue());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updatePosData 3");
        }
    }

    private void removePosFromModules(final Pos pos) {
        final List<PosModule> list = posModuleRepository.findByPos(pos);
        for (final PosModule module : list) {
            module.setPos(null);
        }
        posModuleRepository.save(list);
    }

    private boolean isUpdatePosList() {
        return posRepository.getMinCachedUntil().before(new Date());
    }

    public List<Pos> getPosList() {
        return posRepository.findAll();
    }

    public List<Pos> getPosenInSystem(final String system) {
        final List<Pos> posen = new ArrayList<Pos>();
        for (final Pos pos : getPosList()) {
            if (pos.getSystem().equals(system)) {
                posen.add(pos);
            }
        }
        return posen;
    }

    public void setEveApiRepository(final EveApiRepository eveApiRepository) {
        this.eveApiRepository = eveApiRepository;
    }

    public void setPosRepository(final PosRepository posRepository) {
        this.posRepository = posRepository;
    }

    public void setLocationRepository(final SystemLocationsRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public void setInvTypesRepository(final InvTypesRepository invTypesRepository) {
        this.invTypesRepository = invTypesRepository;
    }

    public void setPosModuleRepository(final PosModuleRepository posModuleRepository) {
        this.posModuleRepository = posModuleRepository;
    }

    public Pos getPos(final long itemId) {
        return posRepository.findOne(itemId);
    }

    private List<Pos> downloadPosData() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("downloadPosData");
        }
        List<Pos> posListNew = null;
        final Set<Starbase> baseList = eveApiRepository.getPosList();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getPosList: " + baseList);
        }
        if (baseList != null) {
            posListNew = new ArrayList<Pos>();
            final List<Long> locationIds = new ArrayList<Long>();
            for (final Starbase base : baseList) {
                final Pos pos = getPosFromApiStarbase(base);
                locationIds.add(pos.getItemID());
                final StarbaseDetailResponse posDetails = eveApiRepository.getPosDetails(pos.getItemID());
                addPosDetails(pos, posDetails);
                posListNew.add(pos);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Pos: " + pos);
                }
            }
            addLocationsToPoses(posListNew, locationIds);
        }
        return posListNew;
    }

    private void addLocationsToPoses(final List<Pos> posListNew, final List<Long> locationIds) {
        final Set<Location> locationNames = eveApiRepository.getLocationNames(locationIds);
        if (locationNames != null) {
            for (final Location loc : locationNames) {
                final long itemID = loc.getItemID();
                final String name = loc.getItemName();
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(itemID + " " + name);
                }
                for (final Pos pos : posListNew) {
                    if (pos.getItemID() == itemID) {
                        pos.setName(name);
                        break;
                    }
                }
            }
        }
    }

    private Pos getPosFromApiStarbase(final Starbase base) {
        final String system = locationRepository.getName(base.getLocationID());
        String moonName = locationRepository.getName(base.getMoonID());
        moonName = moonName.replace(system + " ", "").replace(" Moon", "");
        final String type = invTypesRepository.getName(base.getTypeID());
        return new Pos(base.getItemID(), type, base.getTypeID(), system, base.getLocationID(), moonName, base.getState(), base.getStateTimestamp(),
                base.getOnlineTimestamp());
    }

    private void addPosDetails(final Pos pos, final StarbaseDetailResponse posDetails) {
        for (final Entry<Integer, Integer> entry : posDetails.getFuelMap().entrySet()) {
            if (entry.getKey() == PosTypeIds.TYPEID_STRONTIUM) {
                pos.setStrontium(entry.getValue());
            } else {
                pos.setFuel(entry.getValue());
            }
        }
        pos.setCachedUntil(posDetails.getCachedUntil());
        pos.setAllowAllianceMembers(posDetails.isAllowAllianceMembers());
        pos.setAllowCorpMembers(posDetails.isAllowCorporationMembers());
        pos.setUpdateTime(posDetails.getCurrentTime());
    }

    public Date getLastUpdate() {
        return new Date(lastUpdate);
    }

    public Date getCachedUntil() {
        return posRepository.getMinCachedUntil();
    }

    public void setBroadcastTrigger(final Runnable broadcastTrigger) {
        this.broadcastTrigger = broadcastTrigger;
    }
}

package net.troja.eve.corpman.pos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.troja.eve.corpman.evedata.SovereigntyRepository;
import net.troja.eve.corpman.pos.db.Pos;
import net.troja.eve.corpman.pos.db.Pos.PosSize;
import net.troja.eve.corpman.pos.db.Pos.Race;
import net.troja.eve.corpman.pos.db.PosModule;
import net.troja.eve.corpman.pos.web.PosWebBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PosHelper {
    private static final int FUEL_SMALL = 10;
    private static final int FUEL_MEDIUM = 20;
    private static final int FUEL_LARGE = 40;
    private static final int MINUTES_PER_HOUR = 60;
    private static final double STRONTIUM_LARGE = 400d;
    private static final double STRONTIUM_MEDIUM = 200d;
    private static final double STRONTIUM_SMALL = 100d;

    public static final double TIME_CALC_DAY = 24;

    @Autowired
    private SovereigntyRepository sovereigntyRepository;

    public PosHelper() {
    }

    public PosWebBean getPosWebBean(final Pos pos) {
        final PosWebBean result = new PosWebBean(pos);
        result.setFuelRunTime(getFuelRunTime(pos));
        result.setReinforceRunTime(getReinforceRunTime(pos));
        result.setFuelPercent(getFuelPercent(pos));
        result.setStrontiumPercent(getStrontiumPercent(pos));
        return result;
    }

    public PosSize getSize(final Pos pos) {
        PosSize result = PosSize.LARGE;
        final String type = pos.getType();
        if (type.endsWith("Small")) {
            result = PosSize.SMALL;
        } else if (type.endsWith("Medium")) {
            result = PosSize.MEDIUM;
        }
        return result;
    }

    /**
     * TODO Add factions
     */
    public Race getRace(final Pos pos) {
        Race result = Race.MINMATAR;
        final String type = pos.getType();
        if (type.startsWith("Amarr")) {
            result = Race.AMARR;
        } else if (type.startsWith("Caldari")) {
            result = Race.CALDARI;
        } else if (type.startsWith("Gallente")) {
            result = Race.GALLENTE;
        }
        return result;
    }

    public Date getReinforceEndTime(final Pos pos) {
        final Calendar calendar = Calendar.getInstance();
        final int hour = getReinforceRunTime(pos);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }

    public int getReinforceRunTime(final Pos pos) {
        final int strontium = pos.getStrontium();
        double strontiumPerHour = STRONTIUM_SMALL;
        if (getSize(pos) == PosSize.MEDIUM) {
            strontiumPerHour = STRONTIUM_MEDIUM;
        } else if (getSize(pos) == PosSize.LARGE) {
            strontiumPerHour = STRONTIUM_LARGE;
        }
        if (sovereigntyRepository.isUnderOwnSovereignty(pos.getSystemId())) {
            strontiumPerHour = strontiumPerHour * 0.75;
        }
        return (int) Math.floor(strontium / strontiumPerHour);
    }

    public Date getFuelEndTime(final Pos pos) {
        final Calendar onlineCal = Calendar.getInstance();
        onlineCal.setTime(pos.getOnlineTimestamp());
        final int minute = onlineCal.get(Calendar.MINUTE);
        // prepare calendar
        final Calendar calendar = Calendar.getInstance();
        final int currentMinute = calendar.get(Calendar.MINUTE);
        if (minute >= currentMinute) {
            calendar.add(Calendar.MINUTE, ((MINUTES_PER_HOUR - minute) + currentMinute) * -1);
        } else {
            calendar.add(Calendar.MINUTE, (currentMinute - minute) * -1);
        }
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        final int hour = getFuelRunTime(pos);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }

    public int getFuelRunTime(final Pos pos) {
        final int fuel = pos.getFuel();
        double fuelPerHour = 10d;
        final PosSize size = getSize(pos);
        if (size == PosSize.MEDIUM) {
            fuelPerHour = 20d;
        } else if (size == PosSize.LARGE) {
            fuelPerHour = 40d;
        }
        if (sovereigntyRepository.isUnderOwnSovereignty(pos.getSystemId())) {
            fuelPerHour = fuelPerHour * 0.75;
        }
        final int hour = (int) Math.floor(fuel / fuelPerHour);
        return hour;
    }

    public double getFuelPercent(final Pos pos) {
        double maxFuel = 28000d;
        final PosSize size = getSize(pos);
        if (size == PosSize.MEDIUM) {
            maxFuel = 14000d;
        } else if (size == PosSize.SMALL) {
            maxFuel = 7000d;
        }
        return (pos.getFuel() / maxFuel) * 100;
    }

    public double getStrontiumPercent(final Pos pos) {
        double maxStrontium = 16666d;
        final PosSize size = getSize(pos);
        if (size == PosSize.MEDIUM) {
            maxStrontium = 8333d;
        } else if (size == PosSize.SMALL) {
            maxStrontium = 4166d;
        }
        return (pos.getStrontium() / maxStrontium) * 100;
    }

    public Integer getMaxContainerVolume(final PosModule module) {
        final long type = module.getTypeID();
        if ((type == PosTypeIds.TYPEID_REACTOR_SIMPLE) || (type == PosTypeIds.TYPEID_REACTOR_COMPLEX)) {
            return 1;
        } else if (type == PosTypeIds.TYPEID_COUPLING_ARRAY) {
            if (module.getPos() != null) {
                final Race race = getRace(module.getPos());
                if (race == Race.AMARR) {
                    return 2250;
                } else if (race == Race.GALLENTE) {
                    return 3000;
                }
            }
            return 1500;
        } else if (type == PosTypeIds.TYPEID_SILO) {
            if (module.getPos() != null) {
                final Race race = getRace(module.getPos());
                if (race == Race.AMARR) {
                    return 30000;
                } else if (race == Race.GALLENTE) {
                    return 40000;
                }
            }
            return 20000;
        }
        return 0;
    }

    public static String createNiceTimeString(final long timeHours) {
        final int days = (int) Math.floor(timeHours / TIME_CALC_DAY);
        final int hours = (int) Math.floor(timeHours % TIME_CALC_DAY);
        String result = null;
        if (days > 0) {
            result = days + "d " + hours + "h";
        } else {
            result = hours + "h ";
        }
        return result;
    }

    public Date getEndTime(final int hours) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return cal.getTime();
    }

    public static boolean isReactor(final long typeId) {
        return (typeId == PosTypeIds.TYPEID_REACTOR_SIMPLE) || (typeId == PosTypeIds.TYPEID_REACTOR_COMPLEX);
    }

    public static boolean isContainer(final long typeId) {
        return (typeId == PosTypeIds.TYPEID_SILO) || (typeId == PosTypeIds.TYPEID_COUPLING_ARRAY);
    }

    public int getFuelPerHourAllPos(final List<Pos> posen) {
        int count = 0;
        for (final Pos pos : posen) {
            if (!pos.getType().contains("Gallente")) {
                continue;
            }
            switch (getSize(pos)) {
                case LARGE:
                    count += FUEL_LARGE;
                    break;
                case MEDIUM:
                    count += FUEL_MEDIUM;
                    break;
                case SMALL:
                    count += FUEL_SMALL;
                    break;
                default:
                    break;
            }
        }
        return count;
    }

    public void mergeModuleToPos(final List<Pos> posList, final List<PosModule> moduleList) {
        for (final PosModule module : moduleList) {
            if (module.getPos() != null) {
                continue;
            }

            final Pos pos = findSingleMatchingPos(posList, module.getSystem());
            module.setPos(pos);
        }
    }

    public List<Pos> findMatchingPoses(final List<Pos> posList, final String system) {
        final List<Pos> matchesList = new ArrayList<Pos>();
        for (final Pos pos : posList) {
            if (pos.getSystem().equals(system)) {
                matchesList.add(pos);
            }
        }
        return matchesList;
    }

    public Pos findSingleMatchingPos(final List<Pos> posList, final String system) {
        final List<Pos> matchingPoses = findMatchingPoses(posList, system);
        Pos found = null;
        if (matchingPoses.size() == 1) {
            found = matchingPoses.get(0);
        }
        return found;
    }

    public List<PosModule> getModulesForPos(final List<PosModule> moduleList, final Pos pos) {
        final List<PosModule> result = new ArrayList<>();
        for (final PosModule module : moduleList) {
            final Pos modulePos = module.getPos();
            if ((modulePos != null) && modulePos.equals(pos)) {
                result.add(module);
            }
        }
        return result;
    }

    public List<PosModule> getModulesWithoutOutput(final List<PosModule> modList) {
        final List<PosModule> outList = new ArrayList<PosModule>();
        for (final PosModule mod : modList) {
            if (mod.getOutputModule() == null) {
                outList.add(mod);
            }
        }
        return outList;
    }

    public List<PosModule> getModulesWithoutInput(final List<PosModule> modList) {
        final List<PosModule> outList = new ArrayList<PosModule>();
        for (final PosModule mod : modList) {
            boolean found = false;
            for (final PosModule modCheck : modList) {
                if ((modCheck.getOutputModule() != null) && modCheck.getOutputModule().equals(mod)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                outList.add(mod);
            }
        }
        return outList;
    }

    public static PosModule getInputModuleOf(final List<PosModule> modList, final PosModule posModule) {
        // Only single result as we need it only for silos
        PosModule result = null;
        if (modList != null) {
            for (final PosModule mod : modList) {
                if ((mod.getOutputModule() != null) && mod.getOutputModule().equals(posModule)) {
                    result = mod;
                    break;
                }
            }
        }
        return result;
    }

    public List<PosModule> getInputModulesOf(final List<PosModule> modules, final PosModule module) {
        final List<PosModule> result = new ArrayList<PosModule>();
        if (modules != null) {
            for (final PosModule mod : modules) {
                if ((mod.getOutputModule() != null) && mod.getOutputModule().equals(module)) {
                    result.add(mod);
                }
            }
        }
        return result;
    }

    public boolean containsReactionModule(final List<PosModule> moduleList) {
        boolean result = false;
        for (final PosModule module : moduleList) {
            final long typeID = module.getTypeID();
            if (isReactor(typeID)) {
                result = true;
                break;
            }
        }
        return result;
    }
}

package net.troja.eve.corpman.evedata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.troja.eve.crest.CrestDataHandler.DataType;
import net.troja.eve.crest.CrestHandler;
import net.troja.eve.crest.beans.IndustryFacility;

import org.springframework.stereotype.Repository;

/**
 * SQL: SELECT itemID AS id, itemName AS name, typeID FROM eve.mapDenormalize WHERE typeID IN (5 , 14) LIMIT 1000000;
 */
@Repository
public class SystemLocationsRepository extends IdNameRepository {
    private static final int LOCATION_BORDER1 = 66000000;
    private static final int LOCATION_BORDER2 = 66014933;
    private static final int LOCATION_SUBSTRACT2 = 6000000;
    private static final int LOCATION_SUBSTRACT1 = 6000001;
    private final Map<Integer, IndustryFacility> mapIndustryFacilities;

    public SystemLocationsRepository() {
        super();
        loadCsvData("/locations.csv");
        final CrestHandler crestHandler = new CrestHandler();
        crestHandler.enableDataPrefetching(DataType.INDUSTRY_FACILITY);
        crestHandler.updateData();
        final List<IndustryFacility> industryFacilities = crestHandler.getIndustryFacilities();
        mapIndustryFacilities = new HashMap<>();
        for (final IndustryFacility facility : industryFacilities) {
            mapIndustryFacilities.put(facility.getFacilityId(), facility);
        }
    }

    public String getLocationName(final Long locationID) {
        String result = "";
        if (locationID != null) {
            final String solarSystems = getName(locationID);
            if (solarSystems == null) {
                result = getFacilityLocation(locationID);
            } else {
                result = solarSystems;
            }
        }
        return result;
    }

    private String getFacilityLocation(final Long locationID) {
        String result;
        long workLocationID = locationID;
        if (workLocationID >= LOCATION_BORDER1) {
            if (workLocationID < LOCATION_BORDER2) {
                workLocationID = workLocationID - LOCATION_SUBSTRACT1;
            } else {
                workLocationID = workLocationID - LOCATION_SUBSTRACT2;
            }
        }
        final IndustryFacility industryFacility = mapIndustryFacilities.get((int) workLocationID);
        if (industryFacility == null) {
            result = "unknown (" + locationID + ")";
        } else {
            result = industryFacility.getName();
        }
        return result;
    }

    public boolean isInSpace(final long locationID) {
        return getName(locationID) != null;
    }
}

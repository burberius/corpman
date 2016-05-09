package net.troja.eve.corpman.evedata;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Repository;

/**
 * SQL: SELECT typeID AS id, typeName AS name, volume, i.groupID, g.groupName, g.categoryID, c.categoryName FROM invTypes i LEFT JOIN invGroups g ON
 * i.groupID = g.groupID JOIN invCategories c ON g.categoryID = c.categoryID LIMIT 25000;
 */
@Repository
public class InvTypesRepository extends AbstractCsvRepository {
    private final Map<Long, InvType> invTypesMap = new HashMap<Long, InvType>();

    public InvTypesRepository() {
        super();
        loadCsvData("/invTypes.csv");
    }

    @Override
    protected void processData(final long typeId, final CSVRecord pieces) {
        final double volume = Double.parseDouble(pieces.get(2));
        final int groupId = Integer.parseInt(pieces.get(3));
        final int categoryId = Integer.parseInt(pieces.get(5));
        invTypesMap
        .put(typeId, new InvType((int) typeId, pieces.get(1), volume, groupId, pieces.get(4), categoryId, pieces.get(6)));
    }

    public InvType getInvType(final long typeId) {
        return invTypesMap.get(typeId);
    }

    public String getName(final long typeId) {
        final InvType invType = invTypesMap.get(typeId);
        if (invType != null) {
            return invType.getTypeName();
        }
        return "";
    }

    public Double getVolume(final long typeId) {
        final InvType invType = invTypesMap.get(typeId);
        if (invType != null) {
            return invType.getVolume();
        }
        return 0d;
    }
}

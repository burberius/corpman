package net.troja.eve.corpman.evedata;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;

public class IdNameRepository extends AbstractCsvRepository {
    private final Map<Long, String> entries = new HashMap<>();

    @Override
    protected void processData(final long typeId, final CSVRecord pieces) {
        final String name = pieces.get(1).replaceAll("\"", "");
        entries.put(typeId, name);
    }

    public String getName(final long id) {
        return entries.get(id);
    }
}

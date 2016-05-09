package net.troja.eve.corpman.evedata;

import org.springframework.stereotype.Repository;

/**
 * SQL: SELECT flagID, flagName FROM eve.invFlags;
 */
@Repository
public class FlagsRepository extends IdNameRepository {
    public FlagsRepository() {
        super();
        loadCsvData("/invFlags.csv");
    }
}

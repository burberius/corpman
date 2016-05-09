package net.troja.eve.corpman.evedata;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractCsvRepository {
    private static final Logger LOGGER = LogManager.getLogger(AbstractCsvRepository.class);

    protected final void loadCsvData(final String filename) {
        final InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(filename));
        try {
            final CSVParser parse = CSVFormat.EXCEL.withHeader().parse(reader);
            for(final CSVRecord record : parse.getRecords()) {
                final long typeId = Long.parseLong(record.get(0));
                processData(typeId, record);
            }
            parse.close();
            reader.close();
        } catch (final IOException e) {
            LOGGER.error("Couldn't read csv file", e);
        }
    }

    protected abstract void processData(long typeId, CSVRecord record);
}

package net.troja.eve.corpman.journal;

import java.util.Set;

import net.troja.eve.corpman.EveApiRepository;
import net.troja.eve.corpman.evedata.InvType;
import net.troja.eve.corpman.evedata.InvTypesRepository;
import net.troja.eve.corpman.journal.db.Rat;
import net.troja.eve.corpman.journal.db.RatRepository;
import net.troja.eve.corpman.journal.db.WalletJournal;
import net.troja.eve.corpman.journal.db.WalletJournalRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.beimin.eveapi.model.shared.JournalEntry;
import com.beimin.eveapi.model.shared.RefType;
import com.beimin.eveapi.response.shared.WalletJournalResponse;

@Service
public class WalletJournalService {
    private static final Logger LOGGER = LogManager.getLogger(WalletJournalService.class);

    @Autowired
    private EveApiRepository eveApiRepository;
    @Autowired
    private WalletJournalRepository journalRepository;
    @Autowired
    private RatRepository ratRepository;
    @Autowired
    private InvTypesRepository invTypeRepository;

    @Scheduled(initialDelay = 10000, fixedRate = 6000000)
    public void update() {
        LOGGER.info("update wallet journal");
        final int count = 50;
        long maxRef = 0;
        final WalletJournal maxEntry = journalRepository.findFirstByOrderByRefID_Desc();
        if (maxEntry != null) {
            maxRef = maxEntry.getRefID();
        }
        final double taxRate = eveApiRepository.getTaxRate();
        long lowestId = Long.MAX_VALUE;
        boolean done = false;
        int numLoaded = 0;
        WalletJournalResponse response = eveApiRepository.getWalletJournal(0, count);
        while (response != null) {
            final Set<JournalEntry> entries = response.getAll();
            for (final JournalEntry entry : entries) {
                if (entry.getRefID() <= maxRef) {
                    done = true;
                    continue;
                }
                final WalletJournal journal = new WalletJournal(entry);
                journal.setTaxRate(taxRate);
                final RefType refType = entry.getRefType();
                if ((refType != null) && refType.equals(RefType.BOUNTY_PRIZES)) {
                    parseRats(journal, entry.getReason());
                }
                journalRepository.save(journal);
                ratRepository.save(journal.getRats());
                if (entry.getRefID() < lowestId) {
                    lowestId = entry.getRefID();
                }
                numLoaded++;
            }
            if ((entries.size() < count) || (lowestId <= maxRef) || done) {
                break;
            }
            response = eveApiRepository.getWalletJournal(lowestId, count);
        }
        LOGGER.info("Got " + numLoaded + " new entries");
    }

    private void parseRats(final WalletJournal journal, final String reason) {
        final String[] entries = reason.split(",");
        for (int pos = 0; pos < entries.length; pos++) {
            if (!entries[pos].equals("...")) {
                final String[] split = entries[pos].split(":");
                final InvType invType = invTypeRepository.getInvType(Integer.parseInt(split[0]));
                if (invType != null) {
                    final Rat rat = new Rat(Integer.parseInt(split[1]), invType.getTypeName(), invType.getGroupID(), invType.getGroupName());
                    journal.addRat(rat);
                } else {
                    LOGGER.info("Could not find invType " + split[0] + ", probably the static data is outdated!");
                }
            }
        }
    }
}

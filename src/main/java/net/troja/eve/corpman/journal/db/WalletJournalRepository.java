package net.troja.eve.corpman.journal.db;

import org.springframework.data.repository.CrudRepository;

public interface WalletJournalRepository extends CrudRepository<WalletJournal, Long> {
    WalletJournal findFirstByOrderByRefID_Desc();
}

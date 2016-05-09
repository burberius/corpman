package net.troja.eve.corpman.pos.db;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PosRepository extends CrudRepository<Pos, Long> {
    @Override
    List<Pos> findAll();

    @Query("SELECT MIN(cachedUntil) FROM Pos")
    Date getMinCachedUntil();
}

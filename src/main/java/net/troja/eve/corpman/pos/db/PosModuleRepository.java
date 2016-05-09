package net.troja.eve.corpman.pos.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PosModuleRepository extends CrudRepository<PosModule, Long> {
    List<PosModule> findByPos(Pos pos);
}

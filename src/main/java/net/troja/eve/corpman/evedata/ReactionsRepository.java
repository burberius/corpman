package net.troja.eve.corpman.evedata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Repository;

/**
 * SQL: SELECT reactionTypeID, input, r.typeID, COALESCE(valueInt, valueFloat) * quantity as quantity FROM eve.invTypeReactions r JOIN
 * dgmTypeAttributes a ON r.typeID = a.typeID WHERE a.attributeID = 726;
 */
@Repository
public class ReactionsRepository extends AbstractCsvRepository {
    private final Map<Long, Set<ReactionComponent>> reactionInputs = new HashMap<>();
    private final Map<Long, Set<ReactionComponent>> reactionOutputs = new HashMap<>();

    public ReactionsRepository() {
        super();
        loadCsvData("/reactions.csv");
    }

    @Override
    protected void processData(final long typeId, final CSVRecord pieces) {
        Map<Long, Set<ReactionComponent>> workMap = reactionOutputs;
        if ("1".equals(pieces.get(1))) {
            workMap = reactionInputs;
        }
        final long componentId = Long.parseLong(pieces.get(2));
        final int quantity = Integer.parseInt(pieces.get(3));
        Set<ReactionComponent> set = workMap.get(typeId);
        if (set == null) {
            set = new HashSet<>();
            workMap.put(typeId, set);
        }
        set.add(new ReactionComponent(componentId, quantity));
    }

    public Set<ReactionComponent> getInputs(final long typeId) {
        return reactionInputs.get(typeId);
    }

    public Set<ReactionComponent> getOutputs(final long typeId) {
        return reactionOutputs.get(typeId);
    }
}

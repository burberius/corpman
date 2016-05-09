package net.troja.eve.corpman.pos;

import java.util.List;
import java.util.Set;

import net.troja.eve.corpman.evedata.ReactionComponent;
import net.troja.eve.corpman.evedata.ReactionsRepository;
import net.troja.eve.corpman.pos.db.PosModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PosCalculator {
    @Autowired
    private ReactionsRepository reactionsRepository;

    public int getFixedQuantity(final PosModule posModule, final int quantityPerRun) {
        final int quantity = posModule.getQuantity();
        if (posModule.getTypeID() == PosTypeIds.TYPEID_COUPLING_ARRAY) {
            return quantity;
        }
        final long timeDiff = (System.currentTimeMillis() - posModule.getCachedUntil().getTime()) + (6 * 60 * 60 * 1000);
        final int extraQuantity = quantityPerRun * (int) Math.ceil(timeDiff / (60 * 60 * 1000));
        return quantity + extraQuantity;
    }

    public int getChangeQuantity(final PosModule posModule, final List<PosModule> modList) {
        int result = 0;
        final PosModule inputModule = PosHelper.getInputModuleOf(modList, posModule);
        final PosModule outputModule = posModule.getOutputModule();
        if (inputModule == null) {
            if ((outputModule != null) && PosHelper.isReactor(outputModule.getTypeID())) {
                // Output Silo
                result = -100;
            } else if ((outputModule != null) && PosHelper.isContainer(outputModule.getTypeID())) {
                // Output Silos
                result = getChangeQuantity(outputModule, modList);
            }
        } else if (inputModule.getTypeID() == PosTypeIds.TYPEID_MOON_HARVESTER) {
            // Harvester to Silo
            result = 100;
        } else if (inputModule.getTypeID() == PosTypeIds.TYPEID_REACTOR_SIMPLE) {
            // Simple to Silo
            result = 200;
            if ((outputModule != null) && (outputModule.getTypeID() == PosTypeIds.TYPEID_REACTOR_COMPLEX)) {
                // Simple to Silo to Complex
                result = 100;
            }
        } else if (inputModule.getTypeID() == PosTypeIds.TYPEID_REACTOR_COMPLEX) {
            // Complex to Silo
            final Set<ReactionComponent> outputs = reactionsRepository.getOutputs(inputModule.getContentTypeID());
            result = outputs.iterator().next().getQuantity();
        } else if ((outputModule == null) || PosHelper.isContainer(outputModule.getTypeID())) {
            // Silos in row, search left
            result = getChangeQuantity(inputModule, modList);
        } else if (PosHelper.isContainer(inputModule.getTypeID())) {
            // Silos in row, search right
            result = getChangeQuantity(outputModule, modList);
        }
        return result;
    }

    public void setReactionsRepository(final ReactionsRepository reactionsRepository) {
        this.reactionsRepository = reactionsRepository;
    }

    public void calculateChangeQuantities(final List<PosModule> moduleList) {
        for (final PosModule module : moduleList) {
            if (PosHelper.isContainer(module.getTypeID())) {
                module.setChangeQuantity(getChangeQuantity(module, moduleList));
            }
        }
    }
}

package net.troja.eve.corpman.pos;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.troja.eve.corpman.evedata.ReactionComponent;
import net.troja.eve.corpman.evedata.ReactionsRepository;
import net.troja.eve.corpman.pos.db.PosModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PosMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(PosHelper.class);

    @Autowired
    private ReactionsRepository reactionsRepository;
    @Autowired
    private PosHelper posHelper;

    public void matchModules(final List<PosModule> modList) {
        Collections.sort(modList, getComparator());
        for (final PosModule module : modList) {
            final long moduleTypeID = module.getTypeID();
            LOGGER.info("Working on " + module.getType() + " " + module.getContent() + " " + module.getSystem());
            if (PosHelper.isReactor(moduleTypeID)) {
                matchToReactor(module, modList);
            } else if (PosHelper.isContainer(moduleTypeID)) {
                if (module.getContentTypeID() > 0) {
                    matchToContainer(module, modList);
                }
            } else if (moduleTypeID == PosTypeIds.TYPEID_MOON_HARVESTER) {
                matchToHarvester(module, modList);
            } else {
                LOGGER.info("Häää? " + module.toString());
            }
        }
        printMergeStatus(modList);
    }

    private Comparator<PosModule> getComparator() {
        return new Comparator<PosModule>() {
            @Override
            public int compare(final PosModule left, final PosModule right) {
                return getTypeValue(left) - getTypeValue(right);
            }

            private int getTypeValue(final PosModule module) {
                int result = 3;
                if (PosHelper.isReactor(module.getTypeID())) {
                    result = 0;
                } else if (module.getTypeID() == PosTypeIds.TYPEID_COUPLING_ARRAY) {
                    result = 1;
                } else if (module.getTypeID() == PosTypeIds.TYPEID_SILO) {
                    result = 2;
                }
                return result;
            }
        };
    }

    /**
     * Match reactor with container without an input only.
     *
     * @param module
     * @param modules
     */
    private void matchToReactor(final PosModule module, final List<PosModule> modules) {
        LOGGER.info(" matchToReactor");
        final Set<ReactionComponent> outputs = reactionsRepository.getOutputs(module.getContentTypeID());
        for (final PosModule workModule : modules) {
            if (PosHelper.isContainer(workModule.getTypeID()) && (workModule.getInputCount() == 0)) {
                LOGGER.info("  try " + workModule.getType() + " " + workModule.getContent());
                for (final ReactionComponent comp : outputs) {
                    if (workModule.getContentTypeID() == comp.getTypeId()) {
                        module.setOutputModule(workModule);
                        workModule.addInput(comp.getTypeId());
                        LOGGER.info("  Matched Reactor to Container");
                        return;
                    }
                }
            } else {
                LOGGER.info("  try not " + workModule.getType() + " inputs: " + workModule.getInputCount());
            }
        }
    }

    /**
     * Match container as input for reactors or containers without an input only. Containers must not be already linked!
     *
     * @param module
     * @param modList
     */
    private void matchToContainer(final PosModule module, final List<PosModule> modList) {
        LOGGER.info(" matchToContainer " + module.getContent());
        for (final PosModule workModule : modList) {
            if (module.equals(workModule)) {
                LOGGER.info("  equal");
                continue;
            }
            LOGGER.info("  Check on " + workModule.getType() + " " + workModule.getContent());
            final long workTypeID = workModule.getTypeID();
            if (PosHelper.isReactor(workTypeID)) {
                final long reactionTypeID = workModule.getContentTypeID();
                for (final ReactionComponent comp : reactionsRepository.getInputs(reactionTypeID)) {
                    LOGGER.info("   Input type " + comp.getTypeId() + " - Silo type " + module.getContentTypeID());
                    if ((module.getContentTypeID() == comp.getTypeId()) && !workModule.containsInput(comp.getTypeId())) {
                        LOGGER.info("  Matched Container to Reactor");
                        module.setOutputModule(workModule);
                        workModule.addInput(comp.getTypeId());
                        return;
                    }
                }
            } else if (PosHelper.isContainer(workTypeID) && (workModule.getInputCount() == 0)
                    && (module.getContentTypeID() == workModule.getContentTypeID()) && !module.equals(workModule.getOutputModule())) {
                LOGGER.info("  Matched container to container");
                if ((module.getInputCount() == 1) && (module.getQuantity() > workModule.getQuantity())) {
                    final PosModule inputModule = PosHelper.getInputModuleOf(modList, module);
                    inputModule.setOutputModule(workModule);
                    workModule.setOutputModule(module);
                    workModule.addInput(workModule.getTypeID());
                } else {
                    module.setOutputModule(workModule);
                    workModule.addInput(module.getContentTypeID());
                }
                return;
            } else if ((module.getTypeID() == PosTypeIds.TYPEID_SILO) && (workModule.getTypeID() == PosTypeIds.TYPEID_COUPLING_ARRAY)
                    && (module.getContentTypeID() == workModule.getContentTypeID()) && (workModule.getOutputModule() != null)) {
                // break up coupling array to reactor and put silo in between
                module.setOutputModule(workModule.getOutputModule());
                workModule.setOutputModule(module);
                module.addInput(module.getContentTypeID());
            } else {
                LOGGER.info("  No match for " + workModule.getType());
            }
        }
    }

    /**
     * Match moon harvester as input for simple reactors or containers without an input only.
     *
     * @param module
     * @param modList
     */
    private void matchToHarvester(final PosModule module, final List<PosModule> modList) {
        LOGGER.info(" matchToHarvester");
        for (final PosModule workModule : modList) {
            final long typeID = workModule.getTypeID();
            if (typeID == PosTypeIds.TYPEID_REACTOR_SIMPLE) {
                final Set<ReactionComponent> inputs = reactionsRepository.getInputs(workModule.getContentTypeID());
                final int numInputs = inputs.size();
                if (workModule.getInputCount() < numInputs) {
                    LOGGER.info("  Matched moon harvester to simple reactor");
                    module.setOutputModule(workModule);
                    for (final ReactionComponent comp : inputs) {
                        if (!workModule.containsInput(comp.getTypeId())) {
                            workModule.addInput(comp.getTypeId());
                            break;
                        }
                    }
                    return;
                }
            } else if (PosHelper.isContainer(typeID) && (workModule.getInputCount() == 0) && (workModule.getContentTypeID() > 0)) {
                LOGGER.info("  Matched moon harvester to container");
                module.setOutputModule(workModule);
                workModule.addInput(workModule.getContentTypeID());
                return;
            }
        }
    }

    public void printMergeStatus(final List<PosModule> modList) {
        final Set<PosModule> visited = new HashSet<>();
        for (final PosModule module : modList) {
            if (module.getInputCount() > 0) {
                continue;
            }
            if (module.getOutputModule() == null) {
                LOGGER.error("Found unmatched module: " + module.toString());
            } else {
                visited.add(module);
                final StringBuilder line = new StringBuilder();
                line.append(module.getType()).append("(").append(module.getContent()).append(")");
                PosModule workModule = module.getOutputModule();
                while (workModule != null) {
                    visited.add(workModule);
                    line.append(" -> ").append(workModule.getType()).append("(").append(workModule.getContent()).append(")");
                    workModule = workModule.getOutputModule();
                    if ((workModule != null) && visited.contains(workModule)) {
                        break;
                    }
                }
                LOGGER.info(line.toString());
            }
        }
    }

    public void setReactionsRepository(final ReactionsRepository reactionsRepository) {
        this.reactionsRepository = reactionsRepository;
    }
}

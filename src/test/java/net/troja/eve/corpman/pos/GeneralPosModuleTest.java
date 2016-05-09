package net.troja.eve.corpman.pos;

import java.util.ArrayList;
import java.util.List;

import net.troja.eve.corpman.evedata.ReactionsRepository;
import net.troja.eve.corpman.pos.db.Pos;
import net.troja.eve.corpman.pos.db.PosModule;

public class GeneralPosModuleTest {
    private static final String SYSTEM_A = "Jita";
    private static final String SYSTEM_B = "Yita";
    private static final int TYPE_PLATINUM = 16644;
    private static final int TYPE_TECHNETIUM = 16649;
    private static final int TYPE_PT = 16662;
    private static final int TYPE_CP = 16659;
    private static final int TYPE_F = 16679;
    private static final int REACTION_PT = 17952;
    private static final int REACTION_CP = 17942;
    public static final int REACTION_F = 17967;

    protected final ReactionsRepository reactionsRepository = new ReactionsRepository();

    private Pos posA;
    private Pos posB;
    protected Pos posC;
    protected PosModule moduleA;
    protected PosModule moduleB;
    protected PosModule moduleC;
    protected PosModule moduleD;
    protected PosModule moduleE;
    protected PosModule moduleF;
    protected PosModule moduleG;
    protected PosModule moduleH;
    protected PosModule moduleI;

    protected List<PosModule> getComplexAndSimpleFulleridsPos1() {
        // A\
        // ..C-D-E\
        // B/......G-H-I
        // ......F/
        final List<PosModule> moduleList = new ArrayList<>();
        moduleA.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleA.setContentTypeID(TYPE_PLATINUM);
        moduleA.setContent("P");
        moduleList.add(moduleA);
        moduleB.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleB.setContentTypeID(TYPE_TECHNETIUM);
        moduleB.setContent("T");
        moduleList.add(moduleB);
        moduleC.setTypeID(PosTypeIds.TYPEID_REACTOR_SIMPLE);
        moduleC.setContentTypeID(REACTION_PT);
        moduleC.setType("Simple Reactor C");
        moduleList.add(moduleC);
        moduleD.setTypeID(PosTypeIds.TYPEID_COUPLING_ARRAY);
        moduleD.setContentTypeID(TYPE_PT);
        moduleD.setContent("PT");
        moduleD.setType("Coupling Array D");
        moduleList.add(moduleD);
        moduleE.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleE.setContentTypeID(TYPE_PT);
        moduleE.setContent("PT");
        moduleE.setType("Silo E");
        moduleList.add(moduleE);
        moduleF.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleF.setContentTypeID(TYPE_CP);
        moduleF.setContent("CP");
        moduleList.add(moduleF);
        moduleG.setTypeID(PosTypeIds.TYPEID_REACTOR_COMPLEX);
        moduleG.setContentTypeID(REACTION_F);
        moduleG.setType("Complex Reactor");
        moduleList.add(moduleG);
        moduleH.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleH.setContentTypeID(TYPE_F);
        moduleH.setContent("F");
        moduleH.setQuantity(500);
        moduleList.add(moduleH);
        moduleI.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleI.setContentTypeID(TYPE_F);
        moduleI.setContent("F");
        moduleI.setQuantity(5000);
        moduleList.add(moduleI);
        return moduleList;
    }

    protected List<PosModule> getComplexAndSimpleFulleridsPos2() {
        // A\
        // ..C-E\
        // B/....G-H-I
        // ....F/
        final List<PosModule> moduleList = new ArrayList<>();
        moduleA.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleA.setContentTypeID(TYPE_PLATINUM);
        moduleA.setContent("P");
        moduleList.add(moduleA);
        moduleB.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleB.setContentTypeID(TYPE_TECHNETIUM);
        moduleB.setContent("T");
        moduleList.add(moduleB);
        moduleC.setTypeID(PosTypeIds.TYPEID_REACTOR_SIMPLE);
        moduleC.setContentTypeID(REACTION_PT);
        moduleC.setType("Simple Reactor C");
        moduleList.add(moduleC);
        moduleE.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleE.setContentTypeID(TYPE_PT);
        moduleE.setContent("PT");
        moduleE.setType("Silo E");
        moduleList.add(moduleE);
        moduleF.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleF.setContentTypeID(TYPE_CP);
        moduleF.setContent("CP");
        moduleList.add(moduleF);
        moduleG.setTypeID(PosTypeIds.TYPEID_REACTOR_COMPLEX);
        moduleG.setContentTypeID(REACTION_F);
        moduleG.setType("Complex Reactor");
        moduleList.add(moduleG);
        moduleH.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleH.setContentTypeID(TYPE_F);
        moduleH.setContent("F");
        moduleH.setQuantity(500);
        moduleList.add(moduleH);
        moduleI.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleI.setContentTypeID(TYPE_F);
        moduleI.setContent("F");
        moduleI.setQuantity(5000);
        moduleList.add(moduleI);
        return moduleList;
    }

    protected List<PosModule> getComplexAndSimpleFulleridsPos3() {
        // A\
        // ..C-E\
        // B/....G-H-I
        // ....F/
        final List<PosModule> moduleList = new ArrayList<>();
        moduleA.setTypeID(PosTypeIds.TYPEID_MOON_HARVESTER);
        moduleList.add(moduleA);
        moduleB.setTypeID(PosTypeIds.TYPEID_MOON_HARVESTER);
        moduleList.add(moduleB);
        moduleC.setTypeID(PosTypeIds.TYPEID_REACTOR_SIMPLE);
        moduleC.setContentTypeID(REACTION_CP);
        moduleC.setType("Simple Reactor C");
        moduleList.add(moduleC);
        moduleE.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleE.setContentTypeID(TYPE_CP);
        moduleE.setContent("CP");
        moduleE.setType("Silo E");
        moduleList.add(moduleE);
        moduleF.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleF.setContentTypeID(TYPE_PT);
        moduleF.setContent("PT");
        moduleList.add(moduleF);
        moduleG.setTypeID(PosTypeIds.TYPEID_REACTOR_COMPLEX);
        moduleG.setContentTypeID(REACTION_F);
        moduleG.setType("Complex Reactor");
        moduleList.add(moduleG);
        moduleH.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleH.setContentTypeID(TYPE_F);
        moduleH.setContent("F");
        moduleH.setQuantity(500);
        moduleList.add(moduleH);
        moduleI.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleI.setContentTypeID(TYPE_F);
        moduleI.setContent("F");
        moduleI.setQuantity(5000);
        moduleList.add(moduleI);
        return moduleList;
    }

    protected void preparePoses() {
        posA = new Pos();
        posA.setSystem(SYSTEM_A);
        posB = new Pos();
        posB.setSystem(SYSTEM_B);
        posC = new Pos();
        posC.setSystem(SYSTEM_B);
        moduleA = new PosModule();
        moduleA.setSystem(SYSTEM_A);
        moduleB = new PosModule();
        moduleB.setSystem(SYSTEM_B);
        moduleC = new PosModule();
        moduleC.setSystem(SYSTEM_B);
        moduleD = new PosModule();
        moduleD.setSystem(SYSTEM_B);
        moduleE = new PosModule();
        moduleE.setSystem(SYSTEM_B);
        moduleF = new PosModule();
        moduleF.setSystem(SYSTEM_B);
        moduleG = new PosModule();
        moduleG.setSystem(SYSTEM_B);
        moduleH = new PosModule();
        moduleH.setSystem(SYSTEM_B);
        moduleI = new PosModule();
        moduleI.setSystem(SYSTEM_B);
    }

    protected List<PosModule> getEmptyMoonMiningPos() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleA.setPos(posC);
        moduleA.setTypeID(PosTypeIds.TYPEID_COUPLING_ARRAY);
        moduleA.setContent(null);
        moduleA.setContentTypeID(0);
        moduleList.add(moduleA);
        moduleB.setPos(posC);
        moduleB.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleB.setContent(null);
        moduleB.setContentTypeID(0);
        moduleList.add(moduleB);
        moduleC.setPos(posC);
        moduleC.setTypeID(PosTypeIds.TYPEID_MOON_HARVESTER);
        moduleList.add(moduleC);
        return moduleList;
    }

    protected List<PosModule> getMoonMiningPos() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleA.setPos(posC);
        moduleA.setTypeID(PosTypeIds.TYPEID_COUPLING_ARRAY);
        moduleA.setContent("Chromium");
        moduleA.setContentTypeID(16641);
        moduleList.add(moduleA);
        moduleB.setPos(posC);
        moduleB.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleB.setContent("Chromium");
        moduleB.setContentTypeID(16641);
        moduleList.add(moduleB);
        moduleC.setPos(posC);
        moduleC.setTypeID(PosTypeIds.TYPEID_MOON_HARVESTER);
        moduleList.add(moduleC);
        return moduleList;
    }

    protected List<PosModule> getSimpleReactionPosWithSilos() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleA.setPos(posC);
        moduleA.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleA.setContent("Hexite");
        moduleA.setContentTypeID(16665);
        moduleList.add(moduleA);
        moduleB.setPos(posC);
        moduleB.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleB.setContent("Chromium");
        moduleB.setContentTypeID(16641);
        moduleList.add(moduleB);
        moduleC.setPos(posC);
        moduleC.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleC.setContent("Platinum");
        moduleC.setContentTypeID(16644);
        moduleList.add(moduleC);
        moduleD.setPos(posC);
        moduleD.setTypeID(PosTypeIds.TYPEID_REACTOR_SIMPLE);
        moduleD.setContent("Hexite Reaction");
        moduleD.setContentTypeID(17949);
        moduleList.add(moduleD);
        return moduleList;
    }

    protected List<PosModule> getSimpleReactionPosWithMoonHarvester() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleA.setPos(posC);
        moduleA.setType("Silo");
        moduleA.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleA.setContent("Hexite");
        moduleA.setContentTypeID(16665);
        moduleList.add(moduleA);
        moduleB.setPos(posC);
        moduleB.setType("Harvester");
        moduleB.setTypeID(PosTypeIds.TYPEID_MOON_HARVESTER);
        moduleList.add(moduleB);
        moduleC.setPos(posC);
        moduleC.setType("Harvester");
        moduleC.setTypeID(PosTypeIds.TYPEID_MOON_HARVESTER);
        moduleList.add(moduleC);
        moduleD.setPos(posC);
        moduleD.setType("Reactor");
        moduleD.setTypeID(PosTypeIds.TYPEID_REACTOR_SIMPLE);
        moduleD.setContent("Hexite Reaction");
        moduleD.setContentTypeID(17949);
        moduleList.add(moduleD);
        return moduleList;
    }

}

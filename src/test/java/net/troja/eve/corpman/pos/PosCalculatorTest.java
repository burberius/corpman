package net.troja.eve.corpman.pos;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import net.troja.eve.corpman.pos.db.PosModule;

import org.junit.Before;
import org.junit.Test;

public class PosCalculatorTest extends GeneralPosTest {
    private PosCalculator toTest;

    @Before
    public void setUp() {
        init();
        toTest = new PosCalculator();
        toTest.setReactionsRepository(reactionRepo);
    }

    @Test
    public void changeQuantityForHarvestSilo() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleB.setOutputModule(moduleC);
        moduleB.setTypeID(PosTypeIds.TYPEID_MOON_HARVESTER);
        moduleC.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleC.setOutputModule(moduleA);
        moduleA.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleList.add(moduleA);
        moduleList.add(moduleB);
        moduleList.add(moduleC);

        assertThat(toTest.getChangeQuantity(moduleC, moduleList), equalTo(100));
        assertThat(toTest.getChangeQuantity(moduleA, moduleList), equalTo(100));
    }

    @Test
    public void changeQuantityForSiloToReactor() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleA.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleA.setOutputModule(moduleB);
        moduleB.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleB.setOutputModule(moduleC);
        moduleC.setTypeID(PosTypeIds.TYPEID_REACTOR_SIMPLE);
        moduleList.add(moduleB);
        moduleList.add(moduleC);

        assertThat(toTest.getChangeQuantity(moduleA, moduleList), equalTo(-100));
        assertThat(toTest.getChangeQuantity(moduleB, moduleList), equalTo(-100));
    }

    @Test
    public void changeQuantityForSimpleReactorToSilo() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleB.setOutputModule(moduleC);
        moduleB.setTypeID(PosTypeIds.TYPEID_REACTOR_SIMPLE);
        moduleC.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleList.add(moduleB);
        moduleList.add(moduleC);

        assertThat(toTest.getChangeQuantity(moduleC, moduleList), equalTo(200));
    }

    @Test
    public void changeQuantityForSimpleSiloComplex() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleA.setOutputModule(moduleB);
        moduleA.setTypeID(PosTypeIds.TYPEID_REACTOR_SIMPLE);
        moduleB.setOutputModule(moduleC);
        moduleB.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleC.setTypeID(PosTypeIds.TYPEID_REACTOR_COMPLEX);
        moduleList.add(moduleA);
        moduleList.add(moduleB);
        moduleList.add(moduleC);

        assertThat(toTest.getChangeQuantity(moduleB, moduleList), equalTo(100));
    }

    @Test
    public void changeQuantityForComplexToSilo() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleA.setOutputModule(moduleB);
        moduleA.setTypeID(PosTypeIds.TYPEID_REACTOR_COMPLEX);
        moduleA.setContentTypeID(GeneralPosModuleTest.REACTION_F);
        moduleB.setOutputModule(moduleC);
        moduleB.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleC.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleList.add(moduleA);
        moduleList.add(moduleB);
        moduleList.add(moduleC);

        assertThat(toTest.getChangeQuantity(moduleB, moduleList), equalTo(3000));
        assertThat(toTest.getChangeQuantity(moduleC, moduleList), equalTo(3000));
    }

    @Test
    public void changeQuantityForLonelySilo() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleC.setTypeID(PosTypeIds.TYPEID_SILO);
        moduleList.add(moduleC);

        assertThat(toTest.getChangeQuantity(moduleC, moduleList), equalTo(0));
    }

    @Test
    public void changeQuantityEmptyModList() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleC.setTypeID(PosTypeIds.TYPEID_SILO);

        assertThat(toTest.getChangeQuantity(moduleC, moduleList), equalTo(0));
    }

    @Test
    public void changeQuantityModListNull() {
        moduleC.setTypeID(PosTypeIds.TYPEID_SILO);

        assertThat(toTest.getChangeQuantity(moduleC, null), equalTo(0));
    }
}

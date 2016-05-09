package net.troja.eve.corpman.pos;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import net.troja.eve.corpman.pos.db.Pos;
import net.troja.eve.corpman.pos.db.PosModule;

import org.junit.Before;
import org.junit.Test;

public class PosHelperTest extends GeneralPosTest {
    private PosHelper toTest;

    @Before
    public void setUp() {
        init();
        toTest = new PosHelper();
    }

    @Test
    public void testMergingNoMatch() {
        final List<Pos> posList = new ArrayList<>();
        final List<PosModule> moduleList = new ArrayList<>();
        posList.add(posB);
        posList.add(posC);
        moduleList.add(moduleA);

        toTest.mergeModuleToPos(posList, moduleList);

        assertThat(moduleA.getPos(), nullValue());
    }

    @Test
    public void testMergingWithSinglePos() {
        final List<Pos> posList = new ArrayList<>();
        final List<PosModule> moduleList = new ArrayList<>();
        posList.add(posA);
        posList.add(posB);
        moduleList.add(moduleA);

        toTest.mergeModuleToPos(posList, moduleList);

        assertThat(moduleA.getPos(), equalTo(posA));
    }

    @Test
    public void testMergingWithMultiplePos() {
        final List<Pos> posList = new ArrayList<>();
        final List<PosModule> moduleList = new ArrayList<>();
        posList.add(posA);
        posList.add(posB);
        posList.add(posC);
        moduleList.add(moduleB);

        toTest.mergeModuleToPos(posList, moduleList);

        assertThat(moduleB.getPos(), nullValue());
    }

    @Test
    public void testGetModulesForPos() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleList.add(moduleA);
        moduleB.setPos(posC);
        moduleList.add(moduleB);

        final List<PosModule> posModulesForPos = toTest.getModulesForPos(moduleList, posC);
        assertThat(posModulesForPos.size(), equalTo(1));
        assertThat(posModulesForPos.get(0), equalTo(moduleB));
    }

    @Test
    public void testFindSingleMatchingPosEmptyList() {
        assertThat(toTest.findSingleMatchingPos(new ArrayList<>(), SYSTEM_A), nullValue());
    }

    @Test
    public void testFindSingleMatchingPosNoHit() {
        final List<Pos> posList = new ArrayList<>();
        posList.add(posB);
        posList.add(posC);

        assertThat(toTest.findSingleMatchingPos(posList, SYSTEM_A), nullValue());
    }

    @Test
    public void testFindSingleMatchingPosHit() {
        final List<Pos> posList = new ArrayList<>();
        posList.add(posA);
        posList.add(posC);

        assertThat(toTest.findSingleMatchingPos(posList, SYSTEM_A), equalTo(posA));
    }

    @Test
    public void testFindSingleMatchingPos2PosSystem() {
        final List<Pos> posList = new ArrayList<>();
        posList.add(posB);
        posList.add(posC);

        assertThat(toTest.findSingleMatchingPos(posList, SYSTEM_B), nullValue());
    }

    @Test
    public void testGetModulesWithoutOutputEmptyList() {
        final List<PosModule> modulesWithoutOutput = toTest.getModulesWithoutOutput(new ArrayList<>());
        assertThat(modulesWithoutOutput, notNullValue());
        assertThat(modulesWithoutOutput.size(), equalTo(0));
    }

    @Test
    public void testGetModulesWithoutOutputOneHit() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleList.add(moduleA);
        moduleA.setOutputModule(moduleB);
        moduleList.add(moduleB);

        final List<PosModule> modulesWithoutOutput = toTest.getModulesWithoutOutput(moduleList);
        assertThat(modulesWithoutOutput.size(), equalTo(1));
        assertThat(modulesWithoutOutput.get(0), equalTo(moduleB));
    }

    @Test
    public void testGetModulesWithoutInputEmptyList() {
        final List<PosModule> modulesWithoutOutput = toTest.getModulesWithoutInput(new ArrayList<>());
        assertThat(modulesWithoutOutput, notNullValue());
        assertThat(modulesWithoutOutput.size(), equalTo(0));
    }

    @Test
    public void testGetModulesWithoutInputOneHit() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleList.add(moduleA);
        moduleA.setOutputModule(moduleB);
        moduleList.add(moduleB);

        final List<PosModule> modulesWithoutOutput = toTest.getModulesWithoutInput(moduleList);
        assertThat(modulesWithoutOutput.size(), equalTo(1));
        assertThat(modulesWithoutOutput.get(0), equalTo(moduleA));
    }

    @Test
    public void testContainsReactionModuleHit() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleList.add(moduleA);
        moduleB.setTypeID(PosTypeIds.TYPEID_REACTOR_SIMPLE);
        moduleList.add(moduleB);

        assertThat(toTest.containsReactionModule(moduleList), equalTo(true));
    }

    @Test
    public void testContainsReactionModuleMiss() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleList.add(moduleA);
        moduleList.add(moduleB);

        assertThat(toTest.containsReactionModule(moduleList), equalTo(false));
    }

    @Test
    public void testGetInputModuleOf() {
        final List<PosModule> moduleList = new ArrayList<>();
        moduleA.setOutputModule(moduleC);
        moduleList.add(moduleA);
        moduleList.add(moduleB);
        moduleList.add(moduleC);

        assertThat(PosHelper.getInputModuleOf(moduleList, moduleC), equalTo(moduleA));
    }
}

package net.troja.eve.corpman.pos;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import net.troja.eve.corpman.pos.db.PosModule;

import org.junit.Before;
import org.junit.Test;

public class PosMergerTest extends GeneralPosModuleTest {
    private final PosMerger toTest = new PosMerger();

    @Before
    public void setUp() {
        toTest.setReactionsRepository(reactionsRepository);
        preparePoses();
    }

    @Test
    public void matchWithEmptySilo() {
        final List<PosModule> moduleList = getEmptyMoonMiningPos();

        toTest.matchModules(moduleList);

        assertThat(moduleC.getOutputModule(), nullValue());
        assertThat(moduleB.getOutputModule(), nullValue());
        assertThat(moduleA.getOutputModule(), nullValue());

    }

    @Test
    public void matchMoonMining() {
        final List<PosModule> moduleList = getMoonMiningPos();

        toTest.matchModules(moduleList);

        assertThat(moduleC.getOutputModule(), equalTo(moduleA));
        assertThat(moduleA.getOutputModule(), equalTo(moduleB));
    }

    @Test
    public void matchSimpleReactionPosWithSilos() {
        final List<PosModule> moduleList = getSimpleReactionPosWithSilos();

        toTest.matchModules(moduleList);

        assertThat(moduleB.getOutputModule(), equalTo(moduleD));
        assertThat(moduleC.getOutputModule(), equalTo(moduleD));
        assertThat(moduleD.getOutputModule(), equalTo(moduleA));
        assertThat(moduleA.getOutputModule(), nullValue());
    }

    @Test
    public void matchSimpleReactionPosWithMoonHarvester() {
        final List<PosModule> moduleList = getSimpleReactionPosWithMoonHarvester();

        toTest.matchModules(moduleList);

        assertThat(moduleB.getOutputModule(), equalTo(moduleD));
        assertThat(moduleC.getOutputModule(), equalTo(moduleD));
        assertThat(moduleD.getOutputModule(), equalTo(moduleA));
        assertThat(moduleA.getOutputModule(), nullValue());
    }

    @Test
    public void matchingOfComplexAndSimpleFulleridsPos1() {
        final List<PosModule> moduleList = getComplexAndSimpleFulleridsPos1();
        Collections.shuffle(moduleList);

        toTest.matchModules(moduleList);

        checkComplexAndSimpleFulleridsPos1(moduleList);
    }

    @Test
    public void matchingOfComplexAndSimpleFulleridsPos2() {
        final List<PosModule> moduleList = getComplexAndSimpleFulleridsPos2();
        Collections.shuffle(moduleList);

        toTest.matchModules(moduleList);

        checkComplexAndSimpleFulleridsPos2(moduleList);
    }

    @Test
    public void matchingOfComplexAndSimpleFulleridsPos3() {
        final List<PosModule> moduleList = getComplexAndSimpleFulleridsPos3();
        Collections.shuffle(moduleList);

        toTest.matchModules(moduleList);

        checkComplexAndSimpleFulleridsPos2(moduleList);
    }

    private void checkComplexAndSimpleFulleridsPos1(final List<PosModule> moduleList) {
        assertThat("A->C", moduleA.getOutputModule(), equalTo(moduleC));
        assertThat("B->C", moduleB.getOutputModule(), equalTo(moduleC));
        assertThat("C->D", moduleC.getOutputModule(), equalTo(moduleD));
        assertThat("D->E", moduleD.getOutputModule(), equalTo(moduleE));
        assertThat("E->G", moduleE.getOutputModule(), equalTo(moduleG));
        assertThat("F->G", moduleF.getOutputModule(), equalTo(moduleG));
        assertThat("G->H", moduleG.getOutputModule(), equalTo(moduleH));
        assertThat("H->I", moduleH.getOutputModule(), equalTo(moduleI));
    }

    protected void checkComplexAndSimpleFulleridsPos2(final List<PosModule> moduleList) {
        assertThat("A->C", moduleA.getOutputModule(), equalTo(moduleC));
        assertThat("B->C", moduleB.getOutputModule(), equalTo(moduleC));
        assertThat("C->E", moduleC.getOutputModule(), equalTo(moduleE));
        assertThat("E->G", moduleE.getOutputModule(), equalTo(moduleG));
        assertThat("F->G", moduleF.getOutputModule(), equalTo(moduleG));
        assertThat("G->H", moduleG.getOutputModule(), equalTo(moduleH));
        assertThat("H->I", moduleH.getOutputModule(), equalTo(moduleI));
    }
}

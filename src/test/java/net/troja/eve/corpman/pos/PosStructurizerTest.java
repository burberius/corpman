package net.troja.eve.corpman.pos;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import net.troja.eve.corpman.pos.db.PosModule;

import org.junit.Before;
import org.junit.Test;

public class PosStructurizerTest extends GeneralPosModuleTest {
    private PosStructurizer toTest;

    @Before
    public void setUp() {
        toTest = new PosStructurizer();
        toTest.setPosHelper(new PosHelper());
        preparePoses();
    }

    @Test
    public void structureSimplePos() {
        final List<PosModule> moonMiningPos = getMoonMiningPos();
        moduleC.setOutputModule(moduleA);
        moduleA.setOutputModule(moduleB);

        toTest.calculateStructure(moonMiningPos);

        assertThat(moduleC.getX(), is(10));
        assertThat(moduleA.getX(), is(20));
        assertThat(moduleB.getX(), is(30));
        assertThat(moduleA.getY(), is(10));
        assertThat(moduleB.getY(), is(10));
        assertThat(moduleC.getY(), is(10));
    }

    @Test
    public void structureSimpleReactionPosWithMoonHarvester() {
        // B\
        // ..D-A
        // C/
        final List<PosModule> posModules = getSimpleReactionPosWithMoonHarvester();
        moduleB.setOutputModule(moduleD);
        moduleC.setOutputModule(moduleD);
        moduleD.setOutputModule(moduleA);

        toTest.calculateStructure(posModules);

        assertThat(moduleB.getX(), is(10));
        assertThat(moduleC.getX(), is(10));
        assertThat(moduleD.getX(), is(20));
        assertThat(moduleA.getX(), is(30));
        assertThat(moduleB.getY(), is(10));
        assertThat(moduleC.getY(), is(20));
        assertThat(moduleD.getY(), is(15));
        assertThat(moduleA.getY(), is(15));
    }

    @Test
    public void structureSimpleReactionPosWithSilos() {
        // B\
        // ..D-A
        // C/
        final List<PosModule> posModules = getSimpleReactionPosWithSilos();
        moduleB.setOutputModule(moduleD);
        moduleC.setOutputModule(moduleD);
        moduleD.setOutputModule(moduleA);

        toTest.calculateStructure(posModules);

        assertThat(moduleB.getX(), is(10));
        assertThat(moduleC.getX(), is(10));
        assertThat(moduleD.getX(), is(20));
        assertThat(moduleA.getX(), is(30));
        assertThat(moduleB.getY(), is(10));
        assertThat(moduleC.getY(), is(20));
        assertThat(moduleD.getY(), is(15));
        assertThat(moduleA.getY(), is(15));
    }

    @Test
    public void structureComplexAndSimpleFulleridsPos1() {
        // A\
        // ..C-D-E\
        // B/......G-H-I
        // ......F/
        final List<PosModule> posModules = getComplexAndSimpleFulleridsPos1();
        moduleA.setOutputModule(moduleC);
        moduleB.setOutputModule(moduleC);
        moduleC.setOutputModule(moduleD);
        moduleD.setOutputModule(moduleE);
        moduleE.setOutputModule(moduleG);
        moduleF.setOutputModule(moduleG);
        moduleG.setOutputModule(moduleH);
        moduleH.setOutputModule(moduleI);

        toTest.calculateStructure(posModules);

        // X
        assertThat(moduleA.getX(), is(10));
        assertThat(moduleB.getX(), is(10));
        assertThat(moduleC.getX(), is(20));
        assertThat(moduleD.getX(), is(30));
        assertThat(moduleE.getX(), is(40));
        assertThat(moduleF.getX(), is(40));
        assertThat(moduleG.getX(), is(50));
        assertThat(moduleH.getX(), is(60));
        assertThat(moduleI.getX(), is(70));
        // Y
        assertThat(moduleA.getY(), is(10));
        assertThat(moduleB.getY(), is(20));
        assertThat(moduleC.getY(), is(15));
        assertThat(moduleD.getY(), is(15));
        assertThat(moduleE.getY(), is(15));
        assertThat(moduleF.getY(), is(25));
        assertThat(moduleG.getY(), is(20));
        assertThat(moduleH.getY(), is(20));
        assertThat(moduleI.getY(), is(20));
    }

    @Test
    public void structureComplexAndSimpleFulleridsPos2() {
        // A\
        // ..C-E\
        // B/....G-H-I
        // ....F/
        final List<PosModule> posModules = getComplexAndSimpleFulleridsPos2();
        moduleA.setOutputModule(moduleC);
        moduleB.setOutputModule(moduleC);
        moduleC.setOutputModule(moduleE);
        moduleE.setOutputModule(moduleG);
        moduleF.setOutputModule(moduleG);
        moduleG.setOutputModule(moduleH);
        moduleH.setOutputModule(moduleI);

        toTest.calculateStructure(posModules);

        // X
        assertThat(moduleA.getX(), is(10));
        assertThat(moduleB.getX(), is(10));
        assertThat(moduleC.getX(), is(20));
        assertThat(moduleE.getX(), is(30));
        assertThat(moduleF.getX(), is(30));
        assertThat(moduleG.getX(), is(40));
        assertThat(moduleH.getX(), is(50));
        assertThat(moduleI.getX(), is(60));
        // Y
        assertThat(moduleA.getY(), is(10));
        assertThat(moduleB.getY(), is(20));
        assertThat(moduleC.getY(), is(15));
        assertThat(moduleE.getY(), is(15));
        assertThat(moduleF.getY(), is(25));
        assertThat(moduleG.getY(), is(20));
        assertThat(moduleH.getY(), is(20));
        assertThat(moduleI.getY(), is(20));
    }

    @Test
    public void structureComplexAndSimpleFulleridsPos3() {
        // A\
        // ..C-E\
        // B/....G-H-I
        // ....F/
        final List<PosModule> posModules = getComplexAndSimpleFulleridsPos3();
        moduleA.setOutputModule(moduleC);
        moduleB.setOutputModule(moduleC);
        moduleC.setOutputModule(moduleE);
        moduleE.setOutputModule(moduleG);
        moduleF.setOutputModule(moduleG);
        moduleG.setOutputModule(moduleH);
        moduleH.setOutputModule(moduleI);

        toTest.calculateStructure(posModules);

        // X
        assertThat(moduleA.getX(), is(10));
        assertThat(moduleB.getX(), is(10));
        assertThat(moduleC.getX(), is(20));
        assertThat(moduleE.getX(), is(30));
        assertThat(moduleF.getX(), is(30));
        assertThat(moduleG.getX(), is(40));
        assertThat(moduleH.getX(), is(50));
        assertThat(moduleI.getX(), is(60));
        // Y
        assertThat(moduleA.getY(), is(10));
        assertThat(moduleB.getY(), is(20));
        assertThat(moduleC.getY(), is(15));
        assertThat(moduleE.getY(), is(15));
        assertThat(moduleF.getY(), is(25));
        assertThat(moduleG.getY(), is(20));
        assertThat(moduleH.getY(), is(20));
        assertThat(moduleI.getY(), is(20));
    }
}

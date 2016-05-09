package net.troja.eve.corpman.pos;

import net.troja.eve.corpman.evedata.ReactionsRepository;
import net.troja.eve.corpman.pos.db.Pos;
import net.troja.eve.corpman.pos.db.PosModule;

public class GeneralPosTest {
    protected static final String SYSTEM_A = "Jita";
    protected static final String SYSTEM_B = "Yita";

    protected final ReactionsRepository reactionRepo = new ReactionsRepository();

    protected Pos posA;
    protected Pos posB;
    protected Pos posC;
    protected PosModule moduleA;
    protected PosModule moduleB;
    protected PosModule moduleC;

    public void init() {
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
    }

}

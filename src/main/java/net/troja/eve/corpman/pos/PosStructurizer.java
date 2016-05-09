package net.troja.eve.corpman.pos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.troja.eve.corpman.pos.db.PosModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PosStructurizer {
    private static final int POS_STEP = 10;

    @Autowired
    private PosHelper posHelper;

    public void calculateStructure(final List<PosModule> modules) {
        final List<PosModule> withoutOutput = new ArrayList<PosModule>();
        for (final PosModule module : modules) {
            module.setX(10);
            module.setY(10);
            if (module.getOutputModule() == null) {
                withoutOutput.add(module);
            }
        }
        final Coordinate minPoint = new Coordinate();
        for (final PosModule module : withoutOutput) {
            minPoint.setMin(walkInputs(modules, module));
        }
        for (final PosModule module : modules) {
            module.setX(module.getX() + Math.abs(minPoint.getX()) + 10);
            module.setY((module.getY() + Math.abs(minPoint.getY() - 10)));
        }
    }

    private Coordinate walkInputs(final List<PosModule> modules, final PosModule module) {
        final List<PosModule> inputs = posHelper.getInputModulesOf(modules, module);
        Collections.sort(inputs, (o1, o2) -> {
            final int in1 = posHelper.getInputModulesOf(modules, o1).size();
            final int in2 = posHelper.getInputModulesOf(modules, o2).size();
            if(in1 == in2) {
                return Long.compare(o1.getContentTypeID(), o2.getContentTypeID());
            }
            return Integer.compare(in2, in1);
        });
        int nextY = module.getY();
        if (inputs.size() > 1) {
            nextY -= (((inputs.size() - 1) * POS_STEP) / 2);
        }
        final Coordinate coord = new Coordinate();
        coord.setX(module.getX());
        coord.setY(module.getY());
        for (final PosModule mod : inputs) {
            mod.setX(module.getX() - POS_STEP);
            mod.setY(nextY);
            nextY += POS_STEP;
            coord.setMin(walkInputs(modules, mod));
        }
        return coord;
    }

    public void setPosHelper(final PosHelper posHelper) {
        this.posHelper = posHelper;
    }

    private class Coordinate {
        private int x = Integer.MAX_VALUE;
        private int y = Integer.MAX_VALUE;

        public int getX() {
            return x;
        }

        public void setX(final int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(final int y) {
            this.y = y;
        }

        public void setMin(final Coordinate inCoordinate) {
            setX(Math.min(getX(), inCoordinate.x));
            setY(Math.min(getY(), inCoordinate.y));
        }

        @Override
        public String toString() {
            return "Coordinate [x=" + x + ", y=" + y + "]";
        }

    }
}

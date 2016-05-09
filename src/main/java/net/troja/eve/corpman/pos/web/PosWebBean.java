package net.troja.eve.corpman.pos.web;

import java.util.ArrayList;
import java.util.List;

import net.troja.eve.corpman.pos.db.Pos;

public class PosWebBean {
    private long itemID;
    private String type;
    private long typeID;
    private String system;
    private String moon;
    private String name;
    private String state;
    private int fuel;
    private int strontium;
    private double fuelPercent;
    private double strontiumPercent;
    private int reinforceRunTime;
    private int fuelRunTime;
    private long stateTimestamp;
    private List<PosModuleWebBean> modules = new ArrayList<>();

    public PosWebBean(final Pos pos) {
        super();
        itemID = pos.getItemID();
        type = pos.getType();
        typeID = pos.getTypeID();
        system = pos.getSystem();
        moon = pos.getMoon();
        name = pos.getName();
        state = pos.getState();
        fuel = pos.getFuel();
        strontium = pos.getStrontium();
        stateTimestamp = pos.getStateTimestamp().getTime();
    }

    public long getItemID() {
        return itemID;
    }

    public void setItemID(final long itemID) {
        this.itemID = itemID;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public long getTypeID() {
        return typeID;
    }

    public void setTypeID(final long typeID) {
        this.typeID = typeID;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(final String system) {
        this.system = system;
    }

    public String getMoon() {
        return moon;
    }

    public void setMoon(final String moon) {
        this.moon = moon;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(final int fuel) {
        this.fuel = fuel;
    }

    public int getStrontium() {
        return strontium;
    }

    public void setStrontium(final int strontium) {
        this.strontium = strontium;
    }

    public int getReinforceRunTime() {
        return reinforceRunTime;
    }

    public void setReinforceRunTime(final int reinforceRunTime) {
        this.reinforceRunTime = reinforceRunTime;
    }

    public int getFuelRunTime() {
        return fuelRunTime;
    }

    public void setFuelRunTime(final int fuelRunTime) {
        this.fuelRunTime = fuelRunTime;
    }

    public double getFuelPercent() {
        return fuelPercent;
    }

    public void setFuelPercent(final double fuelPercent) {
        this.fuelPercent = fuelPercent;
    }

    public double getStrontiumPercent() {
        return strontiumPercent;
    }

    public void setStrontiumPercent(final double strontiumPercent) {
        this.strontiumPercent = strontiumPercent;
    }

    public long getStateTimestamp() {
        return stateTimestamp;
    }

    public void setStateTimestamp(final long stateTimestamp) {
        this.stateTimestamp = stateTimestamp;
    }

    public List<PosModuleWebBean> getModules() {
        return modules;
    }

    public void setModules(final List<PosModuleWebBean> modules) {
        this.modules = modules;
    }

    public void addModule(final PosModuleWebBean module) {
        modules.add(module);
    }
}

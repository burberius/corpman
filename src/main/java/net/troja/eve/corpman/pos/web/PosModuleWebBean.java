package net.troja.eve.corpman.pos.web;

import java.util.HashMap;
import java.util.Map;

import net.troja.eve.corpman.pos.PosHelper;
import net.troja.eve.corpman.pos.db.PosModule;

public class PosModuleWebBean {
    private long itemID;
    private String type;
    private long typeID;
    private String name;
    private boolean container;
    private String system;
    private String content;
    private long contentTypeID;
    private int quantity;
    private int containerQuantity;
    private double volume;
    private double percent;
    private int timeLeft;
    private Long outputModule;
    private Long posItemID;
    private boolean input;
    private int x;
    private int y;
    private final Map<Long, String> possiblePos = new HashMap<>();

    public PosModuleWebBean() {
        super();
    }

    public PosModuleWebBean(final PosModule module) {
        itemID = module.getItemID();
        type = module.getType();
        typeID = module.getTypeID();
        name = module.getName();
        container = PosHelper.isContainer(typeID);
        system = module.getSystem();
        content = module.getContent();
        contentTypeID = module.getContentTypeID();
        x = module.getX();
        y = module.getY();
        final PosModule outModule = module.getOutputModule();
        if (outModule != null) {
            outputModule = outModule.getItemID();
        }
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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isContainer() {
        return container;
    }

    public void setContainer(final boolean container) {
        this.container = container;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(final String system) {
        this.system = system;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public long getContentTypeID() {
        return contentTypeID;
    }

    public void setContentTypeID(final long contentTypeID) {
        this.contentTypeID = contentTypeID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }

    public int getContainerQuantity() {
        return containerQuantity;
    }

    public void setContainerQuantity(final int containerQuantity) {
        this.containerQuantity = containerQuantity;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(final double volume) {
        this.volume = volume;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(final int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(final double percent) {
        this.percent = percent;
    }

    public Long getOutputModule() {
        return outputModule;
    }

    public void setOutputModule(final Long outputModule) {
        this.outputModule = outputModule;
    }

    public Map<Long, String> getPossiblePos() {
        return possiblePos;
    }

    public Long getPosItemID() {
        return posItemID;
    }

    public void setPosItemID(final Long posItemID) {
        this.posItemID = posItemID;
    }

    public void addPossiblePos(final Long key, final String value) {
        possiblePos.put(key, value);
    }

    public boolean isInput() {
        return input;
    }

    public void setInput(final boolean input) {
        this.input = input;
    }

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

    @Override
    public String toString() {
        return "PosModuleWebBean [itemID=" + itemID + ", type=" + type + ", typeID=" + typeID + ", name=" + name + ", container=" + container
                + ", system=" + system + ", content=" + content + ", contentTypeID=" + contentTypeID + ", quantity=" + quantity
                + ", containerQuantity=" + containerQuantity + ", volume=" + volume + ", percent=" + percent + ", timeLeft=" + timeLeft
                + ", outputModule=" + outputModule + ", posItemID=" + posItemID + ", x=" + x + ", y=" + y + ", possiblePos=" + possiblePos + "]";
    }
}

package net.troja.eve.corpman.pos.db;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
public class PosModule {
    @Id
    private long itemID;
    private String type;
    private long typeID;
    private String name;
    private String system;
    private String content;
    private long contentTypeID;
    private int quantity;
    private int changeQuantity;
    private int diviation;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date cachedUntil;
    @ManyToOne(fetch = FetchType.EAGER)
    private PosModule outputModule;
    @ManyToOne(fetch = FetchType.EAGER)
    private Pos pos;
    private int x;
    private int y;

    @Transient
    private final Set<Long> inputs = new HashSet<>();

    public PosModule() {
        super();
    }

    public PosModule(final long itemID, final String type, final long typeID, final String system, final Date updateTime, final Date cachedUntil) {
        super();
        this.itemID = itemID;
        this.type = type;
        this.typeID = typeID;
        this.system = system;
        this.updateTime = updateTime;
        this.cachedUntil = cachedUntil;
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

    public int getChangeQuantity() {
        return changeQuantity;
    }

    public void setChangeQuantity(final int changeQuantity) {
        this.changeQuantity = changeQuantity;
    }

    public int getDiviation() {
        return diviation;
    }

    public void setDiviation(final int diviation) {
        this.diviation = diviation;
    }

    public PosModule getOutputModule() {
        return outputModule;
    }

    public void setOutputModule(final PosModule outputModule) {
        this.outputModule = outputModule;
    }

    public Pos getPos() {
        return pos;
    }

    public void setPos(final Pos pos) {
        this.pos = pos;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(final Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCachedUntil() {
        return cachedUntil;
    }

    public void setCachedUntil(final Date cachedUntil) {
        this.cachedUntil = cachedUntil;
    }

    public int getInputCount() {
        return inputs.size();
    }

    public void addInput(final long typeId) {
        inputs.add(typeId);
    }

    public boolean containsInput(final long typeId) {
        return inputs.contains(typeId);
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
        return "PosModule [itemID=" + itemID + ", type=" + type + ", typeID=" + typeID + ", name=" + name + ", system=" + system + ", content="
                + content + ", contentTypeID=" + contentTypeID + ", quantity=" + quantity + ", updateTime=" + updateTime + ", cachedUntil="
                + cachedUntil + ", pos=" + pos + ", x=" + x + ", y=" + y + ", inputs=" + inputs + "]";
    }

    public int update(final PosModule modNew) {
        if (modNew.getContent() != null) {
            setContent(modNew.getContent());
            setContentTypeID(modNew.getContentTypeID());
        }
        final int diff = modNew.getQuantity() - getQuantity();
        setQuantity(modNew.getQuantity());
        setUpdateTime(modNew.getUpdateTime());
        setCachedUntil(modNew.getCachedUntil());
        return diff;
    }
}


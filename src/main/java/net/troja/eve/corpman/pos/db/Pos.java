package net.troja.eve.corpman.pos.db;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Doku: http://wiki.eve-id.net/APIv2_Corp_StarbaseList_XML http://wiki.eve-id.net/APIv2_Corp_StarbaseDetail_XML
 */
@Entity
public class Pos {
    @Id
    private long itemID;
    private String type;
    private long typeID;
    private String system;
    private int systemId;
    private String moon;
    private String name;
    private String state;
    @Temporal(TemporalType.TIMESTAMP)
    private Date stateTimestamp;
    @Temporal(TemporalType.TIMESTAMP)
    private Date onlineTimestamp;
    @Temporal(TemporalType.TIMESTAMP)
    private Date cachedUntil;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
    private int fuel;
    private int strontium;
    private boolean allowAllianceMembers;
    private boolean allowCorpMembers;

    public enum PosSize {
        LARGE, MEDIUM, SMALL;
    }

    public enum Race {
        GALLENTE, AMARR, CALDARI, MINMATAR;
    }

    /**************************************************************************
     * CONSTRUCTOR METHODS
     **************************************************************************/

    public Pos() {
        super();
    }

    public Pos(final long itemID, final String type, final long typeID, final String system, final int systemId, final String moon, final int state,
            final Date stateTimestamp, final Date onlineTimestamp) {
        super();
        this.itemID = itemID;
        this.type = type;
        this.typeID = typeID;
        this.system = system;
        this.systemId = systemId;
        this.moon = moon;
        this.state = getPosState(state);
        this.stateTimestamp = stateTimestamp;
        this.onlineTimestamp = onlineTimestamp;
    }

    /**************************************************************************
     * GETTER/SETTER METHODS
     **************************************************************************/

    public long getItemID() {
        return itemID;
    }

    public void setItemID(final long itemID) {
        this.itemID = itemID;
    }

    public String getName() {
        if (name == null) {
            return "Kein Name";
        }
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(final int systemId) {
        this.systemId = systemId;
    }

    public String getMoon() {
        return moon;
    }

    public void setMoon(final String moon) {
        this.moon = moon;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public Date getStateTimestamp() {
        return stateTimestamp;
    }

    public void setStateTimestamp(final Date stateTimestamp) {
        this.stateTimestamp = stateTimestamp;
    }

    public Date getOnlineTimestamp() {
        return onlineTimestamp;
    }

    public void setOnlineTimestamp(final Date onlineTimestamp) {
        this.onlineTimestamp = onlineTimestamp;
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

    public Date getCachedUntil() {
        return cachedUntil;
    }

    public void setCachedUntil(final Date cachedUntil) {
        this.cachedUntil = cachedUntil;
    }

    public boolean isAllowAllianceMembers() {
        return allowAllianceMembers;
    }

    public void setAllowAllianceMembers(final boolean allowAllianceMembers) {
        this.allowAllianceMembers = allowAllianceMembers;
    }

    public boolean isAllowCorpMembers() {
        return allowCorpMembers;
    }

    public void setAllowCorpMembers(final boolean allowCorpMembers) {
        this.allowCorpMembers = allowCorpMembers;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(final Date updateTime) {
        this.updateTime = updateTime;
    }

    private String getPosState(final int state) {
        switch (state) {
            case 1:
                return "Anchored / Offline";
            case 2:
                return "Onlining";
            case 3:
                return "Reinforced";
            case 4:
                return "Online";
            default:
                return "Unanchored";
        }
    }

    @Override
    public String toString() {
        return "Pos [itemID=" + itemID + ", type=" + type + ", system=" + system + ", moon=" + moon + ", state=" + state + ", stateTimestamp="
                + stateTimestamp + ", onlineTimestamp=" + onlineTimestamp + ", cachedUntil=" + cachedUntil + ", updateTime=" + updateTime + ", fuel="
                + fuel + ", strontium=" + strontium + ", allowAllianceMembers=" + allowAllianceMembers + ", allowCorpMembers=" + allowCorpMembers
                + "]";
    }

    public void update(final Pos posNew) {
        setState(posNew.getState());
        setStrontium(posNew.getStrontium());
        setFuel(posNew.getFuel());
        setAllowAllianceMembers(posNew.isAllowAllianceMembers());
        setAllowCorpMembers(posNew.isAllowCorpMembers());
        setStateTimestamp(posNew.getStateTimestamp());
        setOnlineTimestamp(posNew.getOnlineTimestamp());
        setCachedUntil(posNew.getCachedUntil());
        setUpdateTime(posNew.getUpdateTime());
        setSystem(posNew.getSystem());
        setMoon(posNew.getMoon());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (allowAllianceMembers ? 1231 : 1237);
        result = (prime * result) + (allowCorpMembers ? 1231 : 1237);
        result = (prime * result) + ((cachedUntil == null) ? 0 : cachedUntil.hashCode());
        result = (prime * result) + fuel;
        result = (prime * result) + (int) (itemID ^ (itemID >>> 32));
        result = (prime * result) + ((moon == null) ? 0 : moon.hashCode());
        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
        result = (prime * result) + ((onlineTimestamp == null) ? 0 : onlineTimestamp.hashCode());
        result = (prime * result) + ((state == null) ? 0 : state.hashCode());
        result = (prime * result) + ((stateTimestamp == null) ? 0 : stateTimestamp.hashCode());
        result = (prime * result) + strontium;
        result = (prime * result) + ((system == null) ? 0 : system.hashCode());
        result = (prime * result) + ((type == null) ? 0 : type.hashCode());
        result = (prime * result) + (int) (typeID ^ (typeID >>> 32));
        result = (prime * result) + ((updateTime == null) ? 0 : updateTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pos other = (Pos) obj;
        if (allowAllianceMembers != other.allowAllianceMembers) {
            return false;
        }
        if (allowCorpMembers != other.allowCorpMembers) {
            return false;
        }
        if (cachedUntil == null) {
            if (other.cachedUntil != null) {
                return false;
            }
        } else if (!cachedUntil.equals(other.cachedUntil)) {
            return false;
        }
        if (fuel != other.fuel) {
            return false;
        }
        if (itemID != other.itemID) {
            return false;
        }
        if (moon == null) {
            if (other.moon != null) {
                return false;
            }
        } else if (!moon.equals(other.moon)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (onlineTimestamp == null) {
            if (other.onlineTimestamp != null) {
                return false;
            }
        } else if (!onlineTimestamp.equals(other.onlineTimestamp)) {
            return false;
        }
        if (state == null) {
            if (other.state != null) {
                return false;
            }
        } else if (!state.equals(other.state)) {
            return false;
        }
        if (stateTimestamp == null) {
            if (other.stateTimestamp != null) {
                return false;
            }
        } else if (!stateTimestamp.equals(other.stateTimestamp)) {
            return false;
        }
        if (strontium != other.strontium) {
            return false;
        }
        if (system == null) {
            if (other.system != null) {
                return false;
            }
        } else if (!system.equals(other.system)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (typeID != other.typeID) {
            return false;
        }
        if (updateTime == null) {
            if (other.updateTime != null) {
                return false;
            }
        } else if (!updateTime.equals(other.updateTime)) {
            return false;
        }
        return true;
    }
}

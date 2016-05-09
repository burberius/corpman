package net.troja.eve.corpman.assets;

public class Filter {
    private Long itemID;
    private Integer typeID;
    private Integer groupID;
    private Integer categoryID;
    private Long locationID;
    private Integer flagID;

    public Filter(final Integer typeID, final Integer groupID, final Integer categoryID, final Long locationID, final Integer flagID) {
        super();
        this.typeID = typeID;
        this.groupID = groupID;
        this.categoryID = categoryID;
        this.locationID = locationID;
        this.flagID = flagID;
    }

    public Filter(final Long itemID, final Integer typeID, final Integer groupID, final Integer categoryID, final Long locationID,
            final Integer flagID) {
        super();
        this.itemID = itemID;
        this.typeID = typeID;
        this.groupID = groupID;
        this.categoryID = categoryID;
        this.locationID = locationID;
        this.flagID = flagID;
    }

    public Long getItemID() {
        return itemID;
    }

    public void setItemID(final Long itemID) {
        this.itemID = itemID;
    }

    public Integer getTypeID() {
        return typeID;
    }

    public void setTypeID(final Integer typeID) {
        this.typeID = typeID;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(final Integer groupID) {
        this.groupID = groupID;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(final Integer categoryID) {
        this.categoryID = categoryID;
    }

    public Long getLocationID() {
        return locationID;
    }

    public void setLocationID(final Long locationID) {
        this.locationID = locationID;
    }

    public Integer getFlagID() {
        return flagID;
    }

    public void setFlagID(final Integer flagID) {
        this.flagID = flagID;
    }

    public boolean isContainerContentOnly() {
        return (flagID == null) && (locationID == null) && (categoryID == null) && (groupID == null) && (typeID == null);
    }

    @Override
    public String toString() {
        return "Filter [itemID=" + itemID + ", typeID=" + typeID + ", groupID=" + groupID + ", categoryID=" + categoryID + ", locationID="
                + locationID + ", flagID=" + flagID + "]";
    }
}

package net.troja.eve.corpman.evedata;

public class InvType {
    private int typeId;
    private String typeName;
    private double volume;
    private int groupID;
    private String groupName;
    private int categoryID;
    private String categoryName;

    public InvType(final int typeId, final String typeName, final double volume, final int groupID, final String groupName, final int categoryID,
            final String categoryName) {
        super();
        this.typeId = typeId;
        this.typeName = typeName;
        this.volume = volume;
        this.groupID = groupID;
        this.groupName = groupName;
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(final int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(final double volume) {
        this.volume = volume;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(final int groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(final int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "InvType [typeId=" + typeId + ", typeName=" + typeName + ", volume=" + volume + ", groupID=" + groupID + ", groupName=" + groupName
                + ", categoryID=" + categoryID + ", categoryName=" + categoryName + "]";
    }
}

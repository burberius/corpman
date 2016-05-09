package net.troja.eve.corpman.evedata;

public class ReactionComponent {
    private long typeId;
    private int quantity;

    public ReactionComponent(final long typeId, final int quantity) {
        super();
        this.typeId = typeId;
        this.quantity = quantity;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(final long typeId) {
        this.typeId = typeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + quantity;
        result = (prime * result) + (int) (typeId ^ (typeId >>> 32));
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
        final ReactionComponent other = (ReactionComponent) obj;
        if (quantity != other.quantity) {
            return false;
        }
        if (typeId != other.typeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ReactionComponent [typeId=" + typeId + ", quantity=" + quantity + "]";
    }
}

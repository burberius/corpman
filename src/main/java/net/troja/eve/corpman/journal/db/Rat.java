package net.troja.eve.corpman.journal.db;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Rat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int count;
    private String name;
    private int groupID;
    private String groupName;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "journal_id")
    private WalletJournal journal;

    public Rat() {
    }

    public Rat(final int count, final String name, final int groupID, final String groupName) {
        super();
        this.count = count;
        this.name = name;
        this.groupID = groupID;
        this.groupName = groupName;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

    public WalletJournal getJournal() {
        return journal;
    }

    public void setJournal(final WalletJournal journal) {
        this.journal = journal;
    }

    @Override
    public String toString() {
        return "Rat [id=" + id + ", count=" + count + ", name=" + name + ", groupID=" + groupID + ", groupName=" + groupName + "]";
    }
}

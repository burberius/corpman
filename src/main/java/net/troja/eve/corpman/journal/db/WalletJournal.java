package net.troja.eve.corpman.journal.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class WalletJournal {
    @Id
    private long refID;
    private Date date;
    private int refTypeID;
    private String ownerName1;
    private long ownerID1;
    private String ownerName2;
    private long ownerID2;
    private String argName;
    private long argID;
    private double amount;
    private double balance;
    private String reason;
    private Long taxReceiverID;
    private Double taxAmount;
    private double taxRate;
    @OneToMany(mappedBy = "journal", fetch = FetchType.EAGER)
    private List<Rat> rats = new ArrayList<Rat>();

    public WalletJournal() {
    }

    public WalletJournal(final com.beimin.eveapi.model.shared.JournalEntry entry) {
        refID = entry.getRefID();
        date = entry.getDate();
        refTypeID = entry.getRefTypeID();
        ownerName1 = entry.getOwnerName1();
        ownerID1 = entry.getOwnerID1();
        ownerName2 = entry.getOwnerName2();
        ownerID2 = entry.getOwnerID2();
        argName = entry.getArgName1();
        argID = entry.getArgID1();
        amount = entry.getAmount();
        balance = entry.getBalance();
        reason = entry.getReason();
        taxReceiverID = entry.getTaxReceiverID();
        taxAmount = entry.getTaxAmount();
    }

    public long getRefID() {
        return refID;
    }

    public void setRefID(final long refID) {
        this.refID = refID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public int getRefTypeID() {
        return refTypeID;
    }

    public void setRefTypeID(final int refTypeID) {
        this.refTypeID = refTypeID;
    }

    public String getOwnerName1() {
        return ownerName1;
    }

    public void setOwnerName1(final String ownerName1) {
        this.ownerName1 = ownerName1;
    }

    public long getOwnerID1() {
        return ownerID1;
    }

    public void setOwnerID1(final long ownerID1) {
        this.ownerID1 = ownerID1;
    }

    public String getOwnerName2() {
        return ownerName2;
    }

    public void setOwnerName2(final String ownerName2) {
        this.ownerName2 = ownerName2;
    }

    public long getOwnerID2() {
        return ownerID2;
    }

    public void setOwnerID2(final long ownerID2) {
        this.ownerID2 = ownerID2;
    }

    public String getArgName() {
        return argName;
    }

    public void setArgName(final String argName) {
        this.argName = argName;
    }

    public long getArgID() {
        return argID;
    }

    public void setArgID(final long argID) {
        this.argID = argID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(final double amount) {
        this.amount = amount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(final double balance) {
        this.balance = balance;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    public long getTaxReceiverID() {
        return taxReceiverID;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(final double taxRate) {
        this.taxRate = taxRate;
    }

    public void addRat(final Rat rat) {
        rat.setJournal(this);
        rats.add(rat);
    }

    public void setTaxReceiverID(final Long taxReceiverID) {
        this.taxReceiverID = taxReceiverID;
    }

    public void setTaxAmount(final Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public List<Rat> getRats() {
        return rats;
    }

    public void setRats(final List<Rat> rats) {
        this.rats = rats;
    }

    @Override
    public String toString() {
        return "WalletJournal [refID=" + refID + ", date=" + date + ", refTypeID=" + refTypeID + ", ownerName1=" + ownerName1 + ", ownerID1="
                + ownerID1 + ", ownerName2=" + ownerName2 + ", ownerID2=" + ownerID2 + ", argName=" + argName + ", argID=" + argID + ", amount="
                + amount + ", balance=" + balance + ", reason=" + reason + ", taxReceiverID=" + taxReceiverID + ", taxAmount=" + taxAmount
                + ", taxRate=" + taxRate + ", rats=" + rats + "]";
    }
}

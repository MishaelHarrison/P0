package Models;

import java.util.List;

public class account {
    private int accountID;
    private String accountType;
    private double balance;
    private boolean approved;

    public account(int accountID, String accountType, double balance, boolean approved) {
        this.accountID = accountID;
        this.accountType = accountType;
        this.balance = balance;
        this.approved = approved;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}

package Models;

public class transaction {
    private int transactionID;
    private int accountID;
    private double amount;

    public transaction(int transactionID, int accountID, double amount) {
        this.transactionID = transactionID;
        this.accountID = accountID;
        this.amount = amount;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

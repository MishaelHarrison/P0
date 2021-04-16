package Models;

public class pendingTransaction {
    private int pendingTransactionID;
    private String issuingFname;
    private String issuingLname;
    private double amount;

    public pendingTransaction(int pendingTransactionID, String issuingFname, String issuingLname, double amount) {
        this.pendingTransactionID = pendingTransactionID;
        this.issuingFname = issuingFname;
        this.issuingLname = issuingLname;
        this.amount = amount;
    }

    public int getPendingTransactionID() {
        return pendingTransactionID;
    }

    public void setPendingTransactionID(int pendingTransactionID) {
        this.pendingTransactionID = pendingTransactionID;
    }

    public String getIssuingFname() {
        return issuingFname;
    }

    public void setIssuingFname(String issuingFname) {
        this.issuingFname = issuingFname;
    }

    public String getIssuingLname() {
        return issuingLname;
    }

    public void setIssuingLname(String issuingLname) {
        this.issuingLname = issuingLname;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

package Interfaces;

import Models.*;
import java.util.List;

public interface IBusinessLogic {
    user retrieveUser(String username, String password);
    account addAccount(int userID, String accountType, double startingBalance);
    double readBalance(int accountID);
    Models.transaction exchange(int accountID, double amount);
    List<user> viewCustomers();
    List<account> viewCustomerAccounts(int userID);
    void approveAccount(int accountID);
    user addUser(user user);
    pendingTransaction transfer(int issuingAccountID, int receivingAccountID, double amount);
    transaction acceptTransfer(int pendingTransactionID);
    List<transaction> viewTransactions();
}

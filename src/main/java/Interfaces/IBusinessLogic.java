package Interfaces;

import Models.*;

import java.util.ArrayList;

public interface IBusinessLogic {

    boolean adminLogin(String adminUsername, String adminPassword);

    ArrayList<transaction> getTransactionLog(String admin, String adminPassword);

    ArrayList<pendingTransaction> getPendingTransactions(user id);

    void approveTransaction(int id);

    ArrayList<user> getAllUsers(String admin, String adminPassword);

    ArrayList<account> getUserAccounts(String admin, String adminPassword, int userID);

    ArrayList<transaction> getTransactionHistory(String admin, String adminPassword, int accountID);

    void approveAccount(String admin, String adminPassword, int accountID);

    void addAccount(user loggedUser, String name, double amount);

    user login(String username, String password);

    boolean isUsernameTaken(String username);

    void addUser(user user);

    ArrayList<account> getUserAccounts(user loggedUser);

    boolean accountIdExists(int id);

    void createTransaction(user loggedUser, account account, double amount, int id);

    void cashDeposit(user loggedUser, int accountID, double amount);

    void cashWithdrawal(user loggedUser, int accountID, double amount);
}

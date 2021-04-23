package Interfaces;

import Models.*;

import java.util.ArrayList;
import java.util.List;

public interface IBusinessLogic {

    boolean adminLogin(String admin, String adminPassword);

    ArrayList<transaction> getTransactionLog(String admin, String adminPassword);

    ArrayList<pendingTransaction> getPendingTransactions(int id);

    void aproveTransaction(int id);

    ArrayList<user> getAllUsers(String admin, String adminPassword);

    ArrayList<account> getUserAccounts(String admin, String adminPassword, int userID);

    ArrayList<transaction> getTransactionHistory(int accountID);

    void approveAccount(int accountID);

    void addAccount(user loggedUser, String name, double amount);

    user login(String username, String password);

    boolean isUsernameTaken(String username);

    void addUser(user user);
}

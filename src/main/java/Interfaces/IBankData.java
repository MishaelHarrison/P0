package Interfaces;

import Models.user;
import PZero.Libs.DAO.Entities.accountEntity;
import PZero.Libs.DAO.Entities.transactionEntity;
import PZero.Libs.DAO.Entities.userEntity;

import java.util.ArrayList;

public interface IBankData {
    void createTransaction(Integer issuingID, Integer receivingID, double amount, boolean preApproved);

    accountEntity getAccount(int accountID);

    ArrayList<accountEntity> getAccountsFromUser(int id);

    void addUser(userEntity userEntity);

    boolean doesUsernameExist(String username);

    userEntity login(String username, String password);

    void addAccount(int userID, String name, double amount);

    void approveAccount(int accountID);

    ArrayList<transactionEntity> getTransactionsFromAccount(int accountID);

    ArrayList<userEntity> getAllUsers();

    void approveTransaction(int id);

    ArrayList<transactionEntity> transactionsFromUser(int id, boolean isApproved);

    ArrayList<transactionEntity> fullTransactionLog();
}
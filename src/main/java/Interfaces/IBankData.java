package Interfaces;

import Exceptions.BusinessException;
import Exceptions.InsufficientFunds;
import Models.user;
import PZero.Libs.DAO.Entities.accountEntity;
import PZero.Libs.DAO.Entities.transactionEntity;
import PZero.Libs.DAO.Entities.userEntity;

import java.util.ArrayList;

public interface IBankData {
    void createTransaction(Integer issuingID, Integer receivingID, double amount, boolean preApproved) throws BusinessException, InsufficientFunds;

    accountEntity getAccount(int accountID) throws BusinessException;

    ArrayList<accountEntity> getAccountsFromUser(int id) throws BusinessException;

    void addUser(userEntity userEntity) throws BusinessException;

    boolean doesUsernameExist(String username) throws BusinessException;

    userEntity login(String username, String password) throws BusinessException;

    void addAccount(int userID, String name, double amount) throws BusinessException;

    void approveAccount(int accountID) throws BusinessException;

    ArrayList<transactionEntity> getTransactionsFromAccount(int accountID) throws BusinessException;

    ArrayList<userEntity> getAllUsers() throws BusinessException;

    void approveTransaction(int id) throws BusinessException, InsufficientFunds;

    ArrayList<transactionEntity> transactionsFromUser(int id, boolean isApproved) throws BusinessException;

    ArrayList<transactionEntity> fullTransactionLog() throws BusinessException;
}
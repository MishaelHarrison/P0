package PZero.Libs;

import Interfaces.IBankData;
import Interfaces.IBusinessLogic;
import Models.*;

import java.sql.*;
import java.util.List;

public class BusinessLogic implements IBusinessLogic {
    private final IBankData Data;

    public BusinessLogic(IBankData Data){
        this.Data = Data;
    }

    private user populateUser(ResultSet result) throws SQLException {
        return new user(
                result.getInt("id"),
                result.getString("fname"),
                result.getString("lname"),
                result.getString("username"),
                result.getString("password")
        );
    }

    @Override
    public user retrieveUser(String username, String password) {
//        ResultSet result = Data.query("id, username, password, fname, lname", "user",
//                "where username = "+username+" and password = "+password);
//        user ret = null;
//        try{
//            ret = populateUser(result);
//        } catch (SQLException throwable) {
//            throwable.printStackTrace();
//        }
//        return ret;
        return null;
    }

    @Override
    public account addAccount(int userID, String accountType, double startingBalance) {
        return null;
    }

    @Override
    public double readBalance(int accountID) {
        return 0;
    }

    @Override
    public transaction exchange(int accountID, double amount) {
        return null;
    }

    @Override
    public List<user> viewCustomers() {
        return null;
    }

    @Override
    public List<account> viewCustomerAccounts(int userID) {
        return null;
    }

    @Override
    public void approveAccount(int accountID) {

    }

    @Override
    public user addUser(user user) {
        return null;
    }

    @Override
    public pendingTransaction transfer(int issuingAccountID, int receivingAccountID, double amount) {
        return null;
    }

    @Override
    public transaction acceptTransfer(int pendingTransactionID) {
        return null;
    }

    @Override
    public List<transaction> viewTransactions() {
        return null;
    }
}

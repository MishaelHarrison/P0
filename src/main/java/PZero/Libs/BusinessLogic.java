package PZero.Libs;

import Interfaces.IBankData;
import Interfaces.IBusinessLogic;
import Models.account;
import Models.pendingTransaction;
import Models.transaction;
import Models.user;
import PZero.Libs.DAO.Entities.accountEntity;
import PZero.Libs.DAO.Entities.transactionEntity;
import PZero.Libs.DAO.Entities.userEntity;

import java.util.ArrayList;

public class BusinessLogic implements IBusinessLogic {

    private final String adminUsername = "admin";
    private final String adminPassword = "apache";
    private IBankData data;

    public BusinessLogic(IBankData data) {
        this.data = data;
    }

    @Override
    public boolean adminLogin(String adminUsername, String adminPassword) {
        return adminUsername.equals(this.adminUsername) && adminPassword.equals(this.adminPassword);
    }

    @Override
    public ArrayList<transaction> getTransactionLog(String admin, String adminPassword) {
        ArrayList<transaction> ret = new ArrayList<transaction>();
        if (adminLogin(admin, adminPassword)){
            ArrayList<transactionEntity> query = data.fullTransactionLog();
            for (transactionEntity i : query) {
                ret.add(new transaction(
                        i.getID(), i.getReceivingID(), i.getIssuingID(), i.getAmount(), i.getTimestamp(),
                        i.getReceivingAccount().getUser().getUsername(),
                        i.getIssuingAccount().getUser().getUsername()
                ));
            }
        }else {
            //todo: improper credentials error
        }
        return ret;
    }

    @Override
    public ArrayList<pendingTransaction> getPendingTransactions(user user) {
        ArrayList<pendingTransaction> ret = new ArrayList<>();
        if (login(user.getUsername(), user.getPassword()) != null){
            ArrayList<transactionEntity> query = data.transactionsFromUser(user.getId(), false);
            for (transactionEntity i : query) {
                ret.add(new pendingTransaction(
                        i.getID(), i.getIssuingAccount().getUser().getFname(),i.getIssuingAccount().getUser().getLname(),i.getAmount()
                ));
            }
        }else {
            //todo: improper credentials error
        }
        return ret;
    }

    @Override
    public void approveTransaction(int id) {
        data.approveTransaction(id);
    }

    @Override
    public ArrayList<user> getAllUsers(String admin, String adminPassword) {
        ArrayList<user> ret = new ArrayList<user>();
        if (adminLogin(admin, adminPassword)){
            ArrayList<userEntity> query = data.getAllUsers();
            for (userEntity i :query) {
                ret.add(new user(
                        i.getID(), i.getFname(), i.getLname(), i.getUsername(),""
                ));
            }
        }else {
            //todo improper credentials error
        }
        return ret;
    }

    @Override
    public ArrayList<account> getUserAccounts(String admin, String adminPassword, int userID) {
        ArrayList<account> ret = new ArrayList<account>();
        if (adminLogin(admin, adminPassword)){
            ArrayList<accountEntity> query = data.getAccountsFromUser(userID);
            for (accountEntity i :query) {
                ret.add(new account(
                        i.getID(), i.getName(), i.getBalance(), i.isApproved(), i.getUser().getFname(), i.getUser().getLname()
                ));
            }
        }else{
            //todo improper credentials error
        }
        return ret;
    }

    @Override
    public ArrayList<transaction> getTransactionHistory(String admin, String adminPassword, int accountID) {
        ArrayList<transaction> ret = new ArrayList<transaction>();
        if (adminLogin(admin, adminPassword)){
            ArrayList<transactionEntity> query = data.getTransactionsFromAccount(accountID);
            for (transactionEntity i :query) {
                ret.add(new transaction(
                        i.getID(), i.getReceivingID(), i.getIssuingID(), i.getAmount(), i.getTimestamp(),
                        i.getReceivingAccount().getUser().getUsername(), i.getIssuingAccount().getUser().getUsername()
                ));
            }
        }else{
            //todo improper credentials error
        }
        return ret;
    }

    @Override
    public void approveAccount(String admin, String adminPassword, int accountID) {
        if (adminLogin(admin, adminPassword)) data.approveAccount(accountID);
        else {/*todo invalid credentials error*/}
    }

    @Override
    public void addAccount(user loggedUser, String name, double amount) {
        if (login(loggedUser.getUsername(), loggedUser.getPassword()) != null){
            data.addAccount(loggedUser.getId(), name, amount);
        }else {
            //todo invalid credentials error
        }
    }

    @Override
    public user login(String username, String password) {
        userEntity user = data.login(username, password);
        if(user == null) return null;
        else return new user(
                user.getID(), user.getFname(), user.getLname(), user.getUsername(), user.getPassword()
        );
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return data.doesUsernameExist(username);
    }

    @Override
    public void addUser(user user) {
        data.addUser(new userEntity(
                user.getId(), user.getUsername(), user.getPassword(), user.getFname(), user.getLname()
        ));
    }

    @Override
    public ArrayList<account> getUserAccounts(user loggedUser) {
        ArrayList<account> ret = new ArrayList<account>();
        if (login(loggedUser.getUsername(), loggedUser.getPassword())!= null){
            ArrayList<accountEntity> query = data.getAccountsFromUser(loggedUser.getId());
            for (accountEntity i :query) {
                ret.add(new account(
                        i.getID(), i.getName(), i.getBalance(), i.isApproved(), i.getUser().getFname(), i.getUser().getLname()
                ));
            }
        }else {
            //todo improper credentials error
        }
        return ret;
    }

    @Override
    public boolean accountIdExists(int id) {
        return data.getAccount(id) != null;
    }

    @Override
    public void createTransaction(user loggedUser, account account, double amount, int id) {
        if(login(loggedUser.getUsername(), loggedUser.getPassword()).getId() == data.getAccount(account.getAccountID()).getUserID())
            data.createTransaction(account.getAccountID(), id, amount, false);
        else {/*todo improper credentials error*/}
    }

    @Override
    public void cashDeposit(user loggedUser, int accountID, double amount) {
        if(login(loggedUser.getUsername(), loggedUser.getPassword()).getId() == data.getAccount(accountID).getUserID())
            data.createTransaction(null, accountID, amount, true);
        else {/*todo improper credentials error*/}
    }

    @Override
    public void cashWithdrawal(user loggedUser, int accountID, double amount) {
        if(login(loggedUser.getUsername(), loggedUser.getPassword()).getId() == data.getAccount(accountID).getUserID())
            data.createTransaction(accountID, null, amount, true);
        else {/*todo improper credentials error*/}
    }
}

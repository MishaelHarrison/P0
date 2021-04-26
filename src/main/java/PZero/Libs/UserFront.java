package PZero.Libs;

import Interfaces.IBusinessLogic;
import Interfaces.IUserFront;
import Models.*;

import java.io.InputStream;
import java.util.*;

public class UserFront implements IUserFront {

    private user loggedUser;
    private String userState = "none";
    private String adminPassword = "";
    private IBusinessLogic logic;
    private Scanner input;

    public UserFront(InputStream input, IBusinessLogic logic){
        this.input = new Scanner(input);
        this.logic = logic;
    }

    private void print(String out){
        System.out.println(out);
    }

    private String askInput(ArrayList<String> validInput){
        boolean valid = false;
        String ret = null;
        while (!valid) {
            ret = input.nextLine();
            for (String s: validInput) {
                valid = valid || ret.equals(s) || ret.equals("");
            }
            if(!valid) print("Invalid input");
        }
        return ret;
    }

    private String askInput(String regex){
        String ret;
        do {
            ret = input.nextLine();
            if (!ret.matches(regex))print("Invalid input");
        }
        while (!ret.matches(regex));
        return ret;
    }

    private String askInput(){
        String in = "";
        do {
            in = input.nextLine();
            if (in.equals(""))print("Input expected");
        }
        while (in.equals(""));
        return in;
    }

    @Override
    public void menu() {
        //initialise variables
        String selection = "no selection";
        String[] menuItems = {
                "A) Login",//0
                "B) Create new user",//1
                "C) Apply for a new account",//2
                "D) View all accounts",//3
                "E) View incoming transactions",//4
                "F) Cash deposit/withdrawal",//5
                "G) Logout",//6
                "H) View all users",//7
                "I) View all completed transactions"//8
        };
        boolean[] activeItems = new boolean[menuItems.length];

        while (!selection.equals("")){
            //reset active items
            Arrays.fill(activeItems, false);

            //set active items based on user state
            switch (userState) {
                case "none": for (int i : new int[]{0,1}) activeItems[i] = true;
                    break;
                case "customer": for (int i : new int[]{2,3,4,5,6}) activeItems[i] = true;
                    break;
                case "admin": for (int i : new int[]{6,7,8}) activeItems[i] = true;
                    break;
            }

            //collect options
            ArrayList<String> options = new ArrayList<String>();
            options.add("admin");//admin option (intentionally hidden)
            for (int i = 0; i < activeItems.length; i++) {
                if (activeItems[i]){
                    options.add(String.valueOf((char) (i+(int)'a')));
                    options.add(String.valueOf((char) (i+(int)'A')));
                }
            }

            //communicate to user
            print("Clearing console");
            for (int i = 0; i < 20; i++) print("");
            if (loggedUser == null)print("Hello Welcome to JPMishael. \nEnter a selection or send nothing to exit any menu\n");
            else print("Hello "+ loggedUser.getFname() +' '+ loggedUser.getLname() +" Welcome to JPMishael. \nEnter a selection or send nothing to exit any menu\n");
            for (int i = 0; i < menuItems.length; i++) {
                if (activeItems[i]){
                    print(menuItems[i]);
                }
            }

            //take input
            selection = askInput(options);

            //mega switch statement
            switch (selection){
                case "a": case "A":
                    //login
                    userState = login() ? "customer" : userState ;
                    break;
                case "b": case "B":
                    //new user
                    newUser();
                    break;
                case "c": case "C":
                    //new account
                    newAccount();
                    break;
                case "d": case "D":
                    //view all accounts
                    viewAllAccounts();
                    break;
                case "e": case "E":
                    //view incoming transactions
                    viewIncomingTransactions();
                    break;
                case "f": case "F":
                    //cash transaction
                    cashTransaction();
                    break;
                case "g": case "G":
                    //logout
                    userState = "none";
                    adminPassword = "";
                    loggedUser = null;
                    break;
                case "h": case "H":
                    //view all users
                    viewAllUsers();
                    break;
                case "i": case "I":
                    //view all completed transactions
                    viewCompletedTransactions();
                    break;
                case "admin":
                    //admin login
                    userState = adminLogin() ? "admin" : "none" ;
                    break;
                case "":
                    //quit
                    break;
                default:
                    //if i do a whoops
                    print("coder did a whoops");
                    break;
            }
        }
    }

    private void cashTransaction() {
        ArrayList<account> accounts = logic.getUserAccounts(loggedUser);
        ArrayList<String> options = new ArrayList<String>();

        for (account i: accounts) {
            if(i.isApproved()) print(
                    "ID: "+i.getAccountID()
                            +" Account type: "+i.getAccountType()
                            +" Balance: $"+i.getBalance());
            options.add(i.getAccountID()+"");
        }

        print("\nEnter ID of an account to create cash transaction");
        String selection = askInput(options);
        int accountID;
        if (!selection.equals("")){
            accountID = Integer.parseInt(selection);
        }else return;

        print("\nA) Deposit\n)B Withdrawal");
        String choice = askInput(new ArrayList<String>(Arrays.asList(new String[]{"a", "A", "b", "B"})));
        double amount = 0d;
        if (!choice.equals("")){
            print("Enter amount");
            amount = Double.parseDouble(askInput("^(?:0|[1-9]\\d{0,2}(?:,?\\d{3})*)(?:\\.\\d+)?$"));
        }
        if (choice.contains("aA")){
            logic.cashDeposit(loggedUser, accountID, amount);
        }else if (choice.contains("bB")){
            logic.cashWithdrawal(loggedUser, accountID, amount);
        }
    }

    private void viewAllAccounts() {
        ArrayList<account> accounts = logic.getUserAccounts(loggedUser);
        ArrayList<String> options = new ArrayList<String>();

        for (account i: accounts) {
            if(i.isApproved()) print(
                    "ID: "+i.getAccountID()
                    +" Account type: "+i.getAccountType()
                    +" Balance: $"+i.getBalance());
            options.add(i.getAccountID()+"");
        }

        print("\nEnter ID of an account to create a transaction");
        String selection = askInput(options);
        if (!selection.equals("")){
            int id = Integer.parseInt(selection);
            account account = null;
            for (account i : accounts) {
                if (i.getAccountID() == id){
                    account = i;
                    break;
                }
            }
            createTransaction(account);
        }
    }

    private void createTransaction(account account) {
        print("Enter transaction amount");
        double amount = Double.parseDouble(askInput("^(?:0|[1-9]\\d{0,2}(?:,?\\d{3})*)(?:\\.\\d+)?$"));
        print("Enter receiving account id:");
        int id = 0;
        boolean valid = false;
        while (!valid){
            id = Integer.parseInt(askInput("[1-9][0-9]*"));
            valid = true;
            if (!logic.accountIdExists(id)){
                valid = false;
                print("Account id does not exist");
            }
        }
        logic.createTransaction(loggedUser, account, amount, id);
    }

    private boolean adminLogin() {
        adminPassword = askInput();
        return logic.adminLogin("admin", adminPassword);
    }

    private void viewCompletedTransactions() {
        ArrayList<transaction> transactions = logic.getTransactionLog("admin", adminPassword);
        for (int i = 0; i < transactions.size(); i++) {
            transaction k = transactions.get(i);
            print(k.getTimestamp()+" $"+k.getAmount()+" issuer: "+k.getIssuingUsername()+" receiver: "+k.getReceivingUsername());
        }
        input.nextLine();
    }

    private void viewIncomingTransactions() {
        ArrayList<pendingTransaction> transactions = logic.getPendingTransactions(loggedUser);
        ArrayList<String> options = new ArrayList<String>();
        for (pendingTransaction i: transactions) {
            options.add(i.getPendingTransactionID()+"");
            print("ID: "+i.getPendingTransactionID()+" Amount: "+i.getAmount()+
                    "\nIssuer's name: "+i.getIssuingFname()+" "+i.getIssuingLname());
        }
        print("\nEnter a transaction ID to approve:");
        String choice = askInput(options);
        if (!choice.equals("")){
            int id = Integer.parseInt(choice);
            logic.approveTransaction(id);
        }
    }

    private void viewAllUsers() {
        ArrayList<user> users = logic.getAllUsers("admin", adminPassword);
        ArrayList<String> options = new ArrayList<String>();

        for (user i : users) {
            options.add(i.getId()+"");
            print("ID: "+i.getId()+" Username: "+i.getUsername()+" Name: "+i.getFname()+" "+i.getLname());
        }

        print("\nEnter a user ID to view affiliated accounts:");
        String choice = askInput(options);
        if (!choice.equals("")){
            int id = Integer.parseInt(choice);
            viewUserAccounts(id);
        }
    }

    private void viewUserAccounts(int userID) {
        ArrayList<account> accounts = logic.getUserAccounts("admin", adminPassword, userID);
        ArrayList<String> options = new ArrayList<String>();

        for (account i: accounts) {
            if(i.isApproved()) print(
                    "ID: "+i.getAccountID()
                    +" User's name: "+i.getUserFname()+" "+i.getUserLname()
                    +"\nAccount type: "+i.getAccountType()
                    +" Balance: $"+i.getBalance());
            options.add(i.getAccountID()+"");
        }
        print("\nAccounts pending activation:");
        for (account i: accounts) {
            if(!i.isApproved()) print(
                    "ID: "+i.getAccountID()
                    +" User's name: "+i.getUserFname()+" "+i.getUserLname()
                    +"\nAccount type: "+i.getAccountType()
                    +" Balance: $"+i.getBalance());
        }

        print("\nEnter ID of an account to view transaction history or approve account");
        String selection = askInput(options);
        if (!selection.equals("")){
            int id = Integer.parseInt(selection);
            account account = null;
            for (account i : accounts) {
                if (i.getAccountID() == id){
                    account = i;
                    break;
                }
            }
            actOnAccount(account);
        }
    }

    private void actOnAccount(account account) {
        if(account.isApproved()){
            ArrayList<transaction> transactions = logic.getTransactionHistory("admin", adminPassword, account.getAccountID());
            Collections.sort(transactions);
            for (int i = 0; i < transactions.size(); i++) {
                transaction k = transactions.get(i);
                boolean received = k.getReceivingAccountID()==account.getAccountID()||k.getIssuingAccountID()==null;
                boolean cash = k.getIssuingAccountID()==null||k.getReceivingAccountID()==null;
                print(k.getTimestamp()+
                        (cash?received?" Deposit ":" Withdrawal ":received?" Received ":" Issued ")+
                        "Amount: $"+k.getAmount());
            }
        }else {
            logic.approveAccount("admin", adminPassword, account.getAccountID());
        }
    }

    private void newAccount() {
        print("enter account name:");
        String name = askInput();
        print("enter starting amount:");
        double amount = Double.parseDouble(askInput("^(?:0|[1-9]\\d{0,2}(?:,?\\d{3})*)(?:\\.\\d+)?$"));
        logic.addAccount(loggedUser, name, amount);
    }

    private boolean login() {
        print("enter username:");
        String username = askInput();
        print("enter password:");
        String password = askInput();
        loggedUser = logic.login(username, password);
        return loggedUser != null;
    }

    private void newUser(){
        String username;
        boolean first = true;
        do {
            if (!first) print("username taken");
            else first = false;
            print("enter username:");
            username = askInput();
        }
        while (logic.isUsernameTaken(username));
        print("enter password:");
        String password = askInput();
        print("enter first name:");
        String fname = askInput();
        print("enter last name:");
        String lname = askInput();
        logic.addUser(new user(
                0,
                fname,
                lname,
                username,
                password
        ));
    }
}
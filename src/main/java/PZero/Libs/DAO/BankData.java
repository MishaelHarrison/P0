package PZero.Libs.DAO;

import Interfaces.IBankData;
import PZero.Libs.DAO.Entities.accountEntity;
import PZero.Libs.DAO.Entities.transactionEntity;
import PZero.Libs.DAO.Entities.userEntity;

import java.sql.*;
import java.util.ArrayList;

public class BankData implements IBankData {

    private final String schema;

    public BankData(String schema){
        this.schema = schema;
    }

    private String transactionQuery(){
        return "select m.id as id, m.amount as amount , m.\"timestamp\" as \"timestamp\",\n" +
                "m.approved as approved, m.issuingid as issuingid , m.recievingid as recievingid,\n" +
                "m.accounttype as iaccounttype, m.balance as ibalance, m.userid as iuserid,\n" +
                "m.username as iusername, m.fname as ifname, m.lname as ilname,\n" +
                "r.accounttype as raccounttype, r.balance as rbalance,r.userid as ruserid,\n" +
                "r.username as rusername, r.fname as rfname, r.lname as rlname\n" +
                "from\n" +
                "(select t.id, t.amount, t.\"timestamp\", t.approved, t.issuingid, t.recievingid,\n" +
                "i.accounttype, i.balance, i.userid, i.username, i.fname, i.lname\n" +
                "from "+schema+".transactions t left join\n" +
                "(select a.id, a.accounttype, a.balance, a.userid, u.username, u.fname, u.lname\n" +
                "from "+schema+".accounts a inner join "+schema+".users u on a.userid = u.id) as i\n" +
                "on t.issuingid = i.id) as m\n" +
                "left join\n" +
                "(select a.id, a.accounttype, a.balance, a.userid, u.username, u.fname, u.lname\n" +
                "from "+schema+".accounts a inner join "+schema+".users u on a.userid = u.id) as r\n" +
                "on m.recievingid = r.id ";
    }

    private ArrayList<transactionEntity> transactionsFromRS(ResultSet rs) throws SQLException {
        ArrayList<transactionEntity> ret = new ArrayList<>();
        while (rs.next()) {
            int issuingID = rs.getInt("issuingid");
            int recievingID = rs.getInt("recievingid");
            transactionEntity i = new transactionEntity(
                    rs.getInt("id"),
                    issuingID == 0 ? null : issuingID,
                    recievingID == 0 ? null : recievingID,
                    rs.getDouble("amount"),
                    rs.getBoolean("approved"),
                    rs.getTimestamp("timestamp")
            );
            if (issuingID != 0) {
                i.setIssuingAccount(new accountEntity(
                        issuingID,
                        rs.getInt("iuserid"),
                        rs.getDouble("ibalance"),
                        true,
                        rs.getString("iaccounttype")
                ));
                i.getIssuingAccount().setUser(new userEntity(
                        rs.getInt("iuserid"),
                        rs.getString("iusername"),
                        "password left intentionally hidden",
                        rs.getString("ifname"),
                        rs.getString("ilname")
                ));
            }
            if (recievingID != 0) {
                i.setReceivingAccount(new accountEntity(
                        issuingID,
                        rs.getInt("ruserid"),
                        rs.getDouble("rbalance"),
                        true,
                        rs.getString("raccounttype")
                ));
                i.getReceivingAccount().setUser(new userEntity(
                        rs.getInt("ruserid"),
                        rs.getString("rusername"),
                        "password left intentionally hidden",
                        rs.getString("rfname"),
                        rs.getString("rlname")
                ));
            }
            ret.add(i);
        }
        return ret;
    }

    @Override
    public void createTransaction(Integer issuingID, Integer receivingID, double amount, boolean preApproved) {
        Connection connection = postgresConnector.getConnection();
        String sql="INSERT INTO "+schema+".transactions\n" +
                "(issuingid, recievingid, amount, approved)" +
                "VALUES(?, ?, ?, ?);\n";
        int key = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setObject(1,issuingID, Types.INTEGER);
            preparedStatement.setObject(2,receivingID, Types.INTEGER);
            preparedStatement.setDouble(3, amount);
            preparedStatement.setBoolean(4, preApproved);
            preparedStatement.executeUpdate();

            key = preparedStatement.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
        if (preApproved) approveTransaction(key);
    }

    @Override
    public accountEntity getAccount(int accountID) {
        ArrayList<accountEntity> ret = new ArrayList<>();
        Connection connection = postgresConnector.getConnection();
        String sql="select a.id, a.balance, a.accountType, a.approved, b.id as userid, b.fname, b.lname, b.username from " +
                schema+".accounts as a inner join "+schema+".users as b on b.id = a.userid where a.id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, accountID);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()){
                accountEntity i = new accountEntity(
                        rs.getInt("id"),
                        rs.getInt("userid"),
                        rs.getDouble("balance"),
                        rs.getBoolean("approved"),
                        rs.getString("accountType")
                );
                i.setUser(new userEntity(
                        rs.getInt("id"),
                        rs.getString("username"),
                        "password left intentionally hidden",
                        rs.getString("fname"),
                        rs.getString("lname")
                ));
                ret.add(i);
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
        try{
            return ret.get(0);
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    @Override
    public ArrayList<accountEntity> getAccountsFromUser(int id) {
        ArrayList<accountEntity> ret = new ArrayList<>();
        Connection connection = postgresConnector.getConnection();
        String sql="select a.id, a.balance, a.accountType, a.approved, b.id as userid, b.fname, b.lname, b.username from " +
                schema+".accounts as a inner join "+schema+".users as b on a.userid = b.id where b.id = ?; ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()){
                accountEntity i = new accountEntity(
                        rs.getInt("id"),
                        rs.getInt("userid"),
                        rs.getDouble("balance"),
                        rs.getBoolean("approved"),
                        rs.getString("accountType")
                );
                i.setUser(new userEntity(
                        rs.getInt("id"),
                        rs.getString("username"),
                        "password left intentionally hidden",
                        rs.getString("fname"),
                        rs.getString("lname")
                ));
                ret.add(i);
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
        return ret;
    }

    @Override
    public void addUser(userEntity userEntity) {
        Connection connection = postgresConnector.getConnection();
        String sql="INSERT INTO "+schema+".users\n" +
                "(username, password, fname, lname)\n" +
                "VALUES(?, ?, ?, ?);\n";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, userEntity.getUsername());
            preparedStatement.setString(2, userEntity.getPassword());
            preparedStatement.setString(3, userEntity.getFname());
            preparedStatement.setString(4, userEntity.getLname());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
    }

    @Override
    public boolean doesUsernameExist(String username) {
        ArrayList<userEntity> ret = new ArrayList<>();
        Connection connection = postgresConnector.getConnection();
        String sql="SELECT id, username, \"password\", fname, lname\n" +
                "FROM "+schema+".users where username = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()){
                ret.add(new userEntity(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("fname"),
                        rs.getString("lname")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
        try{
            ret.get(0);
            return true;
        }catch (IndexOutOfBoundsException e){
            return false;
        }
    }

    @Override
    public userEntity login(String username, String password) {
        ArrayList<userEntity> ret = new ArrayList<>();
        Connection connection = postgresConnector.getConnection();
        String sql="SELECT id, username, \"password\", fname, lname\n" +
                "FROM "+schema+".users where username = ? and \"password\" = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()){
                ret.add(new userEntity(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("fname"),
                        rs.getString("lname")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
        try{
            return ret.get(0);
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    @Override
    public void addAccount(int userID, String name, double amount) {
        Connection connection = postgresConnector.getConnection();
        String sql="INSERT INTO "+schema+".accounts\n" +
                "(userID, accountType, balance)\n" +
                "VALUES(?, ?, ?);\n";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, name);
            preparedStatement.setDouble(3, amount);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
    }

    @Override
    public void approveAccount(int accountID) {
        Connection connection = postgresConnector.getConnection();
        String sql="update "+schema+".accounts set approved = true where id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,accountID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
    }

    @Override
    public ArrayList<transactionEntity> getTransactionsFromAccount(int accountID) {
        ArrayList<transactionEntity> ret = new ArrayList<>();
        Connection connection = postgresConnector.getConnection();
        String sql=transactionQuery() + "where recievingid = ? or issuingid = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, accountID);
            preparedStatement.setInt(2, accountID);
            ResultSet rs = preparedStatement.executeQuery();

            ret = transactionsFromRS(rs);
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
        return ret;
    }

    @Override
    public ArrayList<userEntity> getAllUsers() {
        ArrayList<userEntity> ret = new ArrayList<>();
        Connection connection = postgresConnector.getConnection();
        String sql="SELECT id, username, \"password\", fname, lname\n" +
                "FROM "+schema+".users;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()){
                ret.add(new userEntity(
                        rs.getInt("id"),
                        rs.getString("username"),
                        "password left intentionally hidden",
                        rs.getString("fname"),
                        rs.getString("lname")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
        return ret;
    }

    @Override
    public void approveTransaction(int id) {
        Connection connection = postgresConnector.getConnection();
        Transact(id);
        String sql="update "+schema+".transactions set approved = true where id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
    }

    private void Transact(int id) {
        Connection connection = postgresConnector.getConnection();
        String sql= transactionQuery()+"where m.id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            subtractFunds(rs.getInt(""), rs.getDouble());
            addFunds();
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
    }

    @Override
    public ArrayList<transactionEntity> transactionsFromUser(int id, boolean isApproved) {
        ArrayList<transactionEntity> ret = new ArrayList<>();
        Connection connection = postgresConnector.getConnection();
        String sql=transactionQuery() + "where (m.userid = ? or r.userid = ?) and approved = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, id);
            preparedStatement.setBoolean(3, isApproved);
            ResultSet rs = preparedStatement.executeQuery();

            ret = transactionsFromRS(rs);
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
        return ret;
    }

    @Override
    public ArrayList<transactionEntity> fullTransactionLog() {
        ArrayList<transactionEntity> ret = new ArrayList<>();
        Connection connection = postgresConnector.getConnection();
        String sql= transactionQuery()+';';
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet rs = preparedStatement.executeQuery();

            ret = transactionsFromRS(rs);
        } catch (SQLException e) {
            e.printStackTrace(System.out);//todo business exception
        }
        return ret;
    }
}

package PZero.Libs;

import Interfaces.IBankData;

import java.sql.*;

interface connectionAccess{
    void connect(Connection connection) throws SQLException;
}

public class BankData implements IBankData {
    private String schema;

    public BankData(String schema){
        this.schema = schema;
    }

    private void connect(connectionAccess pass){
        Connection connection=null;
        try {
            //Step 1 - Load Driver
            Class.forName("org.postgresql.Driver");
            //System.out.println("Driver Loaded");

            //Step2 - Open Connection
            String url="jdbc:postgresql://localhost:5432/postgres";
            String username="postgres";
            String password="toast";
            connection= DriverManager.getConnection(url,username,password);
            //System.out.println("Connection/Ping Success");

            pass.connect(connection);

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }finally {
            try {
                //Step 6 - Close Connection
                connection.close();
                //System.out.println("Connection closed");
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public int create(String table, String columns, String values) {
        String sql = "insert into "+schema+'.'+table+" ("+columns+") values ("+values+")";

        connect(connection -> {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        });

        return -1;
    }

    @Override
    public String read(String select, String table, String modifiers) {
        StringBuilder output = new StringBuilder();

        connect(connection -> {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select "+select+" from "+schema+'.'+table+" "+modifiers);
            ResultSetMetaData rsmd = result.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (result.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) output.append(",");
                    String columnValue = result.getString(i);
                    output.append(columnValue);
                }
                output.append('\n');
            }
        });

        return output.toString();
    }

    @Override
    public String read(String select, String table) {
        return this.read(select, table, "");
    }

    @Override
    public void update(String table, String columns, String values, String where) {
        StringBuilder updates = new StringBuilder();
        String[] l = columns.split(",");
        String[] r = values.split(",");

        for (int i = 0; i < l.length; i++) {
            updates.append(l[i]+" = "+r[i]+(i==0?"":','));
        }

        String sql = "update "+schema+'.'+table+" set "+updates.toString()+" where "+where;
        System.out.println(sql);

        connect(connection -> {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        });
    }
}

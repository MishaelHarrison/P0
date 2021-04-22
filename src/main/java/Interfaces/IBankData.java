package Interfaces;

import java.sql.ResultSet;

public interface IBankData {
    int create(String table, String columns, String values);

    String read(String select, String table, String modifiers);
    String read(String select, String table);

    void update(String table, String columns, String values, String where);
}
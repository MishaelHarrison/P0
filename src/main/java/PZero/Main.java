package PZero;

import PZero.Libs.BankData;

public class Main {
    public static void main(String[] args) {
        new BankData("test").update("hello", "name", "'timmy'", "age = 100");
        System.out.println(new BankData("test").read("age, city, name", "hello", "where age = 100"));
    }
}

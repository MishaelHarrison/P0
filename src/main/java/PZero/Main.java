package PZero;

import PZero.Libs.BankData;
import PZero.Libs.BusinessLogic;
import PZero.Libs.UserFront;

public class Main {
    public static void main(String[] args) {
        new UserFront(System.in, new BusinessLogic()).menu();
    }
}

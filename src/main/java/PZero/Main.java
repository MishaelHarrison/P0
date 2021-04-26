package PZero;

import PZero.Libs.BusinessLogic;
import PZero.Libs.DAO.BankData;
import PZero.Libs.UserFront;

public class Main {
    public static void main(String[] args) {
        new UserFront(System.in, new BusinessLogic(new BankData("p0"))).menu();
    }
}

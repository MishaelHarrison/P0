package Exceptions;

public class InsufficientFunds extends Exception{
    public InsufficientFunds (){
        super("Error: insufficient funds");
    }
}

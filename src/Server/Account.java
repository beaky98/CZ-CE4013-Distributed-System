package src.Server;

public class Account {
    private int accountNumber;
    private String name;
    private int pw;
    private double balance;

    public Account(){};
    public Account(int accountNumber, String name, int pw, double balance){ 
        this.accountNumber = accountNumber;
        this.name = name;
        this.pw = pw;
        this.balance = balance;
    }


    public int getAccountNumber(){
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber){
        this.accountNumber = accountNumber;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getPin(){
        return pw;
    }

    public void setPin(int pin){
        this.pw = pin;
    }

    public double getBalance(){
        return balance;
    }

    public void setBalance(double balance){
        this.balance = balance;
    }

    public void modifyBalance(double amount){
        this.balance += amount;
    }
}

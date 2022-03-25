package src.Server;

public class Account {
    private int accountNumber;
    private String name;
    private String pw;
    private double balance;
    private String currency;

    public Account(){};
    public Account(int accountNumber, String name, String pw, double balance, String currency){ 
        this.accountNumber = accountNumber;
        this.name = name;
        this.pw = pw;
        this.balance = balance;
        this.currency = currency;
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

    public String getPassword(){
        return pw;
    }

    public void setPassword(String pw){
        this.pw = pw;
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

    public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}

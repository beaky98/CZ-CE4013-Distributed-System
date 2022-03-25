package src.Server;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;


public class Services {
    public static HashMap<Integer, Account> accountdb;
    public Services(){
        accountdb = new HashMap<>();
    }



    public static int createAccount(String name, int pin, double balance){
        int accNum = -1;
		int min = 1000;
		int max = 9999;
		do{
			accNum = ThreadLocalRandom.current().nextInt(min, max);
		}while(accountdb.get(accNum)!=null);

        Account newAcc = new Account();

        newAcc.setAccountNumber(accNum);
        newAcc.setName(name);
        newAcc.setPin(pin);
        newAcc.setBalance(balance);
        accountdb.put(accNum,newAcc);

        System.out.println("Account created, your account number is: " + accNum);
        return accNum;
    }

    public static double updateBalance(String name, int accNum, int pin, int choice, double amount){
        //Check if account number exists in database
        if(accountdb.get(accNum) == null){
            return -1;
        }
        //Check if pin is correct
        if(accountdb.get(accNum).getPin() != pin){
            return -2;
        }

        if(choice == 0){
            System.out.println("Attempting Deposit");


            Account temp = accountdb.get(accNum);
			temp.setBalance(temp.getBalance() + amount);
        }
        
        else if(choice == 1){
            System.out.println("Attempting withdrawal");

            Account temp = accountdb.get(accNum);

            if(temp.getBalance() >= amount){
				temp.setBalance(temp.getBalance() - amount);
				System.out.println("Withdrawal successful\nAccount's balance is " + temp.getBalance());
			}		
			else {
				System.out.println("Insuffient Balance. Current balance: " + temp.getBalance());
				return -3;
			}
        }        
        else return -4;

        return accountdb.get(accNum).getBalance();
        
    }
}

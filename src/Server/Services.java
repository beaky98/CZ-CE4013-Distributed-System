package src.Server;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;


public class Services {
    public static HashMap<Integer, Account> accountdb;
    public Services(){
        accountdb = new HashMap<>();
    }



    public static int createAccount(String name, String pw, double balance){
        int accNum = -1;
		int min = 1000;
		int max = 9999;
		do{
			accNum = ThreadLocalRandom.current().nextInt(min, max);
		}while(accountdb.get(accNum)!=null);

        Account newAcc = new Account();

        newAcc.setAccountNumber(accNum);
        newAcc.setName(name);
        newAcc.setPassword(pw);
        newAcc.setBalance(balance);
        accountdb.put(accNum,newAcc);

        System.out.println("Account created, your account number is: " + accNum);
        return accNum;
    }

    public static String updateBalance(String name, int accNum, String password, int choice, double amount){
        String response = "";

        //Check if account number exists in database
        if(accountdb.get(accNum) == null){
            response = "Account does not exist";
        }
        //Check if pin is correct
        if(accountdb.get(accNum).getPassword() != password){
            response = "Incorrect password, please try again";
        }

        if(choice == 0){
            System.out.println("Attempting Deposit");


            Account temp = accountdb.get(accNum);
			temp.setBalance(temp.getBalance() + amount);

            response = "Deposit for account number " + accNum + " successful\n Balance is " + temp.getBalance() + " " + temp.getCurrency();
        }
        
        else if(choice == 1){
            System.out.println("Attempting withdrawal");

            Account temp = accountdb.get(accNum);

            if(temp.getBalance() >= amount){
				temp.setBalance(temp.getBalance() - amount);
				response = "Withdrawal for account number" + accNum + " successful\n Balance is " + temp.getBalance() + " " + temp.getCurrency();
			}		
			else {
				response = "Insuffient Balance. Current balance: " + temp.getBalance();
			}
        }        
        else response = "Invalid choice, try again.";


        return response;
        
    }


    public static String checkBalance(int accNum, String pw){
        String response = "";

        Account temp = accountdb.get(accNum);

        if(temp == null){
            response = "Account does not exist"; 
        }
        else{
            if(temp.getPassword() == pw){
                response = "Your balance is "+ temp.getBalance() + " " + temp.getCurrency();
            }
            else{
                response = "Incorrect password, please try again.";
            }
        }
        return response;
    }

    public static String closeAccount(String name, int accNum, String pw){
        String response = "";
        Account temp = accountdb.get(accNum);
        if(temp == null){
            response = "Unable to close account, account does not exist.";  
        }
        else{
            if(temp.getPassword() == pw){
                accountdb.remove(accNum);
                response = "Account " + accNum + " successfully removed.";
            }
            else{
                response = "Incorrect password, please try again.";
            }
        }
        return response;
    }

    public static String transferBalance(String name, int accNum, int rec, String pw, double amount){
        String response = "";
        
        Account sender, receiver;

        if(accountdb.get(accNum) == null){
            if(accountdb.get(rec) == null){
                response = "Sender and Receiver account numbers, " + accNum + " and " + rec + ", do not exist";
            }
            else{
                response = "Sender's account number " + accNum + " does not exist";
            }
        }
        else if(accountdb.get(rec) == null){
            response = "Receiver's account number " + rec + " does not exist";
        }
        else{
            sender = accountdb.get(accNum);
            receiver = accountdb.get(rec);

            if(sender.getPassword() != pw){
                response = "Incorrect password, please try again.";
            }

            else if(sender.getBalance() >= amount){
                sender.setBalance(sender.getBalance() - amount);
                receiver.setBalance(receiver.getBalance() + amount);
                response = "Successful transfer \n Your balance is now " + sender.getBalance() + " " + sender.getCurrency();
            }
            else{
                response = "Insufficient funds,  your balance is " + sender.getBalance() + " " + sender.getCurrency();
            }
        }
        return response;
    }
}

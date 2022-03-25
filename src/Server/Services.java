package src.Server;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;


public class Services {
    public static HashMap<Integer, Account> accountdb;
    public Services(){
        accountdb = new HashMap<>();
    }



    public static String createAccount(String name, String pw, double balance){
        int accNum = -1;
		int min = 1000;
		int max = 9999;
		do{
			accNum = ThreadLocalRandom.current().nextInt(min, max);
		}while(accountdb.get(accNum)!=null);

        String response = "";
        Account newAcc = new Account();

        newAcc.setAccountNumber(accNum);
        newAcc.setName(name);
        newAcc.setPassword(pw);
        newAcc.setBalance(balance);
        accountdb.put(accNum,newAcc);

        response = String.format("Account created, your account number is: %d", accNum);
        
        return response;
    }

    public static String updateBalance(String name, int accNum, String password, int choice, double amount){
        String response = "";

        //Check if account number exists in database
        if(accountdb.get(accNum) == null){
            response = String.format("Account does not exist");
        }
        //Check if pin is correct
        if(accountdb.get(accNum).getPassword() != password){
            response = String.format("Incorrect password, please try again");
        }

        if(choice == 0){
            System.out.println("Attempting Deposit");


            Account temp = accountdb.get(accNum);
			temp.setBalance(temp.getBalance() + amount);

            response = String.format("Deposit for account number %d successful\n Balance is %.2f %s", accNum, temp.getBalance(), temp.getCurrency());
        }
        
        else if(choice == 1){
            System.out.println("Attempting withdrawal");

            Account temp = accountdb.get(accNum);

            if(temp.getBalance() >= amount){
				temp.setBalance(temp.getBalance() - amount);
				response = String.format("Withdrawal for account number %d successful\n Balance is %.2f %s", accNum, temp.getBalance(), temp.getCurrency());
			}		
			else {
				response = String.format("Insuffient Balance. Current balance: %.2f", temp.getBalance());
			}
        }        
        else response = String.format("Invalid choice, try again.");


        return response;
        
    }


    public static String checkBalance(int accNum, String pw){
        String response = "";

        Account temp = accountdb.get(accNum);

        if(temp == null){
            response = String.format("Account does not exist"); 
        }
        else{
            if(temp.getPassword() == pw){
                response = String.format("Your balance is %.2f %s", temp.getBalance(), temp.getCurrency());
            }
            else{
                response = String.format("Incorrect password, please try again.");
            }
        }
        return response;
    }

    public static String closeAccount(String name, int accNum, String pw){
        String response = "";
        Account temp = accountdb.get(accNum);
        if(temp == null){
            response = String.format("Unable to close account, account does not exist.");  
        }
        else{
            if(temp.getPassword() == pw){
                accountdb.remove(accNum);
                response = String.format("Account %d successfully removed.", accNum);
            }
            else{
                response = String.format("Incorrect password, please try again.");
            }
        }
        return response;
    }

    public static String transferBalance(String name, int accNum, int rec, String pw, double amount){
        String response = "";
        
        Account sender, receiver;

        if(accountdb.get(accNum) == null){
            if(accountdb.get(rec) == null){
                response = String.format("Sender and Receiver account numbers, %d and %d, do not exist", accNum, rec);
            }
            else{
                response = String.format("Sender's account number %d does not exist", accNum );
            }
        }
        else if(accountdb.get(rec) == null){
            response = String.format("Receiver's account number %d does not exist", accNum );    
        }
        else{
            sender = accountdb.get(accNum);
            receiver = accountdb.get(rec);

            if(sender.getPassword() != pw){
                response = String.format("Incorrect password, please try again.");
            }

            else if(sender.getBalance() >= amount){
                sender.setBalance(sender.getBalance() - amount);
                receiver.setBalance(receiver.getBalance() + amount);
                response = String.format("Successful transfer \n Your balance is now %.2f %s", sender.getBalance(), sender.getCurrency());
            }
            else{
                response = String.format("Insufficient funds, your balance is %.2f %s", sender.getBalance(), sender.getCurrency());
            }
        }
        return response;
    }
}

package src.Server;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 *  Class that contains the functions of the Bank's services
 */

public class Services {
    public static HashMap<Integer, Account> accountdb;

    public static HashMap<String, Long> clientdb;
    public static Callback cb;

    public Services(Callback obj) {
        accountdb = new HashMap<>();
        clientdb = new HashMap<>();
        cb = obj;
    }

    private static double u2s = 1.36;
    private static double e2s = 1.49;
    /**
     * Creates an account
     * @param name Name of account holder
     * @param pw Password for authenticating user
     * @param currency Currency stored in account
     * @param balance Current balance amount in account
     * @return Confirmation message with unique ID of account number
     */
    public String createAccount(String name, String pw, String currency, double balance) {
        int accNum = -1;
        int min = 1000;
        int max = 9999;
        do {
            accNum = ThreadLocalRandom.current().nextInt(min, max);
        } while (accountdb.get(accNum) != null);

        String response = "";
        Account newAcc = new Account();

        newAcc.setAccountNumber(accNum);
        newAcc.setName(name);
        newAcc.setPassword(pw);
        newAcc.setBalance(balance);
        newAcc.setCurrency(currency);
        accountdb.put(accNum, newAcc);

        response = String.format("Account created, your account number is: %d\n", accNum);

        sendUpdate(String.format("Account %d has been created", accNum));

        return response;
    }

    /**
     * Converts currencies between USD-SGD-EUR
     * @param accCurrency Currency of the balance stored in sender's account
     * @param currency Target currency to convert into
     * @param amount Amount that needs to be converted
     * @return The amount after conversion
     */
    private double conversion(String accCurrency, String currency, double amount) {
        if (!accCurrency.equals(currency)) {
            switch (accCurrency) {
                case "EUR":
                    amount *= e2s;
                    break;
                case "USD":
                    amount *= u2s;
                    break;
            }

            switch (currency) {
                case "EUR":
                    amount /= e2s;
                    break;
                case "USD":
                    amount /= u2s;
                    break;
            }
        }
        return amount;
    }

    /**
     * Updates the balance of the account
     * @param name Name of account holder
     * @param accNum Unique ID the account
     * @param password Password to authenticate the user
     * @param choice Whether the user wants to deposit or withdraw
     * @param currency Currency of the amount that is being deposited/withdrawn
     * @param amount Amount that is being deposited/withdrawn
     * @return Result of update request (Error / Acknowledgement)
     */

    public String updateBalance(String name, int accNum, String password, int choice, String currency, double amount) {
        String response = "";

        // Check if account number exists in database
        if (accountdb.get(accNum) == null) {
            response = String.format("Account does not exist\n");
            return response;
        }
        // Check if pin is correct
        if (!accountdb.get(accNum).getPassword().equals(password)) {
            response = String.format("Incorrect password, please try again\n");
            return response;
        }

        if (choice == 0) {
            Account temp = accountdb.get(accNum);

            double conv_amount = conversion(temp.getCurrency(), currency, amount);
            temp.setBalance(temp.getBalance() + conv_amount);

            response = String.format("Deposit for account number %d successful\nBalance is %.2f %s\n", accNum, temp.getBalance(), temp.getCurrency());

            sendUpdate(String.format("Balance of account %d has been updated", accNum));
        }

        else if (choice == 1) {
            Account temp = accountdb.get(accNum);

            double conv_amount = conversion(temp.getCurrency(), currency, amount);
            if (temp.getBalance() >= conv_amount) {
                temp.setBalance(temp.getBalance() - conv_amount);
                response = String.format("Withdrawal for account number %d successful\nAmount of %.2f %s has been withdrawn. Balance is %.2f %s\n", accNum, amount, currency, temp.getBalance(), temp.getCurrency());

                sendUpdate(String.format("Balance of account %d has been updated", accNum));
            } else {
                response = String.format("Insuffient Balance. Current balance: %.2f %s\n", temp.getBalance(), temp.getCurrency());
            }
        } else
            response = String.format("Invalid choice, try again.\n");

        return response;

    }

    /**
     * Checks the balance of account number
     * @param name Name of account holder
     * @param accNum Unique ID of account
     * @param pw Password to authenticate user
     * @return Result of check request (Error / Acknowledgement)
     */

    public String checkBalance(String name, int accNum, String pw) {
        String response = "";

        Account temp = accountdb.get(accNum);

        if (temp == null) {
            response = String.format("Account does not exist\n");
            return response;
        } else {
            if (!temp.getName().equals(name)) {
                response = String.format("The acccount holder is not linked to this account.\n");
            } else if (temp.getPassword().equals(pw)) {
                response = String.format("Your balance is %.2f %s\n", temp.getBalance(), temp.getCurrency());
            } else {
                response = String.format("Incorrect password, please try again.\n");
            }
        }

        return response;
    }

    /**
     * Closes a specific account
     * @param name Name of account holder
     * @param accNum Unique ID of account
     * @param pw Password to authenticate user
     * @return Result of close request (Error / Acknowledgement)
     */

    public String closeAccount(String name, int accNum, String pw) {
        String response = "";
        Account temp = accountdb.get(accNum);
        if (temp == null) {
            response = String.format("Unable to close account, account does not exist.\n");
            return response;
        } else {
            if (!temp.getName().equals(name)) {
                response = String.format("The acccount holder is not linked to this account.\n");
            } else if (temp.getPassword().equals(pw)) {
                accountdb.remove(accNum);
                response = String.format("Account %d successfully removed.\n", accNum);
                sendUpdate(String.format("Account %d has been deleted", accNum));
            } else {
                response = String.format("Incorrect password, please try again.\n");
            }
        }

        return response;
    }

    /**
     * Transfers money from the sender's account to another account
     * @param name 
     * @param name Name of account holder
     * @param accNum Unique ID of sender's account
     * @param pw Password to authenticate user
     * @param rec Unique ID of receiver's account
     * @return Result of transfer request (Error / Acknowledgement)
     */

    public String transferBalance(String name, int accNum, String pw, double amount, int rec) {
        String response = "";

        Account sender, receiver;

        if (accountdb.get(accNum) == null) {
            if (accountdb.get(rec) == null) {
                response = String.format("Sender and Receiver account numbers, %d and %d, do not exist\n", accNum, rec);
                return response;
            } else {
                response = String.format("Sender's account number %d does not exist\n", accNum);
                return response;
            }
        } else if (accountdb.get(rec) == null) {
            response = String.format("Receiver's account number %d does not exist\n", accNum);
            return response;
        } else {
            sender = accountdb.get(accNum);
            receiver = accountdb.get(rec);

            if (!sender.getPassword().equals(pw)) {
                response = String.format("Incorrect password, please try again.\n");
                return response;
            }
            else{
                double conv_amount = conversion(sender.getCurrency(), receiver.getCurrency(), amount);
                if (sender.getBalance() >= amount) {
                    sender.setBalance(sender.getBalance() - amount);
                    receiver.setBalance(receiver.getBalance() + conv_amount);
                    response = String.format("Successful transfer to %s. Your balance is %.2f %s\n", receiver.getName(), sender.getBalance(), sender.getCurrency());
                    sendUpdate(String.format("Balance of account %d and %d have been updated", receiver.getAccountNumber(), sender.getAccountNumber()));
                } else {
                    response = String.format("Insufficient funds, your balance is %.2f %s\n", sender.getBalance(), sender.getCurrency());
                }
            }
        }
        return response;
    }

    /**
     * Registers client into a list of whom to send any server updates
     * @param ip IP address of client
     * @param port Port to send updates
     * @param duration Duration in which updates will be sent
     * @return Confirmation of registration
     */

    public String monitorUpdate(String ip, int port, int duration) {

        long timestamp = Instant.now().getEpochSecond();
        timestamp += TimeUnit.MINUTES.toMillis(duration);
        clientdb.put(String.format("%s:%d", ip, port), timestamp);
        return "Registered client to updates list\n";
    }

    /**
     * Sends any server updates to registered clients
     * @param msg
     */

    private void sendUpdate(String msg) {
        Iterator<String> iter = clientdb.keySet().iterator();

        while (iter.hasNext()) {
            String key = iter.next();

            // Checks if the update is still valid
            long timestamp = Instant.now().getEpochSecond();
            if (clientdb.get(key) < timestamp) {
                iter.remove();
            } else {
                String[] arr = key.split(":");
                String ip = arr[0];
                int port = Integer.parseInt(arr[1]);

                try {
                    cb.sendMessage(msg, ip, port);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}

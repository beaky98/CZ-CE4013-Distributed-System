package src.Client;

import java.util.Scanner;

/**
 * User Interface of the Banking System
 */
public class BankInterface {

    private static int PW_LENGTH = 20;

    public static Scanner sc = new Scanner(System.in);

    /**
     * Prints out the various banking services' options
     * @return Print Options
     */
    public static String printOptions() {
        sc.useDelimiter(System.lineSeparator());
        System.out.println("+-------------------------------------------------------------+");
        System.out.println("| Welcome to Distributed Banking Pte Ltd, how may I help you? |");
        System.out.println("| 1. Create account                                           |");
        System.out.println("| 2. Close account                                            |");
        System.out.println("| 3. Deposit money                                            |");
        System.out.println("| 4. Withdraw money                                           |");
        System.out.println("| 5. Monitor updates                                          |");
        System.out.println("| 6. Check Balance                                            |"); //idempotent
        System.out.println("| 7. Transfer                                                 |"); //non-idempotent
        System.out.println("| 0. Exit application                                         |");
        System.out.println("+-------------------------------------------------------------+");
        
        String option = "";
        try {
            option = sc.next();
            int int_option = Integer.parseInt(option);
            if (int_option >= 0 && int_option <= 7) {
                
                return selectOption(int_option);
            }
        } catch (Exception e) {

        }

        System.out.println("Invalid option");
        return printOptions();
    }

    /**
     * Switch case to select options
     */
    public static String selectOption(Integer option) {
        String request = "";
        switch (option) {
            case 1:
                request = createAccount();
                break;
            case 2:
                request = closeAccount();
                break;
            case 3:
                request = transfer(true);
                break;
            case 4:
                request = transfer(false);
                break;
            case 5:
                request = monitorUpdate();
                break;
            case 6:
                request = checkBalance();  
                break;
            case 7:
                request = transferMoney();
                break;
            case 0:
                request = "0";
                break;
            default:
                System.out.println("No such option");
        }
        return request;
    }

    /**
     * Allows a user to open a new account by specifying  name, a password , the currency type of the account and the initial account balance. 
     * @return Payload
     */
    public static String createAccount() {
        
        System.out.println();
        String option = "1";
        String name = askName();
        String pw = askPassword(true);
        String currency = askCurrencyType();
        Double balance = askInitialBalance();

        System.out.printf("\nYour name: %s\nPassword: %s\nDepositing in: %s\nWith the amount: %.2f\n\n", name, pw, currency, balance);
        String payload = String.join("_", option, name, pw, currency, balance.toString());

        return payload;
    }

    /**
     * Allows a user to close an existing account by specifying name,  account number and the password.
     * @return Payload
     */
    public static String closeAccount() {

        System.out.println();
        String option = "2";
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword(false);

        System.out.printf("\nYour name: %s\nAccount Number: %s\nPassword: %s\n\n", name, acct, pw);
        String payload = String.join("_", option, name, acct, pw);

        return payload;
    }

    /**
     * Allows a user to deposit/withdraw money into/from an account by specifying name, account number, the password, the currency type and amount to deposit/withdraw
     * @param deposit To denote deposit or withdraw
     * @return Payload
     */
    public static String transfer(boolean deposit) {

        System.out.println();
        String option = deposit ? "3" : "4";
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword(false);
        String currency = askCurrencyType();
        Double balance = askTransferAmount(deposit);
        String process = deposit ? "Depositing" : "Withdrawing";

        System.out.printf("\nYour name: %s\nAccount Number: %s\nPassword: %s\n%s: %.2f %s\n\n", name, acct, pw, process, balance, currency);
        String payload = String.join("_", option, name, acct, pw, currency, balance.toString());

        return payload;
    }

    /**
     * Allows a user to monitor updates made to all the bank accounts at the server through callback for a designated time period by specifying monitor interval
     * @return Payload
     */
    public static String monitorUpdate() {

        System.out.println();
        String option = "5";
        Integer interval = askMonitorInterval();

        System.out.printf("\nSetting Monitor Interval(min): %d\n\n", interval);
        String payload = String.join("_", option, interval.toString());

        return payload;
    }

    /**
     * Allows a user to check the current balance in the account by specifying name, account number, the password.
     * @return Payload
     */
    public static String checkBalance() {

        System.out.println();
        String option = "6";
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword(false);

        System.out.printf("\nYour name: %s\nAccount Number: %s\nPassword: %s\n\n", name, acct, pw);
        String payload = String.join("_", option, name, acct, pw);

        return payload;
    }

    /**
     * Allows a user to transfer money into another account by specifying name, account number, the password, recipient account number, the currency type and amount to transfer
     * @return Payload
     */
    public static String transferMoney() {

        System.out.println();
        String option = "7";
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword(false);
        String acctTo = askAccountNumberTo();
        Double balance = askTransferAmount(false);

        System.out.printf("\nYour name: %s\nAccount Number: %s\nPassword: %s\nTransfering: %.2f dollars\nTo Account Number: %s\n\n", name, acct, pw, balance, acctTo);
        String payload = String.join("_", option, name, acct, pw, balance.toString(), acctTo);

        //reply with "Done"

        return payload;
    }

    /**
     * Request user to input name
     * @return Username
     */
    public static String askName() {
        System.out.print("Please enter your name: ");
        String name = sc.next();

        return name;
    }

    /**
     * Request user to input password
     * @param creating denote whether creation of account or checking password
     * @return Password
     */
    public static String askPassword(boolean creating) {
        String msg = "";
        if (creating) {
            msg = " (no longer than " + PW_LENGTH + " characters)";
        }
        System.out.printf("Please enter your password%s: ", msg);
        String pw = sc.next();

        if (creating && pw.length() > PW_LENGTH) {
            System.out.printf("Password must not be longer than %d characters\n", PW_LENGTH);
            return askPassword(true);
        }

        return pw;
    }

    /**
     * Request user to input currency type 
     * @return Currency type
     */
    public static String askCurrencyType() {
        String[] currencies = new String[] {
                "SGD", "USD", "EUR"
        };

        
        for (int i = 0; i < currencies.length; i++) {
            System.out.printf("%d. %s\n", i+1, currencies[i]);
        }
        System.out.print("Please select a currency type: ");
        
        String option = sc.next();
        int int_option = -1;

        try {
            int_option = Integer.parseInt(option) - 1;
        } catch (Exception e) {
            option = option.toUpperCase();
            for (int i = 0; i < currencies.length; i++) {
                if (currencies[i].equals(option)) {
                    int_option = i;
                }
            }
        }

        if (int_option < 0 || int_option >= currencies.length) {
            System.out.println("Invalid option");
            return askCurrencyType();
        }

        return currencies[int_option];
    }

    /**
     * Request user to input initial balance for new account
     * @return Initial balance
     */
    public static Double askInitialBalance() {
        System.out.println("Please enter initial account balance: ");
        Double balance = sc.nextDouble();

        return balance;
    }

    /**
     * Request user to input monitor interval
     * @return Interval
     */
    public static Integer askMonitorInterval() {
        System.out.print("Please enter the monitor interval (min): ");
        int interval = sc.nextInt();

        return interval;
    }

    /**
     * Request user to input account number
     * @return Account number
     */
    public static String askAccountNumber() {
        System.out.print("Please enter your account number: ");
        String acct = sc.next();

        return acct;
    }

    /**
     * Request user to input recipient account number
     * @return Recipient account number
     */
    public static String askAccountNumberTo() {
        System.out.print("Please enter the account number you are transferring to: ");
        String acct = sc.next();

        return acct;
    }

    /**
     * Request user to input transfer amount
     * @param deposit To denote deposit or withdraw
     * @return Transfer amount
     */
    public static Double askTransferAmount(boolean deposit) {
        System.out.printf("Please enter amount to %s: ", deposit ? "deposit" : "withdraw");
        Double amount = sc.nextDouble();

        return amount;
    }

    /**
     * To exit the banking service
     */
    public static void exitMessage() {
        System.out.println("Thanks for banking with us!");
    }
}
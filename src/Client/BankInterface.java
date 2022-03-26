package src.Client;

import java.util.Scanner;

public class BankInterface {

    private static int PW_LENGTH = 20;

    public static Scanner sc = new Scanner(System.in);

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
            case 0: // Client exits interface
                request = "0";
                break;
            default:
                System.out.println("No such option");
        }
        return request;
    }

    public static String createAccount() {
        String option = "1";
        String name = askName();
        String pw = askPassword(true);
        String currency = askCurrencyType();
        Double balance = askInitialBalance();

        System.out.printf("Your name is %s\nPassword: %s\nDepositing in %s\nWith the amount of %.2f\n", name, pw, currency, balance);
        String payload = String.join("_", option, name, pw, currency, balance.toString());

        return payload;
    }

    public static String closeAccount() {
        String option = "2";
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword(false);

        System.out.printf("Your name is %s\nAccount Number: %s\nPassword: %s\n", name, acct, pw);
        String payload = String.join("_", option, name, acct, pw);

        return payload;
    }

    public static String transfer(boolean deposit) {

        String option = deposit ? "3" : "4";
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword(false);
        String currency = askCurrencyType();
        Double balance = askTransferAmount(deposit);
        String process = deposit ? "Depositing" : "Withdrawing";

        System.out.printf("Your name is %s\nAccount Number: %s\nPassword: %s\n%s: %s %.2f\n", name, acct, pw, process, currency, balance);
        String payload = String.join("_", option, name, acct, pw, currency, balance.toString());

        //reply with "Done"

        return payload;
    }

    public static String monitorUpdate() {
        String option = "5";
        Integer interval = askMonitorInterval();

        System.out.printf("Setting Monitor Interval(min): %d\n", interval);
        String payload = String.join("_", option, interval.toString());

        return payload;
    }

    public static String checkBalance() {
        String option = "6";
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword(false);

        System.out.printf("Your name is %s\nAccount Number: %s\nPassword: %s\n", name, acct, pw);
        String payload = String.join("_", option, name, acct, pw);

        return payload;
    }

    public static String transferMoney() {
        String option = "7";
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword(false);
        String acctTo = askAccountNumberTo();
        Double balance = askTransferAmount(false);

        System.out.printf("Your name is %s\nAccount Number: %s\nPassword: %s\nTransfering: %.2f dollars\nTo Account Number: %s\n", name, acct, pw, balance, acctTo);
        String payload = String.join("_", option, name, acct, pw, balance.toString(), acctTo);

        //reply with "Done"

        return payload;
    }

    public static String askName() {
        System.out.print("Please enter your name: ");
        String name = sc.next();

        return name;
    }

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

    public static String askCurrencyType() {
        String[] currencies = new String[] {
                "SGD", "USD", "EUR"
        };
        System.out.println("Please select a currency type: ");
        for (int i = 0; i < currencies.length; i++) {
            System.out.printf("%d. %s\n", i+1, currencies[i]);
        }

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

    public static Double askInitialBalance() {
        System.out.println("Please enter initial account balance: ");
        Double balance = sc.nextDouble();

        return balance;
    }

    public static Integer askMonitorInterval() {
        System.out.print("Please enter the monitor interval (min): ");
        int interval = sc.nextInt();

        return interval;
    }


    public static String askAccountNumber() {
        System.out.print("Please enter your account number: ");
        String acct = sc.next();

        return acct;
    }

    public static String askAccountNumberTo() {
        System.out.print("Please enter the account number you are transferring to: ");
        String acct = sc.next();

        return acct;
    }

    public static Double askTransferAmount(boolean deposit) {
        System.out.printf("Please enter amount to %s: ", deposit ? "deposit" : "withdraw");
        Double amount = sc.nextDouble();

        return amount;
    }

    public static void exitMessage() {
        System.out.println("Thanks for banking with us!");
    }
}
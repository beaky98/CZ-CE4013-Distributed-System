package src;

import java.util.Arrays;
import java.util.Scanner;
import java.text.DecimalFormat;

public class BankInterface {

    private static int PW_LENGTH = 20;

    public static Scanner sc = new Scanner(System.in);

    public static String printOptions() {
        sc.useDelimiter(System.lineSeparator());
        System.out.println("Welcome to Distributed Banking Pte Ltd, how may I help you?");
        System.out.println("1. Create account");
        System.out.println("2. Close account");
        System.out.println("3. Deposit money");
        System.out.println("4. Withdraw money");
        System.out.println("5. Monitor updates");
        System.out.println("6. Currency Converter");
        System.out.println("7. Non-indempotent transaction");
        System.out.println("0. Exit application");
        
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
                request = currencyExch();  
                break;
            case 7:
                request = nonIdemTrans();
                break;
            case 0:
                request = "0";
                break;
            default:
                System.out.println("No such option");
        }
        return request;
    }

    public static String createAccount() {
        String name = askName();
        String pw = askPassword(true);
        String currency = askCurrencyType();
        Double balance = askInitialBalance();

        System.out.printf("%s, %s, %s, %.2f\n", name, pw, currency, balance);
        String payload = String.join("_", name, pw, currency, balance.toString());

        
        return payload;
    }

    public static String closeAccount() {
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword(false);

        System.out.printf("%s, %s, %s, %.2f\n", name, acct, pw);
        String payload = String.join("_", name, acct, pw);

        return payload;
    }

    public static String transfer(boolean deposit) {
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword(false);
        String currency = askCurrencyType();
        Double balance = askTransferAmount(deposit);

        System.out.printf("%s, %s, %s, %.2f\n", name, acct, pw, currency, balance);
        String payload = String.join("_", name, acct, pw, currency, balance.toString());

        //reply with "Done"

        return payload;
    }

    public static String monitorUpdate() {
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword(false);

        System.out.printf("%s, %s, %s\n", name, acct, pw);
        String payload = String.join("_", name, acct, pw);

        return payload;
    }

    public static String currencyExch() {
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword(false);
        askCurrencyConverter();

        System.out.printf("%s, %s, %s\n", name, acct, pw);
        String payload = String.join("_", name, acct, pw);

        return payload;
    }

    public static String nonIdemTrans() {
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword(false);

        System.out.printf("%s, %s, %s\n", name, acct, pw);
        String payload = String.join("_", name, acct, pw);

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

    public static void askCurrencyConverter() {
        double SGD, USD, EUR;
        DecimalFormat f = new DecimalFormat("##.##");
        String[] currencies = new String[] {
            "SGD", "USD", "EUR"
        };
        System.out.println("Please select a currency type to convert from: ");
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
            askCurrencyConverter();
        }

        System.out.printf("Enter the amount you want to convert?");
        Double amount = sc.nextDouble();

        switch (int_option)
      {
         case 1:  // SGD Conversion
            USD = amount * 0.73644;
            System.out.println(amount + " SGD = " + f.format(USD) + " USD");
 
            EUR = amount * 0.67031;
            System.out.println(amount + " SGD = " + f.format(EUR) + " EUR");
 
            break;
 
         case 2:  // USD Conversion
            SGD = amount * 0.13578;
            System.out.println(amount + " USD = " + f.format(SGD) + " SGD");

            EUR = amount * 0.91012;
            System.out.println(amount + " USD = " + f.format(EUR) + " EUR");
            break;
 
         case 3:  // EUR Conversion
            SGD = amount * 0.14920;
            System.out.println(amount + " EUR = " + f.format(SGD) + " SGD");

            USD = amount * 0.10987;
            System.out.println(amount + " EUR = " + f.format(USD) + " USD");
            break;
 
          //Default case
         default:
            System.out.println("Invalid Input");
    }
}

    public static Double askInitialBalance() {
        System.out.println("Please enter initial account balance: ");
        Double balance = sc.nextDouble();

        return balance;
    }

    public static String askAccountNumber() {
        System.out.print("Please enter your account number: ");
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
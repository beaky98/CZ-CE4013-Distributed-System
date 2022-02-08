package src;

import java.util.Scanner;

public class BankInterface {

    public static Scanner sc = new Scanner(System.in);

    public static String printOptions() {
        sc.useDelimiter(System.lineSeparator());
        System.out.println("Welcome to Distributed Banking Pte Ltd, how may I help you?");
        System.out.println("1. Create account");
        System.out.println("2. Close account");
        System.out.println("3. Deposit money");
        System.out.println("4. Withdraw money");
        System.out.println("5. Monitor updates");
        System.out.println("6. Idempotent transaction");
        System.out.println("7. Non-indempotent transaction");
        System.out.println("0. Exit application");

        Integer option = sc.nextInt();
        return selectOption(option);
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
                break;
            case 6:
                break;
            case 7:
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
        String pw = askPassword();
        String currency = askCurrencyType();
        Double balance = askInitialBalance();

        String acctNumber = "12345678";
        return acctNumber;
    }

    public static String closeAccount() {
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword();

        String acctNumber = "12345678";
        return acctNumber;
    }

    public static String transfer(boolean deposit) {
        String name = askName();
        String acct = askAccountNumber();
        String pw = askPassword();
        String currency = askCurrencyType();
        Double balance = askTransferAmount(deposit);

        return "done";
    }

    public static String askName() {
        System.out.print("Please enter your name: ");
        String name = sc.next();

        return name;
    }

    public static String askPassword() {
        System.out.print("Please enter your password: ");
        String pw = sc.next();

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
        Integer option = sc.nextInt();

        return currencies[option - 1];
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
package src.Server;

public class Account {
    int balance;
    int previousTransaction;
    String customerName;
    String customerID;

    Account(String cname, String cid){
        customerName = cname;
        customerID = cid;
    }
}

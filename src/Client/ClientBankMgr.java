package client;

import java.io.IOException;

public class ClientBankMgr {
    private static ClientBankMgr single_instance = null;
    private static final String SERVICENAME = "BankingService/";
    UDPClient comms;  // communication device that uses UDP

    private ClientBankMgr(UDPClient comms){
        this.comms = comms;
    }


    public static ClientBankMgr getInstance(UDPClient comms) {
        if (single_instance == null)
            single_instance = new ClientBankMgr(comms);
        return single_instance;
    }

    public String[] getBank(Utils utils){
        String message = SERVICENAME;
        message += "getBank";
        String response = "";
        response = this.comms.sendMessage(message);
        return response.split("/", -1);
    }

    public void queryBank(Utils utils, String Bank){
        String message = SERVICENAME;
        message += "queryBank/"+Bank;
        String response = "";
        response = this.comms.sendMessage(message);
        utils.println(response);
    }
    
//TO BE CHANGE
    /* public void bookBank(Utils utils, String username, String Bank){
        String message = SERVICENAME;
        message += "updateObject/";
        message += Bank + "/book/" + username + "/";
        //updateObject/$Bank/book/username/date_input/startTime/endTime

        utils.println("For the day (1: Monday, 2: Tuesday, 3: Wednesday, 4: Thursday, 5: Friday, 6: Saturday, 7: Sunday): ");
        int date_input = utils.checkUserIntInput(1,7);
        message += date_input + "/";

        utils.println("For the start time. \nEnter the start hour:");
        int startTime = utils.checkUserIntInput(0,23);
        startTime *= 4;
        utils.println("Enter the start minute ( 0 -> 0 min, 1 -> 15 min, 2 -> 30 min, 3 -> 45 min");
        startTime += utils.checkUserIntInput(0, 3);
        message += startTime + "/";

        utils.println("For the end time: \nEnter the end hour:");
        int endTime = utils.checkUserIntInput(0,23);
        endTime *= 4;
        utils.println("Enter the end minute ( 0 -> 0 min, 1 -> 15 min, 2 -> 30 min, 3 -> 45 min");
        endTime += utils.checkUserIntInput(0, 3);
        message += endTime;


        String response = "";

        response = this.comms.sendMessage(message);
        utils.println(response);
    }

    public void changeBooking(Utils utils, String bookingID, String Bank){
        String message = SERVICENAME;
        message += "updateObject/" + Bank + "/changeBooking/" + bookingID + "/";
        int offset = utils.checkUserIntInput(-24,24);
        message += offset;
        String response = "";

        response = this.comms.sendMessage(message);

        utils.println(response);
    }

    public void monitorBank(String Bank, String username, Utils utils) throws IOException {
        //monitor/register/Bank/username/Port

        int duration = utils.UserInputOptions(1, 100,
                "Enter the duration (in seconds) to monitor from (1-100) inclusive",
                "Please reenter the duration (1-100) inclusive: ");

        String message = SERVICENAME + "monitor/register/" + Bank + "/" + username + "/";   //register for notification
        String localPort = comms.getLocalPort();
        message += localPort;
        utils.println("monitoring....");
        this.comms.sendMessage(message);

        try {
            this.comms.monitorForNotification(duration, utils);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //monitor/unregister/Bank/username
        message = SERVICENAME + "monitor/unregister/" + Bank + "/" + username;    //remove from notification list
        this.comms.sendMessage(message);

        utils.println("Monitoring has ended\n");
    }

    public void getUserBookings(String Bank, String username, Utils utils){
        //getBookings/Bank/username
        String message = SERVICENAME + "getBookings/" + Bank + "/" + username;
        String response = "";
        response = this.comms.sendMessage(message);
        utils.println(response);
    }

    public void extendBooking(String Bank, String bookingID, int extensionTime, Utils utils){
        //extendBooking/Bank/bookingID/extenstionTime
        String message = SERVICENAME + "extendBooking/" + Bank + "/" + bookingID + "/" + extensionTime;
        String response = "";
        response = this.comms.sendMessage(message);
        utils.println(response);
    }
   */
}

package client;

public interface Utils {
    /**
     * interface for handling input/output. Each driver class (Terminal/Socket) needs to implement this
     * 
     */
    int checkUserIntInput(int lower, int upper );
    int UserInputOptions(int start, int stop,String prompt,String reprompt);
    String UserInputString(String prompt);
    String getBankID();
    void println(String s) ;
    void print(String s);

    String nextLine();
}

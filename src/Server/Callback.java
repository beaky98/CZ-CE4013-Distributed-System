package src.Server;

import java.io.IOException;

/**
 * Interface of callback class
 */
interface Callback {
    /**
     * Sends a message to the ip and port
     * @param msg Message to be sent
     * @param ip Ip address of client
     * @param port Port of client
     * @throws IOException
     */
    public void sendMessage(String msg, String ip, int port) throws IOException;
}

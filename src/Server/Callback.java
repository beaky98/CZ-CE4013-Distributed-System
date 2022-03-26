package src.Server;

import java.io.IOException;

interface Callback {
    public void sendMessage(String msg, String ip, int port) throws IOException;
}

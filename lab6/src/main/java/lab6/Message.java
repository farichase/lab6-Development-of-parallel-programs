package lab6;

import java.util.ArrayList;

public class Message {
    private final ArrayList<String> servers;
    public Message(ArrayList<String> servers) {
        this.servers = servers;
    }
    public ArrayList<String> getServers(){
        return this.servers;
    }
}

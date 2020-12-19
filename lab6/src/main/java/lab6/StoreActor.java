package lab6;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;

public class StoreActor extends AbstractActor {
    private ArrayList<String> servers;
    public Receive createReceive(){
        return ReceiveBuilder.create()
                .match(Message.class, msg -> {
                    this.servers = msg.getServers();
                })
                .match(RandomServer.class, msg -> {

                    getSender().tell(servers.);
                })
        .build();

    }
}

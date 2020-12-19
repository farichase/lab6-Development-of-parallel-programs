package lab6;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.Random;

public class StoreActor extends AbstractActor {
    private ArrayList<String> servers;
    public Receive createReceive(){
        return ReceiveBuilder.create()
                .match(Message.class, msg -> {
                    this.servers = msg.getServers();
                })
                .match(RandomServer.class, msg -> {
                    Random rand = new Random();
                    getSender().tell(servers.get(rand.nextInt(servers.size())), ActorRef.noSender());
                })
        .build();

    }
}

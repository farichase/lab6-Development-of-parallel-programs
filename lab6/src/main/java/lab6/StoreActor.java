package lab6;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

public class StoreActor extends AbstractActor {
    public Receive createReceive(){
        return ReceiveBuilder.create()
                .match(Message.class, msg -> {

                })

    }
}

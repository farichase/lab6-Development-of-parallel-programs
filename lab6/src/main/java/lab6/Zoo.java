package lab6;

import akka.actor.ActorRef;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Zoo {
    private ZooKeeper zooKeeper;
    private String CONNECT_STRING = "127.0.0.1:2181";
    private int timeout = 3000;
    private ActorRef storeActor;
    public Zoo(ActorRef storeActor) throws IOException {
        this.zooKeeper = new ZooKeeper(CONNECT_STRING, timeout, (Watcher) null);
        this.storeActor = storeActor;

    }
    private void serverWatch(){

    }
}

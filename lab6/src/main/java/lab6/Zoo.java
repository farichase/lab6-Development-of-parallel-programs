package lab6;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Zoo {
    private final ZooKeeper zooKeeper;
    private final String CONNECT_STRING = "127.0.0.1:2181";
    private final int timeout = 3000;
    private final ActorRef storeActor;
    public Zoo(ActorRef storeActor, String serverUrl) throws IOException, KeeperException, InterruptedException {
        this.zooKeeper = new ZooKeeper(CONNECT_STRING, timeout, watcher);
        this.storeActor = storeActor;
        this.serverWatch(serverUrl);
    }
    private Watcher watcher = watchedEvent -> {
        if (watchedEvent.getType() == Watcher.Event.EventType.NodeCreated ||
            watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted ||
            watchedEvent.getType() == Watcher.Event.EventType.NodeDataChanged){
            ArrayList<String> serversNames = new ArrayList<>();
            Iterator iterator = this.zooKeeper.getChildren("/servers", null).iterator();

            while(iterator.hasNext()){
                String line = (String) iterator.next();
                byte[] serverUrl = this.zooKeeper.getData("/servers/" + line, null, null);
                serversNames.add(new String(serverUrl));
            }
            this.storeActor.tell(new Message(serversNames), ActorRef.noSender());
        } catch(KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

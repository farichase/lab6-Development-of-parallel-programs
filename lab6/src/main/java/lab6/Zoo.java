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
    public Zoo(ActorRef storeActor) throws IOException {
        this.zooKeeper = new ZooKeeper(CONNECT_STRING, timeout, null);
        this.storeActor = storeActor;
        this.serverWatch();
    }
    public void createServer(String serverUrl) throws KeeperException, InterruptedException {
        this.zooKeeper.create("/servers/", serverUrl.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    }
    private void serverWatch(){
        try {
            List<String> serversChildren = this.zooKeeper.getChildren("/servers", watchedEvent -> {
                if (watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged){
                    this.serverWatch();
                }
            });
            ArrayList<String> serversNames = new ArrayList<>();
            Iterator iterator = serversChildren.iterator();

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

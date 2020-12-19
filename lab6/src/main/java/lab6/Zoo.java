package lab6;

import akka.actor.ActorRef;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Zoo {
    private ZooKeeper zooKeeper;
    private String CONNECT_STRING = "127.0.0.1:2181";
    private String PATH = "/servers";
    private int timeout = 3000;
    private String SLASH = "/";
    private ActorRef storeActor;
    public Zoo(ActorRef storeActor) throws IOException {
        this.zooKeeper = new ZooKeeper(CONNECT_STRING, timeout, null);
        this.storeActor = storeActor;
        this.serverWatch();
    }
    private void serverWatch(){
        try {
            List<String> serversChildren = this.zooKeeper.getChildren(PATH, watchedEvent -> {
                if (watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged){
                    this.serverWatch();
                }
            });
            ArrayList<String> serversNames = new ArrayList<>();
            Iterator iterator = serversChildren.iterator();

            while(iterator.hasNext()){
                String line = (String) iterator.next();
                byte[] serverUrl = this.zooKeeper.getData(PATH + SLASH + line, null, null);
                serversNames.add(new String(serverUrl));
            }
            this.storeActor.tell(new Message(serversNames), ActorRef.noSender());
        } catch(KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

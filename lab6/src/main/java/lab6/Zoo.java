package lab6;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;

public class Zoo {
    private ZooKeeper zooKeeper;
    private final String CONNECT_STRING = "localhost:2181";
    private final Duration timeout = Duration.ofSeconds(5);
    private ActorRef storeActor;
    private final String SERVER = "localhost";
    public Zoo(ActorRef storeActor)  {
        this.storeActor = storeActor;
    }
    public void createServer(int port) throws IOException, KeeperException, InterruptedException{
        this.zooKeeper = new ZooKeeper(CONNECT_STRING, (int)timeout.getSeconds() * 1000, null);
        String serverUrl = "http://" + SERVER + port;
        this.zooKeeper.create("/servers/" + SERVER + ":" + port, String.valueOf(port).getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        WatchedEvent event = new WatchedEvent(Watcher.Event.EventType.NodeCreated,
                Watcher.Event.KeeperState.SyncConnected, "");
        watcher.process(event);
    }

    public Watcher watcher = watchedEvent -> {
        if (watchedEvent.getType() == Watcher.Event.EventType.NodeCreated ||
                watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted ||
                watchedEvent.getType() == Watcher.Event.EventType.NodeDataChanged) {
            ArrayList<String> serversNames = new ArrayList<>();
            try {
                Iterator iterator = this.zooKeeper.getChildren("/servers", null).iterator();
                while (iterator.hasNext()) {
                    String line = (String) iterator.next();
                    byte[] serverUrl = this.zooKeeper.getData("/servers/" + line, false, null);
                    serversNames.add(new String(serverUrl));
                }
                this.storeActor.tell(new Message(serversNames), ActorRef.noSender());
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}

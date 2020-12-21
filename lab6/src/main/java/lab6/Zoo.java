package lab6;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class Zoo {
    private ZooKeeper zooKeeper;
    private final String CONNECT_STRING = "127.0.0.1:2181";
    private ActorRef storeActor;
    private final String SERVER = "localhost";
    public Zoo(ActorRef storeActor, int port) throws IOException, KeeperException, InterruptedException {
        this.storeActor = storeActor;
        createServer(port);
    }
    public void createServer(int port) throws IOException, KeeperException, InterruptedException{
        this.zooKeeper = new ZooKeeper(CONNECT_STRING, 5000, null);
        this.zooKeeper.create("/servers/" + SERVER + ":" + port, String.valueOf(port).getBytes(StandardCharsets.UTF_8),
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

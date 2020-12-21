package lab6;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Zoo implements Watcher {
    private ZooKeeper zooKeeper;
    private final String CONNECT_STRING = "127.0.0.1:2181";
    private ActorRef storeActor;
    private final String SERVER = "localhost";
    public Zoo(ActorRef storeActor) throws IOException, KeeperException, InterruptedException {
        this.storeActor = storeActor;
        this.zooKeeper = new ZooKeeper(CONNECT_STRING, 5000, null);
        //sendServers();
    }
    public void sendServers() throws KeeperException, InterruptedException{
        List<String> serversNames = zooKeeper.getChildren("/servers", this);
        this.storeActor.tell(new Message(serversNames), ActorRef.noSender());
    }
    public void createConnection(int port) throws KeeperException, InterruptedException{
        this.zooKeeper.create("/servers/" + SERVER + ":" + "8080",
                String.valueOf(port).getBytes(StandardCharsets.UTF_8),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        this.storeActor.tell(SERVER + ":" + port , ActorRef.noSender());
    }
    @Override
    public void process(WatchedEvent watchedEvent){
        try {
            sendServers();
        } catch (KeeperException | InterruptedException e){
            e.printStackTrace();
        }
    }
}

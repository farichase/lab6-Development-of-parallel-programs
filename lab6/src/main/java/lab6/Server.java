package lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.http.javadsl.model.Uri;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.japi.Pair;
import akka.pattern.Patterns;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class Server extends AllDirectives {
    private static final String URL = "url";
    private static final String SERVER_URL = "http://localhost:";
    private static final String COUNT = "count";
    private static Http http;
    private final ActorRef storeActor;
    private final Duration timeout = Duration.ofSeconds(5);
    public Server(Http http, ActorRef storeActor, int port) throws IOException, KeeperException, InterruptedException{
        this.http = http;
        this.storeActor = storeActor;
        this.zooKeeperInit(port);
    }
    private void zooKeeperInit(int port) throws IOException, KeeperException, InterruptedException {
        Zoo zoo = new Zoo(storeActor, port);
        zoo.createServer(getServerUrl(port));
    }
    private String getServerUrl(int port){
        return SERVER_URL + port;
    }
    private static CompletionStage<HttpResponse> fetch(String url){
        return http.singleRequest(HttpRequest.create(url));
    }
    private String createUrl(String serverUrl, String url, int count){
        return Uri.create(serverUrl).query(Query.create(new Pair[]{
                Pair.create("url", url),
                Pair.create("count", String.valueOf(count - 1))
        })).toString();
    }
    public Route createRoute(){
        return route(get(() ->
            parameter(URL, (url) ->
                    parameter(COUNT, count -> {
                        if (Integer.parseInt(count) <= 0){
                            return completeWithFuture(fetch(url));
                        }
                        return completeWithFuture(Patterns.ask(this.storeActor, new RandomServer(), timeout)
                                .thenCompose((serverUrl) ->
                                    fetch(this.createUrl((String)serverUrl, url, Integer.parseInt(count)))
                                ));
                    })
            )

        ));
    }
}

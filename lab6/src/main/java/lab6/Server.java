package lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;

import java.time.Duration;

import static akka.http.javadsl.server.Directives.*;

public class Server {
    private static final String URL = "url";
    private static final String COUNT = "count";
    private Http http;
    private ActorRef storeActor;
    private Duration timeout = Duration.ofSeconds(5);
    public Server(Http http, ActorRef storeActor){
        this.http = http;
        this.storeActor = storeActor;
    }
    public Route createRoute(){
        return route(get() ->
            parameter(URL, (url) ->
                    parameter(COUNT, count -> {
                        if (Integer.parseInt(count) <= 0){
                            return completeWithFuture(http.singleRequest(HttpRequest.create(url)));
                        }
                        return completeWithFuture(Patterns.ask(this.storeActor, new RandomServer(), timeout))
                                .
                    })

            )

        );
    }
}

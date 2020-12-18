package lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

public class Server {
    private static final String URL = "url";
    private static final String COUNT = "count";
    private static Http http;
    private ActorRef storeActor;
    private Duration timeout = Duration.ofSeconds(5);
    public Server(Http http, ActorRef storeActor){
        this.http = http;
        this.storeActor = storeActor;
    }
    private static CompletionStage<HttpResponse> fetch(String url){
        return http.singleRequest(HttpRequest.create(url));
    }
    private String createUrl(String serverUrl, String url, int count){
        return 
    }
    public Route createRoute(){
        return route(get() ->
            parameter(URL, (url) ->
                    parameter(COUNT, count -> {
                        if (Integer.parseInt(count) <= 0){
                            return completeWithFuture(fetch(url));
                        }
                        return completeWithFuture(Patterns.ask(this.storeActor, new RandomServer(), timeout))
                                .thenCompose((serverUrl) ->
                                    fetch()
                                )
                    })

            )

        );
    }
}

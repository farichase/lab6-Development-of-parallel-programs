package lab6;

import akka.http.javadsl.Http;
import akka.http.javadsl.server.Route;

import static akka.http.javadsl.server.Directives.*;

public class Server {
    private static final String URL = "url";
    private static final String COUNT = "count";
    private Http http;
    public Server(Http http){
        this.http = http;
    }
    public Route createRoute(){
        return route(get() ->
            parameter(URL, (url) ->
                    parameter(COUNT, count -> {
                        if (Integer.parseInt(count) <= 0){
                            return completeWithFuture(http.singleRequest(url));
                        }
                        return completeWithFuture()
                    })

            )

        );
    }
}

package lab6;

import akka.http.javadsl.server.Route;

import static akka.http.javadsl.server.Directives.parameter;
import static akka.http.javadsl.server.Directives.route;

public class Server {
    private static final String URL = "url";
    private static final String COUNT = "count";
    public Server(){
    }
    public Route createRoute(){
        return route(get() ->
            parameter(URL, url ->
                    parameter(COUNT, count -> {
                        
                    })

            )

        );
    }
}

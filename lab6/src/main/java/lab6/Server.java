package lab6;

import akka.http.javadsl.server.Route;

import static akka.http.javadsl.server.Directives.parameter;
import static akka.http.javadsl.server.Directives.route;

public class Server {
    private static final String URL;
    public Server(String URL){
        this.URL = URL;
    }
    public Route createRoute(){
        return route(get() ->
            parameter(URL, url ->
                    parameter()

            )

        );
    }
}

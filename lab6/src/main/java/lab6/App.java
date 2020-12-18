package lab6;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class App {
    private final static String HOST = "localhost";
    private final static int PORT = 8080;
    public static void main(String[] args) throws IOException {
        System.out.println("start!");
        ActorSystem system = ActorSystem.create("routes");
        ActorRef storeActor = system.actorOf(Props.create(StoreActor.class), "storeActor");
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = new ;
        final CompletionStage<ServerBinding> bindingCompletionStage = http.bindAndHandle(
            routeFlow,
            ConnectHttp.toHost(HOST, PORT),
            materializer
        );
        System.out.println("Server online at http://localhost:8080");
        System.in.read();
        bindingCompletionStage.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }
}

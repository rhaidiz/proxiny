package proxiny.gui.JSONRPC;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import proxiny.gui.ProxyServerStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class RpcServer {

    /**
     * Starts the RpcServer.
     *
     * @throws IOException
     */
    public static void StartJSONRPCServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1",1235), 0);
        server.createContext("/proxinygui", new UpdateStatsHandler());
        server.setExecutor(Executors.newCachedThreadPool()); // creates a default executor
        server.start();
    }

    /**
     * Handles update requests sent to the JSONRPC server.
     */
    public static class UpdateStatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

            UiStatsHandler uiStatsHandler = new UiStatsHandler();
            com.github.arteam.simplejsonrpc.server.JsonRpcServer rpcServer = new com.github.arteam.simplejsonrpc.server.JsonRpcServer();

            InputStream in = t.getRequestBody();
            String text = null;
            try (Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
                text = scanner.useDelimiter("\\A").next();
            }

            String response = rpcServer.handle(text, uiStatsHandler);

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            t.close();
        }
    }

    @JsonRpcService
    public static class UiStatsHandler {

        /**
         * Handles the update of the requests counter.
         *
         * @param reqsCounter int representing the number of requests counted so far
         * @return true
         */
        @JsonRpcMethod
        public boolean updateRequestsCounter(@JsonRpcParam("updateReqs") int reqsCounter){
            ProxyServerStatus proxyServerStatus = ProxyServerStatus.getInstance();
            proxyServerStatus.setRequestsCounter(reqsCounter);
            return true;
        }

        /**
         * Handles the update of the responses counter.
         *
         * @param respsCounter int representing the number of responses counted so far
         * @return true
         */
        @JsonRpcMethod
        public boolean updateResponsesCounter(@JsonRpcParam("updateResps") int respsCounter){
            ProxyServerStatus proxyServerStatus = ProxyServerStatus.getInstance();
            proxyServerStatus.setResponsesCounter(respsCounter);
            return true;
        }


        @JsonRpcMethod
        public boolean fatal(@JsonRpcParam("message") String message){
            ProxyServerStatus proxyServerStatus = ProxyServerStatus.getInstance();
            proxyServerStatus.setFatal(true);
            proxyServerStatus.setErrorMessage(message);
            System.out.println("error");
            return true;
        }

        @JsonRpcMethod
        public boolean error(@JsonRpcParam("message") String message){
            ProxyServerStatus proxyServerStatus = ProxyServerStatus.getInstance();
            proxyServerStatus.setErrorMessage(message);
            return true;
        }

    }

}

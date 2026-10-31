import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/** Tiny backend: GET /status/{id} returns a small JSON body. */
public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/status/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String id = path.substring("/status/".length()).replaceAll("[^A-Za-z0-9_-]", "");
            byte[] body = ("{\"id\":\"" + id + "\",\"state\":\"running\"}")
                    .getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });

        server.start();
        System.out.println("Listening on :8080");
    }
}

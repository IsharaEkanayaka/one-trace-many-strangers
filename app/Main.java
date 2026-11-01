import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/** Tiny backend with two unrelated prefix endpoints. */
public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/status/", ex -> respond(ex, "/status/", "state", "running"));
        server.createContext("/orders/", ex -> respond(ex, "/orders/", "total", "42.00"));

        server.start();
        System.out.println("Listening on :8080");
    }

    private static void respond(HttpExchange ex, String prefix, String key, String value)
            throws IOException {
        String id = ex.getRequestURI().getPath()
                .substring(prefix.length())
                .replaceAll("[^A-Za-z0-9_-]", "");
        byte[] body = ("{\"id\":\"" + id + "\",\"" + key + "\":\"" + value + "\"}")
                .getBytes(StandardCharsets.UTF_8);

        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(200, body.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body);
        }
    }
}
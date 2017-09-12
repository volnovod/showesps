package Https;

import com.sun.net.httpserver.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HTTPReceiver {

    private final int port = 10060;
    private String result = "";
    private String address;
    private JSONObject jsonObject;

    public String getResult() {
        return result;
    }

    public void receive() {
        HttpServer server = null;
        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(address, port), 0);
            HttpContext context = server.createContext("/", new EchoHandler());
            context.setAuthenticator(new Auth());

            server.setExecutor(null);
            server.start();

            System.out.println("Server " + Thread.currentThread().getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class EchoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder builder = new StringBuilder();
            builder.append("Ok");

            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String line;
            while ((line = reader.readLine()) != null)
            {
                System.out.println(line);
            }
            reader.close();
            byte[] bytes = builder.toString().getBytes();
            exchange.sendResponseHeaders(200, bytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    static class Auth extends Authenticator {
        @Override
        public Result authenticate(HttpExchange httpExchange) {
            if ("/".equals(httpExchange.getRequestURI().toString()))
                return new Success(new HttpPrincipal("c0nst", "realm"));
            else {
                return new Failure(403);
            }
        }
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

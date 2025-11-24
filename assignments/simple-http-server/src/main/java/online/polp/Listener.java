package online.polp;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Listener implements Runnable {
    private final Socket socket;

    public Listener(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            SocketWrapper socketWrapper = new SocketWrapper(socket);

            userRun(socketWrapper);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void userRun(SocketWrapper socketWrapper) throws IOException {
        String firstLine = socketWrapper.in.readLine();

        String[] firstLineParts = firstLine.split(" ");
        String method = firstLineParts[0];
        String path = firstLineParts[1];
        String version = firstLineParts[2];

        List<String> headers = new ArrayList<>();
        String line;
        do {
            line = socketWrapper.in.readLine();
            headers.add(line);
        } while (!line.isEmpty());

        StringBuilder bodySb = new StringBuilder();

        while (socketWrapper.in.ready()) {
            bodySb.append((char) socketWrapper.in.read());
        }
        String body = bodySb.toString();

        FirstLineHTTPRequest parsedFirstLine = new FirstLineHTTPRequest(
                FirstLineHTTPRequest.Method.valueOf(method),
                path,
                version);

        HTTPRequest request = new HTTPRequest(
                parsedFirstLine,
                headers,
                body
            );

        System.out.println(request);


        if (!method.equals("GET") || !path.equals("/")) {
            String httpResponse = version + " 404 Not Found\r\n\r\n";
            socketWrapper.out.write(httpResponse);
            socketWrapper.out.flush();
            socketWrapper.socket.close();
            return;
        }

        String responseString = "Hello, World!";

        String httpResponse = String.format(
                version + " 200 OK\r\n" +
                "Content-Length: %d\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "%s",
                responseString.length(),
                responseString
        );

        socketWrapper.out.write(httpResponse);
        socketWrapper.out.flush();
        socketWrapper.socket.close();
    }
}

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

        String body = socketWrapper.in.lines().reduce("", (acc, curr) -> acc + curr + "\n");

        FirstLineHTTPRequest parsedFirstLine = new FirstLineHTTPRequest(
                FirstLineHTTPRequest.Method.valueOf(method),
                path,
                version);

        HTTPRequest request = new HTTPRequest(
                parsedFirstLine,
                headers.toArray(new String[0]),
                body);

        System.out.println(request);
    }
}

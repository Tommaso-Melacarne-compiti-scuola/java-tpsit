package online.polp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URLConnection;
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

            socketWrapper.close();
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

    HTTPRequest getRequestFromClient(SocketWrapper socketWrapper) throws IOException {
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
                body);

        return request;
    }

    void userRun(SocketWrapper socketWrapper) throws IOException {
        HTTPRequest request = getRequestFromClient(socketWrapper);

        System.out.println(request);

        FirstLineHTTPRequest firstLine = request.firstLine;
        String version = firstLine.version;

        if (!request.firstLine.method.equals(FirstLineHTTPRequest.Method.GET)) {
            String httpResponse = version + " 405 Method Not Allowed\r\n\r\n";
            socketWrapper.out.write(httpResponse);
            socketWrapper.close();
            return;
        }

        if (firstLine.path.endsWith("/")) {
            firstLine.path += "index.html";
        }

        File file = new File("./static/" + firstLine.path);

        if (!file.exists()) {
            String httpResponse = version + " 404 Not Found\r\n\r\n";
            socketWrapper.out.write(httpResponse);
            return;
        }

        if (file.isDirectory()) {
            String httpResponse = String.format(
                    "%s 301 Moved Permanently\r\nLocation: %s/\r\n",
                    version,
                    firstLine.path);
            socketWrapper.out.write(httpResponse);
        }

        socketWrapper.out.println(String.format(
                "%s 200 OK\nContent-Length: %d\r\nContent-Type: %s\r\n",
                version,
                file.length(),
                getContentType(file)));
        InputStream input = new FileInputStream(file);
        byte[] buf = new byte[8192];
        int n;
        while ((n = input.read(buf)) != -1) {
            socketWrapper.dataOut.write(buf, 0, n);
        }
        input.close();
    }

    private static String getContentType(File file) throws MalformedURLException, IOException {
        URLConnection connection = file.toURI().toURL().openConnection();
        return connection.getContentType();
    }
}

package online.polp;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URLConnection;
import java.util.Map;

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

    private void userRun(SocketWrapper socketWrapper) throws IOException {
        HTTPRequest request = socketWrapper.getRequest();

        System.out.println(request);

        FirstLineHTTPRequest firstLine = request.firstLine;
        String version = firstLine.version;

        if (!request.firstLine.method.equals(FirstLineHTTPRequest.Method.GET)) {
            socketWrapper.sendFirstLineHTTPResponse(version, 405);
            return;
        }

        if (firstLine.path.endsWith("/")) {
            firstLine.path += "index.html";
        }

        File file = new File("./htdocs/" + firstLine.path);

        if (!file.exists()) {
            socketWrapper.sendFirstLineHTTPResponse(version, 404);
            return;
        }

        if (file.isDirectory()) {
            socketWrapper.sendFirstLineHTTPResponse(version, 301);
            socketWrapper.out.printf("Location: %s", firstLine.path);
        }

        socketWrapper.sendFirstLineHTTPResponse(version, 200);

        socketWrapper.sendHeaders(
                Map.of(
                        "Content-Length", String.valueOf(file.length()),
                        "Content-Type", getContentType(file)
                )
        );

        socketWrapper.out.println();
        
        socketWrapper.sendFile(file);
    }

    
    private static String getContentType(File file) throws MalformedURLException, IOException {
        URLConnection connection = file.toURI().toURL().openConnection();
        return connection.getContentType();
    }
}

package online.polp;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import online.polp.FirstLineHTTPRequest.Method;
import online.polp.HTTPResponse.HTTPResponseBuilder;

public class Listener implements Runnable {
    private final Socket socket;

    private static final List<FirstLineHTTPRequest.Method> allowedMethods = List.of(
        FirstLineHTTPRequest.Method.GET,
        FirstLineHTTPRequest.Method.HEAD,
        FirstLineHTTPRequest.Method.POST
    );

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

        HTTPResponseBuilder responseBuilder = HTTPResponse.builder();

        responseBuilder.version(request.firstLine.version);

        FirstLineHTTPRequest firstLine = request.firstLine;

        if (!allowedMethods.contains(firstLine.method)) {
            responseBuilder.statusCode(405);
            responseBuilder.body("Method Not Allowed".getBytes());

            socketWrapper.send(responseBuilder.build());
            return;
        }

        // if POST, echo back the body
        if (firstLine.isHttpMethod(Method.POST)) {
            String body = request.readBody(socketWrapper.in);

            responseBuilder.statusCode(200);
            responseBuilder.body(body.getBytes());

            socketWrapper.send(responseBuilder.build());
            return;
        }

        if (firstLine.path.endsWith("/")) {
            firstLine.path += "index.html";
        }

        File file = new File("./htdocs" + firstLine.path);

        boolean isHead = firstLine.isHttpMethod(Method.HEAD);

        if (!file.exists()) {
            responseBuilder.statusCode(404);

            if (isHead) {
                return;
            }

            File errorFile = new File("./htdocs/404.html");

            String contentType = getContentType(errorFile);

            responseBuilder.headers(Map.of(
                "Content-Type", contentType != null ? contentType : "text/html",
                "Content-Length", String.valueOf(errorFile.length()
            )));

            // open a 404.html file if exists
            responseBuilder.body(getFileBytes(errorFile));
            return;
        }

        if (file.isDirectory()) {
            responseBuilder.statusCode(301);
            responseBuilder.headers(Map.of(
                "Location", firstLine.path + "/"
            ));

            socketWrapper.send(responseBuilder.build());

            return;
        }
        
        responseBuilder.statusCode(200);
        
        String contentType = getContentType(file);

        responseBuilder.headers(Map.of(
            "Content-Type", contentType != null ? contentType : "application/octet-stream",
            "Content-Length", String.valueOf(file.length()
        )));

        responseBuilder.body(getFileBytes(file));

        socketWrapper.send(responseBuilder.build());
    }

    private byte[] getFileBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    private static String getContentType(File file) throws MalformedURLException, IOException {
        URLConnection connection = file.toURI().toURL().openConnection();
        return connection.getContentType();
    }
}

package online.polp;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SocketWrapper implements Closeable {
    public final Socket socket;
    public final BufferedReader in;
    public final PrintWriter out;
    public final DataOutputStream dataOut;

    public SocketWrapper(Socket socket) throws IOException {
        System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.dataOut = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void close() throws IOException {
        out.flush();
        dataOut.flush();
        socket.close();
    }

    public HTTPRequest getRequest() throws IOException {
        String firstLine = in.readLine();

        String[] firstLineParts = firstLine.split(" ");
        String method = firstLineParts[0];
        String path = firstLineParts[1];
        String version = firstLineParts[2];

        List<String> headers = new ArrayList<>();
        String line;
        do {
            line = in.readLine();
            headers.add(line);
        } while (!line.isEmpty());

        FirstLineHTTPRequest parsedFirstLine = new FirstLineHTTPRequest(
                FirstLineHTTPRequest.Method.valueOf(method),
                path,
                version);

        HTTPRequest request = new HTTPRequest(
                parsedFirstLine,
                headers);

        return request;
    }

    public void sendFirstLineHTTPResponse(String version, int statusCode) {
        FirstLineHttpResponse response = new FirstLineHttpResponse(version, statusCode);

        out.println(response.toString());
    }

    public void sendHeaders(Map<String, String> headers) {
        headers.forEach((key, value) -> {
            out.printf("%s: %s%n", key, value);
        });
    }

    public void sendFile(File file) throws IOException {
        try (InputStream input = new FileInputStream(file)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = input.read(buf)) != -1) {
                dataOut.write(buf, 0, n);
            }
        }
    }
}

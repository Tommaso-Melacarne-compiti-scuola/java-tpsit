package online.polp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketWrapper {
    public final Socket socket;
    public final BufferedReader in;
    public final PrintWriter out;

    public SocketWrapper(Socket socket) throws IOException {
        System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }
}

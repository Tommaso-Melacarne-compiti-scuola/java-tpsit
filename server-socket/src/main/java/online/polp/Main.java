package online.polp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    static int PORT = 3000;

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            do {
                Socket socket = serverSocket.accept();

                Thread listenerThread = new Thread(new Listener(socket));
                listenerThread.start();
            } while (true);
        }
    }
}
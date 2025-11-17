package online.polp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    private static final int PORT = 3000;
    private static final List<Message> board = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        TokenStore tokenStore = new TokenStore();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            do {
                Socket socket = serverSocket.accept();

                Thread listenerThread = new Thread(new Listener(socket, tokenStore, board));
                listenerThread.start();
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package online.polp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    static final int PORT = 3000;

    public static void main(String[] args) throws IOException {
        Socket clientSocket = new Socket("127.0.0.1", PORT);

        System.out.println("Connected to server on port " + PORT);

        Scanner consoleInput = new Scanner(System.in);

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        System.out.println("Login with LOGIN <yourusername>, then you can use the following commands:\nADD <textToAdd>\nLIST\nDEL <id>");

        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                System.out.println(in.readLine());
                String userInput = consoleInput.nextLine().trim();
                out.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clientSocket.close();
            consoleInput.close();
        }
    }
}
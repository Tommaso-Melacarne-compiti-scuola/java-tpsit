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

        String serverVersion = in.readLine();
        System.out.println("Server version: " + serverVersion.substring(2));

        try {
            //noinspection InfiniteLoopStatement
            while (true) {

                prompt(
                    "Enter operator (1:Add, 2:Sub, 3:Mul, 4:Div, 5:Exit):",
                    in,
                    out,
                    consoleInput,
                    clientSocket
                );

                prompt("Enter first number: ", in, out, consoleInput, clientSocket);

                prompt("Enter second number", in, out, consoleInput, clientSocket);

                String response = in.readLine();

                if (response.startsWith("ko:")) {
                    System.out.println("Error from server: " + response.substring(3));
                } else {
                    System.out.println("Result from server: " + response.substring(3));
                }
            }
        } catch  (IOException e) {
            System.out.println("Error from server: " + e.getMessage());
        } finally {
            clientSocket.close();
        }
    }

    public static void prompt(String message, BufferedReader in, PrintWriter out, Scanner consoleInput, Socket clientSocket) throws IOException {
        System.out.println(message);
        String userInput = consoleInput.nextLine().trim();

        out.println(userInput);
    }
}
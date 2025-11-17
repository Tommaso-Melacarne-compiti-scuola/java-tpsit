package online.polp;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.ListIterator;

public class Listener implements Runnable {
    private final Socket socket;
    private final List<Message> board;

    public Listener(Socket socket, List<Message> board) {
        this.socket = socket;
        this.board = board;
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
        socketWrapper.out.println("WELCOME");

        String username = null;

        while (true) {
            Command command = getCommand(socketWrapper);

            if (command.commandName.equals("LOGIN")) {
                if (username == null) {
                    // If the user still did not login
                    username = command.commandArg;
                    socketWrapper.out.println("OK");
                    
                    continue;
                }

                // If the user has already logged in
                socketWrapper.out.println("ERR LOGINREQUIRED");
                
                continue;
            }

            // Checks for login before running privileged commands
            if (username == null) {
                socketWrapper.out.println("ERR LOGINREQUIRED");
                continue;
            }

            switch (command.commandName) {
                case "ADD":
                    execAdd(socketWrapper, board, username, command.commandArg);
                    break;
                case "LIST":
                    execList(socketWrapper, board);
                    break;
                case "DEL":
                    execDel(socketWrapper, board, username, command.commandArg);
                    break;
                case "QUIT":
                    socketWrapper.out.println("BYE");
                    socketWrapper.socket.close();
                    break;
            }
        }
        
    }

    void execAdd(SocketWrapper socketWrapper, List<Message> board, String username, String messageToAdd) {
        Message message = new Message(username, messageToAdd);
        board.add(message);
        socketWrapper.out.println("OK ADDED " + message.id);
    }

    void execList(SocketWrapper socketWrapper, List<Message> board) {
        StringBuilder sb = new StringBuilder();

        sb.append("BOARD:\n");

        for (Message message : board) {
            sb.append(String.format("[%s] %s: %s\n", message.id, message.author, message.text));
        }

        sb.append("END");

        socketWrapper.out.println(sb.toString());
    }

    void execDel(SocketWrapper socketWrapper, List<Message> board, String username, String idStringToDelete) {
        int idToDelete;

        try {
            idToDelete = Integer.parseInt(idStringToDelete);
        } catch (NumberFormatException ex) {
            socketWrapper.out.println("ERR SYNTAX");

            return;
        }


        ListIterator<Message> iter = board.listIterator();

        while (iter.hasNext()){
            Message message = iter.next();

            if (message.id == idToDelete){
                if (!message.author.equals(username)) {
                    socketWrapper.out.println("ERR PERMISSION");
                    return;
                }

                iter.remove();
                socketWrapper.out.println("OK DELETED");
                return;
            }
        }

        socketWrapper.out.println("ERR NOTFOUND");
    }

    Command getCommand(SocketWrapper socketWrapper) throws IOException {
        while (true) {
            String fullCommandLine = socketWrapper.in.readLine();
            String[] commandLine = fullCommandLine.split(" ", 2);

            if (commandLine.length < 1) {
                socketWrapper.out.println("ERR UNKNOWNCMD");
                continue;
            }

            return new Command(commandLine[0], commandLine.length == 1 ? null : commandLine[1]);
        }
    }
}

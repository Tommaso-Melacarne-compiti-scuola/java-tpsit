package online.polp;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import online.polp.Command.CommandBuilder;

public class Listener implements Runnable {
    private final Socket socket;
    private final List<Message> board;
    private final TokenStore tokenStore;

    public Listener(Socket socket, TokenStore tokenStore, List<Message> board) {
        this.socket = socket;
        this.tokenStore = tokenStore;
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
        Optional<Command> maybeCommand = getCommand(socketWrapper);

        if (!maybeCommand.isPresent()) {
            return;
        }

        Command command = maybeCommand.get();

        if (command.commandName.equals("LOGIN")) {
            execLogin(socketWrapper, command.commandArg);
            return;
        }

        // Protecting other commands with token

        String username = tokenStore.getUsername(command.token.orElse(""));

        if (username == null) {
            socketWrapper.out.println("ERR LOGINREQUIRED");
            return;
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
            case "LOGOUT":
                tokenStore.invalidateToken(command.token.get());
                socketWrapper.out.println("OK");
                break;
        }
    }

    void execLogin(SocketWrapper socketWrapper, String username) {
        String token = tokenStore.newToken(username);
        socketWrapper.out.println("OK " + token);
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

        while (iter.hasNext()) {
            Message message = iter.next();

            if (message.id == idToDelete) {
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

    Optional<Command> getCommand(SocketWrapper socketWrapper) throws IOException {
        // First line is either COMMAND args or TOKEN thetoken, then COMMAND args on the next line

        List<String> lines = new ArrayList<>();

        String line;
        // Loop to read lines until an empty line is encountered
        while ((line = socketWrapper.in.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }

        if (lines.isEmpty()) {
            return Optional.empty();
        }

        String firstLine = lines.get(0);
        String secondLine = lines.size() > 1 ? lines.get(1) : null;

        CommandBuilder commandBuilder = new CommandBuilder();


        if (firstLine.startsWith("TOKEN ")) {
            String token = firstLine.substring(6).trim();
            commandBuilder.token(Optional.of(token));

            if (secondLine == null) {
                socketWrapper.out.println("ERR SYNTAX");
                return Optional.empty();
            }

            parseCommandLine(commandBuilder, secondLine);
        } else {
            parseCommandLine(commandBuilder, firstLine);
        }

        return Optional.of(commandBuilder.build());
    }

    void parseCommandLine(CommandBuilder commandBuilder, String line) {
        String[] parts = line.split(" ", 2);
        String commandName = parts[0].toUpperCase();
        String commandArg = parts.length > 1 ? parts[1] : "";

        commandBuilder.commandName(commandName).commandArg(commandArg);
    }
}

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


class Server {

    public static char TERMINATOR = '\0';
    public static String DISCONNECT_KEY_WORD = "/exit";
    public static String LIST_KEY_WORD = "/list";
    public static String GREETING = String.format(
            """
                    Hallo my dear stranger. No rules but be polite (and polish).
                    %s - if you wish to exit.
                    %s - if you wish to list.
                    """,
            DISCONNECT_KEY_WORD, LIST_KEY_WORD);

    private final InetAddress ipAddress;
    private final int backlog;
    private ServerSocket serverSocket;
    private final int portNumber;
    private final List<ClientThread> clients = new CopyOnWriteArrayList<>();


    Server(InetAddress ipAddress, int portNumber, int maxConnections) {
        this.portNumber = portNumber;
        this.ipAddress = ipAddress;
        this.backlog = maxConnections;
    }

    public void closeSocket(ClientThread client) {
        try {
            broadcastMessage(String.format("%s has disconnected", client.username), null);
            writeMessage(DISCONNECT_KEY_WORD, client.outputStreamWriter);
            clients.remove(this);
            client.inputStreamReader.close();
            client.outputStreamWriter.close();
            client.socket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing the app", e);
        }
    }


    public void broadcastMessage(String message, ClientThread exclude) {
        System.out.println("Broadcasting: " + message);
        for (ClientThread client : clients) {
            if (client == exclude) {
                continue;
            }
            writeMessage(message, client.outputStreamWriter);
        }
    }

    private void startListening() {
        try {
            Socket socket;

            serverSocket = new ServerSocket(portNumber, backlog, ipAddress);
            System.out.printf("Server is listening on %s:%d max=%d%n\n", ipAddress, portNumber, backlog);
            while (true) {
                socket = serverSocket.accept();
                System.out.println("Request arrived...");
                clients.add(new ClientThread(socket));
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public static StringBuffer readStream(Reader inputStream) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            char x;
            while (true) {
                x = (char) inputStream.read();
                if (x == Server.TERMINATOR) break; // reads till the terminator
                stringBuffer.append(x);
            }
        } catch (IOException e) {
            throw new RuntimeException("ReadStream error: " + e);
        }
        return stringBuffer;
    }

    public static void writeMessage(String message, Writer outputStreamWriter) {
        try {
            outputStreamWriter.write(message + Server.TERMINATOR);
            outputStreamWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException("writeMessage error: " + e);
        }
    }

    public String getAllUsersList() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("###All users:\n");
        for (ClientThread client : clients) {
            stringBuffer.append(String.format("##%s\n", client.username));
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args) {
        List<String> errors = new ArrayList<>();
        int porn = 0;
        int backlog = 0;
        InetAddress ipAddress = null;

        if (args.length != 3) {
            errors.add("Wrong number of arguments. Right format is: ipAddress port maxConnections");
        }
        try {
            porn = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            errors.add("Invalid Port: " + args[1]);
        }
        try {
            backlog = Integer.parseInt(args[2]);
            if (backlog <= 0) {
                errors.add("Backlog must be greater than 0");
            }
        } catch (NumberFormatException e) {
            errors.add("Invalid maxConnections number format: " + args[2]);
        }
        try {
            ipAddress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            errors.add("Invalid IP address: " + args[0]);
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(Arrays.toString(errors.toArray()));
        }
        Server server = new Server(ipAddress, porn, backlog);

        server.startListening();
    }


    class ClientThread extends Thread {
        private Socket socket;
        private String username;
        private StringBuffer stringBuffer;

        OutputStreamWriter outputStreamWriter;
        InputStreamReader inputStreamReader;

        ClientThread(Socket socket) {
            this.socket = socket;
            start();
        }

        public void run() {
            streamInit();

            username = Server.readStream(inputStreamReader).toString();
            writeMessage(GREETING + getAllUsersList(), outputStreamWriter);
            broadcastMessage(String.format("%s has connected", username), null);
            while (true) {
                stringBuffer = Server.readStream(inputStreamReader);
                if (stringBuffer.toString().equals(DISCONNECT_KEY_WORD)) {
                    closeSocket(this);
                    break;
                }
                else if (stringBuffer.toString().equals(LIST_KEY_WORD)) {
                    writeMessage(getAllUsersList(), outputStreamWriter);
                }
                else broadcastMessage(String.format("#%s: %s", username, stringBuffer.toString()), this);
            }
        }


        private void streamInit() {
            try {
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException("stream init exception: " + e);
            }
        }
    }
}
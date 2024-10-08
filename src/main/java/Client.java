import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;



class Client {
    private String username;
    private String ipAddress;
    private int port;
    private ChatThread thread;

    Client(String username, String ipAddress, int port) {
        this.username = username;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    private void connect() throws IOException {
        String request;
        Socket socket = new Socket(ipAddress, port);
        OutputStream outputStream;
        OutputStreamWriter outputStreamWriter;
        Scanner scanner = new Scanner(System.in);

        thread = new ChatThread(socket);
        outputStream = socket.getOutputStream();
        outputStreamWriter = new OutputStreamWriter(outputStream);
        Server.writeMessage(username, outputStreamWriter);

        while (true) {
            request = scanner.nextLine();
            Server.writeMessage(request, outputStreamWriter);
        }
    }

    private void disconnect() {
        try {
            thread.socket.close();
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException("Error during disconnection: " + e);
        }
    }


    public static void main(String data[]) {
        List<String> errors = new ArrayList<>();
        if (data.length != 3)
        {
            errors.add("Wrong number of arguments. Right format is: username ipAddress port");
        }
        int num;
        try {
            num = Integer.parseInt(data[2]);
        } catch (NumberFormatException e) {
            errors.add("Port is not numeric");
        }
        if (!errors.isEmpty())
        {
            throw new IllegalArgumentException(Arrays.toString(errors.toArray()));
        }
        Client client = new Client(data[0], data[1], Integer.parseInt(data[2]));

        try {
            client.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class ChatThread extends Thread
    {
        private Socket socket;

        ChatThread(Socket socket)
        {
            this.socket = socket;
            start();
        }

        public void run()
        {
            InputStream inputStream;
            InputStreamReader inputStreamReader;
            StringBuffer stringBuffer;

            try {
                inputStream = socket.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                while (true)
                {
                    stringBuffer = Server.readStream(inputStreamReader);
                    if (stringBuffer.toString().equals(Server.DISCONNECT_KEY_WORD))
                    {
                        disconnect();
                    }
                    System.out.println(stringBuffer);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
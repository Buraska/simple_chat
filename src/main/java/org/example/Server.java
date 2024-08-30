package org.example;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


class Server {
    public static char TERMINATOR = '\0';
    private ServerSocket serverSocket;
    private final int portNumber;
    private final List<ClientThread> clients = new CopyOnWriteArrayList<>();


    Server(int portNumber) {
        this.portNumber = portNumber;
    }

    public void closeSocket(ClientThread client) {
        try {
            broadcastMessage(String.format("%s has disconnected", client.username), null);
            clients.remove(this);
            client.inputStreamReader.close();
            client.outputStreamWriter.close();
            client.socket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing the app", e);
        }
    }


    public void broadcastMessage(String message, ClientThread exclude)
    {
        System.out.println("Broadcasting: " + message);
        for (ClientThread client : clients)
        {
            if (client == exclude)
            {
                continue;
            }
            writeMessage(message, client.outputStreamWriter);
        }
    }

    private void startListening() {
        try {
            Socket socket;

            serverSocket = new ServerSocket(this.portNumber);
            System.out.println("Server is listening on port : " + this.portNumber);
            while (true) {
                socket = serverSocket.accept();
                System.out.println("Request arrived..");
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

    public static void main(String[] args) {
        int portNumber;

        portNumber = 1111;

        Server server = new Server(portNumber);
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
            broadcastMessage(String.format("%s has connected", username), null);
            while (true) {
                stringBuffer = Server.readStream(inputStreamReader);
                if (stringBuffer.toString().equals("exit")) {
                    closeSocket(this);
                    break;
                }
                broadcastMessage(String.format("%s: %s",username, stringBuffer.toString()), this);
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
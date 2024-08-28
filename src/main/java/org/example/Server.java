package org.example;

import java.io.*;
import java.net.*;


class RequestProcessor extends Thread {
    private Socket socket;

    RequestProcessor(Socket socket) {
        this.socket = socket;
        start();
    }

    public void run() {
        try {
            OutputStream outputStream;
            OutputStreamWriter outputStreamWriter;
            InputStream inputStream;
            InputStreamReader inputStreamReader;
            StringBuffer stringBuffer;

            while (true) {
                System.out.println("Getting the stream");
                inputStream = socket.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                stringBuffer = Server.readStream(inputStreamReader);
                System.out.println("Request: " + stringBuffer.toString());

                outputStream = socket.getOutputStream();
                outputStreamWriter = new OutputStreamWriter(outputStream);
                if (stringBuffer.toString().equals("exit")) {
                    System.out.println("Closing the app");
                    inputStreamReader.close();
                    outputStreamWriter.close();
                    socket.close();
                    break;
                }

                stringBuffer.append(Server.TERMINATOR);
                outputStreamWriter.write(stringBuffer.toString());
                outputStreamWriter.flush(); // response sent
            }
        } catch (IOException exception) {
            System.out.println("Request thread exception: " + exception);
        }
    }
}

class Server {
    public static char TERMINATOR = '\0';
    private ServerSocket serverSocket;
    private int portNumber;

    Server(int portNumber) {
        this.portNumber = portNumber;
    }

    private void startListening() {
        try {
            Socket socket;

            serverSocket = new ServerSocket(this.portNumber);
            System.out.println("Server is listening on port : " + this.portNumber);
            while (true) {
                System.out.println("Waiting for request...");
                socket = serverSocket.accept();
                System.out.println("Request arrived..");
                new RequestProcessor(socket);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public static StringBuffer readStream(Reader inputStream)
    {
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

    public static void main(String[] args) {
        int portNumber;

        portNumber = 1111;

        Server server = new Server(portNumber);
        server.startListening();
    }


}
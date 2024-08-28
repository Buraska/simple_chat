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
            String request;
            int x;

            while (true) {
                System.out.println("Getting the stream");
                inputStream = socket.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                stringBuffer = new StringBuffer();
                while (true) {
                    x = inputStreamReader.read();
                    if (stringBuffer.toString().equals("exit") || x == -1 || x == Server.TERMINATOR) {
                        break;
                    }
                    stringBuffer.append((char) x);
                }
                request = stringBuffer.toString();
                System.out.println("Request: " + request);

                if (request.equals("exit")) {
                    System.out.println("Closing the app");
                    socket.close();
                    break;
                }
                request = request + Server.TERMINATOR;

                outputStream = socket.getOutputStream();
                outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(request);
                outputStreamWriter.flush(); // response sent
            }


        } catch (Exception exception) {
            System.out.println(exception);
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
//                new RequestProcessor(socket);
                connectSocket(socket);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public void connectSocket(Socket socket) {
        try {
            OutputStream outputStream;
            OutputStreamWriter outputStreamWriter;
            InputStream inputStream;
            InputStreamReader inputStreamReader;
            StringBuffer stringBuffer;
            String request;
            int x;

            while (true) {
                System.out.println("Getting the stream");
                inputStream = socket.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                stringBuffer = new StringBuffer();
                while (true) {
                    x = inputStreamReader.read();
                    if (stringBuffer.toString().equals("exit") || x == -1 || x == Server.TERMINATOR) {
                        break;
                    }
                    stringBuffer.append((char) x);
                }
                request = stringBuffer.toString();
                System.out.println("Request: " + request);

                if (request.equals("exit")) {
                    System.out.println("Closing the app");
                    socket.close();
                    serverSocket.close();
                    break;
                }
                request = request + Server.TERMINATOR;

                outputStream = socket.getOutputStream();
                outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(request);
                outputStreamWriter.flush(); // response sent
            }


        } catch (Exception exception) {
            System.out.println(exception);
        }
    }

    public static void main(String[] args) {
        int portNumber;

        portNumber = 1111;

        Server server = new Server(portNumber);
        server.startListening();
    }


}
package org.example;

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
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        StringBuffer stringBuffer;
        String response;
        Scanner scanner = new Scanner(System.in);
        int x;

        while (true) {
            System.out.printf("%s: ", username);
            request = scanner.nextLine() + Server.TERMINATOR;
            outputStream = socket.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(request);
            outputStreamWriter.flush();
            inputStream = socket.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            stringBuffer = new StringBuffer();
            while (true) {
                x = inputStreamReader.read();
                if (x == Server.TERMINATOR || x == -1) break; // reads till the terminator
                stringBuffer.append((char) x);
            }
            response = stringBuffer.toString();
            System.out.println(response);
        }

// Raised in case, connection is refused or some other technical issue
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
}
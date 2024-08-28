package org.example;

import java.io.*;
import java.net.*;
import java.util.Scanner;

class Client
{
    public static void main(String data[])
    {
//data is taken as command line argument
        String ipAddress="localhost";
        int portNumber=1111;
        String request;
//”#” acts as a terminator
        try
        {
            Socket socket=new Socket(ipAddress , portNumber);
// Socket is initialized and attempt is made for connecting to the         server
// Declaring other properties and streams
            OutputStream outputStream;
            OutputStreamWriter outputStreamWriter;
            InputStream inputStream;
            InputStreamReader inputStreamReader;
            StringBuffer stringBuffer;
            String response;
            Scanner scanner = new Scanner(System.in);
            int x;
// retrieving output Stream and its writer, for sending request or acknowledgement


            while (true)
            {
                System.out.println("Write your input:");
                request = scanner.nextLine() + Server.TERMINATOR;
                outputStream=socket.getOutputStream();
                outputStreamWriter=new OutputStreamWriter(outputStream);
                outputStreamWriter.write(request);
                outputStreamWriter.flush();
                inputStream=socket.getInputStream();
                inputStreamReader=new InputStreamReader(inputStream);
                stringBuffer=new StringBuffer();
                while(true)
                {
                    x=inputStreamReader.read();
                    if(x== Server.TERMINATOR || x==-1) break; // reads till the terminator
                    stringBuffer.append((char)x);
                }
                response=stringBuffer.toString();
                System.out.println(response);
            }

        }catch(Exception exception)
        {
// Raised in case, connection is refused or some other technical issue
            System.out.println(exception);
        }
    }
}
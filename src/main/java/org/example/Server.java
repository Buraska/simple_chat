package org.example;

import java.io.*;
import java.net.*;
class RequestProcessor extends Thread
{
    private Socket socket;
    RequestProcessor(Socket socket)
    {
        this.socket=socket;
        start();
    }
    public void run()
    {
        try
        {
            OutputStream outputStream;
            OutputStreamWriter outputStreamWriter;
            InputStream inputStream;
            InputStreamReader inputStreamReader;
            StringBuffer stringBuffer;
            String request;
            int x;
            while (true)
            {
                inputStream=socket.getInputStream();
                inputStreamReader=new InputStreamReader(inputStream);
                stringBuffer=new StringBuffer();
                x=inputStreamReader.read();
                while(x!=-1 && x != '#' )
                {
                    System.out.println(stringBuffer.toString());
                    stringBuffer.append((char)x);
                    if (stringBuffer.toString().equals("exit"))
                    {
                        socket.close();
                        return;
                    }
                    x=inputStreamReader.read();
                }
                stringBuffer.append('\n');

                request=stringBuffer.toString();
                System.out.println("Request : "+request);

                outputStream=socket.getOutputStream();
                outputStreamWriter=new OutputStreamWriter(outputStream);
                outputStreamWriter.write(request);
                outputStreamWriter.flush(); // response sent
            }

        }catch(Exception exception)
        {
            System.out.println(exception);
        }
    }
}
class Server
{
    private ServerSocket serverSocket;
    private int portNumber;
    Server(int portNumber)
    {
        this.portNumber=portNumber;
        try
        {
            serverSocket=new ServerSocket(this.portNumber);
            startListening();
        }catch(Exception e)
        {
            System.out.println(e);
            System.exit(0);
        }
    }
    private void startListening()
    {
        try
        {
            Socket socket;
            while(true)
            {

                System.out.println("Server is listening on port : "+this.portNumber);
                socket=serverSocket.accept(); // server is in listening mode
                System.out.println("Request arrived..");
// diverting the request to processor with the socket reference
                new RequestProcessor(socket);
            }
        }catch(Exception e)
        {
            System.out.println(e);
        }
    }
    public static void main(String[] args)
    {
        int portNumber;

        portNumber = 1111;

        Server server=new Server(portNumber);
    }


}
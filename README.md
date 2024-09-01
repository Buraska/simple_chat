# TCP chat using Java

## Description
This project is a simple console-based chat application implemented in Java. It allows multiple clients to connect to a server via TCP connections and communicate with each other in real time.

### How to Start the Server
java -jar Server.jar [ipAddress] [port] [backlog]

backlog: The maximum number of client connections the server will queue before refusing new connections. 

### How to Start the Client
java -jar Client.jar [username] [ipAddress] [port]



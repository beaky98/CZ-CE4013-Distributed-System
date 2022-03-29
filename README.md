# CZ-CE4013-DistributedSystem

## Compile files
The application is made up of two parts, the client side application, and the server.
To compile the files, type:
```
javac ./src/Server/Server.java ./src/Client/App.java
```

## Run server
To run the server with its default parameters, type:
```
java src/Server/Server
```

Server accepts optional parameters. Check --help for more information.
```
Usage: server [-hV] [--atleastonce] [--loss=<loss_rate>] [--port=<server_port>]
Starts the server for this app.
      --atleastonce          Flag to use at-least-once invocation semantic.
  -h, --help                 Show this help message and exit.
      --loss=<loss_rate>     Simulated rate of packet loss.
      --port=<server_port>   Port number to host the server on.
  -V, --version              Print version information and exit.
```

## Run app
```
java src/Client/App
```
App accepts optional parameters. Check --help for more info.
```
Usage: app [-hV] [--resend] [--ip=<ip>] [--port=<port>] [--timeout=<timeout>]
Starts the client for this app.
  -h, --help                Show this help message and exit.
      --ip=<ip>             IP address of server to connect to.
      --noresend            Flag to not resend messages if no response received.
      --port=<port>         Port number of server to connect to.
      --timeout=<timeout>   Time in milliseconds to wait before resending messages.
  -V, --version             Print version information and exit.
```
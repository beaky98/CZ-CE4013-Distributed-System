# CZ-CE4013-DistributedSystem

## Compile files
```
javac ./src/Server/Server.java ./src/Client/App.java
```

## Run server
```
java src/Server/Server
```
Server accepts optional parameters. Check --help for more info.
```
java src/Server/Server --port=2222 --loss=0.2 --once
```

## Run app
```
java src/Client/App
```
App accepts optional parameters. Check --help for more info.
```
java src/Client/App --ip=127.0.0.1 --port=2222 --timeout=1000 --resend
```

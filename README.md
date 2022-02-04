# CZ-CE4013-DistributedSystem

## Compile files
```
javac ./src/Server.java ./src/App.java
```

## Run server
```
java src/Server
```
Server accepts optional parameters. Check --help for more info.
```
java src/Server --port=1234 --loss=0.2 --once
```

## Run app
```
java src/App
```
App accepts optional parameters. Check --help for more info.
```
java src/App --ip=127.0.0.1 --port=1234 --timeout=1000 --resend
```

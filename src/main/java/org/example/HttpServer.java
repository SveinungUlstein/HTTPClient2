package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    ServerSocket serverSocket;
    public String requestLine;
    Map<String, String> headers;
    public HttpServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        headers = new HashMap<>();
        while (true) {
            Socket clientSocket = serverSocket.accept();
            requestLine = readLine(clientSocket);

            String headerLine;
            while (!(headerLine = readLine(clientSocket)).isEmpty()) {
                String[] headerParts = headerLine.split(" *: *", 2);
                headers.put(headerParts[0], headerParts[1]);
            }

            String responseLine = "HTTP/1.1 200 OK\r\n";
            String body = "<HTML><H1> I AM A SERVER</H1><HTML>";
            String header1 = "Content-Type: text/HTML\r\n";
            String header2 = "Content-Length:" + body.getBytes().length + "\r\n";
            String spacer = "\r\n";

            clientSocket.getOutputStream().write((responseLine +
                    header1 +
                    header2 +
                    spacer +
                    body).getBytes(StandardCharsets.UTF_8));
            clientSocket.getOutputStream().close();
            clientSocket.close();
        }
    }
    private String readLine(Socket socket) throws IOException {
        StringBuilder sb = new StringBuilder();
        int character;

        while ((character = socket.getInputStream().read()) != '\r') {
            sb.append((char) character);
        }
        character = socket.getInputStream().read();
        if(character != '\n'){
            throw new IllegalStateException("Invalid http header - \\r not followed by \\n");
        }
        return sb.toString();
    }
}


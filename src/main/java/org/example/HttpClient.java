package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {

    public final Map<String, String> headers = new HashMap<>();
    public final int statusCode;
    public final String status;
    public final String body;
    public HttpClient(String host, int port, String url) {
        try (Socket socket = new Socket(host, port)){

            String request = "GET " + url +  " HTTP/1.1\r\n" + "Host: " + host + "\r\n\r\n";

            socket.getOutputStream().write(request.getBytes());

            String[] statusLine = readLine(socket.getInputStream()).split(" ");
            statusCode = Integer.parseInt(statusLine[1]);
            status = statusLine[2];

            String headerLine;
            while (!(headerLine = readLine(socket.getInputStream())).isEmpty()){
                String[] headerParts = headerLine.split(" *: *", 2);
                headers.put(headerParts[0],headerParts[1]);
            }

            StringBuilder body = new StringBuilder();
            for (int i = 0; i<getContentLength(); i++){
                body.append((char) socket.getInputStream().read());
            }

            this.body = body.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getContentLength(){
        return Integer.parseInt(headers.get("Content-Length"));
    }
    private String readLine(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        int character;

        while ((character = inputStream.read()) != '\r') {
            sb.append((char) character);
        }
        character = inputStream.read();
        if(character != '\n'){
            throw new IllegalStateException("Invalid http header - \\r not followed by \\n");
        }
        return sb.toString();
    }
}

package ru.netology;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();

        // добавление хендлеров (обработчиков)
        server.addHandler("GET", "/classic.html", new Handler() {
            @Override
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                var params = request.getQueryParams("redirect");
                var path = request.getPath();
                if(!params.isEmpty()) {
                    //path = params.get(0); // Вроде и так работает, но наверное не совсем честно
                    var newPath = params.get(0);
                    responseStream.write((
                            "HTTP/1.1 302 Found\r\n" +
                                    "Location: " + newPath + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    responseStream.flush();
                    return;
                }
                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                responseStream.write(content);
                responseStream.flush();
            }
        });

        server.listen(9999);
    }
}



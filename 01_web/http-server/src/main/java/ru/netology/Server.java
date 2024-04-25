package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server {
    private final List<String> validPaths = loadValidPaths();

    private final ExecutorService threadPool = Executors.newFixedThreadPool(64);
    private final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();

    public void addHandler(String method, String uri, Handler handler) {
        handlers.computeIfAbsent(method, keyMethod -> new HashMap<>()).put(uri, handler);
    }

    private List<String> loadValidPaths() {
        try (Stream<Path> walk = Files.walk(Paths.get("./public"))) {
            return walk
                    .filter(Files::isRegularFile)
                    .map(path -> path.toString().substring(8))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private final Handler defaultHandler = (request, responseStream) -> {
        if (!validPaths.contains(request.getPath())) {
            responseStream.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            responseStream.flush();
            return;
        }

        final var filePath = Path.of(".", "public", request.getPath());
        final var mimeType = Files.probeContentType(filePath);
        final var length = Files.size(filePath);

        responseStream.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, responseStream);
        responseStream.flush();
    };

    public void listen(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                var socket = serverSocket.accept();
                threadPool.execute(() -> handler(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handler(Socket socket) {
        try (
                socket;
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                return;
            }

            requestHandler(new Request(parts[0], parts[1]), out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestHandler(Request request, BufferedOutputStream out) throws IOException {

        Handler handler = handlers.getOrDefault(request.getMethod(), Map.of()).getOrDefault(request.getPath(), defaultHandler);

        try {
            handler.handle(request, out);
        } catch (IOException e) {
            out.write((
                    "HTTP/1.1 500 Internal Server Error\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
        }
    }
}

package cpen221.mp3.server;

import com.google.gson.Gson;
import cpen221.mp3.wikimediator.WikiMediator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class WikiMediatorServer {

    /* Representation Invariant */
    // serverSocket != null
    // executorService != null

    /* Abstract Function */
    //

    /* Thread Safety */
    // TODO

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private WikiMediator wikiMediator;

    /**
     * Start a server at a given port number, with the ability to process
     * up to n requests concurrently by making up to n threads.
     *
     * @param port         the port number to bind the server to, 9000 <= {@code port} <= 9999
     * @param n            the number of concurrent requests the server can handle, 0 < {@code n} <= 32
     * @param wikiMediator the WikiMediator instance to use for the server, {@code wikiMediator} is not {@code null}
     */
    public WikiMediatorServer(int port, int n, WikiMediator wikiMediator) {
        this.wikiMediator = wikiMediator;
        this.executorService = Executors.newFixedThreadPool(n);
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("[IOException] was thrown while constructing the server.");
        }
    }

    /**
     * Run the server, listening for connections and handling them.
     */
    public void serve() {
        while (true) {
            // block, wait, and listening to a client socket
            try {
                final Socket socket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(
                    "[IOExeption] was thrown while accepting client request.");
            }

            // TODO somehow create new threads and use handle method and then start the thread
            // TODO maybe executor service submit
        }
    }

    private void handle(Socket socket) {
        System.err.println("client connected");
        BufferedReader in;
        PrintWriter out;
        // get the socket's input stream, and wrap converters around it
        // that convert it from a byte stream to a character stream,
        // and that buffer it so that we can read a line at a time
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException("IOException was thrown while trying to read the request");
        }

        // similarly, wrap character => bytestream converter around the
        // socket output stream, and wrap a PrintWriter around that so
        // that we have more convenient ways to write Java primitive
        // types to it.
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        } catch (IOException e) {
            throw new RuntimeException("IOException was thrown while trying to write the response");
        }

        try {
            // each request is a single line containing a number
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                System.err.println("request: " + line);

                Request request = new Gson().fromJson(line, Request.class);
                Response<?> response = null;

                // TODO
                // if request.type =
                // Search(String query, int limit) :  return List<String>
                // getPage(String pageTitle) : String
                // zeitgeist(int limit) : List<String>
                // trend(int timeLimit, int maxitems) : List <String>
                // windowPeakLoad(int timewindowInSeconds) : int
                // windowPeakLoad() : int
                // important! our PrintWriter is auto-flushing, but if it were not:
                // out.flush();
                // Perform operations according to request type
                switch (request.type) {
                    case "Search":
                        Future<List<String>> resultSearch = executorService.submit(
                            () -> wikiMediator.search(request.query, request.limit));
                        response = new Response<>(request.id, resultSearch.get());
                        break;
                    case "getPage":
                        Future<String> resultGetPage =
                            executorService.submit(() -> wikiMediator.getPage(request.pageTitle));
                        break;
                    case "zeitgeist":
                        Future<List<String>> resultZeitgeist =
                            executorService.submit(() -> wikiMediator.zeitgeist(request.limit));
                        break;
                    case "trending":
                        Future<List<String>> resultTrending = executorService.submit(
                            () -> wikiMediator.trending(request.timeLimitInSeconds,
                                request.maxitems));
                        break;
                    case "windowPeakLoad":
                        if (request.timeWindowInSeconds != null) {
                            Future<Integer> resultWindowPeakLoad = executorService.submit(
                                () -> wikiMediator.windowedPeakLoad(request.timeWindowInSeconds));
                        } else {
                            Future<Integer> resultWindowPeakLoad =
                                executorService.submit(() -> wikiMediator.windowedPeakLoad());
                        }
                        break;
                    case "stop":
                        response = new Response<>(request.id, "bye");
                        out.println(new Gson().toJson(response));
                        socket.close();
                        out.close();
                        in.close();
                        break;
                }

                out.println(new Gson().toJson(response));

            }
        } catch (IOException e) {
            throw new RuntimeException("IOException while trying to read in");
        } finally {
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException("IOException while trying to read in");
            }
        }
    }

    /**
     * This Request contains the client's request to the server.
     */
    private static class Request {
        String id;
        String type;
        String query;
        String pageTitle;
        int limit;
        int timeLimitInSeconds;
        int timeWindowInSeconds;
        int maxitems;
        int timeout;
    }

    /**
     * This Response stores the information about the server's response.
     */
    private static class Response<T> {
        String id;
        String status;
        T response;

        public Response(String id, String status, T response) {
            this.id = id;
            this.status = status;
            this.response = response;
        }

        public Response(String id, T response) {
            this.id = id;
            this.response = response;
        }
    }
}

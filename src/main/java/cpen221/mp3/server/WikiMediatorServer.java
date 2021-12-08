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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class WikiMediatorServer {

    /* Representation Invariant */
    // serverSocket != null
    // executorService != null
    // WikiMediator != null
    // port != null
    // n != null

    /* Abstract Function */
    // WikiMediatorServer is a server that the client can request WikiMediator services
    //      to obtain the result from the server and if request is invalid, respond error messages.
    // WikiMediatorServer can handle multiple number of requests concurrently by creating
    //      up to 'n' threads.

    /* Thread Safety */
    // Used ExecutorService's newFixedThreadPool to set the maximum number of threads to create.
    // ExecutorService creates threads and allows us to execute tasks on threads asynchronously.

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
                try {
                    handle(socket);
                } finally {
                    socket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(
                    "[IOExeption] was thrown while accepting client request.");
            }
        }
    }

    /**
     * Read client's request in json formatted String and converts to Request object type.
     * Filter the invalid client request and if request is invalid, respond error message to the client.
     * Execute the requested task according to request type by submit to the executorService.
     * Return the result of the request.
     *
     * @param socket contains the information about client's request.
     */
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
                Boolean isTimeout = false;

                // Check client's request specified the timeout or not
                if (request.timeout != null) {
                    isTimeout = true;
                }

                // First, check if required information is not included in the request and reply error message.
                // Then, check whether the request has timeout
                // Then, perform operations according to request type
                switch (request.type) {

                    case "search":
                        if (request.query == null || request.limit == null) {
                            response = new Response<>(request.id, "fail",
                                "Invalid request format to perform search.");
                        } else {
                            Future<List<String>> resultSearch = executorService.submit(
                                () -> wikiMediator.search(request.query, request.limit));
                            if (isTimeout) {
                                try {
                                    response =
                                        new Response<>(request.id, "success", resultSearch.get(
                                            request.timeout, TimeUnit.SECONDS));
                                } catch (TimeoutException e) {
                                    System.out.println("No response after one second");
                                    resultSearch.cancel(true);
                                    response =
                                        new Response<>(request.id, "fail", "Operation timed out");
                                }
                            } else {
                                response =
                                    new Response<>(request.id, "success", resultSearch.get());
                            }
                        }
                        break;

                    case "getPage":
                        if (request.pageTitle == null) {
                            response =
                                new Response<>(request.id, "fail", "No page title in the request.");
                        } else {
                            Future<String> resultGetPage =
                                executorService.submit(
                                    () -> wikiMediator.getPage(request.pageTitle));
                            if (isTimeout) {
                                try {
                                    response =
                                        new Response<>(request.id, "success", resultGetPage.get(
                                            request.timeout, TimeUnit.SECONDS));
                                } catch (TimeoutException e) {
                                    System.out.println("No response after one second");
                                    resultGetPage.cancel(true);
                                    response =
                                        new Response<>(request.id, "fail", "Operation timed out");
                                }
                            } else {
                                response = new Response<>(request.id, "success", resultGetPage.get());
                            }
                        }
                        break;

                    case "zeitgeist":
                        if (request.limit == null) {
                            response =
                                new Response<>(request.id, "fail", "No limit for zeitgeist.");
                        } else {
                            Future<List<String>> resultZeitgeist =
                                executorService.submit(() -> wikiMediator.zeitgeist(request.limit));
                            if (isTimeout) {
                                try {
                                    response =
                                        new Response<>(request.id, "success", resultZeitgeist.get(
                                            request.timeout, TimeUnit.SECONDS));
                                } catch (TimeoutException e) {
                                    System.out.println("No response after one second");
                                    resultZeitgeist.cancel(true);
                                    response =
                                        new Response<>(request.id, "fail", "Operation timed out");
                                }
                            } else {
                                response = new Response<>(request.id, "success", resultZeitgeist.get());

                            }
                        }
                        break;

                    case "trending":
                        if (request.timeLimitInSeconds == null || request.maxitems == null) {
                            response = new Response<>(request.id, "fail",
                                "Invalid request format for trending.");
                        } else {
                            Future<List<String>> resultTrending = executorService.submit(
                                () -> wikiMediator.trending(request.timeLimitInSeconds,
                                    request.maxitems));
                            if (isTimeout) {
                                try {
                                    response =
                                        new Response<>(request.id, "success", resultTrending.get(
                                            request.timeout, TimeUnit.SECONDS));
                                } catch (TimeoutException e) {
                                    System.out.println("No response after one second");
                                    resultTrending.cancel(true);
                                    response =
                                        new Response<>(request.id, "fail", "Operation timed out");
                                }
                            } else {
                                response = new Response<>(request.id, "success", resultTrending.get());
                            }
                        }
                        break;

                    case "windowPeakLoad":
                        if (request.timeWindowInSeconds != null) {
                            Future<Integer> resultWindowPeakLoad = executorService.submit(
                                () -> wikiMediator.windowedPeakLoad(request.timeWindowInSeconds));
                            if (isTimeout) {
                                try {
                                    response =
                                        new Response<>(request.id, "success", resultWindowPeakLoad.get(
                                            request.timeout, TimeUnit.SECONDS));
                                } catch (TimeoutException e) {
                                    System.out.println("No response after one second");
                                    resultWindowPeakLoad.cancel(true);
                                    response =
                                        new Response<>(request.id, "fail", "Operation timed out");
                                }
                            } else {
                                response =
                                    new Response<>(request.id, "success", resultWindowPeakLoad.get());
                            }
                        } else {
                            Future<Integer> resultWindowPeakLoad =
                                executorService.submit(() -> wikiMediator.windowedPeakLoad());
                            if (isTimeout) {
                                try {
                                    response =
                                        new Response<>(request.id, "success", resultWindowPeakLoad.get(
                                            request.timeout, TimeUnit.SECONDS));
                                } catch (TimeoutException e) {
                                    System.out.println("No response after one second");
                                    resultWindowPeakLoad.cancel(true);
                                    response =
                                        new Response<>(request.id, "fail", "Operation timed out");
                                }
                            } else {
                                response =
                                    new Response<>(request.id, "success", resultWindowPeakLoad.get());
                            }
                        }
                        break;

                    case "stop":
                        response = new Response<>(request.id, "bye");
                        out.println(new Gson().toJson(response));
                        executorService.shutdown();
                        try {
                            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                                executorService.shutdownNow();
                            }
                        } catch (InterruptedException e) {
                            executorService.shutdownNow();
                        }
                        socket.close();
                        out.close();
                        in.close();
                        System.exit(0);
                        break;

                    default:
                        response = new Response<>(request.id, "fail",
                            "Invalid request type. Please Check your request.");
                }

                // This PrintWriter is auto-flushing, so we do not have to out.flush() manually.
                out.println(new Gson().toJson(response));
            }
        } catch (IOException e) {
            throw new RuntimeException("IOException while trying to read in");
        } catch (ExecutionException e) {
            throw new RuntimeException(
                "ExecutionException while trying to get the result from the thread.");
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        /* Representation Invariant */
        // id != null
        // type != null

        /* Abstract Function */
        // Contains information about the client's request.

        String id;
        String type;
        String query;
        String pageTitle;
        Integer limit;
        Integer timeLimitInSeconds;
        Integer timeWindowInSeconds;
        Integer maxitems;
        Integer timeout;
    }

    /**
     * This Response stores the information about the server's response.
     */
    private static class Response<T> {
        /* Representation Invariant */
        // id != null
        // status != null
        // response != null

        /* Abstract Function */
        // Stores the information about the server's respond.
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

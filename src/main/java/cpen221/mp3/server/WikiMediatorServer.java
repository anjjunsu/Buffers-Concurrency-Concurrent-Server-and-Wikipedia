package cpen221.mp3.server;

import cpen221.mp3.wikimediator.WikiMediator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    /**
     * Start a server at a given port number, with the ability to process
     * up to n requests concurrently by making up to n threads.
     *
     * @param port         the port number to bind the server to, 9000 <= {@code port} <= 9999
     * @param n            the number of concurrent requests the server can handle, 0 < {@code n} <= 32
     * @param wikiMediator the WikiMediator instance to use for the server, {@code wikiMediator} is not {@code null}
     */
    public WikiMediatorServer(int port, int n, WikiMediator wikiMediator) {
        executorService = Executors.newFixedThreadPool(n);
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


        }
    }
}

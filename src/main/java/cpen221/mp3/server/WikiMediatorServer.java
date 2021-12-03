package cpen221.mp3.server;

import cpen221.mp3.wikimediator.WikiMediator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class WikiMediatorServer {

    /* Representation Invariant */
    // serverSocket != null

    /* Abstract Function */
    //

    /* Thread Safety */
    //

    private ServerSocket serverSocket;

    /**
     * Start a server at a given port number, with the ability to process
     * upto n requests concurrently.
     *
     * @param port         the port number to bind the server to, 9000 <= {@code port} <= 9999
     * @param n            the number of concurrent requests the server can handle, 0 < {@code n} <= 32
     * @param wikiMediator the WikiMediator instance to use for the server,
     *                     {@code wikiMediator} is not {@code null}
     */
    public WikiMediatorServer(int port, int n, WikiMediator wikiMediator) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    /**
     * Run the server, listening for connections and handling them.
     *
     * @throws IOException if the main server socket is broken
     */
    public void serve() throws IOException {
        while (true) {
            // block until a client connects
            Socket socket = serverSocket.accept();

            // TODO: need to find a way to create a thread so that can handle the client
            try {
                handle(socket);
            } catch (IOException ioe) {
                ioe.printStackTrace();  // but don't terminate serve()
            } finally {
                socket.close();
            }
        }
    }

    /**
     * Handle one client connection. Return when client disconnects.
     *
     * @param socket socket where client is connected
     * @throws IOException if connection encounters an error
     */
    private void handle(Socket socket) throws IOException {
        System.err.println("client connected");

        // get the socket's input stream, and wrap converters around it
        // that convert it from a byte stream to a character stream,
        // and that buffer it so that we can read a line at a time
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // similarly, wrap character => bytestream converter around the
        // socket output stream, and wrap a PrintWriter around that so
        // that we have more convenient ways to write Java primitive
        // types to it.
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));


        // TODO : implement what server needs to do
        // Maybe use something like case () in Verilog

    }

    // TODO : Learn how exactly gson thing works

    /**
     *
     */
    private class Request {
        String id;
        String type;
        String query;
        String getPage;
        int limit;
        int timeout;
    }

    /**
     *
     * @param <T>
     */
    private class Response<T> {
        String id;
        String status;
        T response;

        public Response(String id, String status, T response) {
            this.id = id;
            this.status = status;
            this.response = response;
        }

        public Response (String id, T response) {
            this.id = id;
            this.response = response;
        }
    }
}

package cpen221.mp3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * TesterClient is a client that sends requests to the WikiMediatorServer
 * and interprets its replies.
 * A new TesterClient is "open" until the close() method is called,
 * at which point it is "closed" and may not be used further.
 */
public class TesterClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    /* Representation Invariant*/
    // socket, in, out != null


    /**
     * Make a TesterClient and connect it to a server running on
     * hostname at the specified port.
     *
     * @throws IOException if can't connect
     */
    public TesterClient(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Send a request to the server. Requires this is "open".
     *
     * @param json to json formatted String which is the request to the server
     * @throws IOException if network or server failure
     */
    public void sendRequest(String json) throws IOException {
        out.print(json + "\n");
        out.flush(); // important! make sure x actually gets sent
    }

    /**
     * Get a reply from the next request that was submitted.
     * Requires this is "open".
     *
     * @return the requested Fibonacci number
     * @throws IOException if network or server failure
     */
    public String getReply() throws IOException {
        String reply = in.readLine();
        if (reply == null) {
            throw new IOException("connection terminated unexpectedly");
        }

        try {
            return reply;
        } catch (NumberFormatException nfe) {
            throw new IOException("misformatted reply: " + reply);
        }
    }

    /**
     * Closes the client's connection to the server.
     * This client is now "closed". Requires this is "open".
     *
     * @throws IOException if close fails
     */
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}

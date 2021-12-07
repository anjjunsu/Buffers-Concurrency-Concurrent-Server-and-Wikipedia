package cpen221.mp3;

import com.google.gson.Gson;
import cpen221.mp3.server.WikiMediatorServer;
import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.Before;
import org.junit.Test;

public class Task4_Test_ServerRun {
    /** Default port number where the server listens for connections. */
    public static final int TEST4_PORT = 9012;
    public static final int CAPACITY = 24;
    public static final int STALENESS_INTERVAL = 120;

    // This main class launches the WikiMediator server.
    public static void main(String[] args) {
        int numClients = 10;
        WikiMediator wm = new WikiMediator(CAPACITY, STALENESS_INTERVAL);
        WikiMediatorServer wms = new WikiMediatorServer(TEST4_PORT, numClients, wm);
        wms.serve();
    }
}

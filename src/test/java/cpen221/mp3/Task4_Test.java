package cpen221.mp3;

import com.google.gson.Gson;
import cpen221.mp3.server.WikiMediatorServer;
import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.Before;
import org.junit.Test;

public class Task4_Test {

    // Run the server
    @Before
    public void basic() {
        int capacity = 24;
        int stalenessInterval = 120;
        int port = 9012;
        int numClients = 10;
        WikiMediator wm = new WikiMediator(capacity, stalenessInterval);
        WikiMediatorServer wms = new WikiMediatorServer(port, numClients, wm);
        wms.serve();
    }


//    // Remove this main class
//    public static void main(String[] args) {
//        String testJson = "{'id':'one','type':'search','maxitems':35}";
//
//        Request req = new Gson().fromJson(testJson, Request.class);
//
//        System.out.println();
//    }
//
//
//    // Remove this Request class
//    private static class Request {
//        String id;
//        String type;
//        String query;
//        String pageTitle;
//        int limit;
//        int maxitems;
//        int timeout;
//    }
}

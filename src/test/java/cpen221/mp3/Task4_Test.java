package cpen221.mp3;

import com.google.gson.Gson;
import cpen221.mp3.server.WikiMediatorServer;
import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class Task4_Test {
    /**
     * Must launch the server first before using this test
     * Can launch server in Task4_Test_ServerRun and run the main method
     */
    /** Default port number where the server listens for connections. */
    public static final int TEST4_PORT = 9012;
    public static final int CAPACITY = 24;
    public static final int STALENESS_INTERVAL = 120;
    public static final int ONE_SEC = 1000;
    public static final String HOST_NAME = "127.0.0.1";

    // This main class launches the WikiMediator server.
    public static void main(String[] args) {
        int numClients = 10;
        WikiMediator wm = new WikiMediator(CAPACITY, STALENESS_INTERVAL);
        WikiMediatorServer wms = new WikiMediatorServer(TEST4_PORT, numClients, wm);
        wms.serve();

        // Sleep for half second to ensure server is launched before the actual testing
        try {
            Thread.sleep(ONE_SEC / 2);
        } catch (InterruptedException e) {
            System.out.println("[Task4 Test] InterruptedException was thrown while sleeping.");
        }
    }

    // Test WikiMediator search request
    @Test
    public void testSearch() {
        String req = "{'id':'1','type':'search','query':'Barack Obama','limit':'12'}";
        String response = null;
        WikiMediator reference = new WikiMediator(CAPACITY, STALENESS_INTERVAL);

        List<String> refereceSearchResult = reference.search("Barack Obama", 12);

        try {
            TesterClient client = new TesterClient(HOST_NAME, TEST4_PORT);

            // Send request to the server.
            System.out.println("**testSearch Begin**");
            client.sendRequest(req);
            System.out.println("Request to Sever : " + req);

            // Receive response from the server.
            response = client.getReply();
            System.out.println("Response from Server : " + response);

            client.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Response<List<String>> responseFromServer = new Gson().fromJson(response, Response.class);

        // Don't touch. Only look with your eye.
        System.out.println("Result without using server : ");
        System.out.println(refereceSearchResult);
        System.out.println("=========================================");
        System.out.println("Result using server : ");
        System.out.println(responseFromServer);
        System.out.println("**testSearch End**");
    }

    // Test stop request from the client properly shutdowns the server
    @Test
    public void testStop() {
        String requestToServer = "{'id':'1','type':'stop'}";
        String response = null;

        try {
            TesterClient client = new TesterClient(HOST_NAME, TEST4_PORT);

            // Send request to the server.
            System.out.println("**testStop Begin**");
            client.sendRequest(requestToServer);
            System.out.println("Request to Sever : " + requestToServer);

            // Receive response from the server.
            response = client.getReply();
            System.out.println("Response from Server : " + response);

            client.close();
        } catch (IOException e) {
            fail("Unexpected IOException while constructing a client");
        }

        Response<String> responseFromServer = new Gson().fromJson(response, Response.class);

        Assert.assertEquals("bye", responseFromServer.response);
    }

    // Test invalid request type properly handled by the server
    @Test
    public void testInvalidRequest() {
        String requestToServer = "{'id':'Junsu','type':'Hello. World'}";
        String response = null;

        try {
            TesterClient client = new TesterClient(HOST_NAME, TEST4_PORT);

            // Send request to the server.
            System.out.println("**testInvalidRequest Begin**");
            client.sendRequest(requestToServer);
            System.out.println("Request to Sever : " + requestToServer);

            // Receive response from the server.
            response = client.getReply();
            System.out.println("Response from Server : " + response);

            client.close();
        } catch (IOException e) {
            fail("Unexpected IOException while constructing a client");
        }

        Response<String> responseFromServer = new Gson().fromJson(response, Response.class);

        Assert.assertEquals("fail", responseFromServer.status);
    }


    static class Response<T> {
        String id;
        String status;
        T response;
    }
}

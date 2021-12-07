package cpen221.mp3;

import com.google.gson.Gson;
import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class Task4_Test_Client {
    /**
     * Must launch the server first before using this test
     * Can launch server in Task4_Test_ServerRun and run the main method
     */
    @Test
    public void testSearch() {
        String req = "{'id':'1','type':'search','query':'Barack Obama','limit':'12'}";
        String response = null;
        WikiMediator reference = new WikiMediator(Task4_Test_ServerRun.CAPACITY, Task4_Test_ServerRun.STALENESS_INTERVAL);

        List<String> refereceSearchResult = reference.search("Barack Obama", 12);

        try {
            TesterClient client = new TesterClient("127.0.0.1",
                Task4_Test_ServerRun.TEST4_PORT);

            // Send request to the server.
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
    }

    static class Response<T> {
        String id;
        String status;
        T response;
    }
}

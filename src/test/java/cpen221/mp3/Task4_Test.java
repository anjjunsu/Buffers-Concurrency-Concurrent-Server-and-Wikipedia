package cpen221.mp3;

import com.google.gson.Gson;

public class Task4_Test {

    // Remove this main class
    public static void main(String[] args) {
        String testJson = "{'id':'one','type':'search','maxitems':35}";

        Request req = new Gson().fromJson(testJson, Request.class);

        System.out.println();
    }


    // Remove this Request class
    private static class Request {
        String id;
        String type;
        String query;
        String pageTitle;
        int limit;
        int maxitems;
        int timeout;
    }
}

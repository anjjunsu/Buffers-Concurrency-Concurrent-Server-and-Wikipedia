package cpen221.mp3;

import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class Task3_Test {
    public static final int FIVE_CAPACITY = 5;
    public static final int TEN_CAPACITY = 10;
    public static final int FIVE_SEC_TO_LIVE = 5;
    public static final int TEN_SEC_TO_LIVE = 10;
    public static final int ONE_SEC = 1000;

    @Test
    public void testSearch() {
        WikiMediator wikime = new WikiMediator(FIVE_CAPACITY, FIVE_SEC_TO_LIVE);
        System.out.println(wikime.search("Barack Obama", 5));
        System.out.println(wikime.search("Hello", 10));
    }

    @Test
    public void testGetPageAndZeitgeist() {
        WikiMediator wikime = new WikiMediator(FIVE_CAPACITY, FIVE_SEC_TO_LIVE);
        System.out.println(wikime.getPage("Hello"));
        wikime.getPage("Barack Obama");
        System.out.println(wikime.getPage("Barack Obama"));
        wikime.getPage("Hello");
        wikime.getPage("Hello");
        System.out.println(wikime.getPage("abc"));
        wikime.getPage("abc");
        wikime.getPage("abc");
        wikime.getPage("abc");

        List<String> zeitgeistList = new ArrayList<>();
        zeitgeistList.add("abc");
        zeitgeistList.add("Hello");
        zeitgeistList.add("Barack Obama");


        Assert.assertEquals(zeitgeistList, wikime.zeitgeist(3));

    }

}
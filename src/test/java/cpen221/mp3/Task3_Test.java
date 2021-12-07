package cpen221.mp3;

import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

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
    public void testSearchAndZeitgeist() {
        WikiMediator wikime = new WikiMediator(FIVE_CAPACITY, TEN_SEC_TO_LIVE);

        wikime.search("Computer Engineering", 2);
        wikime.search("Computer Engineering", 3);
        System.out.println(wikime.search("Computer Engineering", 4));

        wikime.search("Verilog", 1);
        wikime.search("Verilog", 2);
        System.out.println(wikime.search("Verilog", 3));
        wikime.search("Verilog", 5);

        List<String> zeitgeistList = new ArrayList<>();
        zeitgeistList.add("Verilog");
        zeitgeistList.add("Computer Engineering");

        Assert.assertEquals(zeitgeistList, wikime.zeitgeist(5));


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

    @Test
    public void testGetPageSearchZeitgeist() {
        WikiMediator wikime = new WikiMediator(FIVE_CAPACITY, FIVE_SEC_TO_LIVE);

        wikime.search("CPU", 1);
        wikime.getPage("CPU");
        wikime.getPage("CPU");


        wikime.search("UBC", 2);
        wikime.search("UBC", 1);

        List<String> zeitgeistList = new ArrayList<>();
        zeitgeistList.add("CPU");
        zeitgeistList.add("UBC");

        Assert.assertEquals(zeitgeistList, wikime.zeitgeist(5));

    }

    @Test
    public void testTrendingUsingGetPage1() throws InterruptedException {
        WikiMediator wikime = new WikiMediator(FIVE_CAPACITY, TEN_SEC_TO_LIVE);
        wikime.getPage("Hello");
        wikime.getPage("Hello");

        // sleep for three seconds
        TimeUnit.SECONDS.sleep(4);

        wikime.getPage("abc");
        wikime.getPage("abc");
        wikime.getPage("abc");
        wikime.getPage("abc");

        wikime.getPage("Hello");
        wikime.getPage("Barack Obama");
        wikime.getPage("Barack Obama");

        List<String> trendingList = new ArrayList<>();
        trendingList.add("abc");
        trendingList.add("Barack Obama");
        trendingList.add("Hello");

        Assert.assertEquals(trendingList, wikime.trending(3, 5));
    }

    // add more tests regarding trending


    @Test
    public void testWindowPeakLoad() throws InterruptedException {
        WikiMediator wikime = new WikiMediator(TEN_CAPACITY, TEN_SEC_TO_LIVE);
        wikime.getPage("YMCA");
        wikime.search("YMCA", 2);

        TimeUnit.SECONDS.sleep(3);

        wikime.getPage("UBC");
        wikime.getPage("UBC");
        wikime.getPage("UBC");

        TimeUnit.SECONDS.sleep(3);

        Assert.assertEquals(3, wikime.windowedPeakLoad(2));
    }
}

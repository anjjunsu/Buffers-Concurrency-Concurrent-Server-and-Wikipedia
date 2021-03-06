package cpen221.mp3;

import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Task3_Test {
    public static final int FIVE_CAPACITY = 5;
    public static final int TEN_CAPACITY = 10;
    public static final int FIVE_SEC_TO_LIVE = 5;
    public static final int TEN_SEC_TO_LIVE = 10;
    public static final int FIFTY_SEC_TO_LIVE = 50;
    public static final int FIFTY_CAPACITY = 50;

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
    public void testTrendingUsingGetPage() throws InterruptedException {
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
    public void testTrendingUsingSearch() throws InterruptedException {
        WikiMediator wikime = new WikiMediator(FIVE_CAPACITY, TEN_SEC_TO_LIVE);
        wikime.search("Hello", 2);
        wikime.search("Hello", 3);

        // sleep for three seconds
        TimeUnit.SECONDS.sleep(4);

        wikime.search("abc", 5);
        wikime.search("abc", 4);
        wikime.search("abc", 1);
        wikime.search("abc", 2);

        wikime.search("Hello", 2);
        wikime.search("Barack Obama", 3);
        wikime.search("Barack Obama", 1);

        List<String> trendingList = new ArrayList<>();
        trendingList.add("abc");
        trendingList.add("Barack Obama");
        trendingList.add("Hello");

        Assert.assertEquals(trendingList, wikime.trending(3, 5));
    }

    @Test
    public void testTrendingUsingGetPageSearch() throws InterruptedException {
        WikiMediator wikime = new WikiMediator(FIVE_CAPACITY, TEN_SEC_TO_LIVE);
        wikime.search("Hello", 1);
        wikime.getPage("Hello");

        // sleep for three seconds
        TimeUnit.SECONDS.sleep(4);

        wikime.search("abc", 5);
        wikime.getPage("abc");
        wikime.search("abc", 1);
        wikime.search("abc", 2);

        wikime.getPage("Hello");
        wikime.getPage("Barack Obama");
        wikime.search("Barack Obama", 1);

        List<String> trendingList = new ArrayList<>();
        trendingList.add("abc");
        trendingList.add("Barack Obama");
        trendingList.add("Hello");

        Assert.assertEquals(trendingList, wikime.trending(3, 5));
    }


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

    @Test
    public void testWindowPeakLoadOerloaded1() throws InterruptedException {
        WikiMediator wikime = new WikiMediator(FIFTY_CAPACITY, FIFTY_SEC_TO_LIVE);
        wikime.getPage("YMCA");
        wikime.search("YMCA", 3);

        TimeUnit.SECONDS.sleep(1);

        wikime.getPage("UBC");
        wikime.search("UBC", 1);
        wikime.getPage("UBC");
        wikime.search("UBC", 2);

        TimeUnit.SECONDS.sleep(1);

        wikime.getPage("Electrical Engineering");
        wikime.search("Electrical Engineering", 4);

        TimeUnit.SECONDS.sleep(20);

        wikime.getPage("Computer Engineering");
        wikime.getPage("Computer Engineering");
        wikime.getPage("Computer Engineering");
        wikime.getPage("Computer Engineering");
        wikime.getPage("Computer Engineering");
        wikime.getPage("Computer Engineering");

        TimeUnit.SECONDS.sleep(5);

        wikime.search("Computer Engineering", 2);
        wikime.search("Java", 1);
        wikime.search("Intellij", 2);
        wikime.getPage("Intellij");

        Assert.assertEquals(8, wikime.windowedPeakLoad(3));

    }

    @Test
    public void testWindowPeakLoadOerloaded2() throws InterruptedException {
        WikiMediator wikime = new WikiMediator(FIFTY_CAPACITY, FIFTY_SEC_TO_LIVE);
        wikime.getPage("YMCA");
        wikime.search("YMCA", 3);

        TimeUnit.SECONDS.sleep(1);

        wikime.getPage("UBC");
        wikime.search("UBC", 1);
        wikime.getPage("UBC");
        wikime.search("UBC", 2);

        TimeUnit.SECONDS.sleep(1);

        wikime.getPage("Electrical Engineering");
        wikime.search("Electrical Engineering", 4);

        TimeUnit.SECONDS.sleep(20);

        wikime.getPage("Computer Engineering");
        wikime.getPage("Computer Engineering");
        wikime.getPage("Computer Engineering");
        wikime.getPage("Computer Engineering");
        wikime.getPage("Computer Engineering");
        wikime.getPage("Computer Engineering");

        TimeUnit.SECONDS.sleep(5);

        wikime.search("Computer Engineering", 2);
        wikime.search("Java", 1);
        wikime.search("Intellij", 2);
        wikime.getPage("Intellij");

        Assert.assertEquals(6, wikime.windowedPeakLoad(1));

    }

    @Test
    public void testGetPageCache() {
        WikiMediator wikime = new WikiMediator(TEN_CAPACITY, TEN_SEC_TO_LIVE);
        System.out.println(wikime.getPage("UBC"));
        System.out.println(wikime.getPage("UBC"));
    }

    @Test
    public void testZeigeistMapWithoutPriorData() {
        WikiMediator wikime = new WikiMediator(TEN_CAPACITY, TEN_SEC_TO_LIVE);
        List<String> zeitgeistList = new ArrayList<>();
        zeitgeistList.add("CPU");
        zeitgeistList.add("UBC");

        Assert.assertEquals(zeitgeistList, wikime.zeitgeist(5));

    }

}

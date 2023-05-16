package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RainforestShopTest {
    public static void main(String[] args) {
        RainforestShopTest rainforestShopTest = new RainforestShopTest();
        System.out.println("Test create Rainforest Shop");
        rainforestShopTest.testCreateRainforestShop();
    }
    private void testCreateRainforestShop(){

        List<String> usernames = new ArrayList<>();
        usernames.add("user1");
        usernames.add("user2");
        usernames.add("user3");
        HashMap<String, Pair<Double, Integer>> availableProducts = new HashMap<>();

        availableProducts.put("apple", new Pair<>(2.2, 4));
        availableProducts.put("peach", new Pair<>(2.1, 6));
        availableProducts.put("mangosteen", new Pair<>(3.5, 60));

        //test normal case
        RainforestShop normalRainforestShop = new RainforestShop(usernames, availableProducts, true);
        Assertions.assertNotNull(normalRainforestShop);

        //test exception cases
        //test usernames are null
        try {
            RainforestShop testRainforestShop = new RainforestShop(null, availableProducts, true);
            Assertions.assertNotNull(testRainforestShop);
        } catch (Throwable t) {
            Assertions.assertExpectedThrowable(NullPointerException.class, t);
        }

        //test available products are null
        try {
            RainforestShop testRainforestShop = new RainforestShop(usernames, null, true);
            Assertions.assertNotNull(testRainforestShop);
        } catch (Throwable t) {
            Assertions.assertExpectedThrowable(NullPointerException.class, t);
        }

        //test globallock false
        try {
            RainforestShop testRainforestShop = new RainforestShop(usernames, availableProducts, false);
            Assertions.assertNotNull(testRainforestShop);
        } catch (Throwable t) {
            Assertions.assertExpectedThrowable(NullPointerException.class, t);
        }
    }
}



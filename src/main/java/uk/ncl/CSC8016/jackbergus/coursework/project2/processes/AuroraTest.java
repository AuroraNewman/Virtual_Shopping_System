package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;
import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.UniqueProductIdGenerator;
import uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler.Pair;

import javax.swing.text.html.Option;
import java.math.BigInteger;
import java.util.*;

import static uk.ncl.CSC8016.jackbergus.coursework.project2.processes.Assertions.assertFalse;
import static uk.ncl.CSC8016.jackbergus.coursework.project2.processes.Assertions.assertTrue;

public class AuroraTest {

    public static void main(String[] args) throws InterruptedException {
        List<String> usernames = new ArrayList<>();
        usernames.add("user1");
        usernames.add("user2");
        usernames.add("user3");

        Collection<String> clientIDs = new ArrayList<>();
        clientIDs.add("123");
        clientIDs.add("456");
        clientIDs.add("789");

        HashMap<String, Pair<Double, Integer>> availableProducts = new HashMap<>();

        availableProducts.put("apple", new Pair<>(2.2, 4));
        availableProducts.put("peach", new Pair<>(2.1, 6));
        availableProducts.put("mangosteen", new Pair<>(3.5, 60));

        ProductMonitor productMonitor = new ProductMonitor();
        BigInteger biggie = UniqueProductIdGenerator.nextProductId();
        Item item1 = new Item("apple", 2.2, biggie);
        Item item2 = new Item("peach", 1.8, biggie);
        Item item3 = new Item("boost", 4.9, biggie);
        Item item4 = new Item("mangosteen", 3.5, biggie);
        productMonitor.addAvailableProduct(item1);
        productMonitor.addAvailableProduct(item2);
        productMonitor.addAvailableProduct(item3);


        RainforestShop rainforestShop = new RainforestShop(usernames, availableProducts, true);
        UUID myUUID = UUID.randomUUID();
        Transaction newTrans = new Transaction(rainforestShop, "user1", myUUID);
        List<String> avPr = rainforestShop.getAvailableItems(newTrans);
        for (String availPr : avPr) System.out.println(availPr);

        //test login function
        var t1 = new Thread(() -> {
            Optional<Transaction> transactionOptional = rainforestShop.login("user1");
            if (transactionOptional.isPresent()) {
                Transaction newTransaction = transactionOptional.get();
                //test logout function
                rainforestShop.logout(newTransaction);
            }

        });
        var t2 = new Thread(() -> {
            Optional<Transaction> transactionOptional = rainforestShop.login("user2");
            if (transactionOptional.isPresent()) {
                Transaction newTransaction = transactionOptional.get();

                //test getAvailableItems()
                List<String> ls = newTransaction.getAvailableItems();
                for (String productName : ls) System.out.println(productName);

            }
        }
        );
        var t3 = new Thread(() -> {
            System.out.println("multiple logins from same user");
            Optional<Transaction> transactionOptional = rainforestShop.login("user2");
            if (transactionOptional.isPresent()) {
                Transaction newTransaction = transactionOptional.get();

                //test basketProductByName()
                System.out.println(rainforestShop.basketProductByName(newTransaction, item4.productName));
                //test basketing too many products
                for (int j = 5; j > 0; j--) {
                    System.out.println(rainforestShop.basketProductByName(newTransaction, item1.productName));
                }

                //test shelfProduct(), expect true
                //TODO: figure out why this isn't returning properly
                assertTrue(rainforestShop.shelfProduct(newTransaction, item4));
                //test shelfProduct(), expect false
               assertFalse(rainforestShop.shelfProduct(newTransaction, item2));

            }
        }
        );

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }
}

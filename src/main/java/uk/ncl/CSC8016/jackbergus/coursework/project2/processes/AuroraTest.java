package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;
import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.UniqueProductIdGenerator;
import uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler.Pair;

import javax.swing.text.html.Option;
import java.math.BigInteger;
import java.util.*;

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
        productMonitor.addAvailableProduct(item1);
        productMonitor.addAvailableProduct(item2);
        productMonitor.addAvailableProduct(item3);

        /*
        NOTE THE ADDALL DOESN'T SEEM TO BE WORKING
         */

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
                /*
                System.out.println("test getAvailableItems");
                List<String> ls = newTransaction.getAvailableItems();
                for (String availableProd : ls) {
                    System.out.println(availableProd);
                }

                 */
                /*
                try {
                    List<String> availableItems = rainforestShop.getAvailableItems(newTransaction);
                } catch (UnsupportedOperationException e) {
                    e.printStackTrace();
                    rainforestShop.logout(newTransaction);
                }
                //for (String availableItem : availableItems) System.out.println(availableItem);
                System.out.println("test new login, new user");

                 */
            }
        }
        );
        var t3 = new Thread(() -> {
            System.out.println("multiple logins from same user");
            Optional<Transaction> transactionOptional = rainforestShop.login("user2");
            if (transactionOptional.isPresent()) {
                Transaction newTransaction = transactionOptional.get();

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

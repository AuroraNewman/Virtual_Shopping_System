package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.BasketResult;
import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.UtilityMethods;

import java.util.Random;

/**
 * in client lifecycle, client may be using web app online or mobile app in shop
 * process:
 * * client logs in using login method
 * * * only valid users may log in
 * * * which creates a unique transaction ID
 * * * client may log in using same ID multiple times, but each should be new ID
 * * add items to basket
 * * * only as many items as are available
 * * remove items from basket
 * * checkout
 * * * check the items are available here
 * * * * if unavailable, tell the seller to order that many more
 * * * either all or no items are bought
 * * log out
 * * * lose all info on items in basket
 * * * reshelve the items
 */

public class ClientLifecycle implements Runnable {
    private final String username;
    //shop instance
    private final RainforestShop s;
    //number items client desires
    private final int items_to_pick_up;
    private final double total_available_money, shelfing_prob;
    //a random number used to return a random item from list
    private final Random rng;
    //while this is false, it is not time to checkout
    private boolean doCheckOut = true;
    //l is the BasketResult, which seems to be the client's individual basket
    BasketResult l;

    public ClientLifecycle(String username,
                           RainforestShop s,
                           int items_to_pick_up,
                           double shelfing_prob,
                           double total_available_money,
                           int rng_seed) {
        this.username = username;
        this.s = s;
        this.items_to_pick_up = items_to_pick_up;
        this.total_available_money = total_available_money;
        this.shelfing_prob = Double.min(1.0, Double.max(0.0, shelfing_prob));
        rng = new Random(rng_seed);
    }
    //TODO: figure this heffa out
    //creates a new flag about doCheckOut; this thread can be used elsewhere

    public Thread thread(boolean doCheckOut) {
        this.doCheckOut = doCheckOut;
        return new Thread(this);
    }

    public BasketResult getBasketResult() {
        return l;
    }

    public BasketResult startJoinAndGetResult(boolean doCheckOut) throws InterruptedException {
        this.doCheckOut = doCheckOut;
        Thread t = new Thread(this);
        t.start(); t.join();
        return l;
    }

    @Override
    public void run() {
        l = null;
        double nextAfter = Math.nextUp(1.0);
        if (s != null) {
            s.login(username).ifPresent(transaction -> {
                for (int i = 0; i< items_to_pick_up; i++) {
                    var obj = UtilityMethods.getRandomElement(s.getAvailableItems(transaction), rng);
                    if (obj == null) break;
                    if (transaction.basketProduct(obj)) {
                        if (rng.nextDouble(0.0, nextAfter) < shelfing_prob) {
                            if (!transaction.shelfProduct(UtilityMethods.getRandomElement(transaction.getUnmutableBasket(), rng)))
                                throw new RuntimeException("ERROR: I musth be able to shelf a product that I added!");
                        }
                    }
                }
                if (doCheckOut)
                    l = transaction.basketCheckout(total_available_money);
                else
                    l = null;
                transaction.logout();
            });
        }
    }
}

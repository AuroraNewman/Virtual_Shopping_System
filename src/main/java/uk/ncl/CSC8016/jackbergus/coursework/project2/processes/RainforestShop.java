package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.BasketResult;
import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;
import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.MyUUID;
import uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler.Pair;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class RainforestShop {

    /// For correctly implementing the server, please consider that

    private final boolean isGlobalLock;
    private volatile boolean supplierStopped;
    private Set<String> allowed_clients;
    public HashMap<UUID, String> UUID_to_user;
    private volatile HashMap<String, ProductMonitor> available_withdrawn_products;
    private HashMap<String, Double> productWithCost = new HashMap<>();
    private volatile Queue<String> currentEmptyItem;
    //create Object lock to use in synchronized code block
    private Object lock1 = new Object();


    public boolean isGlobalLock() {
        return isGlobalLock;
    }

    /**
     * Please replace this string with your student ID, so to ease the marking process
     * @return  Your student id!
     */
    public String studentId() {
        return "220310897";
    }
    /**
     *
     * @param client_ids                Collection of registered client names that can set up the communication
     * @param available_products        Map associating each product name to its cost and the initial number of available items on the shop
     * @param isGlobalLock              Might be used (but not strictly required) To remark whether your solution uses a
     *                                  pessimistic transaction (isGlobalLock=true) or an optimistic opne (isGlobalLock=false)
     */
    public RainforestShop(Collection<String> client_ids,
                          Map<String, Pair<Double, Integer>> available_products,
                          boolean isGlobalLock) {
        //supplierStopped shows the supplier is not supplying new items
        supplierStopped = true;
        //used to store items that have been withdrawn from the shop but are currently out of stock
        currentEmptyItem = new LinkedBlockingQueue<>();
        //pessimistic/optimistic transaction
        this.isGlobalLock = isGlobalLock;
        //set of IDS of allowed clients
        //if this is null, all clients are allowed
        allowed_clients = new HashSet<>();
        if (client_ids != null) allowed_clients.addAll(client_ids);
        //used to store the inventory of the shop
        this.available_withdrawn_products = new HashMap<>();
        //used to map UUIDs (unique identifiers) to client IDs
        UUID_to_user = new HashMap<>();
        if (available_products != null) for (var x : available_products.entrySet()) {
            if (x.getKey().equals("@stop!")) continue;
            productWithCost.put(x.getKey(), x.getValue().key);
            var p = new ProductMonitor();
            for (int i = 0; i<x.getValue().value; i++) {
                p.addAvailableProduct(new Item(x.getKey(), x.getValue().key, MyUUID.next()));
            }
            this.available_withdrawn_products.put(x.getKey(), p);
        }
    }

    /**
     * Performing an user log-in. To generate a transaction ID, please use the customary Java method     *
     * UUID uuid = UUID.randomUUID();     *
     * @param username      Username that wants to login     *
     * @return A non-empty transaction if the user is logged in for the first time, and he hasn't other instances of itself running at the same time
     *         In all the other cases, thus including the ones where the user is not registered, this returns an empty transaction     *
     */
    public Optional<Transaction> login(String username) {
        Optional<Transaction> result = Optional.empty();
        if (allowed_clients.contains(username)) {
            synchronized (lock1) {
                try {
                    UUID uuid = UUID.randomUUID();
                    UUID_to_user.put(uuid, username);
                    result = Optional.of(new Transaction(this, username, uuid));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * This method should be accessible only to the transaction and not to the public!
     * Logs out the client if and only if there was a transaction that was started with a given UUID and that was associated to
     * a given user
     *
     * @param transaction
     * @return false if the transaction is null or whether that was not created by the system
     */
    boolean logout(Transaction transaction) {
        boolean result = false;
        // TO DO: Implement the remaining part!
        if (transaction.getUnmutableBasket() != null) {
            for (Item productInBasket : transaction.getUnmutableBasket()){
                for (Map.Entry<String, ProductMonitor> entry : available_withdrawn_products.entrySet()) {
                    if (entry.getKey().equals(productInBasket.productName)) {
                        entry.getValue().doShelf(productInBasket);
                    }
                }
            }
        }
        transaction.invalidateTransaction();
        result = true;
        return result;
    }

    /**
     * Lists all of the items that were not basketed and that are still on the shelf
     * @param transaction
     * @return
     */

    List<String> getAvailableItems(Transaction transaction) {
        //TODO: ASK DR. BERGAMI IF I'M SUPPOSED TO BE RETURNING A NEW LIST
        List<String> ls = Collections.emptyList();
        // TO DO: Implement the remaining part!
        if (transaction.getSelf() == null || (transaction.getUuid() == null)) return ls;
        //1. create new list instead of immutable empty list
        List<String> availableItems = new ArrayList<>();
        //2. extract product monitors from the AWP list
        for (ProductMonitor productMonitor : available_withdrawn_products.values()) {
        //3. add all the items to availableItems
            availableItems.addAll(productMonitor.getAvailableItems());
        }
        return availableItems;
    }

    /**
     * If a product can be basketed from the shelf, then a specific instance of the product on the shelf is returned
     * this is done item by item
     *
     * @param transaction   User reference
     * @param name          Product name picked from the shelf
     * @return  Whether the item to be basketed is available or not
     */
    Optional<Item> basketProductByName(Transaction transaction, String name) {
        AtomicReference<Optional<Item>> result = new AtomicReference<>(Optional.empty());
        //if the transaction's rainforest shop object or the uuid is null, return the empty result
        if (transaction.getSelf() == null || (transaction.getUuid() == null)) return result.get();
        // TO DO: Implement the remaining part!
        //create a temporary product monitor
        //productmonitor is list of available and withdrawn items
        //if the product monitor is not null, then we can proceed
        // remove the item from available, add the item to withdrawn (this is done in one step by getavailableitem() )
        //return the resulting optional item

        ProductMonitor productMonitor = available_withdrawn_products.get(name);
        if (productMonitor != null) {
            result.set(productMonitor.getAvailableItem());
        }
        return result.get();
    }

    /**
     * If the current transaction has withdrawn one of the objects from the shelf and put it inside its basket,
     * then the transaction shall be also able to replace the object back where it was (on its shelf)
     * @param transaction   Transaction that basketed the object
     * @param object        Object to be reshelved
     * @return  Returns true if the object existed before and if that was basketed by the current thread, returns false otherwise
     */
    // 6. if successful, change result to true and return
    boolean shelfProduct(Transaction transaction, Item object) {
        boolean result = false;
        if (transaction.getSelf() == null || (transaction.getUuid() == null)) return result;
        // TO DO: Implement the remaining part!
        // 1. check the object is not null
        if (object == null) return result;
        // 2. check to make sure that the object is in the basket (if not, return false)
        if (!transaction.getUnmutableBasket().contains(object)) return result;
        // 3. create temporary product monitor
        ProductMonitor productMonitor = available_withdrawn_products.get(object.productName);
        // 4. use the doshelf() to remove the item from withdrawn and add to available
        if (!productMonitor.doShelf(object)) return result;
        result = true;
        return result;
    }

    /**
     * Stops the food supplier by sending a specific message. Please observe that no product shall be named @stop!
     */
    public void stopSupplier() {
        //TODO: Provide a correct concurrent implementation!
        currentEmptyItem.add("@stop!");
    }

    /**
     * The supplier acknowledges that it was stopped, and updates its internal state. The monitor also receives confirmation
     * @param stopped   Boolean variable from the supplier
     */
    public void supplierStopped(AtomicBoolean stopped) {
        // TODO: Provide a correct concurrent implementation!
        supplierStopped = true;
        stopped.set(true);
    }

    /**
     * The supplier invokes this method when it needs to know that a new product shall be made ready available.
     *
     * This method should be blocking (if currentEmptyItem is empty, then this should wait until currentEmptyItem
     * contains at least one element and, in that occasion, then returns the first element being available)
     * @return
     */
    public String getNextMissingItem() {
        // TODO: Provide a correct concurrent implementation!
        supplierStopped = false;
        while (currentEmptyItem.isEmpty());
        return currentEmptyItem.remove();
    }


    /**
     * This method is invoked by the Supplier to refurbish the shop of n products of a given time (current item)
     * @param n                 Number of elements to be placed
     * @param currentItem       Type of elements to be placed
     */
    public void refurbishWithItems(int n, String currentItem) {
        // Note: this part of the implementation is completely correct!
        Double cost = productWithCost.get(currentItem);
        if (cost == null) return;
        for (int i = 0; i<n; i++) {
            available_withdrawn_products.get(currentItem).addAvailableProduct(new Item(currentItem, cost, MyUUID.next()));
        }
    }

    /**
     * This operation purchases all the elements available on the basket
     * @param transaction               Transaction containing the current withdrawn elements from the shelf (and therefore basketed)
     * @param total_available_money     How much money can the client spend at maximum
     * @return
     */
    public BasketResult basketCheckout(Transaction transaction, double total_available_money) {
        // Note: this part of the implementation is completely correct!
        BasketResult result = null;
        if (UUID_to_user.getOrDefault(transaction.getUuid(), "").equals(transaction.getUsername())) {
            var b = transaction.getUnmutableBasket();
            double total_cost = (0.0);
            List<Item> currentlyPurchasable = new ArrayList<>();
            List<Item> currentlyUnavailable = new ArrayList<>();
            for (Map.Entry<String, List<Item>> entry : b.stream().collect(Collectors.groupingBy(x -> x.productName)).entrySet()) {
                String k = entry.getKey();
                List<Item> v = entry.getValue();
                total_cost += available_withdrawn_products.get(k).updatePurchase(productWithCost.get(k), v, currentlyPurchasable, currentlyUnavailable);
            }
            if ((total_cost > total_available_money)) {
                for (Map.Entry<String, List<Item>> entry : b.stream().collect(Collectors.groupingBy(x -> x.productName)).entrySet()) {
                    String k = entry.getKey();
                    List<Item> v = entry.getValue();
                    available_withdrawn_products.get(k).makeAvailable(v);
                }
                currentlyUnavailable.clear();
                currentlyPurchasable.clear();
                total_cost = (0.0);
            } else {
                Set<String> s = new HashSet<>();
                for (Map.Entry<String, List<Item>> entry : b.stream().collect(Collectors.groupingBy(x -> x.productName)).entrySet()) {
                    String k = entry.getKey();
                    List<Item> v = entry.getValue();
                    if (available_withdrawn_products.get(k).completelyRemove(v))
                        s.add(k);
                }
                currentEmptyItem.addAll(s);
            }
            result = new BasketResult(currentlyPurchasable, currentlyUnavailable, total_available_money, total_cost, total_available_money- total_cost);
        }
        return result;
    }
}
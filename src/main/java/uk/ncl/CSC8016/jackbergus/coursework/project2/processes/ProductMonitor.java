package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;

import java.util.*;
import java.util.stream.Collectors;

public class ProductMonitor {
    Queue<Item> available;
    Queue<Item> withdrawn;
    //I chose to use one lock for the synchronized code blocks instead of two individual ones for available and withdrawn
    //I chose to do this because some methods modify both available and withdrawn.
    // This will ensure the integrity of the data.
    private Object pmLock = new Object();
    public ProductMonitor() {
        available = new LinkedList<>();
        withdrawn = new LinkedList<>();
    }

    public void removeItemsFromUnavailability(Collection<Item> cls) {
        synchronized (pmLock) {
            for (Item x : cls) {
                try {
                    if (withdrawn.remove(x))
                        available.add(x);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * @return
     */

    public Optional<Item> getAvailableItem() {
        Optional<Item> o = Optional.empty();
        synchronized (pmLock) {
            try {
                if (!available.isEmpty()) {
                    var obj = available.remove();
                    if (obj != null) {
                        o = Optional.of(obj);
                        withdrawn.add(o.get());
                    }
                    }
                } catch (Exception e){
                e.printStackTrace();
            }
        }
        return o;
    }

    public boolean doShelf(Item u) {
        boolean result = false;
        synchronized (pmLock) {
            try {
                if (withdrawn.remove(u)) {
                    available.add(u);
                    result = true;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * this method makes a set of string objects
     * then, the list of objects called 'available' is converted into a new set of strings
     * this new list is assigned to the variable s
     * then the method returns the set s with the names of all available items
     * so we're using a product monitor to return a list of all available items
     * @return
     */

    public Set<String> getAvailableItems() {
        Set<String> s = null;
        synchronized (pmLock) {
            try {
                s = available.stream().map(x -> x.productName).collect(Collectors.toSet());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return s;
    }

    public void addAvailableProduct(Item x) {
        synchronized(pmLock) {
            try {
                available.add(x);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public double updatePurchase(Double aDouble,
                                 List<Item> toIterate,
                                 List<Item> currentlyPurchasable,
                                 List<Item> currentlyUnavailable) {
        double total_cost = 0.0;
        synchronized (pmLock) {
            try {
                for (var x : toIterate) {
                    if (withdrawn.contains(x)) {
                        currentlyPurchasable.add(x);
                        total_cost += aDouble;
                    } else {
                        currentlyUnavailable.add(x);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return total_cost;
    }

    /**
     * this method uses the product monitor to move an item from the withdrawn list to the available list
     * @param toIterate
     */

    public void makeAvailable(List<Item> toIterate) {
        synchronized (pmLock) {
            try {
                for (var x : toIterate) {
                    if (withdrawn.remove(x)) {
                        available.add(x);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public boolean completelyRemove(List<Item> toIterate) {
        boolean allEmpty = false;
        synchronized (pmLock) {
            try {
                for (var x : toIterate) {
                    withdrawn.remove(x);
                    available.remove(x);
                }
                allEmpty = withdrawn.isEmpty() && available.isEmpty();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return allEmpty;
    }
    //ADDED toString for testing purposes
    @Override
    public String toString() {
        return available.size() + " are available; " + withdrawn.size() + " are withdrawn.";
    }
}

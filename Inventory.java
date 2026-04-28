import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private static final int MAX_CAPACITY = 10;
    private ArrayList<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public boolean addItem(Item item) {
        if (items.size() >= MAX_CAPACITY) {
            System.out.println("Inventory is full! Cannot add " + item.getItemName() + ".");
            return false;
        }
        items.add(item);
        System.out.println(item.getItemName() + " added to inventory. (" + items.size() + "/" + MAX_CAPACITY + " slots used)");
        return true;
    }

    public boolean removeItem(Item item) {
        if (items.remove(item)) {
            System.out.println(item.getItemName() + " removed from inventory.");
            return true;
        }
        System.out.println(item.getItemName() + " not found in inventory.");
        return false;
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public Item findItemByName(String name) {
        for (Item item : items) {
            if (item.getItemName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public int getSize() { return items.size(); }
    public int getCapacity() { return MAX_CAPACITY; }
    public boolean isFull() { return items.size() >= MAX_CAPACITY; }

    public void printInventory() {
        System.out.println("=== Inventory (" + items.size() + "/" + MAX_CAPACITY + ") ===");
        if (items.isEmpty()) {
            System.out.println("  (empty)");
        } else {
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                System.out.println("  [" + (i + 1) + "] " + item.getItemName() + " (value: " + item.getValue() + ")");
            }
        }
    }
}
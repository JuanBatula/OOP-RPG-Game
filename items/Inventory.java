package items;
import java.util.ArrayList;
import java.util.List;

import core.Fmt;

public class Inventory {
    private static final int MAX_CAPACITY = 10;
    private ArrayList<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public boolean addItem(Item item) {
        if (items.size() >= MAX_CAPACITY) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.BR_RED, "Inventory is full! Cannot add " + item.getItemName() + "."));
            return false;
        }
        items.add(item);
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.GREEN, item.getItemName() + " added to inventory.")
            + Fmt.c(Fmt.DIM,   "  (" + items.size() + "/" + MAX_CAPACITY + " slots)"));
        return true;
    }

    public boolean removeItem(Item item) {
        if (items.remove(item)) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.DIM, item.getItemName() + " removed from inventory."));
            return true;
        }
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.BR_RED, item.getItemName() + " not found in inventory."));
        return false;
    }

    public List<Item> getItems() { return new ArrayList<>(items); }

    public Item findItemByName(String name) {
        for (Item item : items) {
            if (item.getItemName().equalsIgnoreCase(name)) return item;
        }
        return null;
    }

    public int     getSize()     { return items.size();              }
    public int     getCapacity() { return MAX_CAPACITY;              }
    public boolean isFull()      { return items.size() >= MAX_CAPACITY; }

    public void printInventory() {
        Fmt.printHeading("INVENTORY  "
            + Fmt.c(Fmt.DIM, "(" + items.size() + "/" + MAX_CAPACITY + ")"));
        if (items.isEmpty()) {
            Fmt.dim("(empty)");
        } else {
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                System.out.println(Fmt.INDENT
                    + Fmt.c(Fmt.B_YELLOW, "[" + (i + 1) + "]")
                    + Fmt.c(Fmt.WHITE,    " " + item.getItemName())
                    + Fmt.c(Fmt.DIM,      "  " + item.getValue() + "g"));
            }
        }
    }
}
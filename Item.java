public abstract class Item {
    protected String itemName;
    protected int value;

    public Item(String itemName, int value) {
        this.itemName = itemName;
        this.value = value;
    }
    
    public abstract void use(Player target);

    public String getItemName() { return itemName; }
    public int getValue() { return value; }
}
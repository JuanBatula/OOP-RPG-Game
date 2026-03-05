class Inventory {
    private ArrayList<Item> items;
    
    public Inventory() {
        items = new ArrayList<>();
    }
    
    public void addItem(Item item) {
        items.add(item);
        System.out.println(item.name + " added to inventory!");
    }
    
    public void showItems() {
        System.out.println("\n=== INVENTORY ===");
        if (items.isEmpty()) {
            System.out.println("Inventory is empty!");
        } else {
            for (int i = 0; i < items.size(); i++) {
                System.out.println((i+1) + ". " + items.get(i).name);
            }
        }
    }
}
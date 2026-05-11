/*  ConsumableItem — abstract base for single-use items that are consumed
    on use. Subclasses define their effect in applyEffect(). The use()
    method calls applyEffect() then marks the item as spent so callers
    can remove it from Inventory.
    Extends: Item
*/
public abstract class ConsumableItem extends Item {
    private boolean used;

    public ConsumableItem(String itemName, int value) {
        super(itemName, value);
        this.used = false;
    }

    /**
     * Applies this consumable's effect to the target Player.
     * Subclasses implement the actual effect here.
     */
    protected abstract void applyEffect(Player target);

    /**
     * Called to use this item. Delegates to applyEffect() and marks
     * the item as used. Callers should remove used items from Inventory.
     */
    @Override
    public void use(Player target) {
        if (used) {
            System.out.println(itemName + " has already been used!");
            return;
        }
        applyEffect(target);
        used = true;
    }

    public boolean isUsed() { return used; }
}
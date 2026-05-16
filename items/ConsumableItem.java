package items;
import core.Fmt;
import entities.Player;

/**
 * ConsumableItem — abstract base for single-use items.
 * Subclasses define their effect in applyEffect().
 * use() calls applyEffect() then marks the item spent.
 */
public abstract class ConsumableItem extends Item {
    private boolean used;

    public ConsumableItem(String itemName, int value) {
        super(itemName, value);
        this.used = false;
    }

    protected abstract void applyEffect(Player target);

    @Override
    public void use(Player target) {
        if (used) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.BR_RED, itemName + " has already been used!"));
            return;
        }
        applyEffect(target);
        used = true;
    }

    public boolean isUsed() { return used; }
}
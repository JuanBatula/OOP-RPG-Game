package items;
import core.Fmt;
import effects.StatusEffectManager;
import entities.Player;

/**
 * Antidote — ConsumableItem that clears all active StatusEffects from
 * the player via their StatusEffectManager.
 */
public class Antidote extends ConsumableItem {

    public Antidote(String itemName, int value) {
        super(itemName, value);
    }

    @Override
    protected void applyEffect(Player target) {
        StatusEffectManager manager = target.getStatusEffectManager();
        if (manager.getActiveEffects().isEmpty()) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.B_CYAN, target.getName())
                + Fmt.c(Fmt.DIM,    " used " + itemName + ", but there were no status effects to clear."));
        } else {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.B_CYAN,    target.getName())
                + Fmt.c(Fmt.MAGENTA,   " used ")
                + Fmt.c(Fmt.WHITE,     itemName)
                + Fmt.c(Fmt.MAGENTA,   "!"));
            manager.clearAll();
        }
    }
}
/*  Antidote — ConsumableItem that clears all active StatusEffects from
    the player via their StatusEffectManager.
    Extends: ConsumableItem
*/
public class Antidote extends ConsumableItem {

    public Antidote(String itemName, int value) {
        super(itemName, value);
    }

    @Override
    protected void applyEffect(Player target) {
        StatusEffectManager manager = target.getStatusEffectManager();
        if (manager.getActiveEffects().isEmpty()) {
            System.out.println(target.getName() + " used " + itemName +
                    ", but there were no status effects to clear.");
        } else {
            System.out.println(target.getName() + " used " + itemName + "!");
            manager.clearAll();
        }
    }
}
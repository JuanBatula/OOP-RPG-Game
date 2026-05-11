/*  Elixir — ConsumableItem that temporarily boosts the player's attack power.
    The boost is permanent for the current run session (no expiry mechanic
    needed here; Member 4's Ability system can handle timed buffs later).
    Extends: ConsumableItem
*/
public class Elixir extends ConsumableItem {
    private static final int DEFAULT_ATTACK_BOOST = 5;

    private int attackBoost;

    public Elixir(String itemName, int value) {
        this(itemName, value, DEFAULT_ATTACK_BOOST);
    }

    public Elixir(String itemName, int value, int attackBoost) {
        super(itemName, value);
        this.attackBoost = attackBoost;
    }

    @Override
    protected void applyEffect(Player target) {
        target.addAttackPower(attackBoost);
        System.out.println(target.getName() + " drank " + itemName +
                " and gained +" + attackBoost + " attack power!");
    }

    public int getAttackBoost() { return attackBoost; }
}
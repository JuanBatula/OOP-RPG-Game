package items;
import core.Fmt;
import entities.Player;

/**
 * Elixir — ConsumableItem that permanently boosts the player's attack power
 * for the current session.
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
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN,    target.getName())
            + Fmt.c(Fmt.BR_YELLOW, " drank ")
            + Fmt.c(Fmt.WHITE,     itemName)
            + Fmt.c(Fmt.BR_YELLOW, " and gained +")
            + Fmt.c(Fmt.BOLD,      String.valueOf(attackBoost))
            + Fmt.c(Fmt.BR_YELLOW, " attack power!"));
    }

    public int getAttackBoost() { return attackBoost; }
}
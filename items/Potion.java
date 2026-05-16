package items;
import core.Fmt;
import entities.Player;

public class Potion extends Item {
    private int healAmount;

    public Potion(String itemName, int value, int healAmount) {
        super(itemName, value);
        this.healAmount = healAmount;
    }

    @Override
    public void use(Player target) {
        target.heal(healAmount);
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN,  target.getName())
            + Fmt.c(Fmt.GREEN,   " used ")
            + Fmt.c(Fmt.WHITE,   itemName)
            + Fmt.c(Fmt.GREEN,   " and restored ")
            + Fmt.c(Fmt.BOLD,    String.valueOf(healAmount))
            + Fmt.c(Fmt.GREEN,   " HP!"));
    }

    public int getHealAmount() { return healAmount; }
}
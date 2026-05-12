public class Weapon extends Item {
    private int bonusDamage;

    public Weapon(String itemName, int value, int bonusDamage) {
        super(itemName, value);
        this.bonusDamage = bonusDamage;
    }

    @Override
    public void use(Player target) {
        target.addAttackPower(bonusDamage);
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN,  target.getName())
            + Fmt.c(Fmt.GREEN,   " equipped ")
            + Fmt.c(Fmt.WHITE,   itemName)
            + Fmt.c(Fmt.GREEN,   "! Attack power increased by ")
            + Fmt.c(Fmt.BOLD,    String.valueOf(bonusDamage))
            + Fmt.c(Fmt.GREEN,   "."));
    }

    public int getBonusDamage() { return bonusDamage; }
}
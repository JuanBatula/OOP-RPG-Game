public class Armor extends Item {
    private int baseDefenseBonus;

    public Armor(String itemName, int value, int baseDefenseBonus) {
        super(itemName, value);
        this.baseDefenseBonus = baseDefenseBonus;
    }

    @Override
    public void use(Player target) {
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN,  target.getName())
            + Fmt.c(Fmt.GREEN,   " equipped ")
            + Fmt.c(Fmt.WHITE,   itemName)
            + Fmt.c(Fmt.GREEN,   "!")
            + Fmt.c(Fmt.DIM,     "  Defense bonus: +" + baseDefenseBonus));
    }

    public int getBaseDefenseBonus() { return baseDefenseBonus; }
}
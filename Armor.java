public class Armor extends Item {
    private int baseDefenseBonus;

    public Armor(String itemName, int value, int baseDefenseBonus) {
        super(itemName, value);
        this.baseDefenseBonus = baseDefenseBonus;
    }

    @Override
    public void use(Player target) {
        System.out.println(target.getName() + " equipped " + itemName +
                ", but defense stat is not yet implemented in Character.");
    }

    public int getBaseDefenseBonus() { return baseDefenseBonus; }
}
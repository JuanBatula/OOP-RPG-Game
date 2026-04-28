public class Potion extends Item {
    private int healAmount;

    public Potion(String itemName, int value, int healAmount) {
        super(itemName, value);
        this.healAmount = healAmount;
    }


    @Override
    public void use(Player target) {
        target.heal(healAmount);
        System.out.println(target.getName() + " used " + itemName +
                " and restored " + healAmount + " HP!");
    }

    public int getHealAmount() { return healAmount; }
}
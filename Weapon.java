public class Weapon extends Item {
    private int bonusDamage;

    public Weapon(String itemName, int value, int bonusDamage) {
        super(itemName, value);
        this.bonusDamage = bonusDamage;
    }

    @Override
    public void use(Player target) {
        target.addAttackPower(bonusDamage);
        System.out.println(target.getName() + " equipped " + itemName +
                "! Attack power increased by " + bonusDamage + ".");
    }

    public int getBonusDamage() { return bonusDamage; }
}
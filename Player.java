public class Player extends Character {
    private int level;
    private int exp;
    private int baseAttackPower;

    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private Inventory inventory;

    public Player(String name, int health, int maxHealth, int attackPower) {
        super(name, health, maxHealth, attackPower);
        this.level = 1;
        this.exp = 0;
        this.baseAttackPower = attackPower;
        this.equippedWeapon = null;
        this.equippedArmor = null;
        this.inventory = new Inventory();
    }

    public void heal(int amount) {
        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
        System.out.println(name + " healed for " + amount + " HP! (HP: " + health + "/" + maxHealth + ")");
    }

    public void gainExp(int amount) {
        this.exp += amount;
        System.out.println(name + " gained " + amount + " experience points. (EXP: " + exp + "/100)");
        if (this.exp >= 100) {
            levelUp();
        }
    }

    public void levelUp() {
        this.level++;
        this.maxHealth += 10;
        this.health = this.maxHealth;
        this.baseAttackPower += 2;
        this.attackPower = calculateTotalDamage();
        this.exp = 0;
        System.out.println("Congratulations! " + name + " reached level " + level + "!");
    }

    public void equipWeapon(Weapon weapon) {
        if (equippedWeapon != null) {
            unequipWeapon();
        }
        equippedWeapon = weapon;
        this.attackPower = calculateTotalDamage();
        System.out.println(name + " equipped " + weapon.getItemName() +
                "! Attack power: " + baseAttackPower + " + " + weapon.getBonusDamage() +
                " bonus = " + this.attackPower + " total.");
    }

    public void equipArmor(Armor armor) {
        if (equippedArmor != null) {
            unequipArmor();
        }
        equippedArmor = armor;
        this.defense = armor.getBaseDefenseBonus();
        System.out.println(name + " equipped " + armor.getItemName() +
                "! Defense set to " + this.defense + ".");
    }

    public void unequipWeapon() {
        if (equippedWeapon == null) {
            System.out.println(name + " has no weapon equipped.");
            return;
        }
        System.out.println(name + " unequipped " + equippedWeapon.getItemName() + ".");
        equippedWeapon = null;
        this.attackPower = baseAttackPower;
        System.out.println(name + "'s attack power reset to " + attackPower + ".");
    }

    public void unequipArmor() {
        if (equippedArmor == null) {
            System.out.println(name + " has no armor equipped.");
            return;
        }
        System.out.println(name + " unequipped " + equippedArmor.getItemName() + ".");
        equippedArmor = null;
        this.defense = 0;
        System.out.println(name + "'s defense reset to 0.");
    }

    public int calculateTotalDamage() {
        int bonus = (equippedWeapon != null) ? equippedWeapon.getBonusDamage() : 0;
        return baseAttackPower + bonus;
    }

    public void addAttackPower(int amount) {
        this.baseAttackPower += amount;
        this.attackPower = calculateTotalDamage();
    }

    public void addDefense(int amount) {
        this.defense += amount;
    }

    @Override
    public int attack() {
        this.attackPower = calculateTotalDamage();
        System.out.println(name + " attacks for " + attackPower + " damage!");
        return this.attackPower;
    }

    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public int getBaseAttackPower() { return baseAttackPower; }
    public Weapon getEquippedWeapon() { return equippedWeapon; }
    public Armor getEquippedArmor() { return equippedArmor; }
    public Inventory getInventory() { return inventory; }

    public void printStatus() {
        System.out.println("=== " + name + " (Level " + level + ") ===");
        System.out.println("  HP:     " + health + "/" + maxHealth);
        System.out.println("  ATK:    " + calculateTotalDamage() + " (base: " + baseAttackPower + ")");
        System.out.println("  DEF:    " + defense);
        System.out.println("  EXP:    " + exp + "/100");
        System.out.println("  Weapon: " + (equippedWeapon != null ? equippedWeapon.getItemName() : "none"));
        System.out.println("  Armor:  " + (equippedArmor != null ? equippedArmor.getItemName() : "none"));
    }
}
public class Player extends Character {
    private int level;
    private int exp;

    public Player(String name, int health, int maxHealth, int attackPower) {
        super(name, health, maxHealth, attackPower);
        this.level = 1;
        this.exp = 0;
    }

    public void heal(int amount) {
        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
        System.out.println(name + " healed for " + amount + " HP!");
    }

    public void gainExp(int amount) {
        this.exp += amount;
        System.out.println(name + " gained " + amount + " experience points.");
        if (this.exp >= 100) {
            levelUp();
        }
    }

    public void levelUp() {
        this.level++;
        this.maxHealth += 10;
        this.health = this.maxHealth; 
        this.attackPower += 2;
        this.exp = 0; 
        System.out.println("Congratulations! " + name + " reached level " + level + "!");
    }

    @Override
    public int attack() {
        System.out.println(name + " attacks for " + attackPower + " damage!");
        return this.attackPower;
    }

    public int getLevel() { return level; }
    public int getExp() { return exp; }
}
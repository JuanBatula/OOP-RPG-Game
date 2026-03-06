import java.util.Random;

abstract class Character {
    protected String name;
    protected int health;
    protected int maxHealth;
    protected int attackPower;
    protected Random rand = new Random();
    
    public Character(String name, int health, int attackPower) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.attackPower = attackPower;
    }    
    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttackPower() { return attackPower; }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
        System.out.println(name + " took " + damage + " damage! HP: " + health + "/" + maxHealth);
    }
    
    public boolean isAlive() {
        return health > 0;
    }
    
    public abstract int attack();
}

class Player extends Character {
    private int level;
    private int exp;
    
    public Player(String name, int health, int attackPower) {
        super(name, health, attackPower);
        this.level = 1;
        this.exp = 0;
    }
    
    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
        System.out.println(name + " healed for " + amount + " HP! Current HP: " + health + "/" + maxHealth);
    }
    
    public void gainExp(int amount) {
        exp += amount;
        System.out.println("Gained " + amount + " EXP! (Total: " + exp + ")");
        if (exp >= 100) {
            levelUp();
        }
    }
    
    public void levelUp() {
        level++;
        exp = 0;
        maxHealth += 20;
        health = maxHealth;
        attackPower += 5;
        System.out.println("LEVEL UP! Now level " + level + "! HP: " + maxHealth + ", Attack: " + attackPower);
    }
    
    @Override
    public int attack() {
        boolean critical = rand.nextInt(100) < 20;
        int damage = critical ? attackPower * 2 : attackPower;
        System.out.println(name + " attacks for " + damage + (critical ? " (CRITICAL!)" : ""));
        return damage;
    }
}

class Enemy extends Character {
    private String difficulty;
    private int expValue;
    
    public Enemy(String name, int health, int attackPower, String difficulty) {
        super(name, health, attackPower);
        this.difficulty = difficulty;
        
        switch(difficulty) {
            case "Easy": expValue = 30; break;
            case "Medium": expValue = 50; break;
            case "Hard": expValue = 80; break;
            default: expValue = 40;
        }
    }
    
    public String getDifficulty() { return difficulty; }
    public int getExpValue() { return expValue; }
    
    @Override
    public int attack() {
        int damage = attackPower + rand.nextInt(5);
        System.out.println(name + " attacks for " + damage);
        return damage;
    }
}



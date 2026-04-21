public abstract class Character {

    protected String name;
    protected int health;
    protected int maxHealth;
    protected int attackPower;

    public Character(String name, int health, int maxHealth, int attackPower){
        this.name = name;
        this.health = health;
        this.maxHealth = maxHealth;
        this.attackPower = attackPower;
    }

    public String getName() {
        return name;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
        System.out.println(name + " takes " + damage + " damage! (HP: " + health + "/" + maxHealth + ")");
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    // Abstract method: subclasses MUST define how they attack pls add nya huhu
    public abstract int attack();


}

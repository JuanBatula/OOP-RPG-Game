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



}

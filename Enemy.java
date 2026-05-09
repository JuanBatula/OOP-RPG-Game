/*   Enemy        XP Reward        Special Mechanic                            Difficulty
    Goblin           50             Basic attack                                  Easy
    Troll            150            Heavy damage (1.5x)+20% miss chance           Medium
    Boss             500            Enrage at 30% HP (+20% damage)                Hard
*/  

public abstract class Enemy extends Character {

    protected int xpReward;

    public Enemy(String name, int health, int maxHealth, int attackPower, int defense, int xpReward) {
        super(name, health, maxHealth, attackPower);
        this.defense = defense;
        this.xpReward = xpReward;
    }

    public abstract int getXPReward();

    public abstract int getAttackDamage(int currentHp, int maxHp);

    @Override
    public int attack() {
        int damage = getAttackDamage(this.health, this.maxHealth);
        System.out.println(name + " attacks for " + damage + " damage!");
        return damage;
    }

    public int getExpValue() {
        return getXPReward();
    }
}
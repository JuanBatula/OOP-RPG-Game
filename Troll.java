public class Troll extends Enemy {

    private static final double DAMAGE_MULTIPLIER = 1.5;
    private static final double MISS_CHANCE        = 0.20;

    public Troll(int health, int attackPower, int defense) {
        super("Troll", health, health, attackPower, defense, 150);
    }

    @Override
    public int getXPReward() { return xpReward; }

    @Override
    public int getAttackDamage(int currentHp, int maxHp) {
        if (Math.random() < MISS_CHANCE) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.B_RED, name)
                + Fmt.c(Fmt.DIM,  "'s heavy club swing misses!"));
            return 0;
        }
        return (int)(this.attackPower * DAMAGE_MULTIPLIER);
    }
}
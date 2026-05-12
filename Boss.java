public class Boss extends Enemy {
    private static final double RAGE_THRESHOLD  = 0.30;
    private static final double RAGE_MULTIPLIER = 1.20;

    public Boss(int health, int attackPower, int defense) {
        super("Boss", health, health, attackPower, defense, 500);
    }

    @Override
    public int getXPReward() { return xpReward; }

    @Override
    public int getAttackDamage(int currentHp, int maxHp) {
        boolean enraged = (double) currentHp / maxHp < RAGE_THRESHOLD;
        if (enraged) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.B_BR_RED, "★  " + name + " is ENRAGED!  ★")
                + Fmt.c(Fmt.RED,      "  Damage multiplied!"));
            return (int)(this.attackPower * RAGE_MULTIPLIER);
        }
        return this.attackPower;
    }
}
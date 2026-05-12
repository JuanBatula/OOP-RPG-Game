/**
 * PoisonEffect — deals flat damage each turn, bypassing defense.
 * Duration:  configurable (default 3 turns).
 * Damage:    fixed poisonDamage per tick.
 */
public class PoisonEffect extends StatusEffect {
    private static final int DEFAULT_DURATION      = 3;
    private static final int DEFAULT_POISON_DAMAGE = 5;

    private int poisonDamage;

    public PoisonEffect() {
        this(DEFAULT_POISON_DAMAGE, DEFAULT_DURATION);
    }

    public PoisonEffect(int poisonDamage, int duration) {
        super("Poison", duration);
        this.poisonDamage = poisonDamage;
    }

    @Override
    public void apply(Character target) {
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_MAGENTA, target.getName())
            + Fmt.c(Fmt.MAGENTA,   " is poisoned!")
            + Fmt.c(Fmt.DIM,       "  " + poisonDamage + " dmg/turn for " + duration + " turn(s)."));
    }

    @Override
    public void tick(Character target) {
        if (isExpired()) return;
        // Poison bypasses defense — apply directly to health field
        target.health -= poisonDamage;
        if (target.health < 0) target.health = 0;

        double pct    = (double) target.health / Math.max(1, target.maxHealth);
        String hpCode = (pct <= 0.25) ? Fmt.B_BR_RED : Fmt.WHITE;

        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_MAGENTA, target.getName())
            + Fmt.c(Fmt.MAGENTA,   " writhes in agony — ")
            + Fmt.c(Fmt.BOLD,      String.valueOf(poisonDamage))
            + Fmt.c(Fmt.MAGENTA,   " poison damage!")
            + Fmt.c(Fmt.DIM,       "  (HP: ")
            + Fmt.c(hpCode,        target.health + "/" + target.maxHealth)
            + Fmt.c(Fmt.DIM,       ")"));

        decrementDuration();
        if (isExpired()) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.DIM, target.getName() + "'s poison has worn off."));
        }
    }

    public int getPoisonDamage() { return poisonDamage; }
}
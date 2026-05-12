/**
 * BurnEffect — deals escalating fire damage each turn (damage grows by burnRate).
 * Duration:  configurable (default 3 turns).
 * Damage:    starts at baseDamage, increases by burnRate each tick.
 */
public class BurnEffect extends StatusEffect {
    private static final int DEFAULT_DURATION  = 3;
    private static final int DEFAULT_BASE_DMG  = 3;
    private static final int DEFAULT_BURN_RATE = 2;

    private int currentDamage;
    private int burnRate;

    public BurnEffect() {
        this(DEFAULT_BASE_DMG, DEFAULT_BURN_RATE, DEFAULT_DURATION);
    }

    public BurnEffect(int baseDamage, int burnRate, int duration) {
        super("Burn", duration);
        this.currentDamage = baseDamage;
        this.burnRate      = burnRate;
    }

    @Override
    public void apply(Character target) {
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_RED,   target.getName())
            + Fmt.c(Fmt.RED,     " is burning!")
            + Fmt.c(Fmt.DIM,     "  " + currentDamage + " fire dmg/turn, +" + burnRate
                    + " each turn for " + duration + " turn(s)."));
    }

    @Override
    public void tick(Character target) {
        if (isExpired()) return;
        // Burn bypasses defense — apply directly to health field
        target.health -= currentDamage;
        if (target.health < 0) target.health = 0;

        double pct    = (double) target.health / Math.max(1, target.maxHealth);
        String hpCode = (pct <= 0.25) ? Fmt.B_BR_RED : Fmt.WHITE;

        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_RED, target.getName())
            + Fmt.c(Fmt.RED,   " burns for ")
            + Fmt.c(Fmt.BOLD,  String.valueOf(currentDamage))
            + Fmt.c(Fmt.RED,   " fire damage!")
            + Fmt.c(Fmt.DIM,   "  (HP: ")
            + Fmt.c(hpCode,    target.health + "/" + target.maxHealth)
            + Fmt.c(Fmt.DIM,   ")"));

        currentDamage += burnRate;
        decrementDuration();
        if (isExpired()) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.DIM, target.getName() + "'s burn has faded."));
        }
    }

    public int getCurrentDamage() { return currentDamage; }
    public int getBurnRate()      { return burnRate;       }
}
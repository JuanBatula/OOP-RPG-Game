/**
 * StunEffect — prevents the affected character from acting for its duration.
 * Battle.performTurn() checks StatusEffectManager.isStunned() before
 * resolving that character's attack step.
 * Duration: configurable (default 1 turn).
 */
public class StunEffect extends StatusEffect {
    private static final int DEFAULT_DURATION = 1;

    public StunEffect() { this(DEFAULT_DURATION); }

    public StunEffect(int duration) { super("Stun", duration); }

    @Override
    public void apply(Character target) {
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_MAGENTA, target.getName())
            + Fmt.c(Fmt.MAGENTA,   " is stunned!")
            + Fmt.c(Fmt.DIM,       "  Loses " + duration + " turn(s)."));
    }

    @Override
    public void tick(Character target) {
        if (isExpired()) return;
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_MAGENTA, target.getName())
            + Fmt.c(Fmt.MAGENTA,   " is stunned and cannot act!"));
        decrementDuration();
        if (isExpired()) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.DIM, target.getName() + " is no longer stunned."));
        }
    }
}
/*  StunEffect — prevents the affected character from acting for its duration.
    Battle.performTurn() checks StatusEffectManager.isStunned() before
    resolving that character's attack.
    Duration:   configurable (default 1 turn)
*/
public class StunEffect extends StatusEffect {
    private static final int DEFAULT_DURATION = 1;

    public StunEffect() {
        this(DEFAULT_DURATION);
    }

    public StunEffect(int duration) {
        super("Stun", duration);
    }

    @Override
    public void apply(Character target) {
        System.out.println(target.getName() + " is stunned and will lose " +
                duration + " turn(s)!");
    }

    @Override
    public void tick(Character target) {
        if (isExpired()) return;
        System.out.println(target.getName() + " is stunned and cannot act!");
        decrementDuration();
        if (isExpired()) {
            System.out.println(target.getName() + " is no longer stunned.");
        }
    }
}
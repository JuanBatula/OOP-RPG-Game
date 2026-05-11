/*  PoisonEffect — deals flat damage each turn, ignoring defense.
    Duration:   configurable (default 3 turns)
    Damage:     fixed poisonDamage per tick
*/
public class PoisonEffect extends StatusEffect {
    private static final int DEFAULT_DURATION     = 3;
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
        System.out.println(target.getName() + " is poisoned! Will take " +
                poisonDamage + " poison damage for " + duration + " turn(s).");
    }

    @Override
    public void tick(Character target) {
        if (isExpired()) return;
        // Poison bypasses defense — apply directly to health
        target.health -= poisonDamage;
        if (target.health < 0) target.health = 0;
        System.out.println(target.getName() + " takes " + poisonDamage +
                " poison damage! (HP: " + target.health + "/" + target.maxHealth + ")");
        decrementDuration();
        if (isExpired()) {
            System.out.println(target.getName() + "'s poison has worn off.");
        }
    }

    public int getPoisonDamage() { return poisonDamage; }
}
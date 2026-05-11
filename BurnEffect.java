/*  BurnEffect — deals escalating damage each turn (damage grows by burnRate).
    Duration:   configurable (default 3 turns)
    Damage:     starts at baseDamage, increases by burnRate each tick
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
        System.out.println(target.getName() + " is burning! Takes " +
                currentDamage + " fire damage this turn, increasing by " +
                burnRate + " each turn for " + duration + " turn(s).");
    }

    @Override
    public void tick(Character target) {
        if (isExpired()) return;
        // Burn bypasses defense — apply directly to health
        target.health -= currentDamage;
        if (target.health < 0) target.health = 0;
        System.out.println(target.getName() + " burns for " + currentDamage +
                " fire damage! (HP: " + target.health + "/" + target.maxHealth + ")");
        currentDamage += burnRate;
        decrementDuration();
        if (isExpired()) {
            System.out.println(target.getName() + "'s burn has faded.");
        }
    }

    public int getCurrentDamage() { return currentDamage; }
    public int getBurnRate()      { return burnRate; }
}
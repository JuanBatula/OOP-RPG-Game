public abstract class StatusEffect {
    protected String effectName;
    protected int duration;   // turns remaining

    public StatusEffect(String effectName, int duration) {
        this.effectName = effectName;
        this.duration   = duration;
    }

    /**
     * Called once when the effect is first applied to a character.
     * @param target the Character receiving the effect
     */
    public abstract void apply(Character target);

    /**
     * Called at the start of each of the affected character's turns.
     * Decrements duration by 1 each tick.
     * @param target the Character currently affected
     */
    public abstract void tick(Character target);

    public boolean isExpired()       { return duration <= 0; }
    public String  getEffectName()   { return effectName; }
    public int     getDuration()     { return duration; }

    protected void decrementDuration() {
        if (duration > 0) duration--;
    }
}
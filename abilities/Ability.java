package abilities;
import entities.Character;

public abstract class Ability {
    protected String name;
    protected int cooldown;
    protected int currentCooldown;
    protected String description;

    public Ability(String name, int cooldown, String description) {
        this.name            = name;
        this.cooldown        = cooldown;
        this.currentCooldown = 0;
        this.description     = description;
    }

    /** Use the ability. Implement the actual effect in each subclass. */
    public abstract void use(Character caster, Character target);

    /** Call at the start of each turn to tick cooldown down by 1. */
    public void tickCooldown() {
        if (currentCooldown > 0) currentCooldown--;
    }

    public boolean isReady() { return currentCooldown == 0; }

    /** Call inside use() implementations after the effect fires. */
    protected void triggerCooldown() { currentCooldown = cooldown; }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public int getCooldown()       { return cooldown; }
    public int getCurrentCooldown(){ return currentCooldown; }
}
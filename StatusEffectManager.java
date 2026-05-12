import java.util.ArrayList;
import java.util.List;

/**
 * StatusEffectManager — tracks all active StatusEffects on a Character.
 *
 * Output is coloured magenta throughout; status effects are always
 * displayed in that colour so players can instantly identify them.
 */
public class StatusEffectManager {
    private List<StatusEffect> activeEffects;

    public StatusEffectManager() {
        this.activeEffects = new ArrayList<>();
    }

    /**
     * Applies a new StatusEffect, replacing any existing effect of the same name.
     */
    public void addEffect(StatusEffect effect, Character target) {
        activeEffects.removeIf(e -> e.getEffectName().equalsIgnoreCase(effect.getEffectName()));
        effect.apply(target);
        activeEffects.add(effect);
    }

    /**
     * Ticks all active effects and removes expired ones.
     * Call once per turn for the owning character.
     */
    public void tickAll(Character target) {
        if (activeEffects.isEmpty()) return;
        List<StatusEffect> expired = new ArrayList<>();
        for (StatusEffect effect : activeEffects) {
            effect.tick(target);
            if (effect.isExpired()) expired.add(effect);
        }
        activeEffects.removeAll(expired);
    }

    /**
     * Returns true if a non-expired StunEffect is active.
     * Battle uses this to skip the stunned character's attack.
     */
    public boolean isStunned() {
        for (StatusEffect effect : activeEffects) {
            if (effect instanceof StunEffect && !effect.isExpired()) return true;
        }
        return false;
    }

    public boolean hasEffect(String name) {
        for (StatusEffect effect : activeEffects) {
            if (effect.getEffectName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public void clearAll() {
        activeEffects.clear();
        System.out.println(Fmt.INDENT + Fmt.c(Fmt.MAGENTA, "All status effects cleared."));
    }

    public List<StatusEffect> getActiveEffects() {
        return new ArrayList<>(activeEffects);
    }

    public void printEffects() {
        if (activeEffects.isEmpty()) {
            System.out.println(Fmt.INDENT + Fmt.c(Fmt.DIM, "  (no active status effects)"));
            return;
        }
        for (StatusEffect e : activeEffects) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.MAGENTA, "  [" + e.getEffectName() + "]")
                + Fmt.c(Fmt.DIM,     "  " + e.getDuration() + " turn(s) remaining"));
        }
    }
}
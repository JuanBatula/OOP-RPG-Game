import java.util.ArrayList;
import java.util.List;

/*  StatusEffectManager — sits on Player (and optionally Enemy) and tracks
    all active StatusEffects. Battle calls tickAll() at the start of each
    character's turn and checks isStunned() before resolving their attack.
*/
public class StatusEffectManager {
    private List<StatusEffect> activeEffects;

    public StatusEffectManager() {
        this.activeEffects = new ArrayList<>();
    }

    /**
     * Applies a new StatusEffect to the given character and registers it.
     * If an effect with the same name is already active, it is replaced.
     */
    public void addEffect(StatusEffect effect, Character target) {
        // Remove any existing effect of the same type before re-applying
        activeEffects.removeIf(e -> e.getEffectName().equalsIgnoreCase(effect.getEffectName()));
        effect.apply(target);
        activeEffects.add(effect);
    }

    /**
     * Ticks all active effects and removes any that have expired.
     * Call once per turn for the owning character.
     */
    public void tickAll(Character target) {
        if (activeEffects.isEmpty()) return;
        List<StatusEffect> expired = new ArrayList<>();
        for (StatusEffect effect : activeEffects) {
            effect.tick(target);
            if (effect.isExpired()) {
                expired.add(effect);
            }
        }
        activeEffects.removeAll(expired);
    }

    /**
     * Returns true if any active StunEffect still has duration remaining.
     * Battle uses this to skip the stunned character's attack step.
     */
    public boolean isStunned() {
        for (StatusEffect effect : activeEffects) {
            if (effect instanceof StunEffect && !effect.isExpired()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEffect(String name) {
        for (StatusEffect effect : activeEffects) {
            if (effect.getEffectName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void clearAll() {
        activeEffects.clear();
        System.out.println("All status effects cleared.");
    }

    public List<StatusEffect> getActiveEffects() {
        return new ArrayList<>(activeEffects);
    }

    public void printEffects() {
        if (activeEffects.isEmpty()) {
            System.out.println("  (no active status effects)");
            return;
        }
        for (StatusEffect e : activeEffects) {
            System.out.println("  [" + e.getEffectName() + "] " + e.getDuration() + " turn(s) remaining");
        }
    }
}
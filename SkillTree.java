import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class SkillTree {
    private Map<String, Ability> unlockedAbilities;

    public SkillTree() {
        this.unlockedAbilities = new HashMap<>();
    }

    /**
     * Unlocks an ability, keyed by its name (case-insensitive).
     * If the ability is already unlocked, prints a notice and skips.
     */
    public void unlock(Ability ability) {
        String key = ability.getName().toLowerCase();
        if (unlockedAbilities.containsKey(key)) {
            System.out.println(ability.getName() + " is already unlocked.");
            return;
        }
        unlockedAbilities.put(key, ability);
        System.out.println("Ability unlocked: " + ability.getName()
                + " — " + ability.getDescription());
    }

    /**
     * Returns the named ability if unlocked and ready, or null.
     * Prints a helpful message when unavailable.
     */
    public Ability getAbility(String name) {
        Ability ability = unlockedAbilities.get(name.toLowerCase());
        if (ability == null) {
            System.out.println("Ability \"" + name + "\" is not unlocked.");
            return null;
        }
        if (!ability.isReady()) {
            System.out.println(ability.getName() + " is on cooldown ("
                    + ability.getCurrentCooldown() + " turn(s) remaining).");
            return null;
        }
        return ability;
    }

    public boolean hasAbility(String name) {
        return unlockedAbilities.containsKey(name.toLowerCase());
    }

    /** Ticks the cooldown of every unlocked ability — call once per turn. */
    public void tickAllCooldowns() {
        for (Ability ability : unlockedAbilities.values()) {
            ability.tickCooldown();
        }
    }

    public Collection<Ability> getAllAbilities() {
        return unlockedAbilities.values();
    }

    public void printSkillTree() {
        System.out.println("=== Skill Tree ===");
        if (unlockedAbilities.isEmpty()) {
            System.out.println("  (no abilities unlocked)");
            return;
        }
        for (Ability ability : unlockedAbilities.values()) {
            String status = ability.isReady() ? "READY" : "Cooldown: " + ability.getCurrentCooldown();
            System.out.println("  [" + ability.getName() + "] " + ability.getDescription()
                    + " | " + status);
        }
    }
}
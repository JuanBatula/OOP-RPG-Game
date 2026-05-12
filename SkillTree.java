import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SkillTree {
    private Map<String, Ability> unlockedAbilities;

    public SkillTree() {
        this.unlockedAbilities = new HashMap<>();
    }

    /**
     * Unlocks an ability, keyed by its name (case-insensitive).
     * If already unlocked, prints a notice and skips.
     */
    public void unlock(Ability ability) {
        String key = ability.getName().toLowerCase();
        if (unlockedAbilities.containsKey(key)) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.DIM, ability.getName() + " is already unlocked."));
            return;
        }
        unlockedAbilities.put(key, ability);
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_MAGENTA, "Ability unlocked: " + ability.getName())
            + Fmt.c(Fmt.DIM,       " — " + ability.getDescription()));
    }

    /**
     * Returns the named ability if unlocked and ready, or null.
     */
    public Ability getAbility(String name) {
        Ability ability = unlockedAbilities.get(name.toLowerCase());
        if (ability == null) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.BR_RED, "Ability \"" + name + "\" is not unlocked."));
            return null;
        }
        if (!ability.isReady()) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.BR_RED, ability.getName() + " is on cooldown.")
                + Fmt.c(Fmt.DIM,    "  (" + ability.getCurrentCooldown() + " turn(s) remaining)"));
            return null;
        }
        return ability;
    }

    public boolean hasAbility(String name) {
        return unlockedAbilities.containsKey(name.toLowerCase());
    }

    /** Ticks cooldown of every unlocked ability — call once per turn. */
    public void tickAllCooldowns() {
        for (Ability ability : unlockedAbilities.values()) {
            ability.tickCooldown();
        }
    }

    public Collection<Ability> getAllAbilities() {
        return unlockedAbilities.values();
    }

    public void printSkillTree() {
        Fmt.printHeading("SKILL TREE");
        if (unlockedAbilities.isEmpty()) {
            Fmt.dim("(no abilities unlocked)");
            return;
        }
        for (Ability ability : unlockedAbilities.values()) {
            String status = ability.isReady()
                    ? Fmt.c(Fmt.BR_GREEN, "READY")
                    : Fmt.c(Fmt.BR_RED,   "Cooldown: " + ability.getCurrentCooldown());
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.B_MAGENTA, ability.getName())
                + "  " + status);
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.DIM, "  " + ability.getDescription()));
        }
    }
}
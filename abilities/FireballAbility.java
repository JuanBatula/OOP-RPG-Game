package abilities;
import core.Fmt;
import effects.BurnEffect;
import entities.Character;
import entities.Player;

public class FireballAbility extends Ability {
    private static final int BASE_DAMAGE = 20;
    private static final int COOLDOWN    = 3;

    public FireballAbility() {
        super("Fireball", COOLDOWN,
              "Hurls a fireball dealing " + BASE_DAMAGE + " fire damage and applying Burn.");
    }

    @Override
    public void use(Character caster, Character target) {
        if (!isReady()) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.BR_RED, caster.getName() + " tried to use Fireball, but it's on cooldown!")
                + Fmt.c(Fmt.DIM,    "  (" + currentCooldown + " turn(s) remaining)"));
            return;
        }

        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN,  caster.getName())
            + Fmt.c(Fmt.RED,     " launches a ")
            + Fmt.c(Fmt.B_RED,   "Fireball")
            + Fmt.c(Fmt.RED,     " at ")
            + Fmt.c(Fmt.B_RED,   target.getName())
            + Fmt.c(Fmt.RED,     "!"));

        // Direct fire damage — bypasses defence (matches original behaviour)
        target.health -= BASE_DAMAGE;
        if (target.health < 0) target.health = 0;

        double pct    = (double) target.health / Math.max(1, target.maxHealth);
        String hpCode = (pct <= 0.25) ? Fmt.B_BR_RED : Fmt.WHITE;

        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_RED, target.getName())
            + Fmt.c(Fmt.RED,   " takes ")
            + Fmt.c(Fmt.BOLD,  String.valueOf(BASE_DAMAGE))
            + Fmt.c(Fmt.RED,   " fire damage!")
            + Fmt.c(Fmt.DIM,   "  (HP: ")
            + Fmt.c(hpCode,    target.health + "/" + target.maxHealth)
            + Fmt.c(Fmt.DIM,   ")"));

        // Apply BurnEffect if target is a Player
        if (target instanceof Player) {
            Player playerTarget = (Player) target;
            BurnEffect burn = new BurnEffect();
            playerTarget.getStatusEffectManager().addEffect(burn, playerTarget);
        }

        triggerCooldown();
    }
}
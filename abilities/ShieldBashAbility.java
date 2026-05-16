package abilities;
import core.Fmt;
import effects.StunEffect;
import entities.Character;
import entities.Player;

public class ShieldBashAbility extends Ability {
    private static final int BASE_DAMAGE = 10;
    private static final int COOLDOWN    = 2;

    public ShieldBashAbility() {
        super("Shield Bash", COOLDOWN,
              "Bashes the target for " + BASE_DAMAGE + " damage and stuns them for 1 turn.");
    }

    @Override
    public void use(Character caster, Character target) {
        if (!isReady()) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.BR_RED, caster.getName() + " tried to use Shield Bash, but it's on cooldown!")
                + Fmt.c(Fmt.DIM,    "  (" + currentCooldown + " turn(s) remaining)"));
            return;
        }

        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN,  caster.getName())
            + Fmt.c(Fmt.GREEN,   " shield-bashes ")
            + Fmt.c(Fmt.B_RED,   target.getName())
            + Fmt.c(Fmt.GREEN,   "!"));

        // Physical damage — defence applies via normal takeDamage
        target.takeDamage(BASE_DAMAGE);

        // Apply StunEffect if target is a Player
        if (target instanceof Player) {
            Player playerTarget = (Player) target;
            StunEffect stun = new StunEffect();
            playerTarget.getStatusEffectManager().addEffect(stun, playerTarget);
        }

        triggerCooldown();
    }
}
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
            System.out.println(caster.getName() + " tried to use Shield Bash, but it's on cooldown! ("
                    + currentCooldown + " turn(s) remaining)");
            return;
        }

        System.out.println(caster.getName() + " shield-bashes " + target.getName() + "!");

        // Physical damage — goes through normal takeDamage so defence applies
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
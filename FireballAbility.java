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
            System.out.println(caster.getName() + " tried to use Fireball, but it's on cooldown! ("
                    + currentCooldown + " turn(s) remaining)");
            return;
        }

        System.out.println(caster.getName() + " launches a Fireball at " + target.getName() + "!");

        // Direct fire damage — bypasses defence like other elemental effects in this codebase
        target.health -= BASE_DAMAGE;
        if (target.health < 0) target.health = 0;
        System.out.println(target.getName() + " takes " + BASE_DAMAGE
                + " fire damage! (HP: " + target.health + "/" + target.maxHealth + ")");

        // Apply BurnEffect if target is a Player (StatusEffectManager lives on Player)
        if (target instanceof Player) {
            Player playerTarget = (Player) target;
            BurnEffect burn = new BurnEffect();
            playerTarget.getStatusEffectManager().addEffect(burn, playerTarget);
        }

        triggerCooldown();
    }
}
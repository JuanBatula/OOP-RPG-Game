package entities;
import abilities.Ability;
import abilities.SkillTree;
import core.Fmt;
import effects.StatusEffectManager;
import items.Armor;
import items.Inventory;
import items.Weapon;

public class Player extends Character {
    private int level;
    private int exp;
    private int baseAttackPower;

    private Weapon               equippedWeapon;
    private Armor                equippedArmor;
    private Inventory            inventory;
    private StatusEffectManager  statusEffectManager;
    private SkillTree            skillTree;

    public Player(String name, int health, int maxHealth, int attackPower) {
        super(name, health, maxHealth, attackPower);
        this.level               = 1;
        this.exp                 = 0;
        this.baseAttackPower     = attackPower;
        this.equippedWeapon      = null;
        this.equippedArmor       = null;
        this.inventory           = new Inventory();
        this.statusEffectManager = new StatusEffectManager();
        this.skillTree           = new SkillTree();
    }

    // -------------------------------------------------------------------------
    // Core actions
    // -------------------------------------------------------------------------

    public void heal(int amount) {
        this.health = Math.min(this.health + amount, this.maxHealth);
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN,  name)
            + Fmt.c(Fmt.GREEN,   " is healed for ")
            + Fmt.c(Fmt.BOLD,    String.valueOf(amount))
            + Fmt.c(Fmt.GREEN,   " HP!")
            + Fmt.c(Fmt.DIM,     "  (HP: " + health + "/" + maxHealth + ")"));
    }

    public void gainExp(int amount) {
        this.exp += amount;
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN,    name)
            + Fmt.c(Fmt.BR_YELLOW, " gained " + amount + " EXP.")
            + Fmt.c(Fmt.DIM,       "  (" + exp + " / 100)"));
        if (this.exp >= 100) {
            levelUp();
        }
    }

    public void levelUp() {
        this.level++;
        this.maxHealth       += 10;
        this.health           = this.maxHealth;
        this.baseAttackPower += 2;
        this.attackPower      = calculateTotalDamage();
        this.exp              = 0;
        // Banner is printed by GameRunner.onLevelUp() — we just log the stat change.
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.DIM, "  Stats updated — Max HP: " + maxHealth
                + "  ATK: " + attackPower));
    }

    // -------------------------------------------------------------------------
    // Ability system
    // -------------------------------------------------------------------------

    public SkillTree getSkillTree() { return skillTree; }

    public void unlockAbility(Ability ability) {
        skillTree.unlock(ability);
    }

    public boolean useAbility(String abilityName, Character target) {
        Ability ability = skillTree.getAbility(abilityName);
        if (ability == null) return false;
        ability.use(this, target);
        return true;
    }

    // -------------------------------------------------------------------------
    // Equipment
    // -------------------------------------------------------------------------

    public void equipWeapon(Weapon weapon) {
        if (equippedWeapon != null) unequipWeapon();
        equippedWeapon   = weapon;
        this.attackPower = calculateTotalDamage();
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN,  name)
            + Fmt.c(Fmt.GREEN,   " equipped ")
            + Fmt.c(Fmt.WHITE,   weapon.getItemName())
            + Fmt.c(Fmt.GREEN,   "!")
            + Fmt.c(Fmt.DIM,     "  ATK: " + baseAttackPower + " +" + weapon.getBonusDamage()
                    + " = " + this.attackPower));
    }

    public void equipArmor(Armor armor) {
        if (equippedArmor != null) unequipArmor();
        equippedArmor  = armor;
        this.defense   = armor.getBaseDefenseBonus();
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN, name)
            + Fmt.c(Fmt.GREEN,  " equipped ")
            + Fmt.c(Fmt.WHITE,  armor.getItemName())
            + Fmt.c(Fmt.GREEN,  "!")
            + Fmt.c(Fmt.DIM,    "  DEF: " + this.defense));
    }

    public void unequipWeapon() {
        if (equippedWeapon == null) {
            System.out.println(Fmt.INDENT + Fmt.c(Fmt.DIM, name + " has no weapon equipped."));
            return;
        }
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.DIM, name + " unequipped " + equippedWeapon.getItemName() + "."));
        equippedWeapon   = null;
        this.attackPower = baseAttackPower;
    }

    public void unequipArmor() {
        if (equippedArmor == null) {
            System.out.println(Fmt.INDENT + Fmt.c(Fmt.DIM, name + " has no armor equipped."));
            return;
        }
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.DIM, name + " unequipped " + equippedArmor.getItemName() + "."));
        equippedArmor = null;
        this.defense  = 0;
    }

    // -------------------------------------------------------------------------
    // Damage calculation
    // -------------------------------------------------------------------------

    public int calculateTotalDamage() {
        int bonus = (equippedWeapon != null) ? equippedWeapon.getBonusDamage() : 0;
        return baseAttackPower + bonus;
    }

    public void addAttackPower(int amount) {
        this.baseAttackPower += amount;
        this.attackPower      = calculateTotalDamage();
    }

    public void addDefense(int amount) {
        this.defense += amount;
    }

    @Override
    public int attack() {
        this.attackPower = calculateTotalDamage();
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN, name)
            + Fmt.c(Fmt.GREEN,  " attacks for ")
            + Fmt.c(Fmt.BOLD,   String.valueOf(attackPower))
            + Fmt.c(Fmt.GREEN,  " damage!"));
        return this.attackPower;
    }

    // -------------------------------------------------------------------------
    // Status display
    // -------------------------------------------------------------------------

    public void printStatus() {
        Fmt.printHeading(name.toUpperCase() + " — LEVEL " + level);
        Fmt.printHpBar("HP", health, maxHealth);
        Fmt.blank();
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.YELLOW, String.format("%-10s", "Attack"))
            + Fmt.c(Fmt.WHITE,  calculateTotalDamage() + "")
            + Fmt.c(Fmt.DIM,    " (base " + baseAttackPower + ")"));
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.YELLOW, String.format("%-10s", "Defense"))
            + Fmt.c(Fmt.WHITE,  String.valueOf(defense)));
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.YELLOW, String.format("%-10s", "EXP"))
            + Fmt.c(Fmt.WHITE,  exp + "")
            + Fmt.c(Fmt.DIM,    " / 100"));
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.YELLOW, String.format("%-10s", "Weapon"))
            + Fmt.c(Fmt.WHITE,  equippedWeapon != null ? equippedWeapon.getItemName()
                                                       : Fmt.c(Fmt.DIM, "none")));
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.YELLOW, String.format("%-10s", "Armor"))
            + Fmt.c(Fmt.WHITE,  equippedArmor != null ? equippedArmor.getItemName()
                                                      : Fmt.c(Fmt.DIM, "none")));
        Fmt.blank();
        System.out.println(Fmt.INDENT + Fmt.c(Fmt.YELLOW, "Status Effects:"));
        statusEffectManager.printEffects();
    }

    // -------------------------------------------------------------------------
    // Getters / setters
    // -------------------------------------------------------------------------
    public int     getLevel()              { return level;               }
    public int     getExp()                { return exp;                 }
    public int     getBaseAttackPower()    { return baseAttackPower;     }
    public Weapon  getEquippedWeapon()     { return equippedWeapon;      }
    public Armor   getEquippedArmor()      { return equippedArmor;       }
    public Inventory getInventory()        { return inventory;           }
    public StatusEffectManager getStatusEffectManager() { return statusEffectManager; }
    public void    setExp(int exp)         { this.exp = exp;             }
    // getHealth / getMaxHealth / getDefense inherited from Character
}
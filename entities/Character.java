package entities;
import core.Fmt;

public abstract class Character {

    protected String name;
    public int health;
    public int maxHealth;
    public int attackPower;
    protected int defense;

    public Character(String name, int health, int maxHealth, int attackPower) {
        this.name        = name;
        this.health      = health;
        this.maxHealth   = maxHealth;
        this.attackPower = attackPower;
    }

    public String getName() { return name; }

    /**
     * Applies incoming damage after subtracting defense (minimum 1 dealt).
     * Output is coloured red — damage is always bad news for the receiver.
     * No logic changes from original; only the System.out call is beautified.
     */
    public void takeDamage(int damage) {
        int actual = Math.max(1, damage - this.defense);
        this.health -= actual;
        if (this.health < 0) this.health = 0;

        // Colour the HP readout based on remaining %
        double pct    = (double) this.health / Math.max(1, this.maxHealth);
        String hpCode = (pct <= 0.25) ? Fmt.B_BR_RED : Fmt.WHITE;

        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_RED, name)
            + Fmt.c(Fmt.RED,  " takes ")
            + Fmt.c(Fmt.BOLD, String.valueOf(actual))
            + Fmt.c(Fmt.RED,  " damage!")
            + Fmt.c(Fmt.DIM,  "  (HP: ")
            + Fmt.c(hpCode,   health + "/" + maxHealth)
            + Fmt.c(Fmt.DIM,  ")"));
    }

    public boolean isAlive() { return this.health > 0; }

    public int attack() {
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.WHITE, name + " attacks for ")
            + Fmt.c(Fmt.BOLD, String.valueOf(attackPower))
            + Fmt.c(Fmt.WHITE, " damage!"));
        return attackPower;
    }

    // --- Added getters (from previous refactor) — no logic change ----------
    public int getHealth()    { return health;    }
    public int getMaxHealth() { return maxHealth; }
    public int getDefense()   { return defense;   }
}
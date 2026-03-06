class Item {
    protected String name;
    protected String effect;
    
    public Item(String name, String effect) {
        this.name = name;
        this.effect = effect;
    }
    
    public void use() {
        System.out.println("Using " + name + ": " + effect);
    }
}

class Weapon extends Item {
    private int bonusDamage;
    
    public Weapon(String name, int bonusDamage) {
        super(name, "Deals extra damage");
        this.bonusDamage = bonusDamage;
    }
    
    @Override
    public void use() {
        System.out.println("Equipped " + name + "! Attack +" + bonusDamage);
    }
}

class Potion extends Item {
    private int healAmount;
    
    public Potion(String name, int healAmount) {
        super(name, "Restores HP");
        this.healAmount = healAmount;
    }
    
    public int getHealAmount() { return healAmount; }
    
    @Override
    public void use() {
        System.out.println("Drank " + name + "! Healed " + healAmount + " HP");
    }
}
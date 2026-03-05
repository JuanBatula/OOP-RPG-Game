class Item {
    protected String name;
    protected String effect;

    public Item(String name, String effect){
        this.name = name;
        this.effect = effect;
    }

    public void use(){
        System.out.println("Using  " + name + ": " + effect);
    }
}

class Weapon extends Item {
   private int bonusDamage;

   public Weapon(String var1, int var2) {
      super(var1, "Deals extra damage");
      this.bonusDamage = var2;
   }

   public void use() {
      System.out.println("Equipped " + this.name + "! Attack +" + this.bonusDamage);
   }
}

class Potion extends Item {
   private int healAmount;

   public Potion(String var1, int var2) {
      super(var1, "Restores HP");
      this.healAmount = var2;
   }

   public void use() {
      System.out.println("Drank " + this.name + "! Healed " + this.healAmount + " HP");
   }
}

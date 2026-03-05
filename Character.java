import java.util.Random;

abstract class Character{
    protected String name;
    protected int health;
    protected int maxHealth;
    protected int attackPower;
    protected Random rand = new Random();

    public Character(String name, int health, int attackPower){
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.attackPower = attackPower;
    }

    public void takeDamage(int damage){
        health -= damage;                   
        if (health < 0) health = 0;
        System.out.println(name + " takes " + damage + " damage! (HP: " + health + "/" + maxHealth + ")");
    }

    public boolean isAlive(){
      return health > 0;
    }

    public abstract int attack();
}

class Player extends Character{
  private int level;
  private int experience;

  public Player(String name){
    super(name, 100, 20);        // default health and attack power
    this.level = 1;              // starting level
    this.experience = 0;         // starting experience
  }

  public Player(String name, int health, int attackPower) {
        super(name, health, attackPower);
        this.level = 1;
        this.experience = 0;
    }

  @Override
  public int attack(){
    // random chance for crticial hit
    boolean critical = rand.nextInt(100) < 20; // 20% chance
    int damage = critical ? attackPower*2 : attackPower;
    System.out.println(name + " attacks for " + damage + (critical ? " (Critical!)" : ""));
    return damage;
  }

  public void gainExperience(int exp){   // to level up
    experience += exp;
    System.out.println(name + " gains " + exp + " XP. (Total XP: " + experience + ")");
    if(experience >= 100){
      levelUp();
    }
  }

  private void levelUp(){
    level++;
    experience = 0;
    attackPower += 5;
    maxHealth += 20;
    health = maxHealth;
    System.out.print("Congrats! " + name + " is now level " + level + "! (Attack: " + attackPower + ", Max HP: " + maxHealth + ")");
  }
}

class Enemy extends Character{
  private int difficultyLevel;

  public Enemy(String name, int difficultyLevel){
    super(name, 50+difficultyLevel*10, 10+difficultyLevel*5);    // health and attack power increase with difficulty level
    this.difficultyLevel = difficultyLevel;
  }

  @Override
  public int attack(){
    int damage = attackPower + rand.nextInt(6); //random damage variation
    System.out.println(name + " attacks for " + damage);
    return damage;
  }

  public int giveExperience(){
    return difficultyLevel*20;
  }

  public String getName() {
        return name;
    }

    public int getDifficulty() {
        return difficultyLevel;
    }
}



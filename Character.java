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
    if(experience >= 100){
      level++;
      experience = 0;
      attackPower += 5;
      System.out.println("Congratz! You are now level " +level);
    }
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
    return attackPower;
  }
}



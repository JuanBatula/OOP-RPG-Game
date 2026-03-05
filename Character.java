// import java.util.Scanner;

abstract class Character{
    protected String name;
    protected int health;
    protected int attackPower;

    public Character(String name, int health, int attackPower){
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
    }

    public void takeDamage(int damage){
        health -= damage;
        if (health < 0) health = 0;
    }

    public int getHealth(){
        return health;
    }

    public abstract int attack();
}

class Player extends Character{
  private int level;
  private int experience;

  public Player(String name){
    super(name, 100, 20);
    this.level = 1;
    this.experience = 0;
  }

  @Override
  public int attack(){
    return attackPower;
  }

  public void gainExperience(int exp){
    experience += exp;
    if(experience >= 100){
      level++;
      experience = 0;
      attackPower += 5;
      System.out.println("Level Up! You are now level " +level);
    }
  }
}



<<<<<<< HEAD
=======
// import java.util.Scanner;

>>>>>>> bc2d1cc3f9c029fe497d405520bef364594fe067
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

    //getter 
    public int getHealth(){
        return health;
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
    return attackPower;
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



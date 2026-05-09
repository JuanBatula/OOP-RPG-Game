public class Goblin extends Enemy {
  
  public Goblin(int health, int attackPower, int defense){
    super("Goblin", health, health, attackPower, defense, 50);
  }

  @Override
  public int getXPReward(){
    return xpReward;
  }

  @Override
  public int getAttackDamage(int currentHp, maxHp){
    return this.attackPower;
  }
}
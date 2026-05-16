package combat;
import entities.Boss;
import entities.Enemy;
import entities.Goblin;
import entities.Troll;

public class EnemyFactory {
  private static final int GOBLIN_HP = 25, GOBLIN_ATK = 6, GOBLIN_DEF = 2;
  private static final int TROLL_HP = 70, TROLL_ATK = 15, TROLL_DEF = 6;
  private static final int BOSS_HP = 135, BOSS_ATK = 25, BOSS_DEF = 11;

  public Enemy create(String type, int level) {
    if(level < 1) {
      throw new IllegalArgumentException("Level must be >= 1, got: " +level);
    }
    double scale = 0.8 + level * 0.2;

    switch (type.toLowerCase()) {
      case "goblin":
        return new Goblin(
          scale(GOBLIN_HP, scale),
          scale(GOBLIN_ATK, scale),
          scale(GOBLIN_DEF, scale)
        );

      case "troll":
        return new Troll(
          scale(TROLL_HP, scale),
          scale(TROLL_ATK, scale),
          scale(TROLL_DEF, scale)
        );

      case "boss":
        return new Boss(
          scale(BOSS_HP, scale),
          scale(BOSS_ATK, scale),
          scale(BOSS_DEF, scale)
        );

      default:
        throw new IllegalArgumentException(
          "Unknown enemy type: \"" + type + "\". Valid types: goblin, troll, boss");
    }
  }

  public Goblin createGoblin() { return (Goblin) create("goblin", 1); }
  public Troll createTroll() { return (Troll) create("troll", 1); }
  public Boss createBoss() { return (Boss) create("boss", 1); }

  private static int scale(int base, double factor) {
    return (int)(base*factor);
  }
}
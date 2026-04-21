public class Enemy extends Character {

    private String difficulty;
    private int expValue;

    public Enemy(String name, String difficulty) {
        super(name, 80, 80, 8);
        this.difficulty = difficulty;
        this.expValue = 50;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getExpValue() {
        return expValue;
    }

    @Override
    public int attack() {
        System.out.println(name + " attacks for " + attackPower + " damage!");
        return attackPower;
    }
}
public class Battle {
    private Player player;
    private Enemy enemy;
    private DamageCalculator calculator;
    private TurnManager turnManager;

    public Battle(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.calculator = new DamageCalculator();
        this.turnManager = new TurnManager();
    }

    public void startBattle() {
        System.out.println("Battle Start!");

        while (player.isAlive() && enemy.isAlive()) {

            if (turnManager.isPlayerTurn()) {
                int damage = calculator.calculateDamage(player.attack());

                if (calculator.criticalHit()) {
                    damage *= 2;
                    System.out.println("Critical Hit!");
                }

                enemy.takeDamage(damage);
                System.out.println("You dealt " + damage + " damage.");
                System.out.println("Enemy HP: " + enemy.getHealth());

            } else {
                int damage = calculator.calculateDamage(enemy.attack());
                player.takeDamage(damage);
                System.out.println("Enemy dealt " + damage + " damage.");
                System.out.println("Player HP: " + player.getHealth());
            }

            turnManager.switchTurn();
        }

        if (player.isAlive()) {
            System.out.println("You won!");
            player.gainExperience(50);
        } else {
            System.out.println("You lost!");
        }
    }
}
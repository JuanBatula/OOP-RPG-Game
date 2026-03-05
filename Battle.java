import java.util.Scanner;

class Battle {
    private Player player;
    private Enemy enemy;
    private Scanner scanner;

    public Battle(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.scanner = new Scanner(System.in);
    }

    public void startBattle() {
        System.out.println("\n=== BATTLE START ===");
        System.out.println(player.getName() + " vs " + enemy.getName());

        while (player.isAlive() && enemy.isAlive()) {
            playerTurn();

            if (enemy.isAlive()) {
                enemyTurn();
            }
        }

        checkWinner();
    }

    public void playerTurn() {
        System.out.println("\n" + player.getName() + "'s turn!");
        System.out.println("1. Attack");
        System.out.println("2. Skip");
        System.out.print("Choose: ");

        int choice = scanner.nextInt();

        if (choice == 1) {
            int damage = player.attack();   // get damage
            enemy.takeDamage(damage);       // apply damage
        } else {
            System.out.println("You skipped your turn...");
        }
    }

    public void enemyTurn() {
        System.out.println("\n" + enemy.getName() + "'s turn!");

        int damage = enemy.attack();     // get damage
        player.takeDamage(damage);       // apply damage
    }

    public void checkWinner() {
        System.out.println("\n=== BATTLE OVER ===");

        if (player.isAlive()) {
            System.out.println("YOU WON! " + enemy.getName() + " defeated!");
            player.gainExperience(enemy.giveExperience());
        } else {
            System.out.println("YOU LOST! Game over...");
        }
    }
}
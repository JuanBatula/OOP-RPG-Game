import java.util.Scanner;

public class Game {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== SIMPLE RPG BATTLE ===");
        System.out.println("1. Start Game");
        System.out.println("2. Exit");
        System.out.print("Choose: ");
        
        int choice = scanner.nextInt();
        
        if (choice == 1) {
            startGame();
        } else {
            System.out.println("Goodbye!");
        }
        
        scanner.close();
    }
    
    public static void startGame() {
        Scanner scanner = new Scanner(System.in);
        
        // select character
        System.out.println("\nChoose your character:");
        System.out.println("1. Warrior (High HP, Medium Attack)");
        System.out.println("2. Mage (Medium HP, High Attack)");
        System.out.print("Choose: ");
        
        int charChoice = scanner.nextInt();
        Player player;
        
        if (charChoice == 1) {
            player = new Player("Warrior", 60, 12);
            System.out.println("You chose Warrior!");
        } else {
            player = new Player("Mage", 40, 18);
            System.out.println("You chose Mage!");
        }
        
        // himo inventory nya add items
        Inventory inventory = new Inventory();
        inventory.addItem(new Weapon("Iron Sword", 5));
        inventory.addItem(new Potion("Health Potion", 20));
        
        inventory.showItems();
        
        // start stages
        int currentStage = 1;
        boolean gameRunning = true;
        
        while (gameRunning && player.isAlive()) {
            Stage stage = new Stage(currentStage);
            Enemy enemy = stage.generateEnemy();
            
            System.out.println("\n=== STAGE " + currentStage + " ===");
            System.out.println("A " + enemy.getName() + " appears! (Difficulty: " + enemy.getDifficulty() + ")");
            
            Battle battle = new Battle(player, enemy);
            battle.startBattle();
            
            if (player.isAlive()) {
                System.out.println("\nMoving to next stage...");
                currentStage++;
                
                // heal between stages
                System.out.println("You rest and recover a bit.");
                // would heal the player but I kept it simple
            } else {
                gameRunning = false;
            }
        }
        
        System.out.println("\nThanks for playing!");
        scanner.close();
    }
}
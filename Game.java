import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

class Inventory {
        private ArrayList<Item> items;
        
        public Inventory() {
            items = new ArrayList<>();
        }
        
        public void addItem(Item item) {
            items.add(item);
            System.out.println(item.name + " added to inventory!");
        }
        
        public void showItems() {
            System.out.println("\n=== INVENTORY ===");
            if (items.isEmpty()) {
                System.out.println("Inventory is empty!");
            } else {
                for (int i = 0; i < items.size(); i++) {
                    System.out.println((i+1) + ". " + items.get(i).name);
                }
            }
        }
    }

class Stage {
    private int stageNumber;
    private Random rand;
    
    public Stage(int stageNumber) {
        this.stageNumber = stageNumber;
        this.rand = new Random();
    }
    
    public Enemy generateEnemy() {
        String[] enemyNames = {"Goblin", "Orc", "Skeleton", "Dark Knight", "Dragon"};
        String name = enemyNames[rand.nextInt(enemyNames.length)];
        int difficulty = stageNumber + rand.nextInt(3);
        return new Enemy(name, difficulty);
    }
}

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
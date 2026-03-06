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
                String type = (items.get(i) instanceof Potion) ? "[Potion]" : "[Weapon]";
                System.out.println((i+1) + ". " + type + " " + items.get(i).name);
            }
        }
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public int getSize() {
        return items.size();
    }
    
    public Item getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }
    
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }
}

class Stage {
    private int stageNumber;
    private Random random;
    
    public Stage(int stageNumber) {
        this.stageNumber = stageNumber;
        this.random = new Random();
    }
    
    public Enemy generateEnemy() {
        String[] enemyNames = {"Goblin", "Orc", "Skeleton", "Dark Knight", "Dragon"};
        String[] difficulties = {"Easy", "Medium", "Hard"};
        
        String name = enemyNames[random.nextInt(enemyNames.length)];
        int baseHealth = 30 + (stageNumber * 15);
        int baseAttack = 5 + (stageNumber * 3);
        String difficulty = difficulties[Math.min(stageNumber-1, difficulties.length-1)];
        
        if (stageNumber > 3) difficulty = "Hard";
        else if (stageNumber > 1) difficulty = "Medium";
        
        return new Enemy(name, baseHealth, baseAttack, difficulty);
    }
    
    public int getStageNumber() {
        return stageNumber;
    }
}


public class SimpleRPG {
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
        
        System.out.println("\nChoose your character:");
        System.out.println("1. Warrior (High HP, Medium Attack)");
        System.out.println("2. Mage (Medium HP, High Attack)");
        System.out.print("Choose: ");
        
        int charChoice = scanner.nextInt();
        Player player;
        
        if (charChoice == 1) {
            player = new Player("Warrior", 80, 12);
            System.out.println("You chose Warrior!");
        } else {
            player = new Player("Mage", 50, 18);
            System.out.println("You chose Mage!");
        }
        
        Inventory inventory = new Inventory();
        inventory.addItem(new Weapon("Iron Sword", 5));
        inventory.addItem(new Potion("Health Potion", 30));
        inventory.addItem(new Potion("Health Potion", 30));
        
        inventory.showItems();
        
        int currentStage = 1;
        boolean gameRunning = true;
        
        while (gameRunning && player.isAlive()) {
            Stage stage = new Stage(currentStage);
            Enemy enemy = stage.generateEnemy();
            
            System.out.println("\n=== STAGE " + currentStage + " ===");
            System.out.println("A " + enemy.getName() + " appears! (Difficulty: " + enemy.getDifficulty() + ")");
            System.out.println("Enemy Stats - HP: " + enemy.getHealth() + ", Attack: " + enemy.getAttackPower());
            
            Battle battle = new Battle(player, enemy, inventory);
            battle.startBattle();
            
            if (player.isAlive()) {
                System.out.println("\n=== STAGE CLEAR! ===");
                System.out.println("Moving to next stage...");
                currentStage++;
                
                player.heal(20);
                
                if (currentStage % 2 == 0) {
                    inventory.addItem(new Potion("Health Potion", 30));
                }
            } else {
                gameRunning = false;
            }
        }
        
        System.out.println("\nThanks for playing! You reached Stage " + currentStage);
        scanner.close();
    }
}
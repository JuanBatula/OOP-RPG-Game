import java.util.Scanner;
import java.util.Random;

class Battle {
    private Player player;
    private Enemy enemy;
    private Inventory inventory;
    private Scanner scanner;
    
    public Battle(Player player, Enemy enemy, Inventory inventory) {
        this.player = player;
        this.enemy = enemy;
        this.inventory = inventory;
        this.scanner = new Scanner(System.in);
    }
    
    public void startBattle() {
        System.out.println("\n=== BATTLE START ===");
        System.out.println(player.getName() + " vs " + enemy.getName() + " (" + enemy.getDifficulty() + ")");
        
        while (player.isAlive() && enemy.isAlive()) {
            playerTurn();
            if (enemy.isAlive()) {
                enemyTurn();
            }
        }
        
        checkWinner();
    }
    
    public void playerTurn() {
        System.out.println("\n" + player.getName() + "'s turn! (HP: " + player.getHealth() + "/" + player.getMaxHealth() + ")");
        System.out.println("1. Attack");
        System.out.println("2. Use Item");
        System.out.println("3. Skip");
        System.out.print("Choose: ");
        
        int choice = scanner.nextInt();
        
        if (choice == 1) {
            int damage = player.attack();
            enemy.takeDamage(damage);
        } else if (choice == 2) {
            useItem();
        } else {
            System.out.println("You skipped your turn...");
        }
    }
    
    public void useItem() {
        if (inventory.isEmpty()) {
            System.out.println("No items to use!");
            return;
        }
        
        inventory.showItems();
        System.out.print("Choose item to use (0 to cancel): ");
        int itemChoice = scanner.nextInt();
        
        if (itemChoice == 0) {
            System.out.println("Cancelled.");
            return;
        }
        
        if (itemChoice > 0 && itemChoice <= inventory.getSize()) {
            Item item = inventory.getItem(itemChoice - 1);
            
            if (item instanceof Potion) {
                Potion potion = (Potion) item;
                player.heal(potion.getHealAmount());
                inventory.removeItem(itemChoice - 1);
                System.out.println("Used " + item.name + "!");
            } else {
                System.out.println("Can't use that item in battle!");
            }
        } else {
            System.out.println("Invalid choice!");
        }
    }
    
    public void enemyTurn() {
        System.out.println("\n" + enemy.getName() + "'s turn!");
        int damage = enemy.attack();
        player.takeDamage(damage);
    }
    
    public void checkWinner() {
        System.out.println("\n=== BATTLE OVER ===");
        
        if (player.isAlive()) {
            System.out.println("YOU WON! " + enemy.getName() + " defeated!");
            int expGained = enemy.getExpValue();
            player.gainExp(expGained);
            
            if (rand.nextInt(100) < 30) {
                Potion potion = new Potion("Health Potion", 20);
                inventory.addItem(potion);
                System.out.println("Enemy dropped a Health Potion!");
            }
        } else {
            System.out.println("YOU LOST! Game over...");
        }
    }
    
    private Random rand = new Random();
}

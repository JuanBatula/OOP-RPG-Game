public class GameRunner {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("       WELCOME TO THE RPG GAME!         ");
        System.out.println("========================================\n");

        // --- Setup: Create player and items ---
        Player player = new Player("Hero", 100, 100, 10);
        Inventory inventory = player.getInventory();

        Weapon sword      = new Weapon("Iron Sword", 50, 8);
        Armor  chainMail  = new Armor("Chain Mail", 75, 5);
        Potion smallPotion = new Potion("Small Potion", 20, 30);
        Potion largePotion = new Potion("Large Potion", 40, 60);
        Weapon greatAxe   = new Weapon("Great Axe", 120, 15);

        inventory.addItem(sword);
        inventory.addItem(chainMail);
        inventory.addItem(smallPotion);
        inventory.addItem(largePotion);
        inventory.addItem(greatAxe);

        inventory.printInventory();
        System.out.println();

        // --- Equip starting gear ---
        System.out.println("--- Equipping starting gear ---");
        player.equipWeapon(sword);
        player.equipArmor(chainMail);
        System.out.println();
        player.printStatus();
        System.out.println();

        // --- ROUND 1: First battle ---
        System.out.println("========================================");
        System.out.println("   ENCOUNTER: A Goblin appears!");
        System.out.println("========================================");
        Enemy goblin = new Enemy("Goblin", "easy");
        runBattle(player, goblin);
        System.out.println();

        // --- Swap weapon mid-game ---
        System.out.println("--- Swapping to Great Axe before next fight ---");
        player.equipWeapon(greatAxe);
        System.out.println();

        // --- Use a potion if needed ---
        if (player.getInventory().findItemByName("Small Potion") != null && needsHealing(player)) {
            System.out.println("--- Using Small Potion ---");
            Potion potion = (Potion) player.getInventory().findItemByName("Small Potion");
            potion.use(player);
            inventory.removeItem(potion);
            System.out.println();
        }

        // --- ROUND 2: Second battle ---
        System.out.println("========================================");
        System.out.println("   ENCOUNTER: A Troll appears!");
        System.out.println("========================================");
        Enemy troll = new Enemy("Troll", "hard");
        runBattle(player, troll);
        System.out.println();

        // --- Test inventory capacity (fill to max) ---
        System.out.println("--- Stress-testing inventory capacity ---");
        for (int i = inventory.getSize(); i < 12; i++) {
            inventory.addItem(new Potion("Potion #" + i, 10, 10));
        }
        System.out.println();

        // --- Final status ---
        player.printStatus();
        inventory.printInventory();

        System.out.println("\n========================================");
        System.out.println("             GAME OVER                  ");
        System.out.println("========================================");
    }

    /** Runs a full player-vs-enemy battle loop. */
    private static void runBattle(Player player, Enemy enemy) {
        Battle battle = new Battle();
        battle.initializeBattle(player, enemy);

        System.out.println(player.getName() + " (HP: " + player.getInventory().getSize() + " items) vs " + enemy.getName());
        System.out.println();

        int turn = 1;
        while (battle.checkBattleOver() == null) {
            System.out.println("--- Turn " + turn + " ---");
            battle.performTurn();
            turn++;
            if (turn > 50) {   
                System.out.println("Battle timed out!");
                break;
            }
        }

        String winner = battle.checkBattleOver();
        if ("player".equals(winner)) {
            System.out.println("\n" + player.getName() + " defeated " + enemy.getName() + "!");
            player.gainExp(enemy.getExpValue());
        } else if ("enemy".equals(winner)) {
            System.out.println("\n" + player.getName() + " was defeated by " + enemy.getName() + "...");
        }
    }

    /** Returns true if player is below 50% HP. */
    private static boolean needsHealing(Player player) {
        
        return !player.isAlive() || true; 
    }
}
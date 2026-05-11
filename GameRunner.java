import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameRunner {

    private static final Scanner scanner = new Scanner(System.in);
    private static Player player;
    private static Inventory inventory;
    private static RunSummary summary;
    private static EnemyFactory factory;
    private static int currentLevel = 1;
    private static int gold = 0;

    // =========================================================================
    // ENTRY POINT
    // =========================================================================

    public static void main(String[] args) {
        printBanner();
        printStoryIntro();

        summary = new RunSummary();
        factory = new EnemyFactory();

        String playerName = promptPlayerName();
        player = new Player(playerName, 100, 100, 10);
        inventory = player.getInventory();

        // Grant starting gear
        Weapon startSword = new Weapon("Iron Sword", 50, 8);
        Armor  startArmor = new Armor("Chain Mail", 75, 5);
        Potion startPotion = new Potion("Small Potion", 20, 30);

        inventory.addItem(startSword);
        inventory.addItem(startArmor);
        inventory.addItem(startPotion);

        player.equipWeapon(startSword);
        player.equipArmor(startArmor);

        // Unlock a starting ability
        player.unlockAbility(new FireballAbility());

        printDivider();
        System.out.println("  Welcome, " + playerName + "! Your journey begins...");
        printDivider();
        pause();

        mainMenuLoop();
    }

    // =========================================================================
    // MAIN MENU
    // =========================================================================

    private static void mainMenuLoop() {
        while (true) {
            printHeader("MAIN MENU");
            System.out.println("  Gold: " + gold + "g   |   Level: " + player.getLevel()
                    + "   |   HP: " + player.getHealth() + "/" + player.getMaxHealth());
            printDivider();
            System.out.println("  [1] Explore (find an enemy to fight)");
            System.out.println("  [2] View Stats");
            System.out.println("  [3] View Inventory");
            System.out.println("  [4] View Abilities");
            System.out.println("  [5] Save Game");
            System.out.println("  [6] Quit");
            printDivider();

            int choice = promptInt("Choose an option: ", 1, 6);
            switch (choice) {
                case 1: exploreMenu();    break;
                case 2: showStats();      break;
                case 3: inventoryMenu();  break;
                case 4: abilitiesMenu();  break;
                case 5: saveGame();       break;
                case 6: quitGame();       return;
            }
        }
    }

    // =========================================================================
    // EXPLORE
    // =========================================================================

    private static void exploreMenu() {
        printHeader("EXPLORE");
        System.out.println("  You venture into the wilderness...");
        System.out.println();
        System.out.println("  [1] Search for Goblin  (Easy)");
        System.out.println("  [2] Search for Troll   (Medium)");
        System.out.println("  [3] Challenge the Boss (Hard)");
        System.out.println("  [4] Back");
        printDivider();

        int choice = promptInt("Choose an enemy: ", 1, 4);
        if (choice == 4) return;

        String[] types = { "goblin", "troll", "boss" };
        Enemy enemy = factory.create(types[choice - 1], currentLevel);

        runBattle(enemy);
    }

    // =========================================================================
    // BATTLE LOOP
    // =========================================================================

    private static void runBattle(Enemy enemy) {
        printHeader("BATTLE START");
        System.out.println("  " + player.getName() + " vs " + enemy.getName() + "!");
        printDivider();

        Battle battle = new Battle();
        battle.initializeBattle(player, enemy);
        battle.setRunSummary(summary);

        try {
            int turn = 1;
            while (battle.checkBattleOver() == null) {
                printTurnHeader(turn, enemy);

                tickStatusEffects(player);
                if (!player.isAlive()) break;

                boolean acted = playerTurn(enemy, battle);

                if (acted && enemy.isAlive() && player.isAlive()) {
                    enemyTurn(enemy, player);
                }

                player.getSkillTree().tickAllCooldowns();

                if (battle.checkBattleOver() != null) break;

                turn++;
                if (turn > 100) {
                    System.out.println("\n  [The battle drags on too long — both sides retreat!]");
                    return;
                }
            }
        } catch (FleeException e) {
            return; // Return cleanly to main menu
        }

        String result = battle.checkBattleOver();
        if ("player".equals(result)) {
            onVictory(enemy);
        } else {
            onDefeat();
        }
    }

    private static void tickStatusEffects(Player p) {
        List<StatusEffect> effects = p.getStatusEffectManager().getActiveEffects();
        if (!effects.isEmpty()) {
            System.out.println("  [Status Effects ticking for " + p.getName() + "]");
            p.getStatusEffectManager().tickAll(p);
        }
    }

    private static boolean playerTurn(Enemy enemy, Battle battle) {
        while (true) {
            printHeader("YOUR TURN");
            printCombatStatus(player, enemy);
            printPlayerMenu();

            int choice = promptInt("Action: ", 1, 4);

            switch (choice) {
                case 1: // Attack
                    performPlayerAttack(enemy, battle);
                    return true;

                case 2: // Use Ability
                    if (useAbilityMenu(enemy)) return true;
                    break; // re-show menu if no ability used

                case 3: // Use Item
                    if (useItemMenu()) return true;
                    break;

                case 4: // Flee
                    if (tryFlee()) return false;
                    break;
            }
        }
    }

    private static void performPlayerAttack(Enemy enemy, Battle battle) {
        int damage = player.calculateTotalDamage() - enemy.getDefense();
        damage = Math.max(1, damage);
        System.out.println();
        System.out.println("  " + player.getName() + " attacks " + enemy.getName()
                + " for " + damage + " damage!");
        enemy.takeDamage(player.calculateTotalDamage()); // uses Enemy.takeDamage which applies defense
        summary.recordDamage(damage);
        if (!enemy.isAlive()) summary.recordKill();
    }

    private static boolean useAbilityMenu(Enemy enemy) {
        List<Ability> abilities = new ArrayList<>(player.getSkillTree().getAllAbilities());
        if (abilities.isEmpty()) {
            System.out.println("\n  You have no abilities unlocked!");
            pause();
            return false;
        }

        printHeader("USE ABILITY");
        for (int i = 0; i < abilities.size(); i++) {
            Ability a = abilities.get(i);
            String status = a.isReady() ? "READY" : "Cooldown: " + a.getCurrentCooldown() + " turn(s)";
            System.out.println("  [" + (i + 1) + "] " + a.getName()
                    + " — " + a.getDescription() + " | " + status);
        }
        System.out.println("  [" + (abilities.size() + 1) + "] Cancel");
        printDivider();

        int choice = promptInt("Choose ability: ", 1, abilities.size() + 1);
        if (choice == abilities.size() + 1) return false;

        Ability chosen = abilities.get(choice - 1);
        if (!chosen.isReady()) {
            System.out.println("\n  " + chosen.getName() + " is still on cooldown!");
            pause();
            return false;
        }

        chosen.use(player, enemy);
        return true;
    }

    private static boolean useItemMenu() {
        List<Item> items = inventory.getItems();
        if (items.isEmpty()) {
            System.out.println("\n  Your inventory is empty!");
            pause();
            return false;
        }

        // Filter to usable items only
        List<Item> usable = new ArrayList<>();
        for (Item item : items) {
            if (item instanceof Potion || item instanceof Elixir || item instanceof Antidote) {
                usable.add(item);
            }
        }

        if (usable.isEmpty()) {
            System.out.println("\n  No usable consumable items in inventory!");
            pause();
            return false;
        }

        printHeader("USE ITEM");
        for (int i = 0; i < usable.size(); i++) {
            Item it = usable.get(i);
            String detail = "";
            if (it instanceof Potion)   detail = " (Heals " + ((Potion) it).getHealAmount() + " HP)";
            if (it instanceof Elixir)   detail = " (+ATK " + ((Elixir) it).getAttackBoost() + ")";
            if (it instanceof Antidote) detail = " (Clears status effects)";
            System.out.println("  [" + (i + 1) + "] " + it.getItemName() + detail);
        }
        System.out.println("  [" + (usable.size() + 1) + "] Cancel");
        printDivider();

        int choice = promptInt("Choose item: ", 1, usable.size() + 1);
        if (choice == usable.size() + 1) return false;

        Item chosen = usable.get(choice - 1);
        chosen.use(player);
        inventory.removeItem(chosen);
        return true;
    }

    // Thrown when the player successfully flees a battle
    private static class FleeException extends RuntimeException {}

    private static boolean tryFlee() {
        double fleeChance = 0.40;
        System.out.println();
        if (Math.random() < fleeChance) {
            System.out.println("  You successfully flee from battle!");
            pause();
            throw new FleeException();
        } else {
            System.out.println("  You failed to flee! The enemy blocks your escape.");
            pause();
            return false;
        }
    }

    private static void enemyTurn(Enemy enemy, Player p) {
        printHeader("ENEMY TURN — " + enemy.getName().toUpperCase());
        int damage = enemy.getAttackDamage(enemy.getHealth(), enemy.getMaxHealth());
        if (damage > 0) {
            p.takeDamage(damage);
        }
        // Enemy ability: chance to apply poison (for flavour / uses existing system)
        if (Math.random() < 0.15) {
            PoisonEffect poison = new PoisonEffect();
            p.getStatusEffectManager().addEffect(poison, p);
        }
    }

    // =========================================================================
    // VICTORY / DEFEAT
    // =========================================================================

    private static void onVictory(Enemy enemy) {
        int xp = enemy.getExpValue();
        int goldEarned = enemy.getExpValue() / 5 + (int)(Math.random() * 20);

        printHeader("VICTORY!");
        System.out.println("  " + player.getName() + " defeated " + enemy.getName() + "!");
        System.out.println("  Rewards: +" + xp + " EXP  |  +" + goldEarned + " gold");
        printDivider();

        gold += goldEarned;
        int oldLevel = player.getLevel();
        player.gainExp(xp);
        if (player.getLevel() > oldLevel) {
            onLevelUp();
        }

        summary.recordStageCleared();
        currentLevel++;

        // Random item drop
        if (Math.random() < 0.50) {
            Item drop = randomDrop();
            if (!inventory.isFull()) {
                inventory.addItem(drop);
                System.out.println("  Item dropped: " + drop.getItemName() + "!");
            } else {
                System.out.println("  " + drop.getItemName() + " dropped but inventory is full!");
            }
        }

        pause();
    }

    private static Item randomDrop() {
        double r = Math.random();
        if (r < 0.4) return new Potion("Health Potion", 20, 40);
        if (r < 0.6) return new Elixir("Power Elixir", 30, 3);
        if (r < 0.8) return new Antidote("Antidote", 15);
        return new Weapon("Sharp Dagger", 60, 5);
    }

    private static void onLevelUp() {
        printHeader("LEVEL UP!");
        System.out.println("  " + player.getName() + " is now Level " + player.getLevel() + "!");
        System.out.println("  Max HP increased! Attack Power increased!");
        printDivider();

        // Unlock new ability at certain levels
        if (player.getLevel() == 2) {
            player.unlockAbility(new ShieldBashAbility());
            System.out.println("  New ability unlocked: Shield Bash!");
        }
        pause();
    }

    private static void onDefeat() {
        printHeader("DEFEAT");
        System.out.println("  " + player.getName() + " has fallen...");
        printDivider();
        summary.printReport();

        System.out.println("  [1] Restart");
        System.out.println("  [2] Quit");
        int choice = promptInt("Choose: ", 1, 2);
        if (choice == 1) {
            main(new String[]{});
        } else {
            quitGame();
        }
    }

    // =========================================================================
    // BETWEEN-BATTLE MENUS
    // =========================================================================

    private static void showStats() {
        printHeader("CHARACTER STATS");
        System.out.println("  Name:    " + player.getName());
        System.out.println("  Level:   " + player.getLevel());
        System.out.println("  HP:      " + player.getHealth() + " / " + player.getMaxHealth());
        System.out.println("  ATK:     " + player.calculateTotalDamage()
                + " (base: " + player.getBaseAttackPower() + ")");
        System.out.println("  DEF:     " + player.getDefense());
        System.out.println("  EXP:     " + player.getExp() + " / 100");
        System.out.println("  Gold:    " + gold + "g");
        System.out.println("  Weapon:  " + (player.getEquippedWeapon() != null
                ? player.getEquippedWeapon().getItemName() : "none"));
        System.out.println("  Armor:   " + (player.getEquippedArmor() != null
                ? player.getEquippedArmor().getItemName() : "none"));
        System.out.println("  Status:");
        player.getStatusEffectManager().printEffects();
        printDivider();
        promptEnter();
    }

    private static void inventoryMenu() {
        while (true) {
            printHeader("INVENTORY (" + inventory.getSize() + "/" + inventory.getCapacity() + ")");
            List<Item> items = inventory.getItems();
            if (items.isEmpty()) {
                System.out.println("  (empty)");
            } else {
                for (int i = 0; i < items.size(); i++) {
                    Item item = items.get(i);
                    String tag = "";
                    if (item == player.getEquippedWeapon()) tag = " [E]";
                    if (item == player.getEquippedArmor())  tag = " [E]";
                    System.out.println("  [" + (i + 1) + "] " + item.getItemName()
                            + tag + "  (value: " + item.getValue() + "g)");
                }
            }
            printDivider();
            System.out.println("  [E] Equip a Weapon/Armor");
            System.out.println("  [B] Back");
            printDivider();

            String input = promptString("Choose: ").trim().toUpperCase();
            if (input.equals("B")) return;
            if (input.equals("E")) { equipMenu(); continue; }

            // Try numeric selection for item detail
            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < items.size()) {
                    showItemDetail(items.get(idx));
                } else {
                    System.out.println("  Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  Invalid choice.");
            }
        }
    }

    private static void showItemDetail(Item item) {
        printHeader(item.getItemName().toUpperCase());
        System.out.println("  Type:  " + item.getClass().getSimpleName());
        System.out.println("  Value: " + item.getValue() + "g");
        if (item instanceof Weapon)  System.out.println("  Bonus Damage: +" + ((Weapon) item).getBonusDamage());
        if (item instanceof Armor)   System.out.println("  Defense Bonus: +" + ((Armor) item).getBaseDefenseBonus());
        if (item instanceof Potion)  System.out.println("  Heals: " + ((Potion) item).getHealAmount() + " HP");
        if (item instanceof Elixir)  System.out.println("  Attack Boost: +" + ((Elixir) item).getAttackBoost());
        printDivider();
        promptEnter();
    }

    private static void equipMenu() {
        List<Item> equipable = new ArrayList<>();
        for (Item item : inventory.getItems()) {
            if (item instanceof Weapon || item instanceof Armor) equipable.add(item);
        }

        if (equipable.isEmpty()) {
            System.out.println("\n  No weapons or armor in inventory.");
            pause();
            return;
        }

        printHeader("EQUIP GEAR");
        for (int i = 0; i < equipable.size(); i++) {
            Item it = equipable.get(i);
            String eq = (it == player.getEquippedWeapon() || it == player.getEquippedArmor()) ? " [EQUIPPED]" : "";
            System.out.println("  [" + (i + 1) + "] " + it.getItemName() + eq);
        }
        System.out.println("  [" + (equipable.size() + 1) + "] Cancel");
        printDivider();

        int choice = promptInt("Choose: ", 1, equipable.size() + 1);
        if (choice == equipable.size() + 1) return;

        Item chosen = equipable.get(choice - 1);
        if (chosen instanceof Weapon) player.equipWeapon((Weapon) chosen);
        else if (chosen instanceof Armor) player.equipArmor((Armor) chosen);
        pause();
    }

    private static void abilitiesMenu() {
        printHeader("ABILITIES");
        List<Ability> abilities = new ArrayList<>(player.getSkillTree().getAllAbilities());
        if (abilities.isEmpty()) {
            System.out.println("  No abilities unlocked yet.");
        } else {
            for (Ability a : abilities) {
                String status = a.isReady() ? "READY" : "Cooldown: " + a.getCurrentCooldown() + " turn(s)";
                System.out.println("  " + a.getName() + " [" + status + "]");
                System.out.println("    " + a.getDescription());
                System.out.println("    Cooldown: " + a.getCooldown() + " turn(s) after use");
                System.out.println();
            }
        }
        printDivider();
        promptEnter();
    }

    // =========================================================================
    // SAVE / QUIT
    // =========================================================================

    private static void saveGame() {
        printHeader("SAVE GAME");
        GameState gs = new GameState();
        gs.save(player, inventory, currentLevel, "savegame.txt");
        System.out.println("  Game saved successfully!");
        pause();
    }

    private static void quitGame() {
        printHeader("GAME OVER");
        summary.printReport();
        System.out.println("  Thanks for playing! Farewell, " + player.getName() + ".");
        printDivider();
        scanner.close();
        System.exit(0);
    }

    // =========================================================================
    // UI HELPERS
    // =========================================================================

    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║         CHRONICLES OF THE FALLEN         ║");
        System.out.println("  ║           A Text-Based Java RPG           ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printStoryIntro() {
        System.out.println("  The kingdom is in ruins. Dark creatures roam the land.");
        System.out.println("  You are the last hero brave enough to face them.");
        System.out.println("  Fight. Level up. Survive.");
        System.out.println();
    }

    private static void printDivider() {
        System.out.println("  ------------------------------------------");
    }

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("  ==========================================");
        System.out.printf("  %-42s%n", "  " + title);
        System.out.println("  ==========================================");
    }

    private static void printTurnHeader(int turn, Enemy enemy) {
        System.out.println();
        System.out.println("  --- Turn " + turn + " -------------------------------------------");
    }

    private static void printCombatStatus(Player p, Enemy e) {
        System.out.println("  " + p.getName() + " HP: " + p.getHealth() + "/" + p.getMaxHealth()
                + "  |  " + e.getName() + " HP: " + e.getHealth() + "/" + e.getMaxHealth());
        List<StatusEffect> effects = p.getStatusEffectManager().getActiveEffects();
        if (!effects.isEmpty()) {
            System.out.print("  Status: ");
            for (StatusEffect se : effects) {
                System.out.print("[" + se.getEffectName() + " " + se.getDuration() + "t] ");
            }
            System.out.println();
        }
        printDivider();
    }

    private static void printPlayerMenu() {
        System.out.println("  [1] Attack");
        System.out.println("  [2] Use Ability");
        System.out.println("  [3] Use Item");
        System.out.println("  [4] Flee (40% chance)");
        printDivider();
    }

    // =========================================================================
    // INPUT HELPERS
    // =========================================================================

    private static String promptPlayerName() {
        System.out.print("  Enter your hero's name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = "Hero";
        return name;
    }

    private static int promptInt(String prompt, int min, int max) {
        while (true) {
            System.out.print("  " + prompt);
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) return val;
                System.out.println("  Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input — please enter a number.");
            }
        }
    }

    private static String promptString(String prompt) {
        System.out.print("  " + prompt);
        return scanner.nextLine();
    }

    private static void promptEnter() {
        System.out.print("  Press Enter to continue...");
        scanner.nextLine();
    }

    private static void pause() {
        try { Thread.sleep(400); } catch (InterruptedException ignored) {}
        promptEnter();
    }
}
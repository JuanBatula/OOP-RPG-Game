package core;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import abilities.Ability;
import abilities.FireballAbility;
import abilities.ShieldBashAbility;
import combat.Battle;
import effects.PoisonEffect;
import effects.StatusEffect;
import entities.Enemy;
import combat.EnemyFactory;
import entities.Player;
import items.Antidote;
import items.Armor;
import items.Elixir;
import items.Inventory;
import items.Item;
import items.Potion;
import items.Weapon;
import shop.Shop;

/**
 * GameRunner — interactive entry point for Chronicles of the Fallen.
 *
 * All console output goes through Fmt.* helpers so colour is applied
 * consistently and can be toggled off with the --no-color CLI flag.
 *
 * Game logic lives in Battle, Player, Enemy, etc.
 * This class orchestrates menus, input, flow, and display only.
 */
public class GameRunner {

    // -------------------------------------------------------------------------
    // Static game state
    // -------------------------------------------------------------------------
    private static final Scanner scanner = new Scanner(System.in);
    private static Player        player;
    private static Inventory     inventory;
    private static RunSummary    summary;
    private static EnemyFactory  factory;
    private static int           currentLevel = 1;
    private static int           gold         = 0;
    private static Shop          shop;

    // =========================================================================
    // ENTRY POINT
    // =========================================================================

    public static void main(String[] args) {
        // Honour --no-color flag or NO_COLOR env var (no-color.org convention)
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--no-color")) { Fmt.COLOR = false; break; }
        }
        if (System.getenv("NO_COLOR") != null) Fmt.COLOR = false;

        printBanner();
        printStoryIntro();

        summary  = new RunSummary();
        factory  = new EnemyFactory();
        shop = new Shop();

        String playerName = promptPlayerName();
        player    = new Player(playerName, 100, 100, 10);
        inventory = player.getInventory();

        // Starting gear
        Weapon startSword  = new Weapon("Iron Sword",   50,  8);
        Armor  startArmor  = new Armor ("Chain Mail",   75,  5);
        Potion startPotion = new Potion("Small Potion", 20, 30);

        inventory.addItem(startSword);
        inventory.addItem(startArmor);
        inventory.addItem(startPotion);

        player.equipWeapon(startSword);
        player.equipArmor(startArmor);
        player.unlockAbility(new FireballAbility());

        Fmt.blank();
        Fmt.printDivider();
        Fmt.narrate("Welcome, " + Fmt.c(Fmt.B_CYAN, playerName) + "! Your journey begins...");
        Fmt.printDivider();
        pause();

        mainMenuLoop();
    }

    // =========================================================================
    // MAIN MENU
    // =========================================================================

    private static void mainMenuLoop() {
        while (true) {
            Fmt.printHeading("MAIN MENU");

            // Quick-glance status bar
            String hpCode = hpColorCode(player.getHealth(), player.getMaxHealth());
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.BR_YELLOW, "Gold: " + gold + "g")
                + Fmt.c(Fmt.DIM,       "   │   ")
                + Fmt.c(Fmt.WHITE,     "Level " + player.getLevel())
                + Fmt.c(Fmt.DIM,       "   │   ")
                + Fmt.c(hpCode,        "HP: " + player.getHealth() + "/" + player.getMaxHealth()));
            Fmt.blank();

            printOpt(1, "Explore              — find an enemy to fight");
            printOpt(2, "View Stats");
            printOpt(3, "View Inventory");
            printOpt(4, "View Abilities");
            printOpt(5, "Shop                 — spend your gold");
            printOpt(6, "Save Game");
            printOpt(7, "Quit");
            Fmt.blank();

            int choice = promptInt("Choose: ", 1, 7);
            switch (choice) {
                case 1: exploreMenu();   break;
                case 2: showStats();     break;
                case 3: inventoryMenu(); break;
                case 4: abilitiesMenu(); break;
                case 5: shopMenu();      break;
                case 6: saveGame();      break;
                case 7: quitGame();      return;
            }
        }
    }

    // =========================================================================
    // EXPLORE
    // =========================================================================

    private static void exploreMenu() {
        Fmt.printHeading("EXPLORE");
        Fmt.narrate("You venture into the wilderness, senses sharp...");
        Fmt.blank();

        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_YELLOW, "[1]")
            + Fmt.c(Fmt.WHITE,    " Goblin")
            + Fmt.c(Fmt.DIM,      "  ·················  ")
            + Fmt.c(Fmt.BR_GREEN, "Easy"));
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_YELLOW, "[2]")
            + Fmt.c(Fmt.WHITE,    " Troll")
            + Fmt.c(Fmt.DIM,      "   ·················  ")
            + Fmt.c(Fmt.YELLOW,   "Medium"));
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_YELLOW, "[3]")
            + Fmt.c(Fmt.WHITE,    " Boss")
            + Fmt.c(Fmt.DIM,      "    ·················  ")
            + Fmt.c(Fmt.BR_RED,   "Hard"));
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.DIM, "[4] Back"));
        Fmt.blank();

        int choice = promptInt("Choose enemy: ", 1, 4);
        if (choice == 4) return;

        String[] types = { "goblin", "troll", "boss" };
        Enemy enemy = factory.create(types[choice - 1], currentLevel);
        runBattle(enemy);
    }

    // =========================================================================
    // BATTLE LOOP
    // =========================================================================

    private static void runBattle(Enemy enemy) {
        Fmt.printHeading("BATTLE");
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN, player.getName())
            + Fmt.c(Fmt.WHITE,  "  vs  ")
            + Fmt.c(Fmt.B_RED,  enemy.getName() + "!"));
        Fmt.blank();
        Fmt.printHpBar(player.getName(), player.getHealth(), player.getMaxHealth());
        Fmt.printHpBar(enemy.getName(),  enemy.getHealth(),  enemy.getMaxHealth());
        Fmt.blank();
        Fmt.printDivider();
        pause();

        Battle battle = new Battle();
        battle.initializeBattle(player, enemy);
        battle.setRunSummary(summary);

        try {
            int turn = 1;
            while (battle.checkBattleOver() == null) {
                Fmt.printTurnHeading(turn);

                tickStatusEffects(player);
                if (!player.isAlive()) break;

                boolean acted = playerTurn(enemy);

                if (acted && enemy.isAlive() && player.isAlive()) {
                    enemyTurn(enemy);
                }

                player.getSkillTree().tickAllCooldowns();

                if (battle.checkBattleOver() != null) break;
                turn++;
                if (turn > 100) {
                    Fmt.warn("The battle drags on endlessly — both sides retreat!");
                    return;
                }
            }
        } catch (FleeException e) {
            Fmt.narrate("You slip away into the shadows...");
            pause();
            return;
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
        if (effects.isEmpty()) return;
        Fmt.status("― Status effects tick for " + p.getName() + " ―");
        p.getStatusEffectManager().tickAll(p);
        Fmt.blank();
    }

    // =========================================================================
    // PLAYER TURN
    // =========================================================================

    private static boolean playerTurn(Enemy enemy) {
        while (true) {
            printCombatPanel(enemy);

            System.out.println(Fmt.INDENT + Fmt.c(Fmt.B_YELLOW, "[1]") + Fmt.c(Fmt.WHITE, " Attack"));
            System.out.println(Fmt.INDENT + Fmt.c(Fmt.B_YELLOW, "[2]") + Fmt.c(Fmt.WHITE, " Use Ability"));
            System.out.println(Fmt.INDENT + Fmt.c(Fmt.B_YELLOW, "[3]") + Fmt.c(Fmt.WHITE, " Use Item"));
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.B_YELLOW, "[4]")
                + Fmt.c(Fmt.DIM,      " Flee  (40% chance)"));
            Fmt.blank();

            int choice = promptInt("Action: ", 1, 4);
            switch (choice) {
                case 1:
                    performPlayerAttack(enemy);
                    return true;
                case 2:
                    if (useAbilityMenu(enemy)) return true;
                    break;
                case 3:
                    if (useItemMenu()) return true;
                    break;
                case 4:
                    tryFlee(); // throws FleeException on success; returns false on failure
                    break;
            }
        }
    }

    private static void printCombatPanel(Enemy enemy) {
        Fmt.printDivider();
        Fmt.printHpBar(player.getName(), player.getHealth(), player.getMaxHealth());
        Fmt.printHpBar(enemy.getName(),  enemy.getHealth(),  enemy.getMaxHealth());

        List<StatusEffect> effects = player.getStatusEffectManager().getActiveEffects();
        if (!effects.isEmpty()) {
            StringBuilder sb = new StringBuilder(Fmt.INDENT);
            for (StatusEffect se : effects) {
                sb.append(Fmt.c(Fmt.MAGENTA, "[" + se.getEffectName() + " " + se.getDuration() + "t] "));
            }
            System.out.println(sb);
        }
        Fmt.printDivider();
    }

    // ---- Attack -------------------------------------------------------------

    private static void performPlayerAttack(Enemy enemy) {
        int raw    = player.calculateTotalDamage();
        int damage = Math.max(1, raw - enemy.getDefense());
        Fmt.blank();
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN, player.getName())
            + Fmt.c(Fmt.GREEN,  " strikes ")
            + Fmt.c(Fmt.B_RED,  enemy.getName())
            + Fmt.c(Fmt.GREEN,  " for ")
            + Fmt.c(Fmt.BOLD,   String.valueOf(damage))
            + Fmt.c(Fmt.GREEN,  " damage!"));
        enemy.takeDamage(raw);   // takeDamage applies defense internally
        summary.recordDamage(damage);
        if (!enemy.isAlive()) summary.recordKill();
        Fmt.blank();
    }

    // ---- Ability menu -------------------------------------------------------

    private static boolean useAbilityMenu(Enemy enemy) {
        List<Ability> abilities = new ArrayList<>(player.getSkillTree().getAllAbilities());
        if (abilities.isEmpty()) {
            Fmt.warn("You have no abilities unlocked!");
            pause();
            return false;
        }

        Fmt.printHeading("USE ABILITY");
        for (int i = 0; i < abilities.size(); i++) {
            Ability a   = abilities.get(i);
            String ready = a.isReady()
                    ? Fmt.c(Fmt.BR_GREEN, "READY")
                    : Fmt.c(Fmt.BR_RED,   "Cooldown: " + a.getCurrentCooldown() + "t");
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.B_YELLOW, "[" + (i + 1) + "]")
                + Fmt.c(Fmt.WHITE,    " " + a.getName())
                + "  " + ready);
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.DIM, "    " + a.getDescription()));
        }
        int cancel = abilities.size() + 1;
        System.out.println(Fmt.INDENT + Fmt.c(Fmt.DIM, "[" + cancel + "] Cancel"));
        Fmt.blank();

        int choice = promptInt("Choose ability: ", 1, cancel);
        if (choice == cancel) return false;

        Ability chosen = abilities.get(choice - 1);
        if (!chosen.isReady()) {
            Fmt.warn(chosen.getName() + " is still on cooldown!");
            pause();
            return false;
        }

        Fmt.blank();
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN,    player.getName())
            + Fmt.c(Fmt.MAGENTA,   " uses ")
            + Fmt.c(Fmt.B_MAGENTA, chosen.getName()) + Fmt.c(Fmt.MAGENTA, "!"));
        chosen.use(player, enemy);
        Fmt.blank();
        return true;
    }

    // ---- Item menu ----------------------------------------------------------

    private static boolean useItemMenu() {
        List<Item> usable = new ArrayList<>();
        for (Item item : inventory.getItems()) {
            if (item instanceof Potion || item instanceof Elixir || item instanceof Antidote) {
                usable.add(item);
            }
        }

        if (usable.isEmpty()) {
            Fmt.warn("No usable consumable items in your inventory!");
            pause();
            return false;
        }

        Fmt.printHeading("USE ITEM");
        for (int i = 0; i < usable.size(); i++) {
            Item it = usable.get(i);
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.B_YELLOW, "[" + (i + 1) + "]")
                + Fmt.c(Fmt.WHITE,    " " + it.getItemName())
                + Fmt.c(Fmt.DIM,      itemDetail(it)));
        }
        int cancel = usable.size() + 1;
        System.out.println(Fmt.INDENT + Fmt.c(Fmt.DIM, "[" + cancel + "] Cancel"));
        Fmt.blank();

        int choice = promptInt("Choose item: ", 1, cancel);
        if (choice == cancel) return false;

        Item chosen = usable.get(choice - 1);
        Fmt.blank();
        chosen.use(player);
        inventory.removeItem(chosen);
        Fmt.blank();
        return true;
    }

    private static String itemDetail(Item it) {
        if (it instanceof Potion)   return "  — Heals " + ((Potion) it).getHealAmount() + " HP";
        if (it instanceof Elixir)   return "  — +" + ((Elixir) it).getAttackBoost() + " Attack Power";
        if (it instanceof Antidote) return "  — Clears all status effects";
        return "";
    }

    // ---- Flee ---------------------------------------------------------------

    // Thrown on successful flee to unwind the battle loop cleanly.
    private static class FleeException extends RuntimeException {}

    private static void tryFlee() {
        Fmt.blank();
        if (Math.random() < 0.40) {
            Fmt.success("You break away from the fight!");
            throw new FleeException();
        } else {
            Fmt.danger("The enemy cuts off your escape!");
            pause();
        }
    }

    // =========================================================================
    // ENEMY TURN
    // =========================================================================

    private static void enemyTurn(Enemy enemy) {
        Fmt.printDivider();
        System.out.println(Fmt.INDENT + Fmt.c(Fmt.B_RED, enemy.getName() + "'s turn!"));
        Fmt.blank();

        int damage = enemy.getAttackDamage(enemy.getHealth(), enemy.getMaxHealth());

        if (damage == 0) {
            // Troll miss — the Troll class prints its own "misses!" line
            Fmt.blank();
        } else {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.B_RED, enemy.getName())
                + Fmt.c(Fmt.RED,   " attacks for ")
                + Fmt.c(Fmt.BOLD,  String.valueOf(damage))
                + Fmt.c(Fmt.RED,   " damage!"));
            player.takeDamage(damage);

            // Critical HP warning (≤ 25%)
            if (player.isAlive()
                    && (double) player.getHealth() / player.getMaxHealth() <= 0.25) {
                Fmt.blank();
                Fmt.warn("⚠  " + player.getName() + " is critically wounded!");
            }
        }

        // 15% chance to apply Poison
        if (Math.random() < 0.15) {
            Fmt.blank();
            player.getStatusEffectManager().addEffect(new PoisonEffect(), player);
        }
        Fmt.blank();
    }

    // =========================================================================
    // VICTORY / DEFEAT
    // =========================================================================

    private static void onVictory(Enemy enemy) {
        int xp         = enemy.getExpValue();
        int goldEarned = xp / 5 + (int)(Math.random() * 20);

        Fmt.printVictoryBanner(enemy.getName());

        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.BR_YELLOW, "+" + xp + " EXP")
            + Fmt.c(Fmt.DIM,       "   │   ")
            + Fmt.c(Fmt.BR_YELLOW, "+" + goldEarned + " gold"));
        Fmt.blank();

        gold += goldEarned;
        int oldLevel = player.getLevel();
        player.gainExp(xp);
        if (player.getLevel() > oldLevel) {
            onLevelUp(oldLevel, player.getLevel());
        }

        summary.recordStageCleared();
        currentLevel++;

        // 50% item drop
        if (Math.random() < 0.50) {
            Item drop = randomDrop();
            Fmt.blank();
            if (!inventory.isFull()) {
                inventory.addItem(drop);
                System.out.println(Fmt.INDENT
                    + Fmt.c(Fmt.BR_YELLOW, "★  Item dropped: " + drop.getItemName() + "!"));
            } else {
                Fmt.warn("Item dropped but inventory is full: " + drop.getItemName());
            }
        }

        pause();
    }

    private static Item randomDrop() {
        double r = Math.random();
        if (r < 0.40) return new Potion("Health Potion", 20, 40);
        if (r < 0.60) return new Elixir("Power Elixir",  30,  3);
        if (r < 0.80) return new Antidote("Antidote",    15);
        return new Weapon("Sharp Dagger", 60, 5);
    }

    private static void onLevelUp(int oldLevel, int newLevel) {
        Fmt.printLevelUpBanner(oldLevel, newLevel);
        Fmt.gold("Max HP +10   ·   Attack Power +2");
        Fmt.blank();

        if (newLevel == 2) {
            player.unlockAbility(new ShieldBashAbility());
            Fmt.printAbilityUnlocked("Shield Bash");
        }
    }

    private static void onDefeat() {
        Fmt.printDefeatBanner();
        summary.printReport();

        Fmt.blank();
        printOpt(1, "Try again");
        printOpt(2, "Quit");
        Fmt.blank();

        int choice = promptInt("Choose: ", 1, 2);
        if (choice == 1) {
            currentLevel = 1;
            gold         = 0;
            main(new String[]{});
        } else {
            quitGame();
        }
    }

    // =========================================================================
    // BETWEEN-BATTLE MENUS
    // =========================================================================

    private static void showStats() {
        Fmt.printHeading("CHARACTER STATS");

        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_CYAN,    player.getName())
            + Fmt.c(Fmt.DIM,       "   ·   ")
            + Fmt.c(Fmt.BR_YELLOW, "Level " + player.getLevel()));
        Fmt.blank();

        Fmt.printHpBar("HP", player.getHealth(), player.getMaxHealth());
        Fmt.blank();

        statRow("Attack",   player.calculateTotalDamage()
                            + Fmt.c(Fmt.DIM, " (base " + player.getBaseAttackPower() + ")"));
        statRow("Defense",  String.valueOf(player.getDefense()));
        statRow("EXP",      player.getExp() + Fmt.c(Fmt.DIM, " / 100"));
        statRow("Gold",     Fmt.c(Fmt.BR_YELLOW, gold + "g"));
        statRow("Weapon",   player.getEquippedWeapon() != null
                            ? player.getEquippedWeapon().getItemName()
                            : Fmt.c(Fmt.DIM, "none"));
        statRow("Armor",    player.getEquippedArmor() != null
                            ? player.getEquippedArmor().getItemName()
                            : Fmt.c(Fmt.DIM, "none"));
        Fmt.blank();

        List<StatusEffect> effects = player.getStatusEffectManager().getActiveEffects();
        if (effects.isEmpty()) {
            statRow("Status", Fmt.c(Fmt.DIM, "none"));
        } else {
            for (StatusEffect se : effects) {
                Fmt.status("  [" + se.getEffectName() + "]  "
                        + se.getDuration() + " turn(s) remaining");
            }
        }

        Fmt.blank();
        Fmt.printDivider();
        promptEnter();
    }

    private static void statRow(String label, String value) {
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.YELLOW, String.format("%-10s", label))
            + Fmt.c(Fmt.WHITE,  value));
    }

    // ---- Inventory ----------------------------------------------------------

    private static void inventoryMenu() {
        while (true) {
            String cap = Fmt.c(Fmt.DIM, "(" + inventory.getSize() + "/" + inventory.getCapacity() + ")");
            Fmt.printHeading("INVENTORY  " + cap);

            List<Item> items = inventory.getItems();
            if (items.isEmpty()) {
                Fmt.dim("(empty)");
            } else {
                for (int i = 0; i < items.size(); i++) {
                    Item it = items.get(i);
                    String eq = (it == player.getEquippedWeapon() || it == player.getEquippedArmor())
                            ? "  " + Fmt.c(Fmt.BR_GREEN, "[E]") : "";
                    System.out.println(Fmt.INDENT
                        + Fmt.c(Fmt.B_YELLOW, "[" + (i + 1) + "]")
                        + Fmt.c(Fmt.WHITE,    " " + it.getItemName())
                        + eq
                        + Fmt.c(Fmt.DIM,      "  " + it.getValue() + "g"));
                }
            }

            Fmt.blank();
            Fmt.printDivider();
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.B_YELLOW, "[E]") + Fmt.c(Fmt.WHITE, " Equip Weapon/Armor"));
            System.out.println(Fmt.INDENT + Fmt.c(Fmt.DIM, "[B] Back"));
            Fmt.blank();

            String input = promptString("Choose: ").trim().toUpperCase();

            if (input.equals("B")) return;
            if (input.equals("E")) { equipMenu(); continue; }

            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < items.size()) {
                    showItemDetail(items.get(idx));
                } else {
                    Fmt.warn("Invalid selection — enter 1–" + items.size() + ", E, or B.");
                }
            } catch (NumberFormatException ex) {
                Fmt.warn("Invalid input — enter a number, E, or B.");
            }
        }
    }

    private static void showItemDetail(Item item) {
        Fmt.printHeading(item.getItemName().toUpperCase());
        statRow("Type",       item.getClass().getSimpleName());
        statRow("Value",      item.getValue() + "g");
        if (item instanceof Weapon)
            statRow("Dmg Bonus",  "+" + ((Weapon) item).getBonusDamage());
        if (item instanceof Armor)
            statRow("Def Bonus",  "+" + ((Armor)  item).getBaseDefenseBonus());
        if (item instanceof Potion)
            statRow("Heals",      ((Potion) item).getHealAmount() + " HP");
        if (item instanceof Elixir)
            statRow("Atk Boost", "+" + ((Elixir) item).getAttackBoost());
        Fmt.blank();
        promptEnter();
    }

    private static void equipMenu() {
        List<Item> equipable = new ArrayList<>();
        for (Item item : inventory.getItems()) {
            if (item instanceof Weapon || item instanceof Armor) equipable.add(item);
        }

        if (equipable.isEmpty()) {
            Fmt.warn("No weapons or armor in inventory.");
            pause();
            return;
        }

        Fmt.printHeading("EQUIP GEAR");
        for (int i = 0; i < equipable.size(); i++) {
            Item it = equipable.get(i);
            boolean isE = (it == player.getEquippedWeapon() || it == player.getEquippedArmor());
            String tag  = isE ? "  " + Fmt.c(Fmt.BR_GREEN, "[EQUIPPED]") : "";
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.B_YELLOW, "[" + (i + 1) + "]")
                + Fmt.c(Fmt.WHITE,    " " + it.getItemName())
                + tag);
        }
        int cancel = equipable.size() + 1;
        System.out.println(Fmt.INDENT + Fmt.c(Fmt.DIM, "[" + cancel + "] Cancel"));
        Fmt.blank();

        int choice = promptInt("Equip: ", 1, cancel);
        if (choice == cancel) return;

        Item chosen = equipable.get(choice - 1);
        if (chosen instanceof Weapon) {
            player.equipWeapon((Weapon) chosen);
            Fmt.success("Weapon equipped: " + chosen.getItemName());
        } else {
            player.equipArmor((Armor) chosen);
            Fmt.success("Armor equipped: " + chosen.getItemName());
        }
        pause();
    }

    // ---- Abilities ----------------------------------------------------------

    private static void abilitiesMenu() {
        Fmt.printHeading("ABILITIES");

        List<Ability> abilities = new ArrayList<>(player.getSkillTree().getAllAbilities());
        if (abilities.isEmpty()) {
            Fmt.dim("No abilities unlocked yet. Level up to unlock them.");
        } else {
            for (Ability a : abilities) {
                String ready = a.isReady()
                        ? Fmt.c(Fmt.BR_GREEN, "READY")
                        : Fmt.c(Fmt.BR_RED,   "Cooldown: " + a.getCurrentCooldown() + "t");
                System.out.println(Fmt.INDENT
                    + Fmt.c(Fmt.B_MAGENTA, a.getName())
                    + "  " + ready);
                System.out.println(Fmt.INDENT
                    + Fmt.c(Fmt.DIM, "  " + a.getDescription()));
                System.out.println(Fmt.INDENT
                    + Fmt.c(Fmt.DIM, "  Cooldown: " + a.getCooldown() + " turn(s) after use"));
                Fmt.blank();
            }
        }

        Fmt.printDivider();
        promptEnter();
    }

    // =========================================================================
    // SAVE / QUIT
    // =========================================================================

    private static void saveGame() {
        Fmt.printHeading("SAVE GAME");
        new GameState().save(player, inventory, currentLevel, "savegame.txt");
        Fmt.success("Game saved to  savegame.txt");
        pause();
    }

    private static void quitGame() {
        Fmt.printHeading("FAREWELL");
        summary.printReport();
        Fmt.narrate("Thanks for playing, "
            + Fmt.c(Fmt.B_CYAN, player.getName()) + ". Until next time.");
        Fmt.blank();
        scanner.close();
        System.exit(0);
    }

    // =========================================================================
    // INTRO SCREENS
    // =========================================================================

    private static void printBanner() {
        Fmt.blank();
        String tl = "  ╔══════════════════════════════════════════════════╗";
        String m1 = "  ║       CHRONICLES  OF  THE  FALLEN                ║";
        String m2 = "  ║            A Text-Based Java RPG                 ║";
        String bl = "  ╚══════════════════════════════════════════════════╝";
        System.out.println(Fmt.c(Fmt.B_CYAN, tl));
        System.out.println(Fmt.c(Fmt.B_CYAN, m1));
        System.out.println(Fmt.c(Fmt.B_CYAN, m2));
        System.out.println(Fmt.c(Fmt.B_CYAN, bl));
        Fmt.blank();
        if (Fmt.COLOR) {
            Fmt.dim("Tip: run with --no-color if your terminal doesn't support ANSI codes.");
        }
        Fmt.blank();
    }

    private static void printStoryIntro() {
        Fmt.narrate("The kingdom is in ruins. Dark creatures roam the land.");
        Fmt.narrate("Villages burn. Hope is ash.");
        Fmt.narrate("You are the last hero brave enough to face them.");
        Fmt.blank();
        Fmt.narrate("Fight. Level up. Survive.");
        Fmt.blank();
    }

    private static String promptPlayerName() {
        System.out.print(Fmt.INDENT
            + Fmt.c(Fmt.B_YELLOW, "▶ ")
            + Fmt.c(Fmt.WHITE, "Enter your hero's name: "));
        String name = scanner.nextLine().trim();
        return name.isEmpty() ? "Hero" : name;
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    /** Returns the ANSI colour code appropriate for this HP percentage. */
    private static String hpColorCode(int current, int max) {
        double pct = (double) current / Math.max(1, max);
        if (pct > 0.50) return Fmt.GREEN;
        if (pct > 0.25) return Fmt.YELLOW;
        return Fmt.BR_RED;
    }

    /** Prints a numbered menu option. */
    private static void printOpt(int n, String label) {
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.B_YELLOW, "[" + n + "]")
            + Fmt.c(Fmt.WHITE,    " " + label));
    }

    // =========================================================================
    // INPUT HELPERS
    // =========================================================================

    /** Prompts for an integer in [min, max], re-prompting on bad input. */
    private static int promptInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(Fmt.INDENT
                + Fmt.c(Fmt.B_YELLOW, "▶ ")
                + Fmt.c(Fmt.WHITE, prompt));
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) return val;
                Fmt.warn("Enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                Fmt.warn("Invalid input — please enter a number.");
            }
        }
    }

    /** Prompts for a raw string. */
    private static String promptString(String prompt) {
        System.out.print(Fmt.INDENT
            + Fmt.c(Fmt.B_YELLOW, "▶ ")
            + Fmt.c(Fmt.WHITE, prompt));
        return scanner.nextLine();
    }

    /** "Press Enter to continue" gate. */
    private static void promptEnter() {
        System.out.print(Fmt.INDENT + Fmt.c(Fmt.DIM, "Press Enter to continue..."));
        scanner.nextLine();
    }

    /** Short sleep, then Enter gate — called after key events for pacing. */
    private static void pause() {
        try { Thread.sleep(350); } catch (InterruptedException ignored) {}
        promptEnter();
    }
}
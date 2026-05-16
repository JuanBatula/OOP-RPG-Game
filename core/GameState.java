package core;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import entities.Player;
import items.Antidote;
import items.Armor;
import items.Elixir;
import items.Inventory;
import items.Item;
import items.Potion;
import items.Weapon;

public class GameState {

    public static class GameStateData {
        public final Player    player;
        public final Inventory inventory;
        public final int       level;

        public GameStateData(Player player, Inventory inventory, int level) {
            this.player    = player;
            this.inventory = inventory;
            this.level     = level;
        }
    }

    // -------------------------------------------------------------------------
    // Save
    // -------------------------------------------------------------------------

    public void save(Player player, Inventory inventory, int level, String filePath) {
        try (PrintWriter pw = new PrintWriter(filePath)) {

            pw.println("NAME:"  + player.getName());
            pw.println("LEVEL:" + level);
            pw.println("HP:"    + player.getHealth());
            pw.println("MAXHP:" + player.getMaxHealth());
            pw.println("ATK:"   + player.getBaseAttackPower());
            pw.println("DEF:"   + player.getDefense());
            pw.println("EXP:"   + player.getExp());

            Weapon w = player.getEquippedWeapon();
            if (w != null) pw.println("WEAPON:" + w.getItemName() + "|" + w.getValue() + "|" + w.getBonusDamage());
            else           pw.println("WEAPON:none");

            Armor a = player.getEquippedArmor();
            if (a != null) pw.println("ARMOR:" + a.getItemName() + "|" + a.getValue() + "|" + a.getBaseDefenseBonus());
            else           pw.println("ARMOR:none");

            for (Item item : inventory.getItems()) {
                String line = buildItemLine(item);
                if (line != null) pw.println(line);
            }
            pw.println("END");

            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.GREEN, "Game saved to ")
                + Fmt.c(Fmt.WHITE, filePath));

        } catch (IOException e) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.BR_RED, "ERROR: Could not save game — " + e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // Load
    // -------------------------------------------------------------------------

    public GameStateData load(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String playerName   = "Hero";
            int level = 1, hp = 100, maxHp = 100, baseAtk = 10, def = 0, exp = 0;
            Weapon    equippedWeapon = null;
            Armor     equippedArmor  = null;
            Inventory inventory      = new Inventory();

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("END")) break;

                if      (line.startsWith("NAME:"))   playerName    = line.substring(5);
                else if (line.startsWith("LEVEL:"))  level         = parseInt(line.substring(6), 1);
                else if (line.startsWith("HP:"))     hp            = parseInt(line.substring(3), 100);
                else if (line.startsWith("MAXHP:"))  maxHp         = parseInt(line.substring(6), 100);
                else if (line.startsWith("ATK:"))    baseAtk       = parseInt(line.substring(4), 10);
                else if (line.startsWith("DEF:"))    def           = parseInt(line.substring(4), 0);
                else if (line.startsWith("EXP:"))    exp           = parseInt(line.substring(4), 0);
                else if (line.startsWith("WEAPON:")) equippedWeapon = parseWeapon(line.substring(7));
                else if (line.startsWith("ARMOR:"))  equippedArmor  = parseArmor(line.substring(6));
                else if (line.startsWith("ITEM:"))   loadItem(line.substring(5), inventory);
            }

            Player player = new Player(playerName, hp, maxHp, baseAtk);
            player.setExp(exp);
            if (def > 0) player.addDefense(def);

            if (equippedWeapon != null) { inventory.addItem(equippedWeapon); player.equipWeapon(equippedWeapon); }
            if (equippedArmor  != null) { inventory.addItem(equippedArmor);  player.equipArmor(equippedArmor);  }

            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.GREEN, "Game loaded from ")
                + Fmt.c(Fmt.WHITE, filePath));
            return new GameStateData(player, inventory, level);

        } catch (FileNotFoundException e) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.DIM, "No save file found — starting new game."));
            return defaultState();
        } catch (IOException e) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.BR_RED, "ERROR reading save file — starting new game. (" + e.getMessage() + ")"));
            return defaultState();
        } catch (Exception e) {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.BR_RED, "Corrupted save file — starting new game. (" + e.getMessage() + ")"));
            return defaultState();
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers — no changes to parsing logic, only warning output styled
    // -------------------------------------------------------------------------

    private String buildItemLine(Item item) {
        if (item instanceof Weapon)  { Weapon  w = (Weapon)  item; return "ITEM:Weapon|"  + w.getItemName() + "|" + w.getValue() + "|" + w.getBonusDamage();      }
        if (item instanceof Armor)   { Armor   a = (Armor)   item; return "ITEM:Armor|"   + a.getItemName() + "|" + a.getValue() + "|" + a.getBaseDefenseBonus(); }
        if (item instanceof Potion)  { Potion  p = (Potion)  item; return "ITEM:Potion|"  + p.getItemName() + "|" + p.getValue() + "|" + p.getHealAmount();        }
        if (item instanceof Elixir)  { Elixir  el= (Elixir)  item; return "ITEM:Elixir|"  + el.getItemName()+ "|" + el.getValue()+ "|" + el.getAttackBoost();      }
        if (item instanceof Antidote)                              { return "ITEM:Antidote|" + item.getItemName() + "|" + item.getValue() + "|0"; }
        System.out.println(Fmt.INDENT
            + Fmt.c(Fmt.BR_RED, "WARNING: Skipping unknown item type for '" + item.getItemName() + "'"));
        return null;
    }

    private Weapon parseWeapon(String data) {
        if (data.equals("none")) return null;
        String[] p = data.split("\\|");
        return (p.length < 3) ? null : new Weapon(p[0], parseInt(p[1], 0), parseInt(p[2], 0));
    }

    private Armor parseArmor(String data) {
        if (data.equals("none")) return null;
        String[] p = data.split("\\|");
        return (p.length < 3) ? null : new Armor(p[0], parseInt(p[1], 0), parseInt(p[2], 0));
    }

    private void loadItem(String data, Inventory inventory) {
        String[] parts = data.split("\\|");
        if (parts.length < 4) return;
        String type  = parts[0];
        String name  = parts[1];
        int    value = parseInt(parts[2], 0);
        int    extra = parseInt(parts[3], 0);

        Item item = null;
        if      (type.equals("Weapon"))  item = new Weapon(name, value, extra);
        else if (type.equals("Armor"))   item = new Armor(name, value, extra);
        else if (type.equals("Potion"))  item = new Potion(name, value, extra);
        else if (type.equals("Elixir"))  item = new Elixir(name, value, extra);
        else if (type.equals("Antidote"))item = new Antidote(name, value);
        else {
            System.out.println(Fmt.INDENT
                + Fmt.c(Fmt.BR_RED, "WARNING: Unknown item type '" + type + "' — skipped."));
        }

        if (item != null) inventory.addItem(item);
    }

    private GameStateData defaultState() {
        Player player = new Player("Hero", 100, 100, 10);
        return new GameStateData(player, player.getInventory(), 1);
    }

    private int parseInt(String s, int fallback) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return fallback; }
    }
}
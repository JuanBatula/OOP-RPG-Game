package shop;

import core.Fmt;
import entities.Player;
import items.Antidote;
import items.Armor;
import items.Elixir;
import items.Inventory;
import items.Item;
import items.Potion;
import items.Weapon;

import java.util.ArrayList;
import java.util.List;

public class Shop {
  private List <ShopItem> stock;

  public Shop() {
    stock = new ArrayList<>();

      stock.add(new ShopItem(new Potion("Health Potion",    20,  40),  20));
      stock.add(new ShopItem(new Potion("Large Potion",     40,  80),  40));
      stock.add(new ShopItem(new Elixir("Power Elixir",     30,   5),  30));
      stock.add(new ShopItem(new Antidote("Antidote",       15),       15));
      stock.add(new ShopItem(new Weapon("Steel Sword",      80,  12),  80));
      stock.add(new ShopItem(new Weapon("Battle Axe",      120,  18), 120));
      stock.add(new ShopItem(new Armor("Plate Mail",       100,  10), 100));
      stock.add(new ShopItem(new Armor("Dragon Scale",     180,  16), 180));
  }
  public int buy(int index, int gold, Player player, Inventory inv) {
    if (index < 0 || index >= stock.size()) {
      Fmt.warn("Invalid selection.");
      return -1;
    }

    ShopItem si = stock.get(Index);
    if(gold < si.price) {
      Fmt.warn("Not enough gold!  "
          + Fmt.c(Fmt.BR_YELLOW, gold + "g")
          + Fmt.c(Fmt.DIM,       " / needed ")
          + Fmt.c(Fmt.BR_YELLOW, si.price + "g"));
            return -1;
    }
    if (inv.isFull()) {
      Fmt.warn("Inventory is full - sell or use something first.");
      return -1;
    }

    int remaining = gold - si.price;
    inv.addItems(si.item);

    System.out.println(Fmt.INDENT
        + Fmt.c(Fmt.B_CYAN,    player.getName())
        + Fmt.c(Fmt.BR_YELLOW, " bought ")
        + Fmt.c(Fmt.WHITE,     si.item.getItemName())
        + Fmt.c(Fmt.BR_YELLOW, "  −" + si.price + "g")
        + Fmt.c(Fmt.DIM,       "   (gold left: " + remaining + "g)"));

    return remaining;
  }

  public int sell(Item item, Player player, Inventory inv) {
    if (!inv.getItems().contains(item)) {
        Fmt.warn("That item is not in your inventory.");
        return -1;
    }

    // Auto-unequip if the item is currently equipped
    if (item instanceof Weapon && item == player.getEquippedWeapon()) {
        player.unequipWeapon();
    } else if (item instanceof Armor && item == player.getEquippedArmor()) {
        player.uneqipArmor();
    }

    int earned = Math.max(1, item.getValue() / 2);
    inv.removeItem(item);

    System.out.println(Fmt.INDENT
        + Fmt.c(Fmt.B_CYAN,    player.getName())
        + Fmt.c(Fmt.BR_YELLOW, " sold ")
        + Fmt.c(Fmt.WHITE,     item.getItemName())
        + Fmt.c(Fmt.BR_YELLOW, "  +" + earned + "g")
        + Fmt.c(Fmt.DIM,       "   (sell price: " + earned + "g)"));

    return earned;
  }

  public List<ShopItem> getStock() { return stock; }

  public static class ShopItem {
    public final Item item;
    public final int price;
    public ShopItem(Item item, int price) { this.item = item; this.price = price; }
  }
}
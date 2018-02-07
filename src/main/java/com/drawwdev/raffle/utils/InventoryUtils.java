package com.drawwdev.raffle.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

    public static boolean hasInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public static boolean containsAtLeast(Inventory inv, Material material, int minAmount) {
        Integer contained = 0;

        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() == material) {
                contained += item.getAmount();
            }
        }

        return contained >= minAmount;
    }

    public static boolean containsAtLeast(Inventory inv, Material material, int minAmount, short data, String name) {
        Integer contained = 0;

        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() == material && item.getDurability() == data && item.getItemMeta().getDisplayName().equals(name)) {
                contained += item.getAmount();
            }
        }

        return contained >= minAmount;
    }

    public static boolean containsAtLeast(Inventory inv, Material material, int minAmount, String name) {
        Integer contained = 0;

        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() == material && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(name)) {
                contained += item.getAmount();
            }
        }
        return contained >= minAmount;
    }

    public static void removeInventoryItems(Inventory inv, Material type, int amount, String name) {
        for (ItemStack is : inv.getContents()) {
            if (is != null && is.getType() == type && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().equals(name)) {
                int newamount = is.getAmount() - amount;
                if (newamount > 0) {
                    is.setAmount(newamount);
                    break;
                } else {
                    if (amount == 0){
                        break;
                    }
                    //inv.remove(is);
                    is.setAmount(newamount);
                    inv.removeItem(is);
                    amount = -newamount;
                }
            }
        }
    }

    public static void removeInventoryItems(Inventory inv, Material type, int amount) {
        for (ItemStack is : inv.getContents()) {
            if (is != null && is.getType() == type) {
                int newamount = is.getAmount() - amount;
                if (newamount > 0) {
                    is.setAmount(newamount);
                    break;
                } else {
                    inv.remove(is);
                    amount = -newamount;
                    if (amount == 0) break;
                }
            }
        }
    }

    public static boolean containsAtLeast(Inventory inv, Material material, int minAmount, short data) {
        Integer contained = 0;

        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() == material && item.getDurability() == data) {
                contained += item.getAmount();
            }
        }

        return contained >= minAmount;
    }

}

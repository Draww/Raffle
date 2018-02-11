package com.drawwdev.raffle.utils;

import com.drawwdev.raffle.Main;
import com.drawwdev.raffle.Raffle;
import com.drawwdev.raffle.RaffleData;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class ScriptSystem {

    private Main plugin;

    public ScriptSystem(Main plugin) {
        this.plugin = plugin;
    }

    public void executeActions(Player player, List<String> actions, RaffleData raffleData) {
        if (actions == null || actions.isEmpty()) {
            return;
        }
        for (String action : actions) {
            executeAction(player, action, raffleData);
        }
    }

    private boolean executeAction(Player player, String action, RaffleData raffleData) {
        int delayTimer = 0;
        int chance = 100;
        if (action.contains("[Chance=")) {
            for (int i = 1; i <= 100; ++i) {
                if (action.contains("[Chance=" + i + "]")) {
                    chance = i;
                    action = action.replace("[Chance=" + i + "] ", "").replace("[Chance=" + i + "]", "");
                }
            }
        }
        if (action.contains("[HasItem]")) {
            action = action.replace("[HasItem] ", "").replace("[HasItem]", "");
            final String[] item = action.split(";");
            ItemStack hasItem = null;
            Integer stackItem = null;
            if (item.length == 1) {
                hasItem = new ItemStack(Material.valueOf(item[0]), 1);
            } else if (item.length == 2) {
                hasItem = new ItemStack(Material.valueOf(item[0]), Integer.parseInt(item[1]));
                stackItem = Integer.valueOf(item[1]);
            } else if (item.length == 3) {
                hasItem = new ItemStack(Material.valueOf(item[0]), Integer.parseInt(item[1]), (short) (byte) Integer.parseInt(item[2]));
            } else if (item.length == 4) {
                hasItem = new ItemStack(Material.valueOf(item[0]), Integer.parseInt(item[1]), (short) (byte) Integer.parseInt(item[2]));
                final ItemMeta meta = hasItem.getItemMeta();
                meta.setDisplayName(StringUtil.setPlaceholders(player, item[3]));
                hasItem.setItemMeta(meta);
            }
            if (hasItem != null && stackItem != null) {
                if (!player.getInventory().containsAtLeast(hasItem, stackItem)) {
                    return false;
                }
            }
        }
        if (action.contains("[Delay=")) {
            for (int i = 1; i < 61; ++i) {
                if (action.contains("[Delay=" + i + "]")) {
                    delayTimer = i * 20;
                    action = action.replace("[Delay=" + i + "] ", "").replace("[Delay=" + i + "]", "");
                }
            }
        }
        if (action.contains("[Delay=1000]")) {
            action = action.replace("[Delay=1000]", "");
            delayTimer = 2;
        }
        final String runAction = action;
        if (chance != 100) {
            final double chanceCheck = Math.random() * 100.0;
            if (chanceCheck > chance) {
                return false;
            }
        }
        if (delayTimer != 0) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> runAction(player, runAction, raffleData), (long) delayTimer);
        } else {
            runAction(player, runAction, raffleData);
        }
        return false;
    }

    public String replaceArgs(String toReplace, RaffleData raffleData) {
        for (int i = raffleData.size(); i > 0; i--) {
            if (toReplace.contains("$arg" + i)) {
                toReplace = toReplace.replace("$arg" + i, (CharSequence) String.valueOf(raffleData.get(i - 1)));
            }
        }
        if ((toReplace.contains("$multiargs")) && (raffleData.size() > 1)) {
            toReplace = toReplace.replace("$multiargs", raffleData.getAllString());
        }
        return toReplace;
    }

    public String math(String toReplace) {
        if (toReplace.contains("$math(")) {
            String matheditor = toReplace.split(Pattern.quote("$math("))[1].split(Pattern.quote(")"))[0];
            Double returnDouble = arithmeticExpression.eval(matheditor);
            toReplace = toReplace.replace("$math(" + matheditor + ")", String.valueOf(returnDouble.toString()));
        }
        return toReplace;
    }

    public void playerOptions(String to, Player player, RaffleData raffleData) {

        Player p = player;
        if (to.startsWith("#PlayerOptions==")) {
            p = Bukkit.getPlayer(to.split("#PlayerOptions==")[1].split("#")[0]);
            if (p == null || !p.isOnline()) {
                return;
            }
            to = to.replace("#PlayerOptions==" + p.getName() + "#", "#PlayerOptions#");
        }
        to = StringUtil.setPlaceholders(p, to);
        String actionType = to.split("#PlayerOptions#")[1].split(Pattern.quote("("))[1].split(Pattern.quote(")"))[0];
        String d = to.split("#PlayerOptions#")[1].split(Pattern.quote("("))[1].split(Pattern.quote(")"))[1];
        String[] data = d.split(":");
        if (data.length == 0) {
            plugin.getLogger().info("[Raffle] Empty PlayerOptions");
        }
        try {
            if (actionType.equals("setAllowFlight")) {
                p.setAllowFlight(Boolean.valueOf(data[0]).booleanValue());
                return;
            } else if (actionType.equals("setCanPickupItems")) {
                p.setCanPickupItems(Boolean.valueOf(data[0]).booleanValue());
                return;
            } else if (actionType.equals("setCustomNameVisible")) {
                p.setCustomNameVisible(Boolean.valueOf(data[0]).booleanValue());
                return;
            } else if (actionType.equals("setCustomName")) {
                p.setCustomName(data[0]);
                return;
            } else if (actionType.equals("setDisplayName")) {
                p.setDisplayName(data[0]);
                return;
            } else if (actionType.equals("setExhaustion")) {
                p.setExhaustion(Integer.valueOf(data[0]).intValue());
                return;
            } else if (actionType.equals("setExp")) {
                p.setExp(Integer.valueOf(data[0]).intValue());
                return;
            } else if (actionType.equals("setFallDistance")) {
                p.setFallDistance(Integer.valueOf(data[0]).intValue());
                return;
            } else if (actionType.equals("setFireTicks")) {
                p.setFireTicks(Integer.valueOf(data[0]).intValue());
                return;
            } else if (actionType.equals("setFlying")) {
                p.setFlying(Boolean.valueOf(data[0]).booleanValue());
                return;
            } else if (actionType.equals("setFlySpeed")) {
                p.setFlySpeed(Float.valueOf(data[0]).floatValue());
                return;
            } else if (actionType.equals("setGameMode")) {
                p.setGameMode(GameMode.valueOf(data[0]));
                return;
            } else if (actionType.equals("setHealth")) {
                p.setHealth(Double.valueOf(data[0]).doubleValue());
                return;
            } else if (actionType.equals("setFoodLevel")) {
                p.setFoodLevel(Integer.valueOf(data[0]).intValue());
                return;
            } else if (actionType.equals("setLastDamage")) {
                p.setLastDamage(Double.valueOf(data[0]).doubleValue());
                return;
            } else if (actionType.equals("setLevel")) {
                p.setLevel(Integer.valueOf(data[0]).intValue());
                return;
            } else if (actionType.equals("setMaxHealth")) {
                p.setMaxHealth(Double.valueOf(data[0]).doubleValue());
                return;
            } else if (actionType.equals("setMaximumAir")) {
                p.setMaximumAir(Integer.valueOf(data[0]).intValue());
                return;
            } else if (actionType.equals("setNoDamageTicks")) {
                p.setNoDamageTicks(Integer.valueOf(data[0]).intValue());
                return;
            } else if (actionType.equals("setOp")) {
                p.setOp(Boolean.valueOf(data[0]).booleanValue());
                return;
            } else if (actionType.equals("setPlayerListName")) {
                p.setPlayerListName(data[0]);
                return;
            } else if (actionType.equals("setRemainingAir")) {
                p.setRemainingAir(Integer.valueOf(data[0]).intValue());
                return;
            } else if (actionType.equals("setSaturation")) {
                p.setSaturation(Float.valueOf(data[0]).floatValue());
                return;
            } else if (actionType.equals("setSneaking")) {
                p.setSneaking(Boolean.valueOf(data[0]).booleanValue());
                return;
            } else if (actionType.equals("setSprinting")) {
                p.setSprinting(Boolean.valueOf(data[0]).booleanValue());
                return;
            } else if (actionType.equals("setTotalExperience")) {
                p.setTotalExperience(Integer.valueOf(data[0]).intValue());
                return;
            } else if (actionType.equals("setWhitelisted")) {
                p.setWhitelisted(Boolean.valueOf(data[0]).booleanValue());
                return;
            } else if (actionType.equals("leaveVehicle")) {
                if (p.isInsideVehicle()) {
                    p.leaveVehicle();
                }
                return;
            } else if (actionType.equals("resetMaxHealth")) {
                p.resetMaxHealth();
                return;
            } else if (actionType.equals("closeInventory")) {
                p.closeInventory();
                return;
            } else if (actionType.equals("clearInventory")) {
                p.getInventory().clear();
                return;
            } else if (actionType.equals("resetMaxHealth")) {
                p.resetMaxHealth();
                return;
            } else if (actionType.equals("giveExp")) {
                p.giveExp(Integer.valueOf(data[0]).intValue());
                return;
            } else if (actionType.equals("giveExpLevels")) {
                p.giveExpLevels(Integer.valueOf(data[0]).intValue());
                return;
            } else if (actionType.equals("damage")) {
                p.damage(Double.valueOf(data[0]).doubleValue());
                return;
            } else if (actionType.equals("kickPlayer")) {
                p.kickPlayer(data[0]);
                return;
            } else if (actionType.equals("teleport")) {
                if (data.length > 5) {
                    Location playerLoc = new Location(Bukkit.getWorld(data[0]), Double.valueOf(data[1]).doubleValue(), Double.valueOf(data[2]).doubleValue(), Double.valueOf(data[3]).doubleValue(), Float.valueOf(data[4]).floatValue(), Float.valueOf(data[5]).floatValue());
                    p.teleport(playerLoc);
                } else if (data.length == 3) {
                    Location playerLoc = new Location(Bukkit.getWorld(data[0]), Double.valueOf(data[1]).doubleValue(), Double.valueOf(data[2]).doubleValue(), Double.valueOf(data[3]).doubleValue());
                    p.teleport(playerLoc);
                }
                return;
            } else if (actionType.equals("setBedSpawnLocation")) {
                if (data.length == 3) {
                    Location loc = new Location(Bukkit.getWorld(data[0]), Double.valueOf(data[1]).doubleValue(), Double.valueOf(data[2]).doubleValue(), Double.valueOf(data[3]).doubleValue());
                    p.setBedSpawnLocation(loc);
                }
                return;
            } else if (actionType.equals("setCompassTarget")) {
                if (data.length == 3) {
                    Location loc = new Location(Bukkit.getWorld(data[0]), Double.valueOf(data[1]).doubleValue(), Double.valueOf(data[2]).doubleValue(), Double.valueOf(data[3]).doubleValue());
                    p.setCompassTarget(loc);
                }
                return;
            } else if (actionType.equals("setItemInHand")) {
                ItemStack Item = CreateItemStack(player, data, raffleData);
                player.getInventory().setItemInMainHand(Item);
                return;
            } else if (actionType.equals("setItemInOffHand")) {
                ItemStack Item = CreateItemStack(player, data, raffleData);
                player.getInventory().setItemInOffHand(Item);
                return;
            } else if (actionType.equals("addItem")) {
                ItemStack Item = CreateItemStack(player, data, raffleData);
                player.getInventory().addItem(new ItemStack[] { Item });
                return;
            } else if (actionType.equals("setBoots")) {
                ItemStack Item = CreateItemStack(player, data, raffleData);
                p.getInventory().setBoots(Item);
                return;
            } else if (actionType.equals("setChestplate")) {
                ItemStack Item = CreateItemStack(player, data, raffleData);
                p.getInventory().setChestplate(Item);
                return;
            } else if (actionType.equals("setHelmet")) {
                ItemStack Item = CreateItemStack(player, data, raffleData);
                p.getInventory().setHelmet(Item);
                return;
            } else if (actionType.equals("setLeggings")) {
                ItemStack Item = CreateItemStack(player, data, raffleData);
                p.getInventory().setLeggings(Item);
                return;
            } else if (actionType.equals("setHeldItemSlot")) {
                p.getInventory().setHeldItemSlot(Integer.valueOf(data[0]).intValue());
                return;
            } else if (actionType.equals("setItemOnCursor")) {
                ItemStack Item = CreateItemStack(player, data, raffleData);
                p.setItemOnCursor(Item);
                return;
            } else if (actionType.equals("chat")) {
                p.chat(data[0]);
                return;
            } else if (actionType.equals("sendMessage")) {
                p.sendMessage(data[0]);
                return;
            } else if (actionType.equals("playSound")) {
                p.playSound(p.getLocation(), Sound.valueOf(data[0]), Float.valueOf(data[1]).floatValue(), Float.valueOf(data[2]).floatValue());
                return;
            } else if (actionType.equals("playEffect")) {
                Effect e = Effect.valueOf(data[0]);
                int edata = Integer.valueOf(data[1]).intValue();
                p.getWorld().playEffect(p.getLocation(), e, edata);
                return;
            } else if (actionType.equals("addPotionEffect")) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(data[0]), Integer.valueOf(data[1]).intValue(), Integer.valueOf(data[2]).intValue()));
                return;
            } else if (actionType.equals("removePotionEffect")) {
                p.removePotionEffect(PotionEffectType.getByName(data[0]));
                return;
            } else if (actionType.equals("removePotionEffect")) {
                p.removePotionEffect(PotionEffectType.getByName(data[0]));
                return;
            }
            plugin.getLogger().info("No method found with " + actionType + " name");
        } catch (NumberFormatException e) {
            plugin.getLogger().info("An error occurred while parsing " + actionType + " function. (NumberFormatException)");
        } catch (IllegalArgumentException e) {
            plugin.getLogger().info("An error occurred while parsing " + actionType + " function. (IllegalArgumentException)");
        }
    }

    public ItemStack CreateItemStack(Player player, String[] data, RaffleData raffleData) {
        if (data.length > 3) {
            ItemStack ITEM = new ItemStack(Material.AIR, 0);
            int item_amount = Integer.valueOf(data[1]).intValue();
            int itemdata = 0;
            String ItemName = null;
            String Lore = null;
            String Enchantments = null;
            try {
                itemdata = Integer.valueOf(data[2]).intValue();
            } catch (Exception e) {
                plugin.getLogger().info("[Raffle] AddItem : ItemMeta must be an number : " + data[2]);
            }
            if (data.length > 4) {
                Enchantments = data[3];
            }
            if (data.length > 5) {
                ItemName = data[4];
            }
            if (data.length > 6) {
                Lore = data[5];
            }
            HashMap<String, Integer> enchants_and_level = new HashMap();
            int level = 0;
            String latest_enchant_added;
            int localException2;
            try {
                if ((Enchantments != null) &&
                        (!Enchantments.equalsIgnoreCase(""))) {
                    boolean addenchant = true;
                    latest_enchant_added = "";
                    String[] arrayOfString2;
                    int i = (arrayOfString2 = Enchantments.split(";")).length;
                    for (localException2 = 0; localException2 < i; localException2++) {
                        String s = arrayOfString2[localException2];
                        if (addenchant) {
                            enchants_and_level.put(s, Integer.valueOf(0));
                            latest_enchant_added = s;
                            addenchant = false;
                        } else {
                            level = Integer.valueOf(s).intValue();
                            enchants_and_level.put(latest_enchant_added, Integer.valueOf(level));
                            addenchant = true;
                        }
                    }
                }
            } catch (Exception localException1) {
            }
            try {
                if (itemdata == 0) {
                    ITEM = new ItemStack(Material.getMaterial(data[0]), item_amount);
                } else {
                    ITEM = new ItemStack(Material.getMaterial(data[0]), item_amount, (short) itemdata);
                }
            } catch (Exception e1) {
                plugin.getLogger().info("[Raffle] Impossible create this ItemStack : " + data[0]);
                return ITEM;
            }
            if (!enchants_and_level.isEmpty()) {
                for (String e : enchants_and_level.keySet()) {
                    try {
                        ITEM.addUnsafeEnchantment(Enchantment.getByName(e), ((Integer) enchants_and_level.get(e)).intValue());
                    } catch (Exception err) {
                        plugin.getLogger().info("[Raffle] Impossible add this enchant : " + e + " on this item :" + data[0]);
                    }
                }
            }
            if (ItemName != null) {
                ItemMeta im = ITEM.getItemMeta();
                im.setDisplayName(StringUtil.cc(ItemName));
                ITEM.setItemMeta(im);
            }
            if (Lore != null) {
                ArrayList<String> lores = new ArrayList();
                String[] arrayOfString1;
                localException2 = (arrayOfString1 = Lore.split(";")).length;
                for (int err = 0; err < localException2; err++) {
                    String s = arrayOfString1[err];
                    s = StringUtil.cc(s);
                    lores.add(s);
                }
                ItemMeta im = ITEM.getItemMeta();
                im.setLore(lores);
                ITEM.setItemMeta(im);
            }
            return ITEM;
        }
        ItemStack ITEM = new ItemStack(Material.getMaterial(data[0]), Integer.valueOf(data[1]).intValue());
        return ITEM;
    }

    public Boolean runCondition(Player player, String action, RaffleData raffleData) {
        int chance = 100;
        if (action.contains("[Chance=")) {
            for (int i = 1; i <= 100; ++i) {
                if (action.contains("[Chance=" + i + "]")) {
                    chance = i;
                    action = action.replace("[Chance=" + i + "]", "");
                    if (chance != 100) {
                        final double chanceCheck = Math.random() * 100.0;
                        if (chanceCheck > chance) {
                            if (action.contains("[Action]")) {
                                action.replace("[ElseAction] ", "").replace("[ElseAction]", "");
                                executeAction(player, action, raffleData);
                            }
                            return false;
                        }
                    }
                }
            }
        }
        if (action.contains("[HasItem]")) {
            action = action.replace("[HasItem] ", "").replace("[HasItem]", "");
            String[] splitAction = action.split(Pattern.quote(" [ElseAction] "));
            final String[] item = splitAction[0].split(";");
            Integer stackItem = Integer.parseInt(item[1]);
            if (item[2] != null) {
                if (!InventoryUtils.containsAtLeast(player.getInventory(), Material.valueOf(item[0]), stackItem, Short.parseShort(item[2]))) {
                    if (splitAction[1] != null) {
                        executeAction(player, splitAction[1], raffleData);
                    }
                    return false;
                }
            } else {
                if (!InventoryUtils.containsAtLeast(player.getInventory(), Material.valueOf(item[0]), stackItem)) {
                    if (splitAction[1] != null) {
                        executeAction(player, splitAction[1], raffleData);
                    }
                    return false;
                }
            }
        }
        if (action.contains("[HaveMoney]")) {
            if (!Main.getInstance().getEconomyDepend().dependent()) {
                return false;
            }
            action = action.replace("[HaveMoney] ", "").replace("[HaveMoney]", "");
            String[] splitAction = action.split(Pattern.quote(" [ElseAction] "));
            Double moneyPlayer = Main.getInstance().getEconomyDepend().get().getBalance(player);
            Double needMoney = Double.parseDouble(splitAction[0]);
            if (moneyPlayer < needMoney) {
                if (splitAction[1] != null) {
                    executeAction(player, splitAction[1], raffleData);
                }
                return false;
            }
        }
        if (action.contains("[HaveLevel]")) {
            action = action.replace("[HaveLevel] ", "").replace("[HaveLevel]", "");
            String[] splitAction = action.split(Pattern.quote(" [ElseAction] "));
            Integer levelPlayer = player.getLevel();
            Integer needLevel = Integer.parseInt(splitAction[0]);
            if (levelPlayer < needLevel) {
                if (splitAction[1] != null) {
                    executeAction(player, splitAction[1], raffleData);
                }
                return false;
            }
        }
        if (action.contains("[hasGroup]")) {
            action = action.replace("[hasGroup] ", "").replace("[hasGroup]", "");
            String[] splitAction = action.split(Pattern.quote(" [ElseAction] "));
            PermissionUser permissionUser = PermissionsEx.getUser(player);
            Boolean controlGroup = true;
            for (String group : permissionUser.getGroupNames()) {
                if (group != splitAction[0]) {
                    controlGroup = false;
                } else {
                    controlGroup = true;
                }
                if (!controlGroup) {
                    if (splitAction[1] != null) {
                        executeAction(player, splitAction[1], raffleData);
                    }
                }
                return controlGroup;
            }
        }
        try {
            if (action.contains("#Script#")) {
                action = action.replace("#Script# ", "").replace("#Script#", "");
                if (action.contains("[IF]")) {
                    List<String> elseActions = new ArrayList<>();
                    action = action.replace("[IF] ", "").replace("[IF]", "");
                    if (action.contains(" [ElseAction] ")) {
                        String elsestring = action.split(Pattern.quote(" [ElseAction] "))[1];
                        action = action.split(Pattern.quote(" [ElseAction] "))[0];
                        elseActions.addAll(Arrays.asList(elsestring.split(";")));
                    }
                    action = replaceArgs(action, raffleData);
                    action = StringUtil.setPlaceholders(player, action);
                    action = math(action);

                    ArrayList<String> ORstatments = new ArrayList<>();
                    if (action.split("<or>").length > 1) {
                        String[] arrayOfString;
                        int j = (arrayOfString = action.split("<or>")).length;
                        for (int i = 0; i < j; i++) {
                            String s = arrayOfString[i];
                            ORstatments.add(s);
                        }
                    } else {
                        ORstatments.add(action);
                    }
                    for (String orString : ORstatments) {
                        List<String> ANDstatments = new ArrayList<>();
                        int AND_TRUE_RESULTS = 0;
                        if (orString.split("<and>").length > 1) {
                            String[] arrayOfString;
                            int m = (arrayOfString = orString.split("<and>")).length;
                            for (int k = 0; k < m; k++) {
                                String s = arrayOfString[k];
                                ANDstatments.add(s);
                            }
                        } else {
                            ANDstatments.add(orString);
                        }
                        for (String st : ANDstatments) {
                            if (st.contains("HasPermission==")) {
                                String permission = st.split("HasPermission==")[1];
                                if (player.hasPermission(permission)) {
                                    AND_TRUE_RESULTS++;
                                }
                            } else if (st.contains(".args.lenght>")) {
                                String a = st.split(".args.lenght>")[0];
                                int b = 0;
                                try {
                                    b = Integer.valueOf(st.split(".args.lenght>")[1]).intValue();
                                } catch (Exception localException) {
                                }
                                if (a.split(" ").length > b) {
                                    AND_TRUE_RESULTS++;
                                }
                            } else if (st.contains(".string.lenght>")) {
                                String a = st.split(".string.lenght>")[0];
                                int b = 0;
                                try {
                                    b = Integer.valueOf(st.split(".string.lenght>")[1]).intValue();
                                } catch (Exception localException1) {
                                }
                                if (a.length() > b) {
                                    AND_TRUE_RESULTS++;
                                }
                            } else if (st.contains("<=")) {
                                Double v1 = Double.valueOf(0.0D);
                                Double v2 = Double.valueOf(0.0D);
                                try {
                                    v1 = Double.valueOf(st.split("<=")[0]);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    v2 = Double.valueOf(st.split("<=")[1]);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                if (v1.doubleValue() <= v2.doubleValue()) {
                                    AND_TRUE_RESULTS++;
                                }
                            } else if (st.contains(">=")) {
                                Double v1 = Double.valueOf(0.0D);
                                Double v2 = Double.valueOf(0.0D);
                                try {
                                    v1 = Double.valueOf(st.split(">=")[0]);
                                } catch (NumberFormatException e) {
                                    v1 = Double.valueOf(0.0D);
                                }
                                try {
                                    v2 = Double.valueOf(st.split(">=")[1]);
                                } catch (NumberFormatException e) {
                                    v2 = Double.valueOf(0.0D);
                                }
                                if (v1.doubleValue() >= v2.doubleValue()) {
                                    AND_TRUE_RESULTS++;
                                }
                            } else if (st.contains(">")) {
                                Double v1 = Double.valueOf(0.0D);
                                Double v2 = Double.valueOf(0.0D);
                                try {
                                    v1 = Double.valueOf(st.split(">")[0]);
                                } catch (NumberFormatException e) {
                                    v1 = Double.valueOf(0.0D);
                                }
                                try {
                                    v2 = Double.valueOf(st.split(">")[1]);
                                } catch (NumberFormatException e) {
                                    v2 = Double.valueOf(0.0D);
                                }
                                if (v1.doubleValue() > v2.doubleValue()) {
                                    AND_TRUE_RESULTS++;
                                }
                            } else if (st.contains("<")) {
                                Double v1 = Double.valueOf(0.0D);
                                Double v2 = Double.valueOf(0.0D);
                                try {
                                    v1 = Double.valueOf(st.split("<")[0]);
                                } catch (NumberFormatException e) {
                                    v1 = Double.valueOf(0.0D);
                                }
                                try {
                                    v2 = Double.valueOf(st.split("<")[1]);
                                } catch (NumberFormatException e) {
                                    v2 = Double.valueOf(0.0D);
                                }
                                if (v1.doubleValue() < v2.doubleValue()) {
                                    AND_TRUE_RESULTS++;
                                }
                            } else if (st.contains("==")) {
                                if (st.split("==")[0].equalsIgnoreCase(st.split("==")[1])) {
                                    AND_TRUE_RESULTS++;
                                }
                            } else if (st.contains("!=")) {
                                if (!st.split("!=")[0].equalsIgnoreCase(st.split("!=")[1])) {
                                    AND_TRUE_RESULTS++;
                                }
                            } else if (st.contains(".contains=")) {
                                if (st.split(".contains=")[0].contains(st.split(".contains=")[1])) {
                                    AND_TRUE_RESULTS++;
                                }
                            } else if (st.contains(".type=")) {
                                Boolean control = true;
                                Object variable = st.split(".type=")[0];
                                String type = st.split(".type=")[1];
                                if (type.equalsIgnoreCase("number")) {
                                    try {
                                        Double parse = Double.parseDouble(String.valueOf(variable));
                                        control = true;
                                    } catch (NumberFormatException ex) {
                                        control = false;
                                    }
                                }
                                if (control) AND_TRUE_RESULTS++;
                            }
                        }
                        if (AND_TRUE_RESULTS >= ANDstatments.size()) {
                            return true;
                        } else {
                            if (!elseActions.isEmpty()) {
                                for (String ea : elseActions) {
                                    runAction(player, ea, raffleData);
                                }
                            }
                        }
                    }
                    return false;
                }
            }
        } catch (NumberFormatException er) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while executing %if%/%while% syntax. NumberFormatException");
            return false;
        }
        return true;
    }

    private void runAction(Player player, String action, RaffleData raffleData) {
        action = action.replace("[Delay=0]", "").replace("[Delay=0]", "");
        if (action.contains("[JavaScript=")) {
            HashMap<String, String> scripts = new HashMap<>();
            String script = null;
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");
            engine.put("BukkitPlayer", player);
            engine.put("ActionAPI", this.plugin);
            engine.put("PlayerCommand", "[PlayerCommand]");
            engine.put("ConsoleCommand", "[ConsoleCommand]");
            engine.put("OperatorCommand", "[OperatorCommand]");
            engine.put("Message", "[Message]");
            engine.put("Broadcast", "[Broadcast]");
            engine.put("Sound", "[Sound]");
            engine.put("VaultGive", "[VaultGive]");
            engine.put("VaultTake", "[VaultTake]");
            engine.put("Teleport", "[Teleport]");
            engine.put("GiveItem", "[GiveItem]");
            engine.put("Title", "[Title]");
            engine.put("ActionBar", "[ActionBar]");
            engine.put("JSONMessage", "[JSONMessage]");
            engine.put("JSONBroadcast", "[JSONBroadcast]");
            engine.put("Bungee", "[Bungee]");
            for (int i = 0; i < action.length(); ++i) {
                if (action.charAt(i) == '[' && action.substring(i, i + 12).equals("[JavaScript=")) {
                    for (int e = i + 12; e < action.length(); ++e) {
                        if (action.charAt(e) == ']') {
                            final String orginalScript;
                            script = (orginalScript = action.substring(i + 12, e));
                            script = StringUtil.setPlaceholders(player, script);
                            script = replaceArgs(script, raffleData);
                            script = math(script).toString();
                            String result = null;
                            try {
                                final Object obj = engine.eval(script);
                                if (obj != null) {
                                    result = obj.toString();
                                }
                            } catch (ScriptException e2) {
                                e2.printStackTrace();
                            }
                            if (result != null && !result.isEmpty()) {
                                scripts.put(orginalScript, result);
                            }
                            e = action.length();
                        }
                    }
                }
            }
            if (scripts != null && !scripts.isEmpty()) {
                for (final String sc : scripts.keySet()) {
                    action = action.replace("[JavaScript=" + sc + "]", scripts.get(sc));
                }
            }
        }
        if (action.contains("[PlayerCommand]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[PlayerCommand] ", "").replace("[PlayerCommand]", ""));
            action = replaceArgs(action, raffleData);
            action = math(action);
            player.performCommand(action);
        } else if (action.contains("[ConsoleCommand]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[ConsoleCommand] ", "").replace("[ConsoleCommand]", ""));
            action = replaceArgs(action, raffleData);
            action = math(action);
            this.plugin.getServer().dispatchCommand((CommandSender) this.plugin.getServer().getConsoleSender(), action);
        } else if (action.contains("[OperatorCommand]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[OperatorCommand] ", "").replace("[OperatorCommand]", ""));
            action = replaceArgs(action, raffleData);
            action = math(action);
            if (!player.isOp()) {
                player.setOp(true);
                this.plugin.getServer().dispatchCommand((CommandSender) player, action);
                player.setOp(false);
            } else {
                this.plugin.getServer().dispatchCommand((CommandSender) player, action);
            }
        } else if (action.contains("[Message]")) {
            action = ChatColor.translateAlternateColorCodes('&', StringUtil.setPlaceholders(player, action.replace("[Message] ", "").replace("[Message]", "")));
            action = replaceArgs(action, raffleData);
            action = math(action);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', action));
        } else if (action.contains("[Broadcast]")) {
            action = ChatColor.translateAlternateColorCodes('&', StringUtil.setPlaceholders(player, action.replace("[Broadcast] ", "").replace("[Broadcast]", "")));
            action = replaceArgs(action, raffleData);
            action = math(action);
            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', action));
        } else if (action.contains("[Sound]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[Sound] ", "").replace("[Sound]", ""));
            action = replaceArgs(action, raffleData);
            action = math(action);
            final float soundFloat = 1.0f;
            player.playSound(player.getLocation(), Sound.valueOf(action.toUpperCase()), soundFloat, soundFloat);
        } else if (action.contains("[VaultGive]")) {
            if (plugin.getEconomyDepend().dependent()) {
                action = StringUtil.setPlaceholders(player, action.replace("[VaultGive] ", "").replace("[VaultGive]", ""));
                action = replaceArgs(action, raffleData);
                action = math(action);
                final int amount = Integer.parseInt(action);
                this.plugin.getEconomyDepend().get().depositPlayer((OfflinePlayer) player, (double) amount);
            }
        } else if (action.contains("[VaultTake]")) {
            if (plugin.getEconomyDepend().dependent()) {
                action = StringUtil.setPlaceholders(player, action.replace("[VaultTake] ", "").replace("[VaultTake]", ""));
                action = replaceArgs(action, raffleData);
                action = math(action);
                final int amount = Integer.parseInt(action);
                this.plugin.getEconomyDepend().get().withdrawPlayer((OfflinePlayer) player, (double) amount);
            }
        } else if (action.contains("[Teleport]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[Teleport] ", "").replace("[Teleport]", ""));
            action = replaceArgs(action, raffleData);
            action = math(action);
            final String[] location = action.split(";");
            Location destination = null;
            if (location.length == 4) {
                final World world = Bukkit.getWorld(location[0]);
                final double x = Double.parseDouble(location[1]);
                final double y = Double.parseDouble(location[2]);
                final double z = Double.parseDouble(location[3]);
                destination = new Location(world, x, y, z);
            } else if (location.length == 6) {
                final World world = Bukkit.getWorld(location[0]);
                final double x = Double.parseDouble(location[1]);
                final double y = Double.parseDouble(location[2]);
                final double z = Double.parseDouble(location[3]);
                final float yaw = Float.parseFloat(location[4]);
                final float pitch = Float.parseFloat(location[5]);
                destination = new Location(world, x, y, z, yaw, pitch);
            }
            if (location != null) {
                player.teleport(destination);
            }
        } else if (action.contains("[GiveItem]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[GiveItem] ", "").replace("[GiveItem]", ""));
            action = replaceArgs(action, raffleData);
            action = math(action);
            final String[] item = action.split(";");
            ItemStack newItem = null;
            if (item.length >= 1) {
                newItem = new ItemStack(Material.valueOf(item[0]), 1);
                if (item.length >= 2) {
                    if (item[1] != "") {
                        newItem.setAmount(Integer.parseInt(item[1]));
                    } else {
                        newItem.setAmount(1);
                    }
                    if (item.length >= 3) {
                        if (item[2] != "") {
                            newItem.setDurability(Short.parseShort(item[2]));
                        }
                        if (item.length >= 4) {
                            if (item[3] != "") {
                                newItem.setData(new MaterialData(Material.valueOf(item[0]), Byte.parseByte(item[3])));
                            }
                            if (item.length >= 5) {
                                if (item[4] != "") {
                                    ItemMeta meta = newItem.getItemMeta();
                                    meta.setDisplayName(StringUtil.cc(item[4]));
                                    newItem.setItemMeta(meta);
                                }
                                if (item.length >= 6) {
                                    if (item[6] != "") {
                                        List<String> splitLore = Arrays.asList(item[6].split(":"));
                                        ItemMeta meta = newItem.getItemMeta();
                                        List<String> colorizedLore = new ArrayList<>();
                                        for (String s : splitLore) {
                                            colorizedLore.add(StringUtil.cc(s));
                                        }
                                        meta.setLore(colorizedLore);
                                        newItem.setItemMeta(meta);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (newItem != null) {
                if (player.getInventory().firstEmpty() < 0) {
                    player.getWorld().dropItemNaturally(player.getLocation(), newItem);
                } else {
                    player.getInventory().addItem(new ItemStack[]{newItem});
                }
            }
        } else if (action.contains("[RemoveItem]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[RemoveItem] ", "").replace("[RemoveItem]", ""));
            action = replaceArgs(action, raffleData);
            action = math(action);
            final String[] item = action.split(";");
            ItemStack removeItem = null;
            if (item.length == 1) {
                removeItem = new ItemStack(Material.valueOf(item[0]), 1);
            } else if (item.length == 2) {
                removeItem = new ItemStack(Material.valueOf(item[0]), Integer.parseInt(item[1]));
            } else if (item.length == 3) {
                removeItem = new ItemStack(Material.valueOf(item[0]), Integer.parseInt(item[1]), (short) Integer.parseInt(item[2]));
            } else if (item.length == 4) {
                removeItem = new ItemStack(Material.valueOf(item[0]), Integer.parseInt(item[1]), (short) Integer.parseInt(item[2]), (byte) Integer.parseInt(item[3]));
            }
            if (removeItem != null) {
                player.getInventory().removeItem(removeItem);
            }
        } else if (action.contains("[CloseInventory]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[CloseInventory] ", "").replace("[CloseInventory]", ""));
            action = replaceArgs(action, raffleData);
            action = math(action);
            player.closeInventory();
        } else if (action.contains("[Title]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[Title] ", "").replace("[Title]", ""));
            action = replaceArgs(action, raffleData);
            action = math(action);
            final String[] titleString = action.split(";");
            if (titleString.length == 1 || titleString.length == 2) {
                this.plugin.getCompatabilityManager().sendTitle(player, StringUtil.setPlaceholders(player, titleString[0]));
                if (titleString.length == 2) {
                    this.plugin.getCompatabilityManager().sendSubtitle(player, StringUtil.setPlaceholders(player, titleString[1]));
                }
            }
        } else if (action.contains("[ActionBar]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[ActionBar] ", "").replace("[ActionBar]", ""));
            action = replaceArgs(action, raffleData);
            action = math(action);
            this.plugin.getCompatabilityManager().sendAction(player, StringUtil.setPlaceholders(player, action));
        } else if (action.contains("[JSONMessage]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[JSONMessage] ", "").replace("[JSONMessage]", ""));
            action = replaceArgs(action, raffleData);
            action = math(action);
            this.plugin.getCompatabilityManager().sendJSONMessage(player, action);
        } else if (action.contains("[JSONBroadcast]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[JSONBroadcast] ", "").replace("[JSONBroadcast]", ""));
            action = replaceArgs(action, raffleData);
            action = math(action);
            this.plugin.getCompatabilityManager().sendJSONBroadcast(this.plugin.getServer().getOnlinePlayers(), action);
        } else if (action.contains("[Bungee]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[Bungee] ", "").replace("[Bungee]", ""));
            action = replaceArgs(action, raffleData);
            action = math(action);
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(action);
            player.sendPluginMessage((Plugin) this.plugin, "BungeeCord", out.toByteArray());
        } else if (action.contains("[addGroup]")) {
            if (plugin.getPermissionsExDepend().dependent()) {
                action = StringUtil.setPlaceholders(player, action.replace("[addGroup] ", "").replace("[addGroup]", ""));
                action = replaceArgs(action, raffleData);
                action = math(action);
                String[] splitAction = action.split(" [World] ");
                String[] groups = splitAction[0].split(", ");
                PermissionUser permissionUser = PermissionsEx.getUser(player.getName());
                if (splitAction.length == 1) {
                    for (String g : groups) {
                        permissionUser.addGroup(g);
                    }
                } else if (splitAction.length == 2) {
                    for (String g : groups) {
                        permissionUser.addGroup(g, splitAction[1]);
                    }
                }
            }
        } else if (action.contains("[setGroup]")) {
            if (plugin.getPermissionsExDepend().dependent()) {
                action = StringUtil.setPlaceholders(player, action.replace("[setGroup] ", "").replace("[setGroup]", ""));
                action = replaceArgs(action, raffleData);
                action = math(action);
                String[] splitAction = action.split(" [World] ");
                String[] groups = splitAction[0].split(", ");
                PermissionUser permissionUser = PermissionsEx.getUser(player.getName());
                if (splitAction.length == 1) {
                    permissionUser.setGroups(groups);
                } else if (splitAction.length == 2) {
                    permissionUser.setGroups(groups, splitAction[1]);
                }
            }
        } else if (action.contains("[removeGroup]")) {
            if (plugin.getPermissionsExDepend().dependent()) {
                action = StringUtil.setPlaceholders(player, action.replace("[removeGroup] ", "").replace("[removeGroup]", ""));
                action = replaceArgs(action, raffleData);
                action = math(action);
                String[] splitAction = action.split(" [World] ");
                String[] groups = splitAction[0].split(", ");
                PermissionUser permissionUser = PermissionsEx.getUser(player.getName());
                if (splitAction.length == 1) {
                    for (String g : groups) {
                        permissionUser.removeGroup(g);
                    }
                } else if (splitAction.length == 2) {
                    for (String g : groups) {
                        permissionUser.removeGroup(g, splitAction[1]);
                    }
                }
            }
        } else if (action.startsWith("#PlayerOptions#") || action.startsWith("#PlayerOptions==")) {
            action = replaceArgs(action, raffleData);
            action = math(action);
            playerOptions(action, player, raffleData);
        }
    }
}

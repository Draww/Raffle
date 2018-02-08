package com.drawwdev.raffle.utils;

import com.drawwdev.raffle.Main;
import com.drawwdev.raffle.Raffle;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class ScriptSystem {

    private Main plugin;

    public ScriptSystem(Main plugin) {
        this.plugin = plugin;
    }

    public void executeActions(Player player, List<String> actions) {
        if (actions == null || actions.isEmpty()) {
            return;
        }
        for (String action : actions) {
            executeAction(player, action);
        }
    }

    private boolean executeAction(Player player, String action) {
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
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> runAction(player, runAction), (long) delayTimer);
        } else {
            runAction(player, runAction);
        }
        return false;
    }

    public Boolean runCondition(Player player, String action) {
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
                                action.replace("[Action] ", "").replace("[Action]", "");
                                executeAction(player, action);
                            }
                            return false;
                        }
                    }
                }
            }
        }
        if (action.contains("[HasItem]")) {
            action = action.replace("[HasItem] ", "").replace("[HasItem]", "");
            String[] splitAction = action.split(Pattern.quote(" [Action] "));
            final String[] item = splitAction[0].split(";");
            Integer stackItem = Integer.parseInt(item[1]);
            if (item[2] != null) {
                if (!InventoryUtils.containsAtLeast(player.getInventory(), Material.valueOf(item[0]), stackItem, Short.parseShort(item[2]))) {
                    if (splitAction[1] != null) {
                        executeAction(player, splitAction[1]);
                    }
                    return false;
                }
            } else {
                if (!InventoryUtils.containsAtLeast(player.getInventory(), Material.valueOf(item[0]), stackItem)) {
                    if (splitAction[1] != null) {
                        executeAction(player, splitAction[1]);
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
            String[] splitAction = action.split(Pattern.quote(" [Action] "));
            Double moneyPlayer = Main.getInstance().getEconomyDepend().get().getBalance(player);
            Double needMoney = Double.parseDouble(splitAction[0]);
            if (moneyPlayer < needMoney) {
                if (splitAction[1] != null) {
                    executeAction(player, splitAction[1]);
                }
                return false;
            }
        }
        if (action.contains("[HaveLevel]")) {
            action = action.replace("[HaveLevel] ", "").replace("[HaveLevel]", "");
            String[] splitAction = action.split(Pattern.quote(" [Action] "));
            Integer levelPlayer = player.getLevel();
            Integer needLevel = Integer.parseInt(splitAction[0]);
            if (levelPlayer < needLevel) {
                if (splitAction[1] != null) {
                    executeAction(player, splitAction[1]);
                }
                return false;
            }
        }
        if (action.contains("[hasGroup]")) {
            action = action.replace("[hasGroup] ", "").replace("[hasGroup]", "");
            String[] splitAction = action.split(Pattern.quote(" [Action] "));
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
                        executeAction(player, splitAction[1]);
                    }
                }
                return controlGroup;
            }
        }
        return true;
    }

    private void runAction(Player player, String action) {
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
            player.performCommand(action);
        } else if (action.contains("[ConsoleCommand]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[ConsoleCommand] ", "").replace("[ConsoleCommand]", ""));
            this.plugin.getServer().dispatchCommand((CommandSender) this.plugin.getServer().getConsoleSender(), action);
        } else if (action.contains("[OperatorCommand]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[OperatorCommand] ", "").replace("[OperatorCommand]", ""));
            if (!player.isOp()) {
                player.setOp(true);
                this.plugin.getServer().dispatchCommand((CommandSender) player, action);
                player.setOp(false);
            } else {
                this.plugin.getServer().dispatchCommand((CommandSender) player, action);
            }
        } else if (action.contains("[Message]")) {
            action = ChatColor.translateAlternateColorCodes('&', StringUtil.setPlaceholders(player, action.replace("[Message] ", "").replace("[Message]", "")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', action));
        } else if (action.contains("[Broadcast]")) {
            action = ChatColor.translateAlternateColorCodes('&', StringUtil.setPlaceholders(player, action.replace("[Broadcast] ", "").replace("[Broadcast]", "")));
            this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', action));
        } else if (action.contains("[Sound]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[Sound] ", "").replace("[Sound]", ""));
            final float soundFloat = 1.0f;
            player.playSound(player.getLocation(), Sound.valueOf(action.toUpperCase()), soundFloat, soundFloat);
        } else if (action.contains("[VaultGive]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[VaultGive] ", "").replace("[VaultGive]", ""));
            final int amount = Integer.parseInt(action);
            this.plugin.getEconomyDepend().get().depositPlayer((OfflinePlayer) player, (double) amount);
        } else if (action.contains("[VaultTake]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[VaultTake] ", "").replace("[VaultTake]", ""));
            final int amount = Integer.parseInt(action);
            this.plugin.getEconomyDepend().get().withdrawPlayer((OfflinePlayer) player, (double) amount);
        } else if (action.contains("[Teleport]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[Teleport] ", "").replace("[Teleport]", ""));
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
            final String[] item = action.split(";");
            ItemStack newItem = null;
            if (item.length == 1) {
                newItem = new ItemStack(Material.valueOf(item[0]), 1);
            } else if (item.length == 2) {
                newItem = new ItemStack(Material.valueOf(item[0]), Integer.parseInt(item[1]));
            } else if (item.length == 3) {
                newItem = new ItemStack(Material.valueOf(item[0]), Integer.parseInt(item[1]), (short) (byte) Integer.parseInt(item[2]));
            } else if (item.length == 4) {
                newItem = new ItemStack(Material.valueOf(item[0]), Integer.parseInt(item[1]), (short) (byte) Integer.parseInt(item[2]));
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
            player.closeInventory();
        } else if (action.contains("[Title]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[Title] ", "").replace("[Title]", ""));
            final String[] titleString = action.split(";");
            if (titleString.length == 1 || titleString.length == 2) {
                this.plugin.getCompatabilityManager().sendTitle(player, StringUtil.setPlaceholders(player, titleString[0]));
                if (titleString.length == 2) {
                    this.plugin.getCompatabilityManager().sendSubtitle(player, StringUtil.setPlaceholders(player, titleString[1]));
                }
            }
        } else if (action.contains("[ActionBar]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[ActionBar] ", "").replace("[ActionBar]", ""));
            this.plugin.getCompatabilityManager().sendAction(player, StringUtil.setPlaceholders(player, action));
        } else if (action.contains("[JSONMessage]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[JSONMessage] ", "").replace("[JSONMessage]", ""));
            this.plugin.getCompatabilityManager().sendJSONMessage(player, action);
        } else if (action.contains("[JSONBroadcast]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[JSONBroadcast] ", "").replace("[JSONBroadcast]", ""));
            this.plugin.getCompatabilityManager().sendJSONBroadcast(this.plugin.getServer().getOnlinePlayers(), action);
        } else if (action.contains("[Bungee]")) {
            action = StringUtil.setPlaceholders(player, action.replace("[Bungee] ", "").replace("[Bungee]", ""));
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(action);
            player.sendPluginMessage((Plugin) this.plugin, "BungeeCord", out.toByteArray());
        } else if (action.contains("[addGroup]")) {
            if (plugin.getPermissionsExDepend().dependent()){
                action = StringUtil.setPlaceholders(player, action.replace("[addGroup] ", "").replace("[addGroup]", ""));
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
            if (plugin.getPermissionsExDepend().dependent()){
                action = StringUtil.setPlaceholders(player, action.replace("[setGroup] ", "").replace("[setGroup]", ""));
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
            if (plugin.getPermissionsExDepend().dependent()){
                action = StringUtil.setPlaceholders(player, action.replace("[removeGroup] ", "").replace("[removeGroup]", ""));
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
        }
    }


}

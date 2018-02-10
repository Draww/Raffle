package com.drawwdev.raffle;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.drawwdev.raffle.utils.StringUtil.cc;

public class RaffleCommand implements CommandExecutor {

    private Main plugin;
    private RaffleManager raffleManager;

    public RaffleCommand(Main plugin) {
        this.plugin = plugin;
        raffleManager = new RaffleManager(plugin);
        plugin.getCommand("raffle").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(cc(plugin.getConfig().getString("prefix") + " &7you can only do it from the game."));
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("raffle.*")){
            sender.sendMessage(cc(plugin.getConfig().getString("prefix") + " &7you are not authorized to perform this command."));
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(cc(plugin.getConfig().getString("prefix") + " &7Command was entered missing."));
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            if (args.length < 2){
                player.sendMessage(cc(plugin.getConfig().getString("prefix") + " &7Command was entered missing."));
                return true;
            }
            String rType = args[1].toUpperCase();
            Boolean typeExist = Arrays.stream(getRaffleManager().getRaffleStorage().getAllKey().toArray(new String[0])).anyMatch(p -> p.equals(args[1].toUpperCase()));
            if (!typeExist){
                player.sendMessage(cc(plugin.getConfig().getString("prefix") + " &7No such raffle type."));
                return true;
            }
            if (raffleManager.getStatus()){
                player.sendMessage(cc(plugin.getConfig().getString("prefix") + " &7There's a raffle going on!"));
                return true;
            }
            Raffle raffle = getRaffleManager().getRaffleStorage().getOraffle().get(rType);
            if (raffle == null){
                player.sendMessage(cc(plugin.getConfig().getString("prefix") + " &7This raffle is disabled!"));
                return true;
            }
            if (raffle.getConsumer() == null && raffle.getPredicate() == null){
                player.sendMessage(cc(plugin.getConfig().getString("prefix") + " &7This raffle is disabled!"));
                return true;
            }
            RaffleData raffleData = new RaffleData().set(Arrays.asList(args).subList(2, Arrays.asList(args).size()));
            if (getRaffleManager().getRaffleStorage().getPredicate(rType).check(player, raffleData)){
                try {
                    if (!plugin.getConfig().getStringList("disabledGroup").isEmpty()) {
                        getRaffleManager().start(player, rType, raffleData, plugin.getConfig().getStringList("disabledGroup").toArray(new String[0]));
                    } else {
                        getRaffleManager().start(player, rType, raffleData);
                    }
                } catch (RaffleException e) {
                    player.sendMessage(cc(plugin.getConfig().getString("prefix") + e.getMessage()));
                    return true;
                }
            } else {
                player.sendMessage(cc(plugin.getConfig().getString("prefix") + " &7You don't meet the conditions"));
            }
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (!raffleManager.getStatus()){
                player.sendMessage(cc(plugin.getConfig().getString("prefix") + " There's no Raffles right now!"));
                return true;
            }
            try {
                getRaffleManager().stop(player);
            } catch (RaffleException e) {
                player.sendMessage(cc(plugin.getConfig().getString("prefix") + e.getMessage()));
                return true;
            }
        } else if (args[0].equalsIgnoreCase("types")) {
            player.sendMessage(cc("&6o0=======&c[&eRaffle Types&c]&6========0o"));
            String[] raffleTypes = getRaffleManager().getRaffleStorage().getAllKey().toArray(new String[0]);
            for (String r : raffleTypes){
                Raffle raffle = getRaffleManager().getRaffleStorage().getOraffle().get(r);
                String state = getRaffleManager().getRaffleStorage().getConsumer(r) != null && getRaffleManager().getRaffleStorage().getPredicate(r) != null ? "&aON" : "&cOFF";
                if (raffle.getRaffleType().equals(RaffleType.CUSTOM)){
                    player.sendMessage(cc("&f- &b" + r + "&7(" + raffle.getDatatype() + ") " + state + " &7|| &2CUSTOM"));
                } else {
                    player.sendMessage(cc("&f- &b" + r + "&7(" + raffle.getDatatype() + ") " + state));
                }
            }
        } else if (args[0].equalsIgnoreCase("reload")){
            plugin.reloadConfig();
            Main.getInstance().getConfigs().get("custom").load();
            getRaffleManager().reload();
            player.sendMessage(cc(plugin.getConfig().getString("prefix") + " &7All systems reloaded and restored!"));
        } else if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage(cc("&6o0=======&c[&eRaffle Help&c]&6========0o"));
            player.sendMessage(cc("&b/" + label + " start <type> <data> &f- &e" + "Starting a raffle"));
            player.sendMessage(cc("&b/" + label + " stop &f- &e" + "Stop the raffle"));
            player.sendMessage(cc("&b/" + label + " types &f- &e" + "types of Raffles"));
            player.sendMessage(cc("&b/" + label + " reload &f- &e" + "Reloads the all systems"));
        }
        return true;
    }

    public RaffleManager getRaffleManager() {
        return raffleManager;
    }
}

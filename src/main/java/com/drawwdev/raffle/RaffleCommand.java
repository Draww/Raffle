package com.drawwdev.raffle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

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
            sender.sendMessage(cc(plugin.getConfig().getString("prefix") + " you can only do it from the game."));
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(cc(plugin.getConfig().getString("prefix") + " Command was entered missing."));
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            if (args.length < 2){
                player.sendMessage(cc(plugin.getConfig().getString("prefix") + " Command was entered missing."));
                return true;
            }
            Boolean typeExist = Arrays.stream(RaffleType.values()).anyMatch(p -> p.name().equals(args[1].toUpperCase()));
            if (!typeExist){
                player.sendMessage(cc(plugin.getConfig().getString("prefix") + " No such raffle type."));
                return true;
            }
            if (raffleManager.getStatus()){
                player.sendMessage(cc(plugin.getConfig().getString("prefix") + " There's a raffle going on!"));
                return true;
            }
            try {
                getRaffleManager().start(player, RaffleType.valueOf(args[1].toUpperCase()), new RaffleData().set(0, Double.valueOf(args[2])), 5);
            } catch (RaffleException e) {
                player.sendMessage(cc(plugin.getConfig().getString("prefix") + e.getMessage()));
                return true;
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
            RaffleType[] raffleTypes = RaffleType.values();
            for (RaffleType r : raffleTypes){
                String state = getRaffleManager().getOrganizedRaffle().get(r) != null ? "&aON" : "&cOFF";
                player.sendMessage(cc("&f- &b" + r + "&7(" + r.getData() + ") " + state));
            }
        } else if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage(cc("&6o0=======&c[&eRaffle Help&c]&6========0o"));
            player.sendMessage(cc("&b/" + label + " start <type> <data> &f- &e" + "Starting a raffle"));
            player.sendMessage(cc("&b/" + label + " stop &f- &e" + "Stop the raffle"));
            player.sendMessage(cc("&b/" + label + " types &f- &e" + "Raffle types"));
        }
        return true;
    }

    public RaffleManager getRaffleManager() {
        return raffleManager;
    }
}

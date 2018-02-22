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
            sender.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("only-game-command")));
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("missing-command")));
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            if (!player.hasPermission("raffle.start")){
                sender.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("not-authorized-command")));
                return true;
            }
            if (args.length < 2){
                player.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("missing-command")));
                return true;
            }
            String rType = args[1].toUpperCase();
            Boolean typeExist = Arrays.stream(getRaffleManager().getRaffleStorage().getAllKey().toArray(new String[0])).anyMatch(p -> p.equals(args[1].toUpperCase()));
            if (!typeExist){
                player.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("no-such-raffletype")));
                return true;
            }
            if (raffleManager.getStatus()){
                player.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("already-continue")));
                return true;
            }
            Raffle raffle = getRaffleManager().getRaffleStorage().getOraffle().get(rType);
            if (raffle == null){
                player.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("disabled-raffle")));
                return true;
            }
            if (raffle.getConsumer() == null && raffle.getPredicate() == null){
                player.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("disabled-raffle")));
                return true;
            }
            RaffleData raffleData = new RaffleData().set(Arrays.asList(args).subList(2, Arrays.asList(args).size()));
            if (getRaffleManager().getRaffleStorage().getPredicate(rType).check(player, raffleData)){
                try {
                    if (!getRaffleManager().getRaffleStorage().getDisabledGroups(rType).isEmpty()) {
                        if (plugin.getDepends().get("PermissionsEx").dependent()){
                            getRaffleManager().start(player, rType, raffleData, getRaffleManager().getRaffleStorage().getDisabledGroups(rType).toArray(new String[0]));
                        } else {
                            player.sendMessage(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("permissionsex-not-depend"));
                            return true;
                        }
                    } else {
                        getRaffleManager().start(player, rType, raffleData);
                    }
                } catch (RaffleException e) {
                    player.sendMessage(cc(plugin.getLanguage().tl("prefix") + e.getMessage()));
                    return true;
                }
            } else {
                player.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("not-met-conditions")));
            }
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (!player.hasPermission("raffle.stop")){
                sender.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("not-authorized-command")));
                return true;
            }
            if (!raffleManager.getStatus()){
                player.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("no-ongoing")));
                return true;
            }
            try {
                getRaffleManager().stop(player);
            } catch (RaffleException e) {
                player.sendMessage(cc(plugin.getLanguage().tl("prefix") + e.getMessage()));
                return true;
            }
        } else if (args[0].equalsIgnoreCase("types")) {
            if (!player.hasPermission("raffle.types")){
                sender.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("not-authorized-command")));
                return true;
            }
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
            if (!player.hasPermission("raffle.reload")){
                sender.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("not-authorized-command")));
                return true;
            }
            plugin.reloadConfig();
            plugin.getLanguage().reload();
            Main.getInstance().getConfigs().get("custom").load();
            getRaffleManager().reload();
            player.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("reload")));
        } else if (args[0].equalsIgnoreCase("help")) {
            if (!player.hasPermission("raffle.help")){
                sender.sendMessage(cc(plugin.getLanguage().tl("prefix") + plugin.getLanguage().tl("not-authorized-command")));
                return true;
            }
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

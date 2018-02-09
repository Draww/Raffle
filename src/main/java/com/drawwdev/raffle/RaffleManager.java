package com.drawwdev.raffle;

import com.drawwdev.raffle.utils.ListUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.tehkode.permissions.PermissionUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import static com.drawwdev.raffle.utils.StringUtil.cc;

public class RaffleManager {

    private Main plugin;
    private Boolean status = false;
    private String raffleName = null;

    private Player maker = null;

    private BukkitTask task = null;
    static Integer taskTimer = 1;

    private RaffleStorage raffleStorage;

    public RaffleManager(Main plugin) {
        this.plugin = plugin;
        raffleStorage = new RaffleLoader(plugin, new RaffleStorage(plugin)).load();
        reset();
    }

    public void reload(){
        reset();
        raffleStorage = new RaffleLoader(plugin, new RaffleStorage(plugin)).load();
    }

    public void start(Player player, String raffleName, RaffleData raffleData, String... nonGroup) throws RaffleException {
        if (getStatus()) throw new RaffleException(" &7There's a raffle going on!");
        if (getRaffleStorage().getConsumer(raffleName) == null)
            throw new RaffleException(" &7This type is not identified.");
        player.sendMessage(cc(plugin.getConfig().getString("prefix") + " &7Starting the raffle."));
        status = true;
        this.maker = player;
        this.raffleName = raffleName;
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
        this.taskTimer = 0;
        Bukkit.broadcastMessage(cc(getPlugin().getConfig().getString("prefix") + " &7The raffle started!"));
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        List<Player> organizedPlayers = new ArrayList<>();
        if (nonGroup.length > 0 && plugin.getPermissionsExDepend().dependent()) {
            for (Player p : onlinePlayers) {
                PermissionUser user = plugin.getPermissionsExDepend().getUser(p);
                for (String group : user.getGroupNames()) {
                    if (!Arrays.stream(nonGroup).parallel().anyMatch(group::contains)) {
                        organizedPlayers.add(p);
                    }
                }
            }
        } else {
            organizedPlayers = new ArrayList<Player>(onlinePlayers);
        }
        List<Player> finalOrganizedPlayers = organizedPlayers;
        Integer time = getRaffleStorage().getTime(raffleName);
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            taskTimer++;
            Integer countdown = (time + 1) - taskTimer;

            if (countdown <= 0) {
                Player randomPlayer = ListUtil.getList(finalOrganizedPlayers);
                Bukkit.broadcastMessage(cc(plugin.getConfig().getString("prefix") + " &7Winner: &6&l" + randomPlayer.getName()));
                task.cancel();
                status = false;
                task = null;
                getRaffleStorage().getConsumer(raffleName).run(randomPlayer, raffleData, maker);
                return;
            }
            Bukkit.broadcastMessage(cc(plugin.getConfig().getString("prefix") + " &d&llast " + countdown + " seconds to determine the lucky one."));
        }, 20L, 20L);
    }

    public void stop(Player player) throws RaffleException {
        if (!getStatus()) throw new RaffleException(" &7There's no Raffles right now!");
        reset();
        player.sendMessage(cc(plugin.getConfig().getString("prefix") + " &7The raffle stopped."));
    }

    public void reset() {
        this.status = false;
        this.maker = null;
        if (task != null) {
            task.cancel();
            task = null;
        }
        this.raffleName = null;
        this.taskTimer = 1;
    }

    public void reset(Boolean status, Player maker, Integer time, BukkitTask task, String raffleName, Integer taskTimer) {
        this.status = status;
        this.maker = maker;
        this.task = task;
        this.raffleName = raffleName;
        this.taskTimer = taskTimer;
    }

    public Main getPlugin() {
        return plugin;
    }

    public String getRaffleName() {
        return raffleName;
    }

    public Player getMaker() {
        return maker;
    }

    public Boolean getStatus() {
        return status;
    }

    public BukkitTask getTask() {
        return task;
    }

    public static Integer getTaskTimer() {
        return taskTimer;
    }

    public RaffleStorage getRaffleStorage() {
        return raffleStorage;
    }
}

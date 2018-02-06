package com.drawwdev.raffle;

import com.drawwdev.raffle.utils.ListUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.tehkode.permissions.PermissionUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.drawwdev.raffle.utils.StringUtil.cc;

public class RaffleManager {

    private Main plugin;
    private Boolean status = false;
    private RaffleType raffleType = null;

    private Player maker = null;
    private Integer time = null;

    private BukkitTask task = null;
    static Integer taskTimer = 1;

    private OrganizedRaffle organizedRaffle;

    public RaffleManager(Main plugin) {
        this.plugin = plugin;
        setupOrganizedRaffle();
        reset();
    }

    public void setupOrganizedRaffle() {
        organizedRaffle = new OrganizedRaffle(plugin);
        if (plugin.getEconomyDepend().dependent()){
            organizedRaffle.create(RaffleType.MONEY, new RaffleRunnable() {
                @Override
                public void run(Player player, RaffleData raffleData) {
                    plugin.getEconomyDepend().get().depositPlayer(player, (Double) raffleData.get(0));
                    Bukkit.broadcastMessage(cc(plugin.getConfig().getString("prefix") + " &6The player " + player.getName() + " &7a &a$" + (Double) raffleData.get(0) + " &7was given"));
                }
            });
            organizedRaffle.create(RaffleType.EXP, new RaffleRunnable() {
                @Override
                public void run(Player player, RaffleData raffleData) {
                    player.setExp(player.getExp() + (Float) raffleData.get(0));
                    Bukkit.broadcastMessage(cc(plugin.getConfig().getString("prefix") + " &6The player " + player.getName() + " &7a &a" + (Float) raffleData.get(0) + " Exp &7was given"));
                }
            });
            organizedRaffle.create(RaffleType.LEVEL, new RaffleRunnable() {
                @Override
                public void run(Player player, RaffleData raffleData) {
                    player.setLevel(player.getLevel() + (Integer) raffleData.get(0));
                    Bukkit.broadcastMessage(cc(plugin.getConfig().getString("prefix") + " &6The player " + player.getName() + " &7a &a" + (Integer) raffleData.get(0) + " Level &7was given"));
                }
            });
        }
    }

    public void start(Player player, RaffleType raffleType, RaffleData raffleData, Integer time, String... nonGroup) throws RaffleException {
        if (getStatus()) throw new RaffleException(" &7There's a raffle going on!");
        if (getOrganizedRaffle().get(raffleType) == null) throw new RaffleException(" &7This type is not identified.");
        player.sendMessage(cc(plugin.getConfig().getString("prefix") + " &7Starting the raffle."));
        status = true;
        this.maker = player;
        this.time = time;
        this.raffleType = raffleType;
        if (task != null && !task.isCancelled()){
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
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            taskTimer++;
            Integer countdown = (time + 1) - taskTimer;

            if (countdown <= 0){
                Player randomPlayer = ListUtil.getList(finalOrganizedPlayers);
                Bukkit.broadcastMessage(cc(plugin.getConfig().getString("prefix") + " &7Winner: &6&l" + randomPlayer.getName()));
                task.cancel();
                status = false;
                task = null;
                getOrganizedRaffle().get(raffleType).run(randomPlayer, raffleData);
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

    public void reset(){
        this.status = false;
        this.maker = null;
        this.time = null;
        if (task != null){
            task.cancel();
            task = null;
        }
        this.raffleType = null;
        this.taskTimer = 1;
    }

    public void reset(Boolean status, Player maker, Integer time, BukkitTask task, RaffleType raffleType, Integer taskTimer){
        this.status = status;
        this.maker = maker;
        this.time = time;
        this.task = task;
        this.raffleType = raffleType;
        this.taskTimer = taskTimer;
    }

    public Main getPlugin() {
        return plugin;
    }

    public RaffleType getRaffleType() {
        return raffleType;
    }

    public Player getMaker() {
        return maker;
    }

    public Integer getTime() {
        return time;
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

    public OrganizedRaffle getOrganizedRaffle() {
        return organizedRaffle;
    }
}

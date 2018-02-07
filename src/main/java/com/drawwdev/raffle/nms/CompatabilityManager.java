package com.drawwdev.raffle.nms;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract interface CompatabilityManager {

    public abstract String getVersion();

    public abstract void sendTitle(Player paramPlayer, String paramString);

    public abstract void sendSubtitle(Player paramPlayer, String paramString);

    public abstract void sendAction(Player paramPlayer, String paramString);

    public abstract void sendJSONMessage(Player paramPlayer, String paramString);

    public abstract void sendJSONBroadcast(Collection<? extends Player> paramCollection, String paramString);

    public Player getToPlayer(OfflinePlayer offlinePlayer);

}

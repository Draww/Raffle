package com.drawwdev.raffle.nms;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_9_R2.CraftServer;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;

public class Compatability_1_9_R2 implements CompatabilityManager {

    @Override
    public String getVersion() {
        return "1_9_R2";
    }

    @Override
    public void sendTitle(Player paramPlayer, String paramString) {
        if ((paramPlayer == null) || (paramString == null) || (paramString.isEmpty())) {
            return;
        }
        IChatBaseComponent localIChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + paramString + "\"}");

        PacketPlayOutTitle localPacketPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, localIChatBaseComponent);

        ((CraftPlayer)paramPlayer).getHandle().playerConnection.sendPacket(localPacketPlayOutTitle);
    }

    @Override
    public void sendSubtitle(Player paramPlayer, String paramString) {
        if ((paramPlayer == null) || (paramString == null) || (paramString.isEmpty())) {
            return;
        }
        IChatBaseComponent localIChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + paramString + "\"}");

        PacketPlayOutTitle localPacketPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, localIChatBaseComponent);

        ((CraftPlayer)paramPlayer).getHandle().playerConnection.sendPacket(localPacketPlayOutTitle);
    }

    @Override
    public void sendAction(Player paramPlayer, String paramString) {
        if ((paramPlayer == null) || (paramString == null) || (paramString.isEmpty())) {
            return;
        }
        IChatBaseComponent localIChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + paramString + "\"}");

        PacketPlayOutChat localPacketPlayOutChat = new PacketPlayOutChat(localIChatBaseComponent, (byte)2);

        ((CraftPlayer)paramPlayer).getHandle().playerConnection.sendPacket(localPacketPlayOutChat);
    }

    @Override
    public void sendJSONMessage(Player paramPlayer, String paramString) {
        if ((paramPlayer == null) || (paramString == null) || (paramString.isEmpty())) {
            return;
        }
        String[] arrayOfString = paramString.split("&&");

        IChatBaseComponent localIChatBaseComponent1 = IChatBaseComponent.ChatSerializer.a(arrayOfString[0]);
        if (arrayOfString.length > 1) {
            for (int i = 1; i < arrayOfString.length; i++)
            {
                IChatBaseComponent localIChatBaseComponent2 = IChatBaseComponent.ChatSerializer.a(arrayOfString[i]);
                localIChatBaseComponent1 = localIChatBaseComponent1.addSibling(localIChatBaseComponent2);
            }
        }
        PacketPlayOutChat localPacketPlayOutChat = new PacketPlayOutChat(localIChatBaseComponent1, (byte)0);

        ((CraftPlayer)paramPlayer).getHandle().playerConnection.sendPacket(localPacketPlayOutChat);
    }

    @Override
    public void sendJSONBroadcast(Collection<? extends Player> paramCollection, String paramString) {
        if ((paramCollection == null) || (paramString == null) || (paramString.isEmpty())) {
            return;
        }
        String[] arrayOfString = paramString.split("&&");

        IChatBaseComponent localIChatBaseComponent = IChatBaseComponent.ChatSerializer.a(arrayOfString[0]);
        Object localObject;
        if (arrayOfString.length > 1) {
            for (int i = 1; i < arrayOfString.length; i++)
            {
                localObject = IChatBaseComponent.ChatSerializer.a(arrayOfString[i]);
                localIChatBaseComponent = localIChatBaseComponent.addSibling((IChatBaseComponent)localObject);
            }
        }
        PacketPlayOutChat localPacketPlayOutChat = new PacketPlayOutChat(localIChatBaseComponent, (byte)0);
        for (Iterator localIterator = paramCollection.iterator(); localIterator.hasNext();)
        {
            localObject = (Player)localIterator.next();
            ((CraftPlayer)localObject).getHandle().playerConnection.sendPacket(localPacketPlayOutChat);
        }
    }

    @Override
    public Player getToPlayer(OfflinePlayer offlinePlayer) {
        if (!offlinePlayer.isOnline() && offlinePlayer.hasPlayedBefore()) {
            final MinecraftServer sv = ((CraftServer) Bukkit.getServer()).getServer();
            final EntityPlayer newPlayer = new EntityPlayer(sv, sv.getWorldServer(0), new GameProfile(offlinePlayer.getUniqueId(), offlinePlayer.getName()), new PlayerInteractManager(sv.getWorld()));
            final Player player = (Player)newPlayer.getBukkitEntity();
            if (player != null) {
                player.loadData();
                return player;
            }
        }
        return null;
    }
}

package com.drawwdev.raffle.utils;

import com.drawwdev.raffle.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.codec.DecoderException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.apache.commons.codec.binary.Hex;
import org.bukkit.entity.Player;

public class StringUtil {

    public static String cc(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @Nonnull
    public static String hideText(@Nonnull String text) {
        Objects.requireNonNull(text, "text can not be null!");

        StringBuilder output = new StringBuilder();

        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        String hex = Hex.encodeHexString(bytes);

        for (char c : hex.toCharArray()) {
            output.append(ChatColor.COLOR_CHAR).append(c);
        }

        return output.toString();
    }

    @Nonnull
    public static String revealText(@Nonnull String text) {
        Objects.requireNonNull(text, "text can not be null!");

        if (text.isEmpty()) {
            return text;
        }

        char[] chars = text.toCharArray();

        char[] hexChars = new char[chars.length / 2];

        IntStream.range(0, chars.length)
                .filter(value -> value % 2 != 0)
                .forEach(value -> hexChars[value / 2] = chars[value]);

        try {
            return new String(Hex.decodeHex(hexChars), StandardCharsets.UTF_8);
        } catch (DecoderException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Couldn't decode text", e);
        }
    }

    public static String setPlaceholders(Player paramPlayer, String paramString) {
        if ((paramString == null) || (paramString.isEmpty())) {
            return paramString;
        }
        paramString = setColour(paramString);
        if (!paramString.contains("%")) {
            return paramString;
        }
        paramString = paramString.replace("%prefix%", cc(Main.getInstance().getLanguage().tl("prefix")));
        paramString = paramString.replace("%server_motd%", Bukkit.getServer().getMotd());
        paramString = paramString.replace("%server_maxplayers%", String.valueOf(Bukkit.getServer().getMaxPlayers()));
        paramString = paramString.replace("%server_playercount%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
        if (paramPlayer != null) {
            paramString = paramString.replace("%player%", paramPlayer.getName());
            paramString = paramString.replace("%player_displayname%", paramPlayer.getDisplayName());
            paramString = paramString.replace("%player_saturation%", String.valueOf(paramPlayer.getSaturation()));
            paramString = paramString.replace("%player_hunger%", String.valueOf(paramPlayer.getFoodLevel()));
            paramString = paramString.replace("%player_health%", String.valueOf(paramPlayer.getHealth()));
            paramString = paramString.replace("%player_x%", String.valueOf(paramPlayer.getLocation().getBlockX()));
            paramString = paramString.replace("%player_y%", String.valueOf(paramPlayer.getLocation().getBlockY()));
            paramString = paramString.replace("%player_z%", String.valueOf(paramPlayer.getLocation().getBlockZ()));
            paramString = paramString.replace("%player_world%", String.valueOf(paramPlayer.getWorld().getName()));
            paramString = paramString.replace("%player_level%", String.valueOf(paramPlayer.getLevel()));
            paramString = paramString.replace("%player_exp%", String.valueOf(paramPlayer.getExp()));
        }
        if (Main.getInstance().getDepends().get("Placeholder").dependent()){
            paramString = PlaceholderAPI.setPlaceholders(paramPlayer, paramString);
        }
        return paramString;
    }

    public static String dateFormat(Date paramDate) {
        String str = "N/A";
        if (paramDate == null) {
            return str;
        }
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        str = localSimpleDateFormat.format(paramDate);
        return str;
    }

    public static String numberFormat(double paramDouble) {
        if (String.valueOf(paramDouble).endsWith(".0")) {
            return new DecimalFormat("#,###,###,###,###").format(paramDouble);
        }
        return new DecimalFormat("#,###,###,###,##0.00").format(paramDouble);
    }

    public static String setColour(String paramString) {
        if ((paramString == null) || (paramString.isEmpty())) {
            return null;
        }
        paramString = ChatColor.translateAlternateColorCodes('�', paramString);
        paramString = ChatColor.translateAlternateColorCodes('&', paramString);

        return paramString;
    }

    public static List<String> setColourList(List<String> oldlist) {
        List<String> newlist = new ArrayList<String>();
        for (String string : oldlist) {
            newlist.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        return newlist;
    }

}

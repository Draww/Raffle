package com.drawwdev.raffle.depend;

import com.drawwdev.raffle.Main;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceholderDepend implements Depend {

    private Main plugin;
    private Boolean dependent = false;
    private EzHook ezHook;

    public PlaceholderDepend(Main plugin) {
        this.plugin = plugin;
        setup();
    }

    @Override
    public Boolean dependent() {
        return dependent;
    }

    @Override
    public String name() {
        return "Placeholder";
    }

    @Override
    public DependType dependType() {
        return DependType.NORMAL;
    }

    @Override
    public boolean setup() {
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            dependent = false;
            return false;
        }
        dependent = true;
        ezHook = new EzHook(plugin);
        return true;
    }

    @Override
    public Object get() {
        return null;
    }

    public class EzHook extends EZPlaceholderHook{

        public EzHook(Plugin plugin) {
            super(plugin, "raffleplaceholder");
        }

        @Override
        public String onPlaceholderRequest(Player player, String identifier) {
            return null;
        }
    }
}

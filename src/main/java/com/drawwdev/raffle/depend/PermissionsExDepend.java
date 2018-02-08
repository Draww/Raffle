package com.drawwdev.raffle.depend;

import com.drawwdev.raffle.Main;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsExDepend implements Depend {

    private Main plugin;
    private Boolean dependent = false;
    private DependType dependType;

    public PermissionsExDepend(Main plugin, DependType dependType) {
        this.plugin = plugin;
        this.dependType = dependType;
        setup();
    }

    @Override
    public Boolean dependent() {
        return dependent;
    }

    @Override
    public DependType dependType() {
        return dependType;
    }

    @Override
    public boolean setup() {
        if (plugin.getServer().getPluginManager().getPlugin("PermissionsEx") == null) {
            dependent = false;
            return false;
        }
        dependent = true;
        return true;
    }

    @Override
    public Object get() {
        return null;
    }

    public PermissionUser getUser(Player player){
        return PermissionsEx.getUser(player);
    }

    public PermissionUser getUser(String name){
        return PermissionsEx.getUser(name);
    }

    public PermissionManager getPermManager(){
        return PermissionsEx.getPermissionManager();
    }


}

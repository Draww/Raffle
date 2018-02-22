package com.drawwdev.raffle.depend;

import com.drawwdev.raffle.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Depends {

    private Main plugin;
    private HashMap<String, Depend> depends = new HashMap<>();

    public Depends(Main plugin) {
        this.plugin = plugin;
        load();
        run();
    }

    public void load() {
        add(new EconomyDepend(plugin), new PermissionsExDepend(plugin), new PlaceholderDepend(plugin));
    }

    public void run() {
        List<Depend> dependErrorList = new ArrayList<>();
        for (Depend depend : getDepends().values()) {
            if (depend.dependType().equals(DependType.HIGH) && !depend.dependent()) {
                dependErrorList.add(depend);
            }
        }
        if (!dependErrorList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Integer num = 1;
            for (Depend depend : dependErrorList) {
                if (num == dependErrorList.size()) {
                    sb.append(depend.name());
                } else {
                    sb.append(depend.name()).append(", ");
                }
                num++;
            }
            getPlugin().getLogger().severe(sb.toString() + " are not dependent!");
            getPlugin().getServer().getPluginManager().disablePlugin(getPlugin());
            return;
        }
    }

    public void add(Depend depend) {
        if (!getDepends().containsKey(depend.name())) {
            getDepends().put(depend.name(), depend);
        }
    }

    public void add(Depend... depends) {
        for (Depend d : depends) {
            if (!getDepends().containsKey(d.name())){
                getDepends().put(d.name(), d);
            }
        }
    }

    public void remove(Depend depend) {
        if (getDepends().containsKey(depend.name())) {
            getDepends().remove(depend.name());
        }
    }

    public Boolean exist(Depend depend) {
        return getDepends().containsKey(depend.name());
    }

    public Depend get(String name) {
        return getDepends().getOrDefault(name, null);
    }

    public Main getPlugin() {
        return plugin;
    }

    public HashMap<String, Depend> getDepends() {
        return depends;
    }
}

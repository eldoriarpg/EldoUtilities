package de.eldoria.eldoutilities.core;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import org.bukkit.plugin.Plugin;

public class Configuration extends EldoConfig {
    public Configuration(Plugin plugin) {
        super(plugin);
    }

    public boolean isUpdateCheck() {
        return getConfig().getBoolean("updateCheck", true);
    }
}

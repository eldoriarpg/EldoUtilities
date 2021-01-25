package de.eldoria.eldoutilities.core;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Configuration extends EldoConfig {
    public Configuration(Plugin plugin) {
        super(plugin);
    }

    public boolean isUpdateCheck() {
        return getConfig().getBoolean("updateCheck", true);
    }

    public FileConfiguration getUtilConfig() {
        Path plugin = Bukkit.getUpdateFolderFile().toPath().getParent();
        return loadConfig(Paths.get(plugin.toString(), "EldoUtilities", "config.yml"), s -> {
        }, true);
    }
}

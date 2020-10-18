package de.eldoria.eldoutilities.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public abstract class EldoConfig {
    protected final Plugin plugin;

    public EldoConfig(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Saves the config to disk.
     */
    public abstract void save();

    /**
     * Discards any unsaved changes in the config and reloads the config
     */
    public abstract void reload();

    /**
     * Set a value if not set
     *
     * @param path  path in section
     * @param value value to set
     *
     * @return true if the value was not present and was set.
     */
    protected boolean setIfAbsent(String path, Object value) {
        FileConfiguration config = plugin.getConfig();
        if (!config.isSet(path)) {
            config.set(path, value);
            return true;
        }
        return false;
    }

    /**
     * Set a value if not set
     *
     * @param section section
     * @param path    path in section
     * @param value   value to set
     *
     * @return true if the value was not present and was set.
     */
    protected boolean setIfAbsent(ConfigurationSection section, String path, Object value) {
        if (!section.isSet(path)) {
            section.set(path, value);
            return true;
        }
        return false;
    }
}

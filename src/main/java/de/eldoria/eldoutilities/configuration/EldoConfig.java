package de.eldoria.eldoutilities.configuration;

import de.eldoria.eldoutilities.utils.ObjUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * A wrapper class for {@link FileConfiguration}.
 * <p>
 * This class can also be act as a main config which holds references to external configuration files.
 *
 * @since 1.1.0
 */
public abstract class EldoConfig {
    private static final Map<Class<? extends Plugin>, EldoConfig> PLUGIN_MAIN_CONFIGS = new HashMap<>();
    protected final Plugin plugin;
    private final Path pluginData;
    private final Map<String, FileConfiguration> configs = new HashMap<>();
    private FileConfiguration config;

    public EldoConfig(Plugin plugin) {
        this.plugin = plugin;
        pluginData = plugin.getDataFolder().toPath();
        PLUGIN_MAIN_CONFIGS.putIfAbsent(plugin.getClass(), this);
        plugin.saveDefaultConfig();
        if (isMainConfig()) {
            init();
        }
        reload();
    }

    /**
     * Checks if a plugin is in debug state.
     *
     * @param clazz clazz to check
     * @return true if plugin is in debug state.
     */
    public static boolean isDebug(Class<? extends Plugin> clazz) {
        return ObjUtil.nonNull(ObjUtil.nonNull(PLUGIN_MAIN_CONFIGS.get(clazz), i -> {
            return ObjUtil.nonNull(i.config, c -> {
                return c.getBoolean("debug", false);
            });
        }), false);
    }

    /**
     * Saves the config to disk.
     */
    public final void save() {
        saveConfigs();
        writeConfigs();
    }

    /**
     * Write objects to file configs.
     * <p>
     * This message will be called first, when {@link #save()} is called.
     * <p>
     * {@link #writeConfigs()} will be called afterwards.
     */
    protected void saveConfigs() {

    }

    /**
     * Discards any unsaved changes in the config and reloads the config files
     */
    public final void reload() {
        readConfigs();
        reloadConfigs();
    }

    /**
     * Invalidates the cached config objects and reloads.
     * <p>
     * Called after {@link #readConfigs()}}.
     * <p>
     * All configs are already reloaded.
     */
    protected void reloadConfigs() {

    }

    private void readConfigs() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        setIfAbsent("debug", false);
        for (Map.Entry<String, FileConfiguration> entry : configs.entrySet()) {
            loadConfig(Paths.get(entry.getKey()), null, true);
        }
    }

    /**
     * Set a value if not set
     *
     * @param path  path in section
     * @param value value to set
     * @return true if the value was not present and was set.
     */
    protected final boolean setIfAbsent(String path, Object value) {
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
     * @return true if the value was not present and was set.
     */
    protected final boolean setIfAbsent(ConfigurationSection section, String path, Object value) {
        if (!section.isSet(path)) {
            section.set(path, value);
            return true;
        }
        return false;
    }

    /**
     * Load a file from a directory inside the plugin directory.
     * <p>
     * Directory and or file will be created if not exits.
     *
     * @param path           path to the file. the file ending .yml is appended by the function
     * @param defaultCreator Creator of config setting, if the file is not present. If the creator is null and the file
     *                       does not exist null will be returned.
     * @param reload         forces to load the file configuration from disk even if it was already loaded
     * @return file configuration or null if something went wrong.
     * @throws ExternalConfigException When load config is invoked on a eldo config which is not the main config.
     */
    protected final FileConfiguration loadConfig(String path, @Nullable Consumer<FileConfiguration> defaultCreator, boolean reload) {
        Path configPath = Paths.get(pluginData.toString(), path + ".yml");
        return loadConfig(configPath, defaultCreator, reload);
    }

    /**
     * @param configPath     path to the file.
     * @param defaultCreator Creator of config setting, if the file is not present. If the creator is null and the file
     *                       does not exist null will be returned.
     * @param reload         forces to load the file configuration from disk even if it was already loaded
     * @return File configuration which was already loaded, loaded or created.
     * @throws ExternalConfigException When load config is invoked on a eldo config which is not the main config.
     */
    protected final FileConfiguration loadConfig(Path configPath, @Nullable Consumer<FileConfiguration> defaultCreator, boolean reload) {
        validateMainConfigEntry();
        File configFile = configPath.toFile();

        try {
            Files.createDirectories(configPath.getParent());
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "could not create directory " + configPath.getParent().toString(), e);
            return null;
        }

        if (!configFile.exists()) {
            try {
                Files.createFile(configPath);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not create config.", e);
                return null;
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            ObjUtil.nonNull(defaultCreator, d -> {
                d.accept(config);
            });

            try {
                config.save(configFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not save default config.");
                return null;
            }
        }

        if (reload) {
            return configs.compute(configPath.toString(), (k, v) -> YamlConfiguration.loadConfiguration(configFile));
        }

        return configs.computeIfAbsent(configPath.toString(), p -> YamlConfiguration.loadConfiguration(configFile));
    }

    private void writeConfigs() {
        plugin.saveConfig();
        for (Map.Entry<String, FileConfiguration> entry : configs.entrySet()) {
            File file = Paths.get(entry.getKey()).toFile();
            if (!file.exists()) {
                try {
                    Files.createFile(file.toPath());
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Could not create config.", e);
                    return;
                }
            }

            try {
                entry.getValue().save(file);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not save config " + file.getAbsolutePath(), e);
            }
        }
    }

    public final Plugin getPlugin() {
        return plugin;
    }

    /**
     * Get the main config.
     * <p>
     * Also refered as the config.yml
     *
     * @param clazz class of plugin to retrieve the main config
     * @return file configuration for the main config.
     */
    public static EldoConfig getMainConfig(Class<? extends Plugin> clazz) {
        return PLUGIN_MAIN_CONFIGS.get(clazz);
    }

    /**
     * Get the main config.
     * <p>
     * Also refered as the config.yml
     *
     * @return file configuration for the main config.
     */
    public final EldoConfig getMainConfig() {
        return PLUGIN_MAIN_CONFIGS.get(plugin.getClass());
    }

    /**
     * Get the underlying file configuration.
     *
     * @return file configuration for the main config.
     */
    public final FileConfiguration getConfig() {
        return config;
    }

    /**
     * Get the config version.
     *
     * @return config version or -1 if not set.
     */
    public final int getVersion() {
        return config.getInt("version", -1);
    }

    /**
     * Set the config version
     *
     * @param version new config version
     * @param save    true to save after set.
     */
    public final void setVersion(int version, boolean save) {
        config.set("version", version);
        if (save) {
            save();
        }
    }

    /**
     * Called after constructor and before reload.
     * <p>
     * Intialize everything here.
     */
    protected void init() {
    }

    public boolean isMainConfig() {
        return PLUGIN_MAIN_CONFIGS.get(plugin.getClass()) == this;
    }

    private void validateMainConfigEntry() {
        if (!isMainConfig()) {
            throw new ExternalConfigException();
        }
    }
}

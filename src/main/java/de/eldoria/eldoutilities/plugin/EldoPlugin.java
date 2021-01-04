package de.eldoria.eldoutilities.plugin;

import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

public class EldoPlugin extends JavaPlugin {
    private static Map<Class<? extends EldoPlugin>, EldoPlugin> instance;
    private PluginManager pluginManager = null;
    private BukkitScheduler scheduler = null;

    public EldoPlugin() {
        registerSelf(this);
    }

    public EldoPlugin(@NotNull JavaPluginLoader loader, @NotNull PluginDescriptionFile description, @NotNull File dataFolder, @NotNull File file) {
        super(loader, description, dataFolder, file);
        registerSelf(this);
    }

    private static void registerSelf(EldoPlugin eldoPlugin) {
        instance.put(eldoPlugin.getClass(), eldoPlugin);
    }

    private static EldoPlugin getEldoPlugin(Class<? extends EldoPlugin> clazz) {
        return instance.get(clazz);
    }

    public static EldoPlugin getInstance(Class<? extends EldoPlugin> clazz) {
        return instance.get(clazz);
    }

    public static Logger logger(Class<? extends EldoPlugin> plugin) {
        return instance.get(plugin).getLogger();
    }

    /**
     * Register a tabexecutor for a command.
     * <p>
     * This tabexecutor will handle execution and tab completion.
     *
     * @param command     name of command
     * @param tabExecutor command executor
     */
    public void registerCommand(String command, TabExecutor tabExecutor) {
        PluginCommand cmd = getCommand(command);
        if (cmd != null) {
            cmd.setExecutor(tabExecutor);
            return;
        }
        getLogger().warning("Command " + command + " not found!");
    }

    /**
     * Registers listener for the plugin
     *
     * @param listener listener to register
     */
    public void registerListener(Listener... listener) {
        for (Listener l : listener) {
            getPluginManager().registerEvents(l, this);
        }
    }

    /**
     * Schedules a repeating task.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param task   Task to be executed
     * @param period Period in server ticks of the task
     * @return Task id number (-1 if scheduling failed)
     */
    public int scheduleRepeatingTask(Runnable task, int period) {
        return scheduleRepeatingTask(task, 100, period);
    }

    /**
     * Schedules a repeating task.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param task   Task to be executed
     * @param delay  Delay in server ticks before executing first repeat
     * @param period Period in server ticks of the task
     * @return Task id number (-1 if scheduling failed)
     */
    public int scheduleRepeatingTask(Runnable task, int delay, int period) {
        return getScheduler().scheduleSyncRepeatingTask(this, task, delay, period);
    }

    /**
     * Get the servers plugin manager
     *
     * @return plugin manager
     */
    public PluginManager getPluginManager() {
        if (pluginManager == null) {
            pluginManager = getServer().getPluginManager();
        }
        return pluginManager;
    }

    /**
     * Get the servers scheduler
     *
     * @return scheduler instance
     */
    public BukkitScheduler getScheduler() {
        if (scheduler == null) {
            scheduler = getServer().getScheduler();
        }
        return scheduler;
    }
}

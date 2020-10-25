package de.eldoria.eldoutilities.updater;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateChecker;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;
import de.eldoria.eldoutilities.updater.spigotupdater.SpigotUpdateChecker;
import de.eldoria.eldoutilities.updater.spigotupdater.SpigotUpdateData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Optional;

public abstract class Updater<T extends UpdateData> implements Listener {
    private final Plugin plugin;
    private final T data;
    private String latestVersion;
    private boolean notifyActive = false;

    public Updater(T data) {
        this.plugin = data.getPlugin();
        this.data = data;
        boolean newVersion = evaluate(getLatestVersion(data));
        if (data.isAutoUpdate() && newVersion) {
            update();
        }
    }

    public static void Spigot(SpigotUpdateData data) {
        new SpigotUpdateChecker(data);
    }

    public static void Butler(ButlerUpdateData data) {
        new ButlerUpdateChecker(data);
    }

    /**
     * The check method will be called after the constructor is called.
     * <p>
     * This method should be implemented as follows:
     * <p>
     * Retrieve the latest version of the plugin from any update service.
     * <p>
     * return the latest version or a empty optional if the version could not be checked.
     *
     * @param data data for plugin updates
     *
     * @return empty optional if the version could not be checked or the latest version.
     */
    protected abstract Optional<String> getLatestVersion(T data);

    /**
     * Evaluates the result from request.
     *
     * @param optionalLatestVersion optional with latest version
     *
     * @return true if a update is available.
     */
    private boolean evaluate(Optional<String> optionalLatestVersion) {
        if (!optionalLatestVersion.isPresent()) {
            plugin.getLogger().info("Could not check latest version.");
            return false;
        }

        latestVersion = optionalLatestVersion.get();

        if (!plugin.getDescription().getVersion().equalsIgnoreCase(latestVersion)) {
            logUpdateMessage();
            registerListener();
            return true;
        } else {
            plugin.getLogger().info("§2Plugin is up to date.");
            return false;
        }
    }

    /**
     * This version should update the plugin. If not implemented set the {@link UpdateData#isAutoUpdate()} to false.
     */
    protected void update() {
    }

    private void logUpdateMessage() {
        plugin.getLogger().warning("New version of " + plugin.getName() + " available.");
        plugin.getLogger().warning("Newest version: " + latestVersion + "! Current version: " + plugin.getDescription().getVersion() + "!");
        plugin.getLogger().warning("Download new version here: " + plugin.getDescription().getWebsite());
    }

    private void registerListener() {
        if (data.isNotifyUpdate() && !notifyActive) {
            notifyActive = true;
            plugin.getServer().getPluginManager()
                    .registerEvents(new UpdateNotifier(plugin, latestVersion, data.getNotifyPermission()), plugin);
        }
    }

    public T getData() {
        return data;
    }

    private static class UpdateNotifier implements Listener {
        private final Plugin plugin;
        private final String permission;
        private final String newestVersion;

        private UpdateNotifier(Plugin plugin, String permission, String latestVersion) {
            this.plugin = plugin;
            this.permission = permission;
            this.newestVersion = latestVersion;
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            PluginDescriptionFile description = plugin.getDescription();
            // send to operator.
            if (event.getPlayer().isOp()
                    || event.getPlayer().hasPermission(permission)) {
                MessageSender.get(plugin).sendMessage(event.getPlayer(), "New version of §b" + plugin.getName() + "§r available.\n"
                        + "Newest version: §a" + newestVersion + "§r! Current version: §c" + description.getVersion() + "§r!\n"
                        + "Download new version here: §b" + description.getWebsite());
            }
        }
    }
}

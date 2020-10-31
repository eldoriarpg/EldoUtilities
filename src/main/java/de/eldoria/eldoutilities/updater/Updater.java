package de.eldoria.eldoutilities.updater;

import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateChecker;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;
import de.eldoria.eldoutilities.updater.notifier.DownloadedNotifier;
import de.eldoria.eldoutilities.updater.notifier.UpdateNotifier;
import de.eldoria.eldoutilities.updater.spigotupdater.SpigotUpdateChecker;
import de.eldoria.eldoutilities.updater.spigotupdater.SpigotUpdateData;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public abstract class Updater<T extends UpdateData> implements Listener {
    private final Plugin plugin;
    private final T data;
    private String latestVersion;
    private boolean notifyActive = false;

    public Updater(T data) {
        this.plugin = data.getPlugin();
        this.data = data;
        performCheck();
    }

    /**
     *
     */
    public final boolean performCheck() {
        Optional<String> optLatest = getLatestVersion(data);
        boolean updateAvailable;
        if (optLatest.isPresent()) {
            latestVersion = optLatest.get();
            updateAvailable = evaluate(this.latestVersion);
        } else {
            plugin.getLogger().info("Could not check latest version.");
            return false;
        }

        if (updateAvailable) {
            logUpdateMessage();
            if (data.isAutoUpdate()) {
                registerListener(new DownloadedNotifier(plugin, data.getNotifyPermission(), latestVersion, update()));
            } else {
                registerListener(new UpdateNotifier(plugin, data.getNotifyPermission(), latestVersion));
            }
        }
        return updateAvailable;
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
     * @param latestVersion optional with latest version
     *
     * @return true if a update is available.
     */
    private boolean evaluate(String latestVersion) {
        if (!plugin.getDescription().getVersion().equalsIgnoreCase(latestVersion)) {
            return true;
        } else {
            plugin.getLogger().info("§2Plugin is up to date.");
            return false;
        }
    }

    /**
     * This version should update the plugin. If not implemented set the {@link UpdateData#isAutoUpdate()} to false.
     *
     * @return true if the update was succesful.
     */
    protected boolean update() {
        return false;
    }

    private void logUpdateMessage() {
        plugin.getLogger().info("§2New version of §6" + plugin.getName() + "§2 available.");
        plugin.getLogger().info("§2Newest version: §3" + latestVersion + "! Current version: §c" + plugin.getDescription().getVersion() + "§2!");
        if (!data.isAutoUpdate()) {
            plugin.getLogger().info("§2Download new version here: §6" + plugin.getDescription().getWebsite());
        }
    }

    private void registerListener(Listener listener) {
        if (data.isNotifyUpdate() && !notifyActive) {
            notifyActive = true;
            plugin.getServer().getPluginManager()
                    .registerEvents(listener, plugin);
        }
    }

    public T getData() {
        return data;
    }

}

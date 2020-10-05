package de.eldoria.eldoutilities.updater;

import de.eldoria.eldoutilities.messages.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class UpdateChecker implements Listener {
    private final Plugin plugin;
    private final String newestVersion;
    private final String permission;

    private UpdateChecker(Plugin plugin, String newestVersion, String permission) {
        this.plugin = plugin;
        this.newestVersion = newestVersion;
        this.permission = permission;
    }

    /**
     * Perform a update check which will notify the server owner with a console message when a new version is available.
     *
     * @param plugin   plugin instance
     * @param spigotId id of the plugin on spigot
     */
    public static void performAndNotifyUpdateCheck(Plugin plugin, int spigotId) {
        performAndNotifyUpdateCheck(plugin, spigotId, false);
    }

    /**
     * Perform a update check which will notify the server owner with a console message when a new version is available.
     *
     * @param plugin          plugin instance
     * @param spigotId        id of the plugin on spigot
     * @param sendLoginNotify set this to true to notify admins and operator when they join.
     */
    public static void performAndNotifyUpdateCheck(Plugin plugin, int spigotId, boolean sendLoginNotify) {
        if (sendLoginNotify) {
            performAndNotifyUpdateCheck(plugin, spigotId, plugin.getDescription().getName());
        } else {
            performAndNotifyUpdateCheck(plugin, spigotId, null);
        }
    }

    /**
     * Perform a update check which will notify the server owner with a console message when a new version is available.
     *
     * @param plugin          plugin instance
     * @param spigotId        id of the plugin on spigot
     * @param notifyPermission enter the permission which a user should have to get a notification. null to disable login notification.
     */
    public static void performAndNotifyUpdateCheck(Plugin plugin, int spigotId, String notifyPermission) {
        boolean updateAvailable = false;

        HttpURLConnection con;
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + spigotId);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
        } catch (IOException e) {
            return;
        }

        StringBuilder newestVersionRequest = new StringBuilder();
        try (InputStream stream = con.getInputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
            String inputLine = in.readLine();
            while (inputLine != null) {
                newestVersionRequest.append(inputLine);
                inputLine = in.readLine();
            }
        } catch (IOException e) {
            return;
        }

        String newestVersion = newestVersionRequest.toString();

        String currentVersion = plugin.getDescription().getVersion();
        if (!currentVersion.equalsIgnoreCase(newestVersion)) {
            plugin.getLogger().warning("New version of " + plugin.getName() + " available.");
            plugin.getLogger().warning("Newest version: " + newestVersion + "! Current version: " + plugin.getDescription().getVersion() + "!");
            plugin.getLogger().warning("Download new version here: " + plugin.getDescription().getWebsite());

            if (notifyPermission != null) {
                Bukkit.getPluginManager().registerEvents(new UpdateChecker(plugin, newestVersion, notifyPermission), plugin);
            }
        }
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
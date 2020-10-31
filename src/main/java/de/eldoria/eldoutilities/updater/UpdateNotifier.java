package de.eldoria.eldoutilities.updater;

import de.eldoria.eldoutilities.messages.MessageSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

class UpdateNotifier implements Listener {
    protected final Plugin plugin;
    protected final String permission;
    protected final String newestVersion;

    UpdateNotifier(Plugin plugin, String permission, String latestVersion) {
        this.plugin = plugin;
        this.permission = permission;
        this.newestVersion = latestVersion;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PluginDescriptionFile description = plugin.getDescription();
        // send to operator.
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission(permission)) {
            MessageSender.get(plugin).sendMessage(event.getPlayer(), "New version of §b" + plugin.getName() + "§r available.\n"
                    + "Newest version: §a" + newestVersion + "§r! Current version: §c" + description.getVersion() + "§r!\n"
                    + "Download new version here: §b" + description.getWebsite());
        }
    }
}

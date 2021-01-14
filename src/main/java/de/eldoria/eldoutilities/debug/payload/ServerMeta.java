package de.eldoria.eldoutilities.debug.payload;

import de.eldoria.eldoutilities.debug.data.PluginMetaData;
import de.eldoria.eldoutilities.debug.data.ServerMetaData;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.util.Arrays;

public class ServerMeta extends ServerMetaData {

    private ServerMeta(String version, int currentPlayers, int loadedWorlds, PluginMetaData[] plugins) {
        super(version, currentPlayers, loadedWorlds, plugins);
    }

    public static ServerMetaData create() {
        Server server = Bukkit.getServer();
        String version = server.getVersion();
        int currentPlayers = server.getOnlinePlayers().size();
        int loadedWorlds = server.getWorlds().size();
        PluginMeta[] plugins = Arrays.stream(server.getPluginManager().getPlugins())
                .map(PluginMeta::create)
                .toArray(PluginMeta[]::new);

        return new ServerMeta(version, currentPlayers, loadedWorlds, plugins);
    }
}

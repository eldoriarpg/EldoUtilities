package de.eldoria.eldoutilities.debug.data;

public class ServerMetaData {
    protected String version;
    protected int currentPlayers;
    protected int loadedWorlds;
    protected PluginMetaData[] plugins;

    protected ServerMetaData(String version, int currentPlayers, int loadedWorlds, PluginMetaData[] plugins) {
        this.version = version;
        this.currentPlayers = currentPlayers;
        this.loadedWorlds = loadedWorlds;
        this.plugins = plugins;
    }
}

package de.eldoria.eldoutilities.debug;

import de.eldoria.eldoutilities.debug.data.*;
import de.eldoria.eldoutilities.debug.payload.ConfigDump;
import de.eldoria.eldoutilities.debug.payload.LogMeta;
import de.eldoria.eldoutilities.debug.payload.PluginMeta;
import de.eldoria.eldoutilities.debug.payload.ServerMeta;
import org.bukkit.plugin.Plugin;

public final class DebugPayload extends DebugPayloadData {

    private DebugPayload(PluginMetaData pluginMeta, ServerMetaData serverMeta, EntryData[] additionalPluginMeta,
                         LogData latestLog, EntryData[] configDumps) {
        super(pluginMeta, serverMeta, additionalPluginMeta, latestLog, configDumps);
    }

    /**
     * Create a new debug payload.
     *
     * @param plugin plugin to create debug data for
     * @return debug payload data
     */
    public static DebugPayloadData create(Plugin plugin, DebugSettings settings) {
        PluginMetaData pluginMeta = PluginMeta.create(plugin);
        ServerMetaData serverMeta = ServerMeta.create();
        EntryData[] additionalPluginMeta = DebugUtil.getAdditionalPluginMeta(plugin);
        LogData latestLog = LogMeta.create(plugin, settings);
        EntryData[] configDumps = ConfigDump.create(plugin, settings);
        return new DebugPayload(pluginMeta, serverMeta, additionalPluginMeta, latestLog, configDumps);
    }
}

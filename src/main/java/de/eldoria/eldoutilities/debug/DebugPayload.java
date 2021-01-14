package de.eldoria.eldoutilities.debug;

import de.eldoria.eldoutilities.debug.data.ConfigDumpData;
import de.eldoria.eldoutilities.debug.data.DebugPayloadData;
import de.eldoria.eldoutilities.debug.data.PluginMetaData;
import de.eldoria.eldoutilities.debug.data.ServerMetaData;
import de.eldoria.eldoutilities.debug.payload.ConfigDump;
import de.eldoria.eldoutilities.debug.payload.PluginMeta;
import de.eldoria.eldoutilities.debug.payload.ServerMeta;
import org.bukkit.plugin.Plugin;

public final class DebugPayload extends DebugPayloadData {

    private DebugPayload(PluginMetaData pluginMeta, ServerMetaData serverMeta, String additionalPluginMeta,
                         String latestLog, ConfigDumpData[] configDumps) {
        super(pluginMeta, serverMeta, additionalPluginMeta, latestLog, configDumps);
    }

    public static DebugPayloadData create(Plugin plugin) {
        PluginMetaData pluginMeta = PluginMeta.create(plugin);
        ServerMetaData serverMeta = ServerMeta.create();
        String additionalPluginMeta = DebugUtil.getAdditionalPluginMeta(plugin);
        String latestLog = DebugUtil.getLatestLog(plugin);
        ConfigDumpData[] configDumps = ConfigDump.create(plugin);
        return new DebugPayload(pluginMeta, serverMeta, additionalPluginMeta, latestLog, configDumps);
    }

}

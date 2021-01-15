package de.eldoria.eldoutilities.debug.data;

public class DebugPayloadData {
    private final int v = 1;
    protected PluginMetaData pluginMeta;
    protected ServerMetaData serverMeta;
    protected EntryData[] additionalPluginMeta;
    protected String latestLog;
    protected EntryData[] configDumps;

    public DebugPayloadData(PluginMetaData pluginMeta, ServerMetaData serverMeta, EntryData[] additionalPluginMeta, String latestLog, EntryData[] configDumps) {
        this.pluginMeta = pluginMeta;
        this.serverMeta = serverMeta;
        this.additionalPluginMeta = additionalPluginMeta;
        this.latestLog = latestLog;
        this.configDumps = configDumps;
    }
}

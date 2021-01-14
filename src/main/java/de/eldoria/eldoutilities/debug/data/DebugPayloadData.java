package de.eldoria.eldoutilities.debug.data;

public class DebugPayloadData {
    private final int v = 1;
    protected PluginMetaData pluginMeta;
    protected ServerMetaData serverMeta;
    protected String additionalPluginMeta;
    protected String latestLog;
    protected ConfigDumpData[] configDumps;

    public DebugPayloadData(PluginMetaData pluginMeta, ServerMetaData serverMeta, String additionalPluginMeta, String latestLog, ConfigDumpData[] configDumps) {
        this.pluginMeta = pluginMeta;
        this.serverMeta = serverMeta;
        this.additionalPluginMeta = additionalPluginMeta;
        this.latestLog = latestLog;
        this.configDumps = configDumps;
    }
}

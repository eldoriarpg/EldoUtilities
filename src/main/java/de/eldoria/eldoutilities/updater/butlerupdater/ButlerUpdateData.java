package de.eldoria.eldoutilities.updater.butlerupdater;

import de.eldoria.eldoutilities.updater.UpdateData;
import org.bukkit.plugin.Plugin;

public class ButlerUpdateData extends UpdateData {
    private final int butlerId;
    private final String host;

    /**
     * Creates a new Update data.
     */
    public ButlerUpdateData(Plugin plugin, String notifyPermission, boolean notifyUpdate, boolean autoUpdate, int butlerId, String host) {
        super(plugin, notifyPermission, notifyUpdate, autoUpdate);
        this.butlerId = butlerId;
        this.host = host;
    }

    public int getButlerId() {
        return butlerId;
    }

    public String getHost() {
        return host;
    }
}

package de.eldoria.eldoutilities.debug;


import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;

public class DebugSettings {
    public static final DebugSettings DEFAULT = new DebugSettings(ButlerUpdateData.HOST);
    private final String host;

    public DebugSettings(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }
}

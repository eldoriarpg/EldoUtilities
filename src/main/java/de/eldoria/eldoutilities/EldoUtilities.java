package de.eldoria.eldoutilities;

import de.eldoria.eldoutilities.plugin.EldoPlugin;

import java.util.logging.Logger;

public final class EldoUtilities extends EldoPlugin {
    @Override
    public void onEnable() {

    }

    public static Logger logger() {
        return getInstance(EldoUtilities.class).getLogger();
    }
}

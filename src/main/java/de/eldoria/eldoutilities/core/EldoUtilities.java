package de.eldoria.eldoutilities.core;

import de.eldoria.eldoutilities.inventory.InventoryActionHandler;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.scheduling.DelayedActions;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;
import org.bstats.bukkit.Metrics;

import java.util.logging.Logger;

public final class EldoUtilities extends EldoPlugin {
    private static DelayedActions delayedActions;
    private static InventoryActionHandler inventoryActionHandler;
    private Configuration configuration;


    public static DelayedActions getDelayedActions() {
        return delayedActions;
    }

    public static InventoryActionHandler getInventoryActions() {
        return inventoryActionHandler;
    }

    public static Logger logger() {
        return getInstance(EldoUtilities.class).getLogger();
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        configuration = new Configuration(this);
        delayedActions = DelayedActions.start(this);
        inventoryActionHandler = new InventoryActionHandler();
        registerListener(inventoryActionHandler);
        Updater.Butler(new ButlerUpdateData(this, "eldoutilities", configuration.isUpdateCheck(),
                false, 9, ButlerUpdateData.HOST)).start();
        Metrics metrics = new Metrics(this, 9958);
        if(metrics.isEnabled()){
            getLogger().info("§2Metrics enabled. Thank you <3");
        }
    }
}

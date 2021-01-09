package de.eldoria.eldoutilities;

import de.eldoria.eldoutilities.inventory.InventoryActionHandler;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.scheduling.DelayedActions;

import java.util.logging.Logger;

public final class EldoUtilities extends EldoPlugin {
    private static DelayedActions delayedActions;
    private static InventoryActionHandler inventoryActionHandler;


    public static DelayedActions getDelayedActions() {
        return delayedActions;
    }

    public static InventoryActionHandler getInventoryActions(){
        return inventoryActionHandler;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        delayedActions = DelayedActions.start(this);
        inventoryActionHandler = new InventoryActionHandler();
        registerListener(inventoryActionHandler);
    }

    public static Logger logger() {
        return getInstance(EldoUtilities.class).getLogger();
    }
}

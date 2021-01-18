package de.eldoria.eldoutilities.core;

import de.eldoria.eldoutilities.conversation.ConversationRequester;
import de.eldoria.eldoutilities.core.commands.EldoDebug;
import de.eldoria.eldoutilities.inventory.InventoryActionHandler;
import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.scheduling.AsyncSyncingCallbackExecutor;
import de.eldoria.eldoutilities.scheduling.DelayedActions;
import de.eldoria.eldoutilities.serialization.MapEntry;
import de.eldoria.eldoutilities.updater.Updater;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public final class EldoUtilities extends EldoPlugin {
    private static DelayedActions delayedActions = null;
    private static InventoryActionHandler inventoryActionHandler = null;
    private static AsyncSyncingCallbackExecutor asyncSyncingCallbackExecutor = null;
    private static ConversationRequester conversationRequester = null;
    private Configuration configuration;

    public static DelayedActions getDelayedActions() {
        if (delayedActions == null) {
            delayedActions = DelayedActions.start(getInstance(EldoUtilities.class));
            logger().config("DelayedActions ignited.");
        }
        return delayedActions;
    }

    public static ConversationRequester getConversationRequester() {
        if (conversationRequester == null) {
            conversationRequester = ConversationRequester.start(getInstance(EldoUtilities.class));
            logger().config("ConversationRequester ignited.");
        }
        return conversationRequester;
    }

    public static InventoryActionHandler getInventoryActions() {
        if (inventoryActionHandler == null) {
            inventoryActionHandler = InventoryActionHandler.create(getInstance(EldoUtilities.class));
            logger().config("InventoryActionHandler ignited.");
        }
        return inventoryActionHandler;
    }

    public static AsyncSyncingCallbackExecutor getAsyncSyncingCallbackExecutor() {
        if (asyncSyncingCallbackExecutor == null) {
            asyncSyncingCallbackExecutor = AsyncSyncingCallbackExecutor.create(getInstance(EldoUtilities.class));
            logger().config("AsyncSyncingCallbackExecutor ignited.");
        }
        return asyncSyncingCallbackExecutor;
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
        MessageSender.create(this, "§6[EU]");
        Updater.Butler(new ButlerUpdateData(this, "eldoutilities", configuration.isUpdateCheck(),
                false, 9, ButlerUpdateData.HOST)).start();
        Metrics metrics = new Metrics(this, 9958);
        if (metrics.isEnabled()) {
            getLogger().info("§2Metrics enabled. Thank you <3");
        }

        registerCommand("eldoDebug", new EldoDebug(this));
        getLogger().info("EldoUtilities armed and ready.");
        getScheduler().runTaskLater(this, this::performLateCleanUp, 5);
    }

    private void performLateCleanUp() {
        Iterator<KeyedBossBar> bossBars = Bukkit.getBossBars();
        while (bossBars.hasNext()) {
            KeyedBossBar bar = bossBars.next();
            NamespacedKey key = bar.getKey();
            if (!key.getNamespace().equalsIgnoreCase(getName())) continue;
            if (key.getKey().startsWith(MessageChannel.KEY_PREFIX)) {
                logger().config("Removed boss bar with key" + key.toString());
                bar.removeAll();
                Bukkit.removeBossBar(key);
            }
        }
    }

    @Override
    public void onDisable() {
        if (delayedActions != null) {
            delayedActions.shutdown();
        }
        if (asyncSyncingCallbackExecutor != null) {
            asyncSyncingCallbackExecutor.shutdown();
        }
    }

    private static void registerPublicServices(EldoUtilities plugin) {
        delayedActions = DelayedActions.start(plugin);
        inventoryActionHandler = new InventoryActionHandler();
        asyncSyncingCallbackExecutor = AsyncSyncingCallbackExecutor.create(plugin);
    }

    @Override
    public List<Class<? extends ConfigurationSerializable>> getConfigSerialization() {
        return Arrays.asList(MapEntry.class);
    }
}

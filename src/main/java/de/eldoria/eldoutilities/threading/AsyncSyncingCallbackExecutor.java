package de.eldoria.eldoutilities.threading;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Scheduler Service which allows to execute a async call and handle the retrieved data in the main thread.
 * Preserves the main thread from overloading
 */
public final class AsyncSyncingCallbackExecutor extends BukkitRunnable {

    private static final int MAX_DURATION_TARGET = 50; // assuming 50ms = 1 tick
    private final Plugin plugin;
    private final BukkitScheduler scheduler;
    private final Queue<Callback<?>> callbacks = new ArrayDeque<>();

    /**
     * Returns a new running executor instance.
     *
     * @param plugin plugin of executor
     * @return running executor instance
     */
    public static AsyncSyncingCallbackExecutor create(Plugin plugin) {
        AsyncSyncingCallbackExecutor executor = new AsyncSyncingCallbackExecutor(plugin);
        executor.runTaskTimer(plugin, 0, 1);
        return executor;
    }

    private AsyncSyncingCallbackExecutor(Plugin plugin) {
        this.plugin = plugin;
        scheduler = Bukkit.getScheduler();
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        long duration = 0;

        while (!callbacks.isEmpty() && duration < MAX_DURATION_TARGET) {
            callbacks.poll().invoke();
            duration = System.currentTimeMillis() - start;
        }
    }

    /**
     * Schedules a new task for execution.
     *
     * @param asyncProvider Supplier which is executed async and provides data for the consumer
     * @param syncAction    Consumer which consumes the data in main thread provided by the supplier
     * @param <T>           type of data
     */
    public <T> void schedule(Supplier<T> asyncProvider, Consumer<T> syncAction) {
        if (isCancelled()) return;
        scheduler.runTaskAsynchronously(plugin, () -> callbacks.add(new Callback<>(asyncProvider.get(), syncAction)));
    }

    /**
     * Shutdown the callbacks.
     */
    public void shutdown() {
        cancel();
        for (Callback<?> callback : callbacks) {
            callback.invoke();
        }
        callbacks.clear();
    }

    private static class Callback<T> {
        private final T data;
        private final Consumer<T> consumer;

        public Callback(T data, Consumer<T> consumer) {
            this.data = data;
            this.consumer = consumer;
        }

        private void invoke() {
            consumer.accept(data);
        }
    }
}

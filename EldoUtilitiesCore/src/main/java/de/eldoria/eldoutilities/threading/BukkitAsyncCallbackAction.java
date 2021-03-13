package de.eldoria.eldoutilities.threading;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class BukkitAsyncCallbackAction<T> {
    private final Plugin plugin;
    private final Supplier<T> supplier;
    private Consumer<T> consumer = t -> {
    };
    private Consumer<Throwable> consumerError = Throwable::printStackTrace;
    private final Consumer<Throwable> supplierError;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    private BukkitAsyncCallbackAction(Plugin plugin, Supplier<T> supplier, Consumer<Throwable> supplierError) {
        this.supplier = supplier;
        this.plugin = plugin;
        this.supplierError = supplierError;
    }

    /**
     * Create a new bukkit action
     *
     * @param plugin plugin of action
     * @param call   call
     * @param <T>    type of call
     * @return new Bukkit action
     */
    public static <T> BukkitAsyncCallbackAction<T> call(Plugin plugin, Supplier<T> call) {
        return new BukkitAsyncCallbackAction<>(plugin, call, Throwable::printStackTrace);
    }

    /**
     * Create a new bukkit action
     *
     * @param plugin        plugin of action
     * @param call          call
     * @param supplierError error handler for supplier error
     * @param <T>           type of call
     * @return new Bukkit action
     */
    public static <T> BukkitAsyncCallbackAction<T> call(Plugin plugin, Supplier<T> call, Consumer<Throwable> supplierError) {
        return new BukkitAsyncCallbackAction<>(plugin, call, supplierError);
    }

    /**
     * Adds a consumer which accepts the async retrieved supplier result
     *
     * @param consumer consumer
     * @return current instance with added consumer
     */
    public BukkitAsyncCallbackAction<T> accept(Consumer<T> consumer) {
        this.consumer = consumer;
        return this;
    }

    /**
     * Adds a consumer which accepts the async retrieved supplier result
     *
     * @param consumer consumer for retrieved result
     * @param error    error handler for consumer
     * @return current instance with added consumers
     */
    public BukkitAsyncCallbackAction<T> accept(Consumer<T> consumer, Consumer<Throwable> error) {
        this.consumer = consumer;
        this.consumerError = error;
        return this;
    }

    /**
     * Queue the action for async execution
     */
    public void queue() {
        executeAsync(supplier, consumer);
    }

    /**
     * Completes the action synced and returns the result
     *
     * @return result of supplier
     */
    public T complete() {
        return supplier.get();
    }

    /**
     * Completes the action synced and passes the result to the consumer
     */
    public void submit() {
        consumer.accept(supplier.get());
    }

    /**
     * Queue the action async
     * @param consumer synced consumer which accepts the results
     * @param consumerError error handler
     */
    public void queue(Consumer<T> consumer, Consumer<Throwable> consumerError) {
        executeAsync(supplier, consumer, supplierError, consumerError);
    }

    /**
     * Queue the action async
     * @param consumer synced consumer which accepts the results
     */
    public void queue(Consumer<T> consumer) {
        executeAsync(supplier, consumer);
    }

    private void executeAsync(Supplier<T> supplier, Consumer<T> consumer) {
        executeAsync(supplier, consumer, supplierError, consumerError);
    }

    private void executeAsync(Supplier<T> supplier, Consumer<T> consumer,
                              Consumer<Throwable> supplierError, Consumer<Throwable> consumerError) {
        scheduler.runTaskAsynchronously(plugin, () -> {
            T result;
            try {
                result = supplier.get();
            } catch (Throwable e) {
                supplierError.accept(e);
                return;
            }
            scheduler.runTask(plugin, () -> {
                try {
                    consumer.accept(result);
                } catch (Throwable e) {
                    consumerError.accept(e);
                }
            });
        });
    }
}

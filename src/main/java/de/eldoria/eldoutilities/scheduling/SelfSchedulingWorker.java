package de.eldoria.eldoutilities.scheduling;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.Collection;

/**
 * A self scheduling worker which will schedule itself when getting tasks.
 *
 * Will unschedule itself if no tasks are left for some time.
 *
 * @param <V> type of collection
 * @param <T> type of collection implementation
 *
 * @since 1.4.0
 */
public abstract class SelfSchedulingWorker<V, T extends Collection<V>> extends BukkitRunnable {
    private final Plugin plugin;
    private final T tasks;
    private boolean running = false;
    private int idleTicks = 0;
    private int maxIdleTicks = 200;
    private boolean active = true;

    public SelfSchedulingWorker(Plugin plugin, int maxIdleTicks) {
        this(plugin);
        this.maxIdleTicks = maxIdleTicks;
    }

    public SelfSchedulingWorker(Plugin plugin) {
        this.plugin = plugin;
        tasks = getQueueImplementation();
    }

    /**
     * handle one object which was polled from the queue
     *
     * @param object object from queue
     */
    protected abstract void execute(V object);

    /**
     * Tick is executed once per tick.
     */
    protected void tick() {
    }

    @Override
    public final void run() {
        if (!tasks.isEmpty()) {
            tick();
            for (V task : tasks) {
                execute(task);
            }
        } else {
            idleTicks++;
            if (idleTicks >= maxIdleTicks) {
                cancel();
                plugin.getLogger().fine(getClass().getSimpleName() + " of " + plugin.getName() + " paused. No tasks left.");
                running = false;
            }
        }
    }

    protected final void register(V object) {
        if (!active) return;
        tasks.add(object);
        if (!running) {
            runTaskTimer(plugin, 0, 1);
            running = true;
            idleTicks = 0;
            plugin.getLogger().fine(getClass().getSimpleName() + " of " + plugin.getName() + " started. Processing tasks.");
        }
    }

    public final void unregister(V object) {
        tasks.remove(object);
    }

    protected abstract T getQueueImplementation();

    public final void shutdown() {
        cancel();
        active = false;
        for (V task : tasks) {
            execute(task);
        }
        tasks.clear();
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isActive() {
        return active;
    }
}

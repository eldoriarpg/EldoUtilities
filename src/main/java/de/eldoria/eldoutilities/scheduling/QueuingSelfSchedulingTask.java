package de.eldoria.eldoutilities.scheduling;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.Queue;

public abstract class QueuingSelfSchedulingTask<T> extends BukkitRunnable {
    private static final int MAX_DURATION_TARGET = 50; // assuming 50ms = 1 tick
    private final Plugin plugin;
    private final Queue<T> tasks;
    private boolean running = false;
    private int idleTicks = 0;
    private int maxIdleTicks = 200;
    private boolean active = true;

    public QueuingSelfSchedulingTask(Plugin plugin, int maxIdleTicks) {
        this(plugin);
        this.maxIdleTicks = maxIdleTicks;
    }

    public QueuingSelfSchedulingTask(Plugin plugin) {
        this.plugin = plugin;
        tasks = getQueueImplementation();
    }

    /**
     * handle one object which was polled from the queue
     *
     * @param object object from queue
     */
    public abstract void execute(T object);

    /**
     * Tick is executed once per tick.
     */
    public void tick() {
    }

    @Override
    public final void run() {
        tick();
        long start = System.currentTimeMillis();
        long duration = 0;

        while (!tasks.isEmpty() && proceed(tasks.peek()) && duration < MAX_DURATION_TARGET) {
            execute(tasks.poll());
            duration = System.currentTimeMillis() - start;
        }

        if (tasks.isEmpty()) {
            idleTicks++;
            if (idleTicks >= maxIdleTicks) {
                cancel();
                plugin.getLogger().fine(getClass().getSimpleName() + " of " + plugin.getName() + " paused. No tasks left.");
                running = false;
            }
        }
    }

    /**
     * Define if this object should be polled from queue or if the scheduler should proceed to the next tick.
     *
     * @param object object to check
     * @return true if the object should be handeld. false if the task should wait.
     */
    protected boolean proceed(T object) {
        return true;
    }

    protected final void schedule(T object) {
        if (!active) return;
        tasks.add(object);
        if (!running) {
            runTaskTimer(plugin, 0, 1);
            running = true;
            idleTicks = 0;
            plugin.getLogger().fine(getClass().getSimpleName() + " of " + plugin.getName() + " started. Processing tasks.");
        }
    }

    protected Queue<T> getQueueImplementation(){
        return new ArrayDeque<>();
    };

    public final void shutdown() {
        cancel();
        active = false;
        for (T task : tasks) {
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

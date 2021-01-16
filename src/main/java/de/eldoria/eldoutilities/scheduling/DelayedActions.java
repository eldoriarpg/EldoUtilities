package de.eldoria.eldoutilities.scheduling;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Scheduler service to schedule actions with only one scheduler and preserving the main thread from overloading.
 *
 * @since 1.2.3
 */
public class DelayedActions extends BukkitRunnable {
    private static final int MAX_DURATION_TARGET = 50; // assuming 50ms = 1 tick
    private final Queue<DelayedTask> delayedTasks = new PriorityQueue<>();
    private int currentTick = 0;

    /**
     * Start a delayed action scheduler for a plugin.
     * <p>
     * This scheduler allows to schedule multiple task without starting a new scheduler for each task.
     * <p>
     * The scheduler will also ensure that the main thread is not abused by overloading operations per tick.
     *
     * @param plugin plugin which owns the instance
     * @return new delayed action instance
     */
    public static DelayedActions start(Plugin plugin) {
        DelayedActions delayedActions = new DelayedActions();
        delayedActions.runTaskTimer(plugin, 0, 1);
        return delayedActions;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        long duration = 0;

        while (!delayedTasks.isEmpty() && delayedTasks.peek().tick <= currentTick && duration < MAX_DURATION_TARGET) {
            delayedTasks.poll().invoke();
            duration = System.currentTimeMillis() - start;
        }
        currentTick++;
    }

    /**
     * Delays an action by a specific amount of ticks
     *
     * @param runnable runnable to execute
     * @param delay    delay for execution.
     */
    public void schedule(Runnable runnable, int delay) {
        if(isCancelled()) return;
        delayedTasks.add(new DelayedTask(runnable, delay + currentTick));
    }

    public void shutdown() {
        cancel();
        for (DelayedTask delayedTask : delayedTasks) {
            delayedTask.invoke();
        }
        delayedTasks.clear();
    }

    private static class DelayedTask implements Comparable<DelayedTask> {
        private final Runnable runnable;
        private final int tick;

        public DelayedTask(Runnable runnable, int tick) {
            this.runnable = runnable;
            this.tick = tick;
        }

        @Override
        public int compareTo(@NotNull DelayedActions.DelayedTask o) {
            return Integer.compare(tick, o.tick);
        }

        public void invoke() {
            runnable.run();
        }
    }
}

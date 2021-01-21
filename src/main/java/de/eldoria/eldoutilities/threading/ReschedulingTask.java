package de.eldoria.eldoutilities.threading;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class ReschedulingTask {
    private BukkitRunnable task = null;
    private final Plugin plugin;
    private boolean active = true;

    public ReschedulingTask(Plugin plugin) {
        this.plugin = plugin;
    }

    public void schedule() {
        if (task != null) {
            task = new InternalTask(getClass(), this::run);
            task.runTaskTimer(plugin, 0, 1);
            plugin.getLogger().fine(getClass().getSimpleName() + " of " + plugin.getName() + " started.");
        }
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
            plugin.getLogger().fine(getClass().getSimpleName() + " of " + plugin.getName() + " paused.");
        }
    }

    public void shutdown() {
        active = false;
    }

    public boolean isRunning() {
        return task != null;
    }

    public boolean isActive() {
        return active;
    }

    public abstract void run();

    public Plugin getPlugin() {
        return plugin;
    }

    private static class InternalTask extends BukkitRunnable {
        private final Class<? extends ReschedulingTask> parent;
        private final Runnable runnable;

        public InternalTask(Class<? extends ReschedulingTask> parent, Runnable runnable) {
            this.parent = parent;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            Thread.currentThread().setName("EldoUtils - " + parent.getSimpleName() + " Task.");
            runnable.run();
        }
    }
}

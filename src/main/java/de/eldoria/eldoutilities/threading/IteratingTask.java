package de.eldoria.eldoutilities.threading;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class IteratingTask<T> extends BukkitRunnable {
    private static final int MAX_DURATION_TARGET = 50; // assuming 50ms = 1 tick

    private final Iterator<T> iterator;
    private final Predicate<T> processor;
    private final Consumer<TaskStatistics> statisticsConsumer;
    private final TaskStatistics statistics;

    public IteratingTask(Iterable<T> iterable, Predicate<T> processor, Consumer<TaskStatistics> statisticsConsumer) {
        this.iterator = iterable.iterator();
        this.processor = processor;
        this.statisticsConsumer = statisticsConsumer;
        this.statistics = new TaskStatistics();
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        long duration;
        do {
            T next;
            if (!iterator.hasNext()) {
                this.statistics.addTime(System.currentTimeMillis() - start);
                cancel();
                this.statisticsConsumer.accept(this.statistics);
                return;
            }
            next = iterator.next();
            if (this.processor.test(next)) {
                this.statistics.processElement();
            }
        } while ((duration = System.currentTimeMillis() - start) < MAX_DURATION_TARGET);
        this.statistics.addTime(duration);
    }
}
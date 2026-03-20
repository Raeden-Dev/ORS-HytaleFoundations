package com.raeden.hytale.utils;

import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.lang.LangKey;

import java.util.Map;
import java.util.concurrent.*;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.HytaleFoundations.myLogger;

public class SchedulerUtils {
    private final HytaleFoundations hytaleFoundations;
    private final ScheduledExecutorService scheduler;
    private final Map<String, ScheduledFuture<?>> activeSchedulers;
    private final boolean debugMode;

    public SchedulerUtils(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        this.scheduler = Executors.newScheduledThreadPool(2);
        activeSchedulers = new ConcurrentHashMap<>();
        debugMode = hytaleFoundations.getConfigManager().getDefaultConfig().isDebugMode();
    }

    // Run a task off main thread
    public Future<?> runTaskAsync(Runnable task) {
        return scheduler.submit(task);
    }

    // Run a task after a delay
    public ScheduledFuture<?> runTaskLater(String taskName, Runnable task, long delay, TimeUnit unit) {
        shutdownScheduler(taskName);

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            try {
                task.run();
            } finally {
                activeSchedulers.remove(taskName);
            }
        }, delay, unit);
        activeSchedulers.put(taskName, future);
        if (debugMode) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.CREATE_SUCCESS,"runTaskLater: " + taskName).getAnsiMessage());
        return future;
    }

    // Run a repeating Task
    public ScheduledFuture<?> runTaskTimer(String taskName, Runnable task, long initialDelay, long period, TimeUnit unit) {
        shutdownScheduler(taskName);
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
        activeSchedulers.put(taskName, future);
        if (debugMode) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.CREATE_SUCCESS,"runTaskTimer: " + taskName).getAnsiMessage());
        return future;
    }

    public void shutdown() {
        for (String taskName : activeSchedulers.keySet()) {
            shutdownScheduler(taskName);
        }

        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    public void shutdownScheduler(String scheduleName) {
        if(!activeSchedulers.containsKey(scheduleName)) return;
        ScheduledFuture<?> scheduledTask = activeSchedulers.remove(scheduleName);

        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            if(debugMode) {
                myLogger.atInfo().log(LM.getConsoleMessage(LangKey.STOP_SUCCESS,"active scheduler " + scheduleName).getAnsiMessage());
            }
        }
    }

    public Map<String, ScheduledFuture<?>> getActiveSchedulers() {
        return activeSchedulers;
    }
}

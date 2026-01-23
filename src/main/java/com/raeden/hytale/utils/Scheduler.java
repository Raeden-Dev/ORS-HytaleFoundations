package com.raeden.hytale.utils;

import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.lang.LangKey;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.*;

import static com.raeden.hytale.HytaleEssentials.langManager;
import static com.raeden.hytale.HytaleEssentials.myLogger;

public class Scheduler {
    private final HytaleEssentials hytaleEssentials;
    private final ScheduledExecutorService scheduler;
    private final LinkedHashMap<String, ScheduledFuture<?>> activeSchedulers;
    private boolean debugMode;

    public Scheduler(HytaleEssentials hytaleEssentials) {
        this.hytaleEssentials = hytaleEssentials;
        this.scheduler = Executors.newScheduledThreadPool(2);
        activeSchedulers = new LinkedHashMap<>();
        debugMode = hytaleEssentials.getConfigManager().getDefaultConfig().isToggleDebug();
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
        if (debugMode) myLogger.atInfo().log(langManager.getMessage(LangKey.CREATE_SUCCESS, "runTaskLater: " + taskName).getAnsiMessage());
        return future;
    }

    // Run a repeating Task
    public ScheduledFuture<?> runTaskTimer(String taskName, Runnable task, long initialDelay, long period, TimeUnit unit) {
        shutdownScheduler(taskName);
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
        activeSchedulers.put(taskName, future);
        if (debugMode) myLogger.atInfo().log(langManager.getMessage(LangKey.CREATE_SUCCESS, "runTaskTimer: " + taskName).getAnsiMessage());
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
                myLogger.atInfo().log(langManager.getMessage(LangKey.STOP_SUCCESS, "active scheduler " + scheduleName).getAnsiMessage());
            }
        }
    }

    public LinkedHashMap<String, ScheduledFuture<?>> getActiveSchedulers() {
        return activeSchedulers;
    }
}

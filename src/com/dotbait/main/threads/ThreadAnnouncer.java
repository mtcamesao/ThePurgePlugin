package com.dotbait.main.threads;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ThreadAnnouncer extends ProgramThread {

    private Plugin plugin;

    public ThreadAnnouncer(Plugin plugin) {
        super(false);
        this.plugin = plugin;
    }

    @Override
    public void addAction(Runnable action, int seconds) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, action, (seconds * 20));
    }

    @Override
    public void stop() {}

    @Override
    public void run() {}
}
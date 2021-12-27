package com.dotbait.main.purgePlaceholder;


import com.dotbait.main.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class PurgePlaceholder extends PlaceholderExpansion {

    private Main plugin;

    BukkitTask colorChange;
    public String countDownTime;
    public boolean colorToggle = false;

    public PurgePlaceholder(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "purge";
    }

    @Override
    public @NotNull
    String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onRequest(@Nullable OfflinePlayer player, @NotNull String identifier) {

        if(player == null){
            return "";
        }

        if(identifier.equals("days")){
            if(!Main.getInstance().purgeEnabled) {
                long timeToPurgeInMilli = Main.getInstance().purgeTime.getTimeInMillis() - System.currentTimeMillis();

                int days = (int) ((timeToPurgeInMilli / (1000*60*60*24)) % 7);

                String countDownDays = String.format("&f%02d &6Day(s)", days);

//                String CountDown = String.format("%02d Day(s) %02d Hrs %02d Min", TimeUnit.MILLISECONDS.toDays(timeToPurgeInMilli),
//                        (TimeUnit.MILLISECONDS.toHours(timeToPurgeInMilli) - (TimeUnit.MILLISECONDS.toDays(timeToPurgeInMilli) * 24)),
//                        Math.max(0, TimeUnit.MILLISECONDS.toMinutes(timeToPurgeInMilli) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToPurgeInMilli))));
                return countDownDays;
            }else{
                long timeToPurgeInMilli = Main.getInstance().purgeTimeEnd.getTimeInMillis() - System.currentTimeMillis();
                int days = (int) ((timeToPurgeInMilli / (1000*60*60*24)) % 7);

//                String CountDown = String.format("%02d Day(s) %02d Hrs %02d Min", TimeUnit.MILLISECONDS.toDays(timeToPurgeInMilli),
//                        (TimeUnit.MILLISECONDS.toHours(timeToPurgeInMilli) - (TimeUnit.MILLISECONDS.toDays(timeToPurgeInMilli) * 24)),
//                        Math.max(0, TimeUnit.MILLISECONDS.toMinutes(timeToPurgeInMilli) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToPurgeInMilli))));
                String countDownDays = String.format("&f%02d &6Day(s)", days);
                return countDownDays;
            }
        }

        if(identifier.equals("time")){
            if(!Main.getInstance().purgeEnabled) {
                long timeToPurgeInMilli = Main.getInstance().purgeTime.getTimeInMillis() - System.currentTimeMillis();

                int seconds = (int) (timeToPurgeInMilli / 1000) % 60 ;
                int minutes = (int) ((timeToPurgeInMilli / (1000*60)) % 60);
                int hours   = (int) ((timeToPurgeInMilli / (1000*60*60)) % 24);




                if(Main.instance.purgeCommencing){
                    if(colorChange == null) {
                        colorChange = new BukkitRunnable() {
                            public void run() {
                                if (colorToggle) {
                                    countDownTime = String.format("&f%02d &6Hrs &f%02d &6Min &f%02d &6Secs", hours, Math.max(0,minutes), Math.max(0, seconds));
                                    colorToggle = false;
                                } else {
                                    countDownTime = String.format("&4%02d &6Hrs &4%02d &6Min &4%02d &6Secs", hours, Math.max(0,minutes), Math.max(0, seconds));
                                    colorToggle = true;
                                }
                            }
                        }.runTaskTimer(Main.getInstance(), 0L, 20L);
                    }
                }else{
                    if(colorChange != null){
                        if(!colorChange.isCancelled()){
                            colorChange.cancel();
                        }
                    }

                    countDownTime = String.format("&f%02d &6Hrs &f%02d &6Min &f%02d &6Secs", hours, Math.max(0,minutes), Math.max(0, seconds));

                }


//                String CountDown = String.format("%02d Day(s) %02d Hrs %02d Min", TimeUnit.MILLISECONDS.toDays(timeToPurgeInMilli),
//                        (TimeUnit.MILLISECONDS.toHours(timeToPurgeInMilli) - (TimeUnit.MILLISECONDS.toDays(timeToPurgeInMilli) * 24)),
//                        Math.max(0, TimeUnit.MILLISECONDS.toMinutes(timeToPurgeInMilli) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToPurgeInMilli))));
                return countDownTime;
            }else{
                long timeToPurgeInMilli = Main.getInstance().purgeTimeEnd.getTimeInMillis() - System.currentTimeMillis();
                int seconds = (int) (timeToPurgeInMilli / 1000) % 60 ;
                int minutes = (int) ((timeToPurgeInMilli / (1000*60)) % 60);
                int hours   = (int) ((timeToPurgeInMilli / (1000*60*60)) % 24);


                if(!Main.instance.purgeCommencing){
                    if(colorChange == null) {
                        colorChange = new BukkitRunnable() {
                            public void run() {
                                if (colorToggle) {
                                    countDownTime = String.format("&f%02d &6Hrs &f%02d &6Min &f%02d &6Secs", hours, Math.max(0,minutes), Math.max(0, seconds));
                                    colorToggle = false;
                                } else {
                                    countDownTime = String.format("&4%02d &6Hrs &4%02d &6Min &4%02d &6Secs", hours, Math.max(0,minutes), Math.max(0, seconds));
                                    colorToggle = true;
                                }
                            }
                        }.runTaskTimer(Main.getInstance(), 0L, 20L);
                    }
                }else{
                    if(colorChange != null) {
                        if (!colorChange.isCancelled()) {
                            colorChange.cancel();
                        }
                    }

                    countDownTime = String.format("&f%02d &6Hrs &f%02d &6Min &f%02d &6Secs", hours, Math.max(0,minutes), Math.max(0, seconds));

                }
                return countDownTime;
            }
        }

        if(identifier.equals("enabled")){
            if(!Main.getInstance().purgeEnabled){
                return "Purge Commencing:";
            }else{
                return "Purge Ending:";
            }
        }
        return null;
    }
}

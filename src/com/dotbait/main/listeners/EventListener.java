package com.dotbait.main.listeners;

import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.dotbait.main.Main;
import com.dotbait.main.discord.Announcer;
import com.dotbait.main.events.PurgeEvent;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {
	
	@EventHandler
	public void onPurgeEvent(PurgeEvent e) {
		if(e.getPurgeEvent()) {
			Main.getInstance().console.sendMessage("Purge started");
			Main.getInstance().purgeEnabled = true;
			Announcer.getInstance().jda.getPresence().setActivity(Activity.watching("Waiting..."));
			for(Player p : Main.getInstance().getServer().getOnlinePlayers()) {
				p.sendTitle(ChatColor.RED + "Purge Started!", ChatColor.GREEN + "All restrictions have been removed", 2, 80, 2);
			}
			Bukkit.broadcastMessage(ChatColor.RED + "[ANNOUNCER] " + ChatColor.GREEN + "The purge has begun!");
		}else {
			Main.getInstance().console.sendMessage("Purge ended");
			Main.getInstance().purgeEnabled = false;
			Announcer.getInstance().jda.getPresence().setActivity(Activity.watching("Waiting..."));
			for(Player p : Main.getInstance().getServer().getOnlinePlayers()) {
				p.sendTitle(ChatColor.RED + "Purge Ended!", ChatColor.GREEN + "All restrictions have re-enabled", 2, 80, 2);
			}
			Bukkit.broadcastMessage(ChatColor.RED + "[ANNOUNCER] " + ChatColor.RED + "The purge has ended!");
		}
	}
	
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		
		if(e.getDamager() instanceof Player) {
			if(e.getEntity() instanceof Player) {
				if(!Main.getInstance().purgeEnabled) {
					e.setCancelled(true);
					Player BooBooMaker = (Player)e.getDamager();
					BooBooMaker.sendMessage(ChatColor.RED+"[ANNOUNCER]"+ChatColor.GREEN+" PVP is disabled while we are not purging. Love one another, get to know eachother.. Then do your worst"
							+ " when the time is right!");
				}
			}
		}else {
			if(Main.getInstance().purgeEnabled)
				return;
			
			if(e.getDamager() instanceof Projectile) {
				Projectile proj = (Projectile) e.getDamager();
				if(proj.getShooter() instanceof Player) {
					if(e.getEntity() instanceof Player) {
						if((Player)e.getEntity() == (Player)proj.getShooter()) {
							return;
						}else {
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		if(Main.getInstance().purgeEnabled){
			e.getPlayer().sendTitle(ChatColor.RED + "Purge is Active", "", 20, 100, 20);
		}
	}
}

package com.dotbait.main.commands;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.dotbait.main.discord.Announcer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import com.dotbait.main.Main;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor, TabCompleter {

	public int day; // The day of the week we want the purge to begin
	public int timeHour; // The time on the day we want the purge to begin
	public int timeMin;
	public int duration; // How long we want it to go
	public int durationMin;
	
	
	
	
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String commandLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("purge")) {
			if(sender instanceof Player) {
					if(args.length > 0 && sender.isOp()) {
						if(args[0].equalsIgnoreCase("clear")) {
							Main.getInstance().config.set("Purge Enabled", false);
							Main.getInstance().config.set("Time Zone", "HST");
							Main.getInstance().config.set("Next Purge Start Date", 0);
							Main.getInstance().config.set("Purge End Date", 0);
							Main.getInstance().saveConfig();
							Main.getInstance().purgeEnabled = false;
							day = 0; // The day of the week we want the purge to begin
							timeHour = 0; // The time on the day we want the purge to begin
							timeMin = 0;
							duration = 0; // How long we want it to go
							durationMin = 0;
							Main.getInstance().purgeTime = Calendar.getInstance();
							Main.getInstance().purgeTimeEnd = Calendar.getInstance();
							
							sender.sendMessage("Purge dates have been cleared.");
							return true;
						}else if(args[0].equalsIgnoreCase("set")){
							//Set the purge for debugging

							Boolean set = Boolean.valueOf(args[1]);
							Main.getInstance().purgeEnabled = set;
							sender.sendMessage(ChatColor.RED+"[ANNOUNCER] The purge has been set for debugging to: "+set);
							return true;
						}else {
							
							setTimer(Integer.parseInt(args[0]),Integer.parseInt(args[1]), Integer.parseInt(args[2]),Integer.parseInt(args[3]),Integer.parseInt(args[4]));
							sender.sendMessage("You set the new Purge Date and Time: ");
							sender.sendMessage(ChatColor.GREEN + "The purge will begin at: [" + ChatColor.RED + Main.getInstance().purgeTime.getTime() + ChatColor.GREEN + "]");
							return true;
						}
					}else {
						sender.sendMessage(ChatColor.GREEN + "The purge will begin at: [" + ChatColor.RED + Main.getInstance().purgeTime.getTime() + ChatColor.GREEN + "]");
						return true;
					}
				}
			}else if(!(sender instanceof Player) && cmd.getName().equalsIgnoreCase("set")){
				Announcer.getInstance().userConnectedAccount(args[1]);

				Main.getInstance().console.sendMessage("[DISCORD] Setting users role to MEMBER via Skoice Pugin: "+ args[1]);
				return true;
			}

		if(cmd.getName().equalsIgnoreCase("link")) { //Voice Command for linking account
			if(sender instanceof Player && args[0].equals("server")) {
				if(args.length == 1) {
					TextComponent server = new TextComponent("The Purge - Minecraft Server Discord");
					server.setColor(ChatColor.GOLD);
					server.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/RQQeJnzZk3"));
					server.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Join the discord for proximity chat!")));
					Player p = (Player)sender;
					p.sendMessage(ChatColor.AQUA + "[DISCORD]" + ChatColor.GREEN + " Here's the discord link: ");
					p.spigot().sendMessage(server);
				}
			}
		}
		return true;
	}

	
	public void setTimer(int d, int t, int tm, int dur, int dm) {
		day = d;
		timeHour = t;
		timeMin = tm; // Hours after purge when to end it.
		durationMin = dm;
		
		Main.getInstance().purgeTime.set(Calendar.DAY_OF_WEEK, day);
		Main.getInstance().purgeTime.set(Calendar.HOUR_OF_DAY, timeHour);
		Main.getInstance().purgeTime.set(Calendar.YEAR, Main.getInstance().calender.getWeekYear());
		Main.getInstance().purgeTime.set(Calendar.MINUTE, timeMin);
		Main.getInstance().purgeTime.set(Calendar.SECOND, 0);
		
		if(timeHour+dur > 24) { // Check if our duration exceeds 24 hours, if it does get the new time and add a day to the calendar.
			duration = timeHour + dur - 24;
			
			if(day + 1 > 7) { // add a day to the calendar, if the week day is greater than Saturday then subtract a week. EX: if its on a Saturday, make it end on the next Sunday.
				day = day - 7;
			}
		}else { // We are good, no need to change anything. Keep as is.
			duration = timeHour + dur;
		}
		Main.getInstance().purgeTimeEnd.set(Calendar.DAY_OF_WEEK, day);
		Main.getInstance().purgeTimeEnd.set(Calendar.HOUR_OF_DAY, duration);
		Main.getInstance().purgeTimeEnd.set(Calendar.YEAR, Main.getInstance().calender.getWeekYear());
		Main.getInstance().purgeTimeEnd.set(Calendar.MINUTE, durationMin);
		Main.getInstance().purgeTimeEnd.set(Calendar.SECOND, 0);

		Main.getInstance().console.sendMessage("[" + Calendar.getInstance().getTime() + "] ");
		Main.getInstance().console.sendMessage("[Start: " + Main.getInstance().purgeTime.getTime() + "] ");
		Main.getInstance().console.sendMessage("[End: " + Main.getInstance().purgeTimeEnd.getTime() + "] ");
		
		Main.getInstance().saveReadConfig();
		
	}

	@Override
	public List<String> onTabComplete (@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args){
		List<String> helper = new ArrayList<>();
		if(cmd.getName().equalsIgnoreCase("discord")){
			if(sender instanceof Player){
				//Player player = (Player) sender;

				if(args.length < 1) {
					helper.add("server");
					helper.add("link");
					helper.add("unlink");

					return helper;
				}
			}
		}
		return helper;
	}
}

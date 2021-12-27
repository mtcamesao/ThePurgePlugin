package com.dotbait.main;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Map.Entry;


import com.dotbait.main.purgePlaceholder.PurgePlaceholder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.dotbait.main.commands.Commands;
import com.dotbait.main.discord.Announcer;
import com.dotbait.main.listeners.EventListener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;




public class Main extends JavaPlugin{
	
	//-----------Singleton------------------------------------
	public static Main instance;
	
	public Main() {
		instance = this;
		new Announcer(this);
	}
	
	public static Main getInstance() {
		return instance;
	}
	//--------------------------------------------------------
	
	boolean timeNotified = false;
	
	public boolean purgeEnabled = false;
	public boolean purgeCommencing = false;
	
	public FileConfiguration config = this.getConfig();
	public FileConfiguration linkedAccounts;
	public File data;
	
	public Map<String, Integer> inviteCodes = new HashMap<>();
	
	public Calendar calender = Calendar.getInstance();
	public Calendar purgeTime = Calendar.getInstance();
	public Calendar purgeTimeEnd = Calendar.getInstance();
	
	
	
	
	public ConsoleCommandSender console = getServer().getConsoleSender();
		
	public void loadConfig() {
		
		config.options().copyDefaults(true);
		this.saveDefaultConfig();
		if(this.getConfig().contains("Next Purge Start Date")) {
			purgeEnabled = config.getBoolean("Purge Enabled");
			purgeTime.setTimeInMillis(config.getLong("Next Purge Start Date"));
			purgeTimeEnd.setTimeInMillis(config.getLong("Purge End Date"));

			purgeTime.setTimeZone(TimeZone.getTimeZone("HST"));
			purgeTimeEnd.setTimeZone(TimeZone.getTimeZone("HST"));

			console.sendMessage("-----Purge Config reloaded-----");
			console.sendMessage("[" + Calendar.getInstance().getTime() + "] ");
			console.sendMessage("[Start: " + purgeTime.getTime() + "] ");
			console.sendMessage("[End: " + purgeTimeEnd.getTime() + "] ");






		}else {
			return;
		}
	}
		
	@Override
	public void onEnable() {
		Announcer.getInstance().botConnect();
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new PurgePlaceholder(this).register();
		}
		loadConfig(); // Remove?
		loadData();
		
		this.getCommand("purge").setExecutor(new Commands());
		this.getCommand("set").setExecutor(new Commands());
		this.getCommand("link").setExecutor(new Commands());
		
		getServer().getPluginManager().registerEvents(new EventListener(), instance);
		
		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run() {
				checkTime(); // Check time function
			}
		}, 0L, 20L);
	}
	
	public void loadData() {
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		
		data = new File(getDataFolder(), "linkedAccounts.yml");
		
		if(!data.exists()) {
			try {
				data.createNewFile();
			}catch(IOException e) {
				getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not create data file");
			}
		}
		
		linkedAccounts = YamlConfiguration.loadConfiguration(data);
		
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Discord linked accounts have been loaded.");
		if(!linkedAccounts.contains("Accounts")) {
			linkedAccounts.set("Accounts.mcUUID.discord", 404806830691319819L);
		}
		saveData();
	}
	
	public void saveData() {
		try {
			linkedAccounts.save(data);
		}catch(IOException e) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save data file");
		}
		
	}

	public void PurgeStart() {
		if(!purgeCommencing)
			purgeCommencing = true;
		else 
			return;
		Announcer.getInstance().BeginPurgeAnnoucement();
		
	}
	
	public void PurgeEnd() {
		if(purgeCommencing)
			purgeCommencing = false;
		else 
			return;
		
		purgeEnabled = false; // Change to end of track
		
		Announcer.getInstance().EndPurgeAnnoucement();
	}
	
	//Save the configuration file
	public void saveReadConfig() {
		config.set("Purge Enabled", purgeEnabled);
		config.set("Time Zone", "HST");
		config.set("Next Purge Start Date", purgeTime.getTimeInMillis());
		
		config.set("Purge End Date", purgeTimeEnd.getTimeInMillis());
		saveConfig();
	}
		
	
	
	public void checkTime() {

		calender.setTimeInMillis(System.currentTimeMillis());
		Long getMilis = purgeTime.getTimeInMillis() - System.currentTimeMillis();
		Long time = getMilis / 60000L;
		if(time.equals(60L) && !timeNotified) {
			timeNotified = true;
			Bukkit.broadcastMessage(ChatColor.RED + "[ANNOUNCER] " + ChatColor.BOLD + ChatColor.DARK_GREEN + "The purge will begin in " + ChatColor.RED + "60 minutes" + ChatColor.DARK_GREEN + ".");
			Announcer.getInstance().sendPurgeCommencingWarning("60 minutes");
		}else if(time.equals(30L) && !timeNotified) {
			timeNotified = true;
			Bukkit.broadcastMessage(ChatColor.RED + "[ANNOUNCER] " + ChatColor.BOLD + ChatColor.DARK_GREEN + "The purge will begin in " + ChatColor.RED + "30 minutes" + ChatColor.DARK_GREEN + ".");
			Announcer.getInstance().sendPurgeCommencingWarning("30 minutes");
		}else if (time.equals(1L) && !timeNotified){
			timeNotified = true;
			Bukkit.broadcastMessage(ChatColor.RED + "[ANNOUNCER] " + ChatColor.BOLD + ChatColor.DARK_GREEN + "The purge will begin in " + ChatColor.RED + "1 minute" + ChatColor.DARK_GREEN + ".");
			Announcer.getInstance().sendPurgeCommencingWarning("1 minute");
		}

		if(time.equals(59L) && timeNotified)
			timeNotified = false;

		if(time.equals(29L) && timeNotified)
			timeNotified = false;

		if(time.equals(0))
			timeNotified = false;
		
		
		if(purgeTime.before(calender)) { //if its time for the purge then start it
			if(purgeTimeEnd.after(calender)) {
				if(!purgeCommencing) {
					PurgeStart(); //Begin purge if its time
				}else {
					return;
				}
			}else {
				if(purgeCommencing) {
					PurgeEnd(); // End purge
				}
			}
		}
	}
	
	public void generateInviteCode(Player p) {
		//Check if the users account is linked or not
		if(!isAccountLinked(p)) { // If not linked
			Random rand = new Random();
			int maxNumPos = 999999;
			int randCode = rand.nextInt(maxNumPos);
			
			if(inviteCodes.containsValue(randCode)) {
				generateInviteCode(p);
				console.sendMessage("Randomly generated code was already in use.... So created a new one.");
				return;
			}
			
			inviteCodes.put(p.getUniqueId().toString(), randCode);
			
			TextComponent server = new TextComponent("Smelly Bait's Discord");
			server.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/fgDsJaem5e"));
			server.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Join the discord for proximity chat!")));
			
			
			p.sendMessage(ChatColor.AQUA + "[DISCORD]" + ChatColor.GREEN + " Send this generated link code: " + ChatColor.YELLOW + ChatColor.BOLD + randCode + ChatColor.GREEN + " to: " + ChatColor.RED + Announcer.getInstance().jda.getSelfUser().getName()+ChatColor.GREEN+".");
			p.sendMessage(ChatColor.AQUA + "[DISCORD]" + ChatColor.GREEN + " Here's the discord link if you havent joined already: "); 
			server.setColor(ChatColor.GOLD);
			p.spigot().sendMessage(server);
		}
	}
	
	public synchronized void codeAccepted(String code, PrivateChannel channel, Long discordID) {
		int intCode = Integer.parseInt(code);
		PrivateChannel pc;
		synchronized(pc = Announcer.getInstance().jda.getPrivateChannelById(channel.getIdLong())) {
			console.sendMessage("Received by bot: " + intCode);
			for(Entry<String, Integer> entry : Main.getInstance().inviteCodes.entrySet()) {
    			if(entry.getValue().equals(intCode)) {
    				console.sendMessage("Code found! Sending confirmation message! Users Discord ID: " + discordID);
	    			pc.sendMessage(Bukkit.getPlayer(UUID.fromString(entry.getKey())).getName() + ", you have successfully linked your account! Please join the 'Purge Proximity' channel to use voice chat!").queue();
	    			addNewLinkedAccount(Bukkit.getPlayer(UUID.fromString(entry.getKey())), discordID);
	    			inviteCodes.remove(entry.getKey());
    			}
    		}
		}
	}
	
	public boolean isAccountLinked(Player p) {
		//Main.getInstance().linkedAccounts.
		if(linkedAccounts.contains("Accounts."+p.getUniqueId().toString())) {
			p.sendMessage(ChatColor.RED + "[ANNOUNCER] " + ChatColor.YELLOW + p.getName() + ChatColor.RED + ", your account is already linked!");
			return true;
		}else
			return false;
	}
	
	public void addNewLinkedAccount(Player p, Long discordID) {
		linkedAccounts.set("Accounts."+p.getUniqueId().toString()+".discord", discordID);
		p.sendMessage(ChatColor.RED + "[ANNOUNCER] " + ChatColor.GREEN + "Welcome, " + ChatColor.YELLOW + p.getName() + ChatColor.GREEN + ". Your discord account is now linked to the server!");
		saveData();
	}
	
	
	
}


package com.dotbait.main.discord;

import com.dotbait.main.Main;
import com.dotbait.main.discord.listeners.AnnouncerListener;
import com.dotbait.main.sounds.PlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Announcer{
	
	//-----------Singleton------------------------------------
		public static Announcer instance;
		public Main plugin;
		
		
		public Announcer(Main plugin) {
			instance = this;
			this.plugin = plugin;
			loadBot();
		}
		
		public static Announcer getInstance() {
			return instance;
		}
	//--------------------------------------------------------


	
	
	/*
	 * 
	 * Bot Configuration
	 * 
	 */
	//public Bot bot;
	public final String TOKEN = "ODE3NjM2OTYxMjU3NzgzMjk2.YEMZwg.AQaFdDjfvYBIdF5oicthWKkkD8k";


	
	//JDA BOT (HOPEFULLY REPLACES THE OLD CRAP)
	JDABuilder builder;
	public JDA jda;
	
	
	public Guild guild;
	public VoiceChannel channel, proxyChannel;

	public TextChannel announcementChannel;
	
	public AudioManager audioManager;
	
	PlayerManager manager = PlayerManager.getInstance();
	
	
	
	public void loadBot() {
		
		
		try {
			builder = JDABuilder.createDefault(TOKEN, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES);
			jda = builder.build();
			jda.addEventListener(new AnnouncerListener());
		} catch (Exception e) {
			e.printStackTrace();
		}
		guild = jda.getGuildById(817456149690450011L);
	}

	public synchronized void botConnect() {
		jda.getPresence().setActivity(Activity.watching("Waiting..."));
		Main.getInstance().console.sendMessage("JDA Voice Channels: "+jda.getVoiceChannels().toString());

		synchronized(channel = jda.getVoiceChannelById(817513613132562493L)) {
			audioManager = channel.getGuild().getAudioManager();
			audioManager.openAudioConnection(channel);
		}
		
	}
	
	public void BeginPurgeAnnoucement() {
		MoveUsers(true);
		manager.loadAndPlay(channel, "https://youtu.be/3z2Lz54MazY");
		jda.getPresence().setActivity(Activity.watching("Announcing Purge..."));
		Main.getInstance().console.sendMessage("Announcer Purge Beginning!");
		
		
	}
	
	public void EndPurgeAnnoucement() {
		MoveUsers(true);
		manager.loadAndPlay(channel, "https://youtu.be/BST-W99_Ef8");
		jda.getPresence().setActivity(Activity.watching("Ending Purge..."));
		Main.getInstance().console.sendMessage("Announcer Purge Ending!");
	}

	public void ShutDownBot(){
		jda.shutdown();
	}

	public synchronized void userConnectedAccount(String memberID){
		if(memberID.equals(null))
			return;

		Main.getInstance().console.sendMessage("Members ID: " + memberID);
		User user = jda.getUserById(memberID);
		synchronized (guild = jda.getGuildById(817456149690450011L)){
			guild.addRoleToMember(memberID, guild.getRoleById(817456921593380864L)).queue();
			Main.getInstance().console.sendMessage("Members ID Pushed: " + memberID);
		}
	}
	
	public synchronized void MoveUsers(boolean toChannel) {
		if(toChannel) {
			//Move players to channel and mute them.
			synchronized(channel = jda.getVoiceChannelById(817513613132562493L)) {
				for(VoiceChannel vc : jda.getVoiceChannels()) {
					if(vc.getManager() != null) {
						for(Member m : vc.getMembers()) {
							if(m != null && m.getUser().getIdLong() != 817636961257783296L) {
								if(channel != null && m.getGuild() != null)
									botMove(m, channel, true);
							}
						}
					}
				}
			}
			
		}else {
			//Move players out of channel and unmute them.
			synchronized(proxyChannel = jda.getVoiceChannelById(817456150148677636L)) {
				for(VoiceChannel vc : jda.getVoiceChannels()) {
					if(vc.getManager() != null) {
						for(Member m : vc.getMembers()) {
							if(m != null && m.getUser().getIdLong() != 817636961257783296L) {
								if(proxyChannel != null && m.getGuild() != null)
									botMove(m, proxyChannel, false);
							}
						}
					}
				}
			}
		}
	}

	public synchronized void sendPurgeCommencingWarning(String time){
		synchronized (announcementChannel = jda.getTextChannelById(817456150148677633L)){
			announcementChannel.sendMessage("@everyone The Purge is commencing in: "+time).queue();
		}
	}
	
	public void botMove(Member m, VoiceChannel toChannel, boolean setMute) {
		m.getGuild().moveVoiceMember(m, toChannel).queue();

		if(!m.isOwner()) {
			m.getGuild().mute(m, setMute);
		}
	}
	
}

package com.dotbait.main.discord.listeners;

import com.dotbait.main.Main;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AnnouncerListener extends ListenerAdapter {

    public void onGuildMemberJoinEvent(GuildMemberJoinEvent e){
        e.getGuild().addRoleToMember(e.getMember().getId(), e.getGuild().getRoleById(817457171431948288L)).queue();
        Main.getInstance().console.sendMessage("A new member has joined the guild!");
    }
}

package com.github.black0nion.blackonionbot.commands.bot;

import java.util.ArrayList;
import java.util.List;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SwearWhitelistCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "swearwhitelist", "antiswearwhitelist", "asw" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		if (args.length >= 3 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
			List<String> mentionedStuff = new ArrayList<>();
			List<Role> roles = message.getMentionedRoles();
			List<TextChannel> channels = message.getMentionedChannels();
			for (int i = 2; i < args.length; i++) {
				String input = args[i];
				Role rl = roles.stream().filter(r -> r.getAsMention().equals(input)).findFirst().orElse(null);
				TextChannel ch = channels.stream().filter(c -> c.getAsMention().equals(input)).findFirst().orElse(null);
				Permission perm = null;
				try { perm = Permission.valueOf(input.toUpperCase()); } catch (Exception ignored) {}
				
				if (rl != null) mentionedStuff.add(rl.getAsMention());
				if (ch != null) mentionedStuff.add(ch.getAsMention());
				if (perm != null) mentionedStuff.add(perm.name());
			}
			
			boolean add = args[1].equalsIgnoreCase("add");
			
			if (mentionedStuff.size() != 0) {
				List<String> newWhitelist = GuildManager.getList(guild, "whitelist", String.class);
				if (newWhitelist == null) newWhitelist = new ArrayList<>();
				List<String> temp = new ArrayList<String>(newWhitelist);
				if (add) {
					temp.retainAll(mentionedStuff);
					newWhitelist.removeAll(temp);
					newWhitelist.addAll(mentionedStuff);
				} else newWhitelist.removeAll(mentionedStuff);
				GuildManager.saveList(guild, "whitelist", newWhitelist);
				channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("whitelistupdated", (add ? LanguageSystem.getTranslatedString("addedtowhitelist", author, guild).replace("%add%", mentionedStuff.toString()) : LanguageSystem.getTranslatedString("removedfromwhitelist", author, guild).replace("%removed%", mentionedStuff.toString())), false).build()).queue();
			}
		} else {
			final List<String> whitelist = GuildManager.getList(guild, "whitelist", String.class);
			channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("antiswearwhitelist", (whitelist != null && whitelist.size() != 0 ? whitelist.toString() : "empty"), false).build()).queue();
		}
	}

	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.ADMINISTRATOR };
	}
	
	@Override
	public String getSyntax() {
		return "<add | remove> <@role | #channel | Permission Name>";
	}
}
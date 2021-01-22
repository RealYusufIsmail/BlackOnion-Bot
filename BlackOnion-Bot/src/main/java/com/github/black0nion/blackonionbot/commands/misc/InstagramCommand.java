package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.enums.Progress;
import com.github.black0nion.blackonionbot.oldcommands.Command;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InstagramCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField("Reason:", LanguageSystem.getTranslatedString("instasshitapi", author, guild), false).build()).queue();
	}
	
	@Override
	public Progress getProgress() {
		return Progress.PLANNED;
	}
	
	@Override
	public Category getCategory() {
		return Category.MISC;
	}

	@Override
	public String[] getCommand() {
		return new String[]{"instagram", "insta"};
	}
}
package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static com.github.black0nion.blackonionbot.utils.config.Config.metadata;

public class SupportCommand extends SlashCommand {

	public SupportCommand() {
		super("support", "Used to get support.");
	}

	private static final String BOT_DEVELOPERS = metadata.authors().entrySet().stream().map(entry -> "[" + entry.getKey() + "](" + entry.getValue() + ")").collect(Collectors.joining(", "));
	private static final String BLACKONION_AUTHORS = metadata.blackonion_authors().entrySet().stream().map(entry -> "[" + entry.getKey() + "](" + entry.getValue() + ")").collect(Collectors.joining(", "));

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		cmde.reply(cmde.success()
			.addField("gethelp", "[Discord Server](https://dsc.gg/blackonion)", false)
			.addField("botdevelopers", BOT_DEVELOPERS, false)
			.addField("blackoniondevs", BLACKONION_AUTHORS, false));
	}
}
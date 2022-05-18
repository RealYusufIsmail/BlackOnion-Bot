package com.github.black0nion.blackonionbot.commands.admin;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.github.black0nion.blackonionbot.utils.config.ConfigManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Optional;

public class StatusCommand extends SlashCommand {

	public StatusCommand() {
		super(builder(Commands.slash("status", "Set the status of the bot").addOptions(
			new OptionData(OptionType.STRING, "status", "The OnlineStatus of the bot", true)
				.addChoices(Arrays.stream(OnlineStatus.values()).map(m -> new Command.Choice(m.name(), m.name())).toList())
		)).setAdminGuild());
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		OnlineStatus status = Utils.parse(OnlineStatus.class, e.getOption("status", OptionMapping::getAsString));
		if (status == null) {
			cmde.send("invalidrole");
			return;
		}
		Config.getInstance().setOnlineStatus(status);
		ConfigManager.saveConfig();
		cmde.send("newstatus", new Placeholder("status", status.name()));

		e.getJDA().getPresence().setStatus(status);
	}

	public static OnlineStatus getStatusFromConfig() {
		return Optional.ofNullable(Config.getInstance().getOnlineStatus()).orElse(OnlineStatus.ONLINE);
	}
}
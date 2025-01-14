package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.stats.StatisticsManager;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

import static com.github.black0nion.blackonionbot.bot.BotInformation.*;
import static com.github.black0nion.blackonionbot.utils.config.Config.metadata;

public class StatsCommand extends SlashCommand {

	public StatsCommand() {
		super("stats", "Shows statistics regarding the bot.");
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final long diff = System.currentTimeMillis() - StatisticsManager.STARTUP_TIME;

		cmde.reply(cmde
			.success()
			.setTitle("Bot Stats")
			.addField("prefix", "`" + "/" + "`", true)
			.addField("runmode", Config.run_mode.name().toUpperCase(), true)
			.addField("os", OS_NAME, true)
			.addField("cpuname", CPU_NAME, true)
			.addField("cpucores", OS_BEAN.getAvailableProcessors(), true)
			.addField("cpuspeed", CPU_MHZ, true)
			.addField("lines", metadata.lines_of_code(), true)
			.addField("files", metadata.files(), true)
			.addField("commandsexecuted", (int) StatisticsManager.TOTAL_COMMANDS_EXECUTED.get(), true)
			.addField("messagessent", (int) StatisticsManager.TOTAL_MESSAGES_SENT.get(), true)
			.addField("commands", SlashCommandBase.getCommandCount(), true)
			.addField("ping", StatisticsManager.getGatewayPing() + "ms", true)
			.addField("usercount", StatisticsManager.getUserCount(), true)
			.addField("guildcount", StatisticsManager.getGuildCount(), true)
			.addField("uptime", Utils.parseDate(diff), true)
			.addField("version", Config.metadata.version(), true)
			.setThumbnail(e.getJDA().getSelfUser().getAvatarUrl() + "?size=512")
			.setTimestamp(Instant.now())
		);
	}
}
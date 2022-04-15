package com.github.black0nion.blackonionbot.commands.fun;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TronaldDumpCommand extends SlashCommand {

	public TronaldDumpCommand() {
		super("tronalddump", "Shows you a random quote from tronald dump");
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		Bot.getInstance().getHttpClient().sendAsync(HttpRequest.newBuilder(URI.create("https://tronalddump.io/random/quote")).header("Accept", "application/json").build(), HttpResponse.BodyHandlers.ofString())
			.thenApply(HttpResponse::body).thenAccept(response -> {
				cmde.reply(cmde.success().setThumbnail("https://www.tronalddump.io/img/tronalddump_850x850.png").setTitle("TronaldDump", "https://tronalddump.io").addField(new JSONObject(response).getString("value"), "bytronalddump", false));
			});
	}
}
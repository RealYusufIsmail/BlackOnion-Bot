package com.github.black0nion.blackonionbot.commands.old;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class NNNCommand extends Command {
	
	public NNNCommand() {
		this.setCommand("nnn", "nonutnovember")
			.setHidden()
			.dontAutoRegister();
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		try {
		final String gameDate = "2020-12-01T00:00:00Z";
		final SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY);
		apiFormat.setTimeZone(TimeZone.getTimeZone("MEZ"));
		final Date dateOfGame = apiFormat.parse(gameDate);
		final long millis = dateOfGame.getTime() - System.currentTimeMillis();
		final ZoneId z = ZoneId.of( "Europe/Berlin" ) ;
		final LocalDate today = LocalDate.now(z) ;
		final LocalDate start = LocalDate.of(2020, 11, 1);
		final LocalDate stop = LocalDate.of(2020, 11, 30);

		final long totalDays = ChronoUnit.DAYS.between( start , stop ) ;
		final long elapsedDays = ChronoUnit.DAYS.between( start , today ) ;

		final long percentComplete = ( elapsedDays * 100 ) / totalDays ;
		
		final String hms = String.format("%02d Tage, %02d Stunden und %02d Minuten", TimeUnit.MILLISECONDS.toDays(millis),
						TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)),
						TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
		message.reply("Zeit bis zum Ende des NoNutNovembers: " + hms + ", bereits geschafft sind " + percentComplete + "% der gesamten Zeit!").queue();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
}
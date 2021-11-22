package com.github.black0nion.blackonionbot.systems.games.connectfour;

import java.util.Map;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class ConnectFour {
    long messageID;
    User playerX;
    User playerY;
    FieldType[][] field;
    public FieldType currentUser;
    MessageChannel channel;

    public ConnectFour(final MessageChannel channel, final User playerX, final User playerY) {

	this.field = new FieldType[ConnectFourGameManager.Y][ConnectFourGameManager.X];

	this.currentUser = Bot.random.nextInt(1) == 0 ? FieldType.X : FieldType.O;

	for (int x = 0; x < ConnectFourGameManager.X; x++) {
	    for (int y = 0; y < ConnectFourGameManager.Y; y++) {
		this.field[y][x] = FieldType.EMPTY;
	    }
	}

	channel.sendMessageEmbeds(EmbedUtils.getSuccessEmbed().setTitle("Connect 4 | Aktueller Spieler: " + Utils.removeMarkdown((this.currentUser == FieldType.X ? playerX.getName() : playerY.getName()))).addField("Current State:", this.getField(), false).build()).queue(success -> this.messageID = success.getIdLong());
	this.channel = channel;
	this.playerX = playerX;
	this.playerY = playerY;
    }

    public Message getMessage() {
	return this.channel.retrieveMessageById(this.messageID).submit().join();
    }

    public long getMessageID() {
	return this.messageID;
    }

    public void setMessageID(final long messageID) {
	this.messageID = messageID;
    }

    public User getPlayerX() {
	return this.playerX;
    }

    public User getPlayerY() {
	return this.playerY;
    }

    public FieldType[][] getfield() {
	return this.field;
    }

    public void setfield(final FieldType[][] field) {
	this.field = field;
    }

    public boolean isPlayer(final String userId) {
	if (this.getPlayerX().getId().equals(userId) || this.getPlayerY().getId().equals(userId)) return true;
	return false;
    }

    public FieldType getWinner() {
	if (this.won(FieldType.X)) return FieldType.X;
	else if (this.won(FieldType.O)) return FieldType.O;
	return FieldType.EMPTY;
    }

    public boolean won(final FieldType player) {
	// horizontalCheck 
	for (int j = 0; j < ConnectFourGameManager.X - 3; j++) {
	    for (int i = 0; i < ConnectFourGameManager.Y; i++) if (this.field[i][j] == player && this.field[i][j + 1] == player && this.field[i][j + 2] == player && this.field[i][j + 3] == player) return true;
	}
	// verticalCheck
	for (int i = 0; i < ConnectFourGameManager.Y - 3; i++) {
	    for (int j = 0; j < ConnectFourGameManager.X; j++) if (this.field[i][j] == player && this.field[i + 1][j] == player && this.field[i + 2][j] == player && this.field[i + 3][j] == player) return true;
	}
	// ascendingDiagonalCheck 
	for (int i = 3; i < ConnectFourGameManager.Y; i++) {
	    for (int j = 0; j < ConnectFourGameManager.X - 3; j++) if (this.field[i][j] == player && this.field[i - 1][j + 1] == player && this.field[i - 2][j + 2] == player && this.field[i - 3][j + 3] == player) return true;
	}
	// descendingDiagonalCheck
	for (int i = 3; i < ConnectFourGameManager.Y; i++) {
	    for (int j = 3; j < ConnectFourGameManager.X; j++) if (this.field[i][j] == player && this.field[i - 1][j - 1] == player && this.field[i - 2][j - 2] == player && this.field[i - 3][j - 3] == player) return true;
	}

	return false;
    }

    public Map.Entry<Integer, Integer> getCoordinatesFromString(final String input) {
	final char[] charInput = input.toCharArray();
	return new Map.Entry<Integer, Integer>() {
	    @Override
	    public Integer getKey() {
		// the letters
		return Integer.parseInt(String.valueOf(charInput[1]));
	    }

	    @Override
	    public Integer getValue() {
		// the numbers
		for (int i = 0; i < Utils.alphabet.size(); i++) if (Utils.alphabet.get(i) == charInput[0]) return i;
		// should never get called because isValidInput is called first
		return null;
	    }

	    @Override
	    public Integer setValue(final Integer value) {
		return null;
	    }
	};
    }

    public boolean isValidInput(final String input) {
	final char[] charInput = input.toCharArray();
	try {
	    final int numbAtOne = Integer.parseInt(String.valueOf(charInput[1]));
	    if (Utils.alphabet.contains(charInput[0]) && Utils.alphabet.indexOf(charInput[0]) < ConnectFourGameManager.X && numbAtOne >= 0 && numbAtOne < ConnectFourGameManager.Y) return true;
	} catch (final Exception ignored) {
	}
	return false;
    }

    public void nextUser() {
	if (this.currentUser == FieldType.X) {
	    this.currentUser = FieldType.O;
	} else {
	    this.currentUser = FieldType.X;
	}
    }

    public String getField() {
	String output = "";
	output += "   A   B   C   D   E   F   G   H   I\n ----------------------------\n";
	for (int y = 0; y < ConnectFourGameManager.Y; y++) {
	    for (int x = 0; x < ConnectFourGameManager.X; x++) {
		output += "\\| " + (this.field[y][x] == FieldType.EMPTY ? "    " : " " + this.field[y][x].name() + " ");
	    }
	    output += "| (" + y + ")\n----------------------------\n";
	}
	return output;
    }
}
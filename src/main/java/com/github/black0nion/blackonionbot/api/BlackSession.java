package com.github.black0nion.blackonionbot.api;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.oauth.OAuthUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.annotation.Nullable;
import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class BlackSession {

	public static final MongoCollection<Document> collection = MongoDB.DATABASE.getCollection("dashboard-sessions");

	@Nullable
	private String sessionId;
	private DiscordUser user;

	public BlackSession(final String sessionId) throws ExecutionException, InputMismatchException {
		this.loginToSession(sessionId);
	}

	/**
	 * The websocket will connect and then provide a session id to log in to. If the
	 * websocket connection isn't logged in, use {@link OAuthUtils#loginWithDiscord(String)}
	 * <p>
	 * Auth flow:
	 * <ul>
	 * 	<li>client gets a session id using {@link OAuthUtils#loginWithDiscord(String)}</li>
	 * 	<li>client saves the session id</li>
	 * 	<li>client uses this session id to authorize in the future</li>
	 * </ul>
	 */
	public void loginToSession(final String sessionId) throws ExecutionException, InputMismatchException, NullPointerException {
		final Document doc = collection.find(Filters.eq("sessionid", sessionId)).first();

		if (doc == null) throw new NullPointerException("Session id not found");

		this.sessionId = sessionId;
		this.user = OAuthUtils.getUserWithToken(doc.getString("access_token"), doc.getString("refresh_token"));
	}

	private static final int TARGET_STRING_LENGTH = 69;
	private static final int LEFT_LIMIT_1 = 'A';
	private static final int LEFT_LIMIT_2 = 'a';
	private static final int LEFT_LIMIT_3 = '0';
	private static final int RIGHT_LIMIT_1 = 'Z';
	private static final int RIGHT_LIMIT_2 = 'z';
	private static final int RIGHT_LIMIT_3 = '9';
	public static String generateSessionId() {
		final String generatedId = ThreadLocalRandom.current().ints(LEFT_LIMIT_3, RIGHT_LIMIT_2 + 1)
			.filter(i ->  ((i <= RIGHT_LIMIT_1 && i >= LEFT_LIMIT_1)
						|| (i <= RIGHT_LIMIT_2 && i >= LEFT_LIMIT_2)
						|| (i <= RIGHT_LIMIT_3 && i >= LEFT_LIMIT_3)))
			.limit(TARGET_STRING_LENGTH)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();

		// check if the database contains an entry for this session id, very unlikely
		if (collection.find(Filters.eq("sessionid", generatedId)).first() != null) return generateSessionId();

		return generatedId;
	}

	public static final String SESSIONID_REGEX = "[a-zA-Z\\d]{69}";

	public DiscordUser getUser() {
		return this.user;
	}

	public final @Nullable String getSessionId() {
		return this.sessionId;
	}
}
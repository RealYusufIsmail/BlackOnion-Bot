/**
 *
 */
package com.github.black0nion.blackonionbot.API.impl.post;

import java.util.HashMap;

import org.bson.Document;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.PostRequest;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.mongodb.client.model.Filters;

import spark.Request;
import spark.Response;

/**
 * Logs out a user from a session (deletes their session id from the database
 * and revoke token)
 *
 * @author _SIM_
 */
public class Logout extends PostRequest {

    @Override
    public String url() {
	return "logout";
    }

    @Override
    public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final DiscordUser user) {
	final Document doc = BlackSession.collection.find(Filters.eq("sessionid", request.headers("sessionid"))).first();
	if (doc != null) {
	    BlackSession.collection.deleteOne(Filters.eq("sessionid", request.headers("sessionid")));
	    return new JSONObject().append("error", false).append("success", true).toString();
	}
	response.status(401);
	return new JSONObject().append("error", true).toString();
    }

    @Override
    public boolean requiresLogin() {
	return true;
    }
}
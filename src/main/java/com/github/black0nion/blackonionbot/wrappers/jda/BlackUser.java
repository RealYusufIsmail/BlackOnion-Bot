package com.github.black0nion.blackonionbot.wrappers.jda;

import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.wrappers.jda.impls.UserImpl;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BlackUser extends UserImpl {

	private static final LoadingCache<User, BlackUser> users = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<>() {
		@Override
		public @Nonnull BlackUser load(final @Nonnull User user) {
			return new BlackUser(user);
		}
	});

	public static BlackUser from(@Nonnull final Member member) {
		return from(member.getUser());
	}

	@Nonnull
	public static BlackUser from(@Nonnull final User user) {
		try {
			return users.get(user);
		} catch (ExecutionException e) {
			throw new UncheckedExecutionException(e);
		}
	}

	@Reloadable("usercache")
	public static void clearCache() {
		users.invalidateAll();
	}

	private Language language;
	private List<CustomPermission> permissions;

	private BlackUser(final User user) {
		super(user);

		this.save("name", user.getName());

		try {
			Document config = configs.find(Filters.eq("userid", user.getIdLong())).first();

			if (config == null) {
				config = new Document();
			}

			this.permissions = Utils.gOD(CustomPermission.parse(Utils.gOD(config.getList("permissions", String.class), new ArrayList<>())), new ArrayList<>());

			this.language = LanguageSystem.getLanguageFromName(config.getString("language"));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Nullable
	public Language getLanguage() {
		return this.language;
	}

	public void setLanguage(final @Nullable Language language) {
		this.language = language;
		if (language == null)
			this.clear("language");
		else
			this.save("language", language.getLanguageCode());
	}

	public List<CustomPermission> getPermissions() {
		return this.permissions;
	}

	public boolean hasPermission(final CustomPermission... permissions) {
		if (permissions == null || permissions.length == 0) return true;
		if (this.permissions == null || this.permissions.size() == 0) return false;
		for (final CustomPermission requiredPerm : permissions) {
			if (!this.hasPermission(requiredPerm)) return false;
		}
		return true;
	}

	public boolean hasPermission(final CustomPermission permission) {
		return CustomPermission.hasRights(permission, this.permissions);
	}

	public void addPermissions(final CustomPermission... permissions) {
		this.addPermissions(Arrays.asList(permissions));
	}

	public void addPermissions(final List<CustomPermission> permissions) {
		final List<CustomPermission> perms = this.permissions;
		for (final CustomPermission perm : permissions) {
			if (!perms.contains(perm)) {
				perms.add(perm);
			}
		}
		this.setPermissions(perms);
	}

	public void removePermissions(final CustomPermission... permissions) {
		this.removePermissions(Arrays.asList(permissions));
	}

	public void removePermissions(final List<CustomPermission> permissions) {
		final List<CustomPermission> perms = this.permissions;
		for (final CustomPermission perm : permissions) {
			perms.remove(perm);
		}
		this.setPermissions(perms);
	}

	public void setPermissions(final List<CustomPermission> permissions) {
		this.permissions = permissions;
		this.save("permissions", permissions.stream().map(CustomPermission::name).collect(Collectors.toList()));
	}

	public String getEscapedName() {
		return Utils.escapeMarkdown(this.getName());
	}

	public String getEscapedEffectiveName() {
		return this.getEscapedName() + "#" + this.getDiscriminator();
	}

	@Override
	protected Document getIdentifier() {
		return new Document("userid", this.user.getIdLong());
	}

	private static final MongoCollection<Document> configs = MongoManager.getCollection("usersettings", MongoDB.DATABASE);

	@Override
	protected MongoCollection<Document> getCollection() {
		return configs;
	}

	private static final MongoCollection<Document> warnsCollection = MongoDB.DATABASE.getCollection("warns");
	private final List<Warn> warns = new ArrayList<>();

	public void warn(final Warn w) {
		this.warns.add(w);
		final Document doc = new Document();
		doc.putAll(this.getIdentifier());
		doc.put("issuer", w.issuer());
		final long l = w.date();
		doc.put("date", l);
		if (w.reason() != null) {
			doc.put("reason", w.reason());
		}
		warnsCollection.insertOne(doc);
	}

	public void deleteWarn(final Warn w) {
		this.warns.remove(w);
		warnsCollection.deleteOne(this.getIdentifier().append("date", w.date()));
	}

	// TODO: check why it's never used
	public void saveWarns() {
		warnsCollection.insertMany(this.warns.stream().map(warn -> {
			final Document doc = new Document();
			doc.put("issuer", warn.issuer());
			doc.putAll(this.getIdentifier());
			if (warn.reason() != null) {
				doc.put("reason", warn.reason());
			}
			return doc;
		}).collect(Collectors.toList()));
	}

	public List<Warn> getWarns() {
		return this.warns;
	}

	@Override
	public String toString() {
		return "BlackUser{" +
			"fullName=" + this.getFullName() +
			"language=" + language +
			", permissions=" + permissions +
			'}';
	}
}
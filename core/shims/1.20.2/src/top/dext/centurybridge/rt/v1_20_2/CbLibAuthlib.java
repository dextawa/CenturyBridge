package top.dext.centurybridge.rt.v1_20_2;

import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.WeakHashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.facade.v1_20_2.AuthlibAgent;

/**
 * authlib 4.0.43 (1.20.1) members that authlib 5.0.47 (1.20.2) changed or removed, with 4.0.43's semantics read off its
 * bytecode: the library front of step 1 (core/segments/damage-lib/1.20.1__1.20.2.tsv).
 * <ul>
 * <li>GameProfile: 4.0.43 allowed a null id (a name-only profile; "Name and ID cannot both be blank") and a null name;
 * 5.0.47 refuses both. 1.20.2 itself writes a name-only profile with Util.NIL_UUID (SkullBlockEntity) -- so a 4.0.43 null id
 * is that id, and reads back as null ({@link #getId}); a null name is the empty name. isComplete: a real id and a non-blank
 * name; isLegacy: only 4.0.43's legacy (pre-Mojang-account) login set it, which 5.0.47 and the game no longer have.</li>
 * <li>Environment was an interface (getAuthHost .. asString, create(..)); 5.0.47's is a record without the auth host, which
 * only legacy login used: remembered for environments built through create(..), 4.0.43's own hosts for PROD / STAGING,
 * otherwise the system property 4.0.43's parser read.</li>
 * <li>MinecraftSessionService: joinServer / hasJoinedServer took the profile; fillProfileProperties became fetchProfile by id
 * (a ProfileResult, null when not found: 4.0.43 answered the profile it was given).</li>
 * <li>ProfileLookupCallback.onProfileLookupFailed took a GameProfile (4.0.43's repository built a name-only one); the
 * repository's findProfilesByNames took an Agent (always MINECRAFT for a game profile lookup).</li>
 * <li>YggdrasilAuthenticationService's client token (legacy login only) went from its constructors; the session service and
 * profile repository take the key set / proxy instead of the authentication service.</li>
 * <li>HttpAuthenticationService's request helpers (createUrlConnection, performGetRequest, performPostRequest) went: 4.0.43's
 * bodies, over the service's proxy.</li>
 * </ul>
 */
public final class CbLibAuthlib {
    private CbLibAuthlib() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CbLibAuthlib.class);
    private static final UUID NIL = new UUID(0L, 0L);
    private static final Map<Environment, String> AUTH_HOSTS = Collections.synchronizedMap(new WeakHashMap<>());

    // ------------------------------------------------------------------ GameProfile / Property

    /** new GameProfile(id, name) as 4.0.43 accepted it */
    public static GameProfile gameProfile(UUID id, String name) {
        if (id == null && StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name and ID cannot both be blank");
        }
        return new GameProfile(id == null ? NIL : id, name == null ? "" : name);
    }

    /** GameProfile.getId(): a name-only profile's id was null */
    public static UUID getId(GameProfile profile) {
        UUID id = profile.getId();
        return NIL.equals(id) ? null : id;
    }

    public static boolean isComplete(GameProfile profile) {
        return getId(profile) != null && StringUtils.isNotBlank(profile.getName());
    }

    public static boolean isLegacy(GameProfile profile) {
        return false;
    }

    /** UUIDTypeAdapter.fromUUID */
    public static String fromUUID(UUID value) {
        return value.toString().replace("-", "");
    }

    /** UUIDTypeAdapter.fromString */
    public static UUID fromString(String input) {
        return UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    // ------------------------------------------------------------------ Environment

    /** EnvironmentParser.PROP_AUTH_HOST */
    public static String PROP_AUTH_HOST() {
        return "minecraft.api.auth.host";
    }

    /** Environment.create(authHost, accountsHost, sessionHost, servicesHost, name) */
    public static Environment create(String auth, String accounts, String session, String services, String name) {
        Environment env = new Environment(accounts, session, services, name);
        AUTH_HOSTS.put(env, auth);
        return env;
    }

    public static String getAuthHost(Environment env) {
        String host = AUTH_HOSTS.get(env);
        if (host != null) {
            return host;
        }
        if (env.equals(YggdrasilEnvironment.PROD.getEnvironment())) {
            return "https://authserver.mojang.com";
        }
        if (env.equals(YggdrasilEnvironment.STAGING.getEnvironment())) {
            return "https://yggdrasil-auth-staging.mojang.com";
        }
        return System.getProperty("minecraft.api.auth.host");
    }

    public static String getAccountsHost(Environment env) {
        return env.accountsHost();
    }

    public static String getSessionHost(Environment env) {
        return env.sessionHost();
    }

    public static String getServicesHost(Environment env) {
        return env.servicesHost();
    }

    public static String getName(Environment env) {
        return env.name();
    }

    public static String asString(Environment env) {
        return new StringJoiner(", ", "", "").add("authHost='" + getAuthHost(env) + "'").add("accountsHost='" + env.accountsHost() + "'")
            .add("sessionHost='" + env.sessionHost() + "'").add("servicesHost='" + env.servicesHost() + "'").add("name='" + env.name() + "'")
            .toString();
    }

    // ------------------------------------------------------------------ session service

    public static void joinServer(MinecraftSessionService service, GameProfile profile, String token, String serverId)
            throws AuthenticationException {
        service.joinServer(profile.getId(), token, serverId);
    }

    public static void joinServer(YggdrasilMinecraftSessionService service, GameProfile profile, String token, String serverId)
            throws AuthenticationException {
        joinServer((MinecraftSessionService) service, profile, token, serverId);
    }

    /** 4.0.43: the response's id with the asking profile's name and the response's properties; null when not joined */
    public static GameProfile hasJoinedServer(MinecraftSessionService service, GameProfile user, String serverId, InetAddress address)
            throws AuthenticationUnavailableException {
        ProfileResult result = service.hasJoinedServer(user.getName(), serverId, address);
        if (result == null) {
            return null;
        }
        GameProfile answer = new GameProfile(result.profile().getId(), user.getName());
        answer.getProperties().putAll(result.profile().getProperties());
        return answer;
    }

    public static GameProfile hasJoinedServer(YggdrasilMinecraftSessionService service, GameProfile user, String serverId,
            InetAddress address) throws AuthenticationUnavailableException {
        return hasJoinedServer((MinecraftSessionService) service, user, serverId, address);
    }

    /** 4.0.43: a profile without an id as it is; otherwise the session server's profile, or the given one when none came back */
    public static GameProfile fillProfileProperties(MinecraftSessionService service, GameProfile profile, boolean requireSecure) {
        UUID id = getId(profile);
        if (id == null) {
            return profile;
        }
        ProfileResult result = service.fetchProfile(id, requireSecure);
        return result == null ? profile : result.profile();
    }

    public static GameProfile fillProfileProperties(YggdrasilMinecraftSessionService service, GameProfile profile, boolean requireSecure) {
        return fillProfileProperties((MinecraftSessionService) service, profile, requireSecure);
    }

    /** a subclass's super.fillGameProfile(profile, requireSecure): the session server's profile, the given one on failure */
    public static GameProfile fillGameProfile(YggdrasilMinecraftSessionService service, GameProfile profile, boolean requireSecure) {
        return fillProfileProperties((MinecraftSessionService) service, profile, requireSecure);
    }

    /** the key set of 4.0.43's YggdrasilMinecraftSessionService(authService, env) */
    public static ServicesKeySet servicesKeySet(YggdrasilAuthenticationService service) {
        return service.getServicesKeySet();
    }

    /** the proxy of a 4.0.43 constructor's authentication service */
    public static Proxy proxy(YggdrasilAuthenticationService service) {
        return service.getProxy();
    }

    // ------------------------------------------------------------------ authentication service / repository

    /** new YggdrasilAuthenticationService(proxy, clientToken): the token served legacy login only */
    public static YggdrasilAuthenticationService authenticationService(Proxy proxy, String clientToken) {
        return new YggdrasilAuthenticationService(proxy);
    }

    /** new YggdrasilAuthenticationService(proxy, clientToken, environment) */
    public static YggdrasilAuthenticationService authenticationService(Proxy proxy, String clientToken, Environment environment) {
        return new YggdrasilAuthenticationService(proxy, environment);
    }

    /** a subclass's super(proxy, clientToken[, environment]): the token dropped */
    public static String dropToken(String clientToken) {
        return clientToken;
    }

    /** new YggdrasilGameProfileRepository(authService, environment) */
    public static YggdrasilGameProfileRepository profileRepository(YggdrasilAuthenticationService service, Environment environment) {
        return new YggdrasilGameProfileRepository(service.getProxy(), environment);
    }

    /** findProfilesByNames(names, agent, callback): 5.0.47 looks up game profiles, what a 4.0.43 MINECRAFT lookup asked */
    public static void findProfilesByNames(GameProfileRepository repository, String[] names, AuthlibAgent agent, ProfileLookupCallback callback) {
        if (repository instanceof CbLibProfileRepositoryOld old) {
            old.findProfilesByNames(names, agent, callback);
        } else {
            repository.findProfilesByNames(names, callback);
        }
    }

    public static void findProfilesByNames(YggdrasilGameProfileRepository repository, String[] names, AuthlibAgent agent,
            ProfileLookupCallback callback) {
        findProfilesByNames((GameProfileRepository) repository, names, agent, callback);
    }

    /** 5.0.47's findProfilesByNames(names, callback) synthesized in a 4.0.43 repository: its own method, asked for MINECRAFT */
    public static void findProfilesByNamesBridge(CbLibProfileRepositoryOld self, String[] names, ProfileLookupCallback callback) {
        self.findProfilesByNames(names, AuthlibAgent.MINECRAFT, callback);
    }

    /**
     * a repository subclass's super.findProfilesByNames(names, agent, callback): 5.0.47's lookup (the synthesized new shape is
     * guarded, so this call lands in YggdrasilGameProfileRepository's own when it comes from the subclass's old method)
     */
    public static void superFindProfilesByNames(YggdrasilGameProfileRepository repository, String[] names, AuthlibAgent agent,
            ProfileLookupCallback callback) {
        repository.findProfilesByNames(names, callback);
    }

    /** YggdrasilServicesKeyInfo.get(url, authService): 4.0.43 fetched the keys unauthenticated over the service's proxy */
    public static com.mojang.authlib.minecraft.client.MinecraftClient unauthenticatedClient(YggdrasilAuthenticationService service) {
        return com.mojang.authlib.minecraft.client.MinecraftClient.unauthenticated(service.getProxy());
    }

    /** a 4.0.43 repository lambda (GameProfileRepository was functional), as a 5.0.47 repository */
    public static GameProfileRepository repositoryFromLambda(CbLibProfileRepositoryOld old) {
        return (names, callback) -> old.findProfilesByNames(names, AuthlibAgent.MINECRAFT, callback);
    }

    /** the profile 4.0.43's repository handed onProfileLookupFailed: name-only */
    public static GameProfile failedProfile(String name) {
        return gameProfile(null, name);
    }

    /** a mod's call of the 4.0.43 failure shape: a converted 4.0.43 callback takes the profile itself, any other its name */
    public static void onProfileLookupFailed(ProfileLookupCallback callback, GameProfile profile, Exception exception) {
        if (callback instanceof CbLibProfileFailedOld old) {
            old.onProfileLookupFailed(profile, exception);
        } else {
            callback.onProfileLookupFailed(profile.getName(), exception);
        }
    }

    /** 5.0.47's onProfileLookupFailed(name, exception) synthesized in a 4.0.43 callback: its own method with the name-only profile */
    public static void failedBridge(CbLibProfileFailedOld self, String name, Exception exception) {
        self.onProfileLookupFailed(failedProfile(name), exception);
    }

    // ------------------------------------------------------------------ a 4.0.43 session service of the mod's own

    /**
     * 5.0.47's joinServer(id, token, serverId) synthesized in a converted 4.0.43 session service. 4.0.43's caller handed the
     * player's whole profile; 5.0.47's hands only its id, so the service's method gets a profile of that id with an empty name
     * (the one value 5.0.47's caller does not have)
     */
    public static void joinServerBridge(CbLibSessionOld self, UUID id, String token, String serverId) throws AuthenticationException {
        self.joinServer(new GameProfile(id, ""), token, serverId);
    }

    /** 5.0.47's hasJoinedServer(name, serverId, address) in a converted service: its own method for the name-only profile */
    public static ProfileResult hasJoinedServerBridge(CbLibSessionOld self, String name, String serverId, InetAddress address)
            throws AuthenticationUnavailableException {
        GameProfile answer = self.hasJoinedServer(gameProfile(null, name), serverId, address);
        return answer == null ? null : new ProfileResult(answer);
    }

    /** 5.0.47's fetchProfile(id, requireSecure) in a converted service: its own fillProfileProperties for a profile of that id */
    public static ProfileResult fetchProfileBridge(CbLibSessionOld self, UUID id, boolean requireSecure) {
        GameProfile answer = self.fillProfileProperties(new GameProfile(id, ""), requireSecure);
        return answer == null ? null : new ProfileResult(answer);
    }

    // ------------------------------------------------------------------ getAuthenticationService

    private static final Map<YggdrasilMinecraftSessionService, YggdrasilAuthenticationService> AUTH_SERVICES =
        Collections.synchronizedMap(new WeakHashMap<>());

    /** after a mod subclass's super(authService, env): 4.0.43's session service kept the authentication service */
    public static void rememberAuthService(YggdrasilMinecraftSessionService session, YggdrasilAuthenticationService service) {
        AUTH_SERVICES.put(session, service);
    }

    /**
     * YggdrasilMinecraftSessionService.getAuthenticationService(): the service a mod-built session service was constructed
     * with; 5.0.47 keeps none, so for a session service the game built there is no 4.0.43 answer (an IllegalStateException
     * saying so, not a made-up service)
     */
    public static YggdrasilAuthenticationService getAuthenticationService(YggdrasilMinecraftSessionService session) {
        YggdrasilAuthenticationService service = AUTH_SERVICES.get(session);
        if (service == null) {
            throw new IllegalStateException("authlib 5.0.47's session service does not keep the authentication service that built it");
        }
        return service;
    }

    // ------------------------------------------------------------------ HTTP helpers

    private static Proxy proxyOf(HttpAuthenticationService service) {
        return service.getProxy();
    }

    /** HttpAuthenticationService.createUrlConnection(url) */
    public static HttpURLConnection createUrlConnection(HttpAuthenticationService service, URL url) throws IOException {
        Objects.requireNonNull(url, "The validated object is null");
        LOGGER.debug("Opening connection to " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxyOf(service));
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }

    /** HttpAuthenticationService.performPostRequest(url, post, contentType) */
    public static String performPostRequest(HttpAuthenticationService service, URL url, String post, String contentType) throws IOException {
        Objects.requireNonNull(url, "The validated object is null");
        Objects.requireNonNull(post, "The validated object is null");
        Objects.requireNonNull(contentType, "The validated object is null");
        HttpURLConnection connection = createUrlConnection(service, url);
        byte[] postAsBytes = post.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
        connection.setRequestProperty("Content-Length", "" + postAsBytes.length);
        connection.setDoOutput(true);
        LOGGER.debug("Writing POST data to " + url + ": " + post);
        try (OutputStream out = connection.getOutputStream()) {
            out.write(postAsBytes);
        }
        return readResponse(connection, url);
    }

    /** HttpAuthenticationService.performGetRequest(url) */
    public static String performGetRequest(HttpAuthenticationService service, URL url) throws IOException {
        return performGetRequest(service, url, null);
    }

    /** HttpAuthenticationService.performGetRequest(url, authentication) */
    public static String performGetRequest(HttpAuthenticationService service, URL url, String authentication) throws IOException {
        Objects.requireNonNull(url, "The validated object is null");
        HttpURLConnection connection = createUrlConnection(service, url);
        if (authentication != null) {
            connection.setRequestProperty("Authorization", authentication);
        }
        return readResponse(connection, url);
    }

    private static String readResponse(HttpURLConnection connection, URL url) throws IOException {
        LOGGER.debug("Reading data from " + url);
        InputStream in = null;
        try {
            in = connection.getInputStream();
            String result = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
            LOGGER.debug("Response: " + result);
            return result;
        } catch (IOException e) {
            closeQuietly(in);
            in = connection.getErrorStream();
            if (in == null) {
                LOGGER.debug("Request failed", e);
                throw e;
            }
            LOGGER.debug("Reading error page from " + url);
            String result = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
            LOGGER.debug("Response: " + result);
            return result;
        } finally {
            closeQuietly(in);
        }
    }

    private static void closeQuietly(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ignored) {
                // IOUtils.closeQuietly
            }
        }
    }
}

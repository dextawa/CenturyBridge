package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.SignatureState;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.TextureUrlChecker;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * authlib 5.0.47 (1.20.2) members that authlib 6.0.52 (1.20.3) changed or removed, with 5.0.47's semantics read off its
 * bytecode: the library front of this step (core/segments/damage-lib/1.20.2__1.20.3.tsv).
 * <ul>
 * <li>Environment lost accountsHost (5.0.47's profile repository looked names up there; 6.0.52's uses servicesHost):
 * the 4-argument constructor builds 6.0.52's environment and remembers its accounts host for accountsHost(),
 * which answers 5.0.47's hosts for its own PROD / STAGING environments and otherwise the system property 5.0.47's
 * EnvironmentParser read.</li>
 * <li>MinecraftSessionService.getTextures(profile, requireSecure) became getPackedTextures + unpackTextures; 5.0.47
 * took the profile's first "textures" property, checked its signature when asked (InsecurePublicKeyException) and
 * answered the payload's textures as a map (empty when absent, undecodable or from a blocked domain). A converted
 * 5.0.47 implementation gets 6.0.52's two shapes (wiring: adapterHooks) answered by its own method.</li>
 * <li>UserApiService.properties() answered what YggdrasilUserApiService fetched when it was built (a service that failed
 * to fetch was never built: Minecraft fell back to UserApiService.OFFLINE); 6.0.52's fetchProperties() fetches.</li>
 * <li>InsecurePublicKeyException.MissingException() carried "No public key information found".</li>
 * </ul>
 */
public final class CbLibAuthlib {
    private CbLibAuthlib() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CbLibAuthlib.class);
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    private static final Map<Environment, String> ACCOUNTS_HOSTS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<UserApiService, UserApiService.UserProperties> PROPERTIES = Collections.synchronizedMap(new WeakHashMap<>());
    /** the profile each packed property handed out by a converted 5.0.47 session service came from */
    private static final Map<Property, GameProfile> PACKED = Collections.synchronizedMap(new WeakHashMap<>());

    /** EnvironmentParser.PROP_ACCOUNT_HOST */
    public static String PROP_ACCOUNT_HOST() {
        return "minecraft.api.account.host";
    }

    /** new Environment(accountsHost, sessionHost, servicesHost, name) */
    public static Environment environment(String accountsHost, String sessionHost, String servicesHost, String name) {
        Environment env = new Environment(sessionHost, servicesHost, name);
        ACCOUNTS_HOSTS.put(env, accountsHost);
        return env;
    }

    /** Environment.accountsHost() */
    public static String accountsHost(Environment env) {
        String host = ACCOUNTS_HOSTS.get(env);
        if (host != null) {
            return host;
        }
        if (env.equals(YggdrasilEnvironment.PROD.getEnvironment())) {
            return "https://api.mojang.com";
        }
        if (env.equals(YggdrasilEnvironment.STAGING.getEnvironment())) {
            return "https://api-staging.mojang.com";
        }
        return System.getProperty("minecraft.api.account.host");
    }

    /** the message 5.0.47's MissingException() passed up (a mod subclass's super()) */
    public static String missingPublicKey() {
        return "No public key information found";
    }

    /** new InsecurePublicKeyException.MissingException() */
    public static InsecurePublicKeyException.MissingException missingException() {
        return new InsecurePublicKeyException.MissingException(missingPublicKey());
    }

    /**
     * MinecraftSessionService.getTextures(profile, requireSecure) on any service: a converted 5.0.47 implementation answers
     * with its own method; any other through 6.0.52's packed / unpacked textures (for 6.0.52's Yggdrasil service the same
     * property, signature check and payload 5.0.47's read)
     */
    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(MinecraftSessionService service,
            GameProfile profile, boolean requireSecure) throws InsecurePublicKeyException {
        if (service instanceof CbLibSessionOld old) {
            return old.getTextures(profile, requireSecure);
        }
        Property textures = service.getPackedTextures(profile);
        if (textures == null) {
            return new HashMap<>();
        }
        if (requireSecure) {
            service.getSecurePropertyValue(textures);
        }
        return asMap(service.unpackTextures(textures));
    }

    /** YggdrasilMinecraftSessionService.getTextures(profile, requireSecure) */
    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(YggdrasilMinecraftSessionService service,
            GameProfile profile, boolean requireSecure) throws InsecurePublicKeyException {
        return getTextures((MinecraftSessionService) service, profile, requireSecure);
    }

    /**
     * a mod subclass's super.getTextures(profile, requireSecure): 5.0.47's YggdrasilMinecraftSessionService body itself, which
     * calls no overridable texture method (the subclass's override, reached through 6.0.52's shapes, must not answer its own
     * super call): the profile's first "textures" property, its secure value when asked, the Base64 JSON payload decoded
     * with a UUID-aware Gson; an empty map when absent, undecodable or naming a blocked domain
     */
    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> superGetTextures(YggdrasilMinecraftSessionService service,
            GameProfile profile, boolean requireSecure) throws InsecurePublicKeyException {
        Property textures = Iterables.getFirst(profile.getProperties().get("textures"), null);
        if (textures == null) {
            return new HashMap<>();
        }
        String value = requireSecure ? service.getSecurePropertyValue(textures) : textures.value();
        MinecraftTexturesPayload payload;
        try {
            payload = GSON.fromJson(new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8), MinecraftTexturesPayload.class);
        } catch (JsonParseException | IllegalArgumentException e) {
            LOGGER.error("Could not decode textures payload", e);
            return new HashMap<>();
        }
        if (payload == null || payload.textures() == null) {
            return new HashMap<>();
        }
        for (MinecraftProfileTexture texture : payload.textures().values()) {
            if (!TextureUrlChecker.isAllowedTextureDomain(texture.getUrl())) {
                LOGGER.error("Textures payload contains blocked domain: {}", texture.getUrl());
                return new HashMap<>();
            }
        }
        return payload.textures();
    }

    private static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> asMap(MinecraftProfileTextures unpacked) {
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> out = new HashMap<>();
        if (unpacked.skin() != null) {
            out.put(MinecraftProfileTexture.Type.SKIN, unpacked.skin());
        }
        if (unpacked.cape() != null) {
            out.put(MinecraftProfileTexture.Type.CAPE, unpacked.cape());
        }
        if (unpacked.elytra() != null) {
            out.put(MinecraftProfileTexture.Type.ELYTRA, unpacked.elytra());
        }
        return out;
    }

    /**
     * getPackedTextures(profile) synthesized in a converted 5.0.47 session service. 5.0.47's skin manager asked the service
     * about any profile (getTextures); 6.0.52's asks for the packed property first, skips a profile without one, and
     * unpacks the property alone. So every profile gets a property -- a copy of its own "textures" one, or an empty one
     * naming the profile -- remembered with the profile it came from, for {@link #unpackTextures} to hand the service's
     * own method.
     */
    public static Property packedTextures(CbLibSessionOld self, GameProfile profile) {
        Property real = Iterables.getFirst(profile.getProperties().get("textures"), null);
        Property packed = real != null ? new Property(real.name(), real.value(), real.signature())
            : new Property("textures", "", "centurybridge:" + profile.getId());
        PACKED.put(packed, profile);
        return packed;
    }

    /**
     * unpackTextures(property) synthesized in a converted 5.0.47 session service: its getTextures for the profile the
     * property came from (a property it never handed out: a profile carrying just that property); the signature state is
     * what its secure read says (MissingException: unsigned, any other InsecurePublicKeyException: invalid)
     */
    public static MinecraftProfileTextures unpackTextures(CbLibSessionOld self, Property packed) {
        GameProfile profile = PACKED.get(packed);
        if (profile == null) {
            profile = new GameProfile(new UUID(0L, 0L), "");
            profile.getProperties().put(packed.name(), packed);
        }
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map;
        SignatureState state;
        try {
            map = self.getTextures(profile, true);
            state = SignatureState.SIGNED;
        } catch (InsecurePublicKeyException e) {
            state = e instanceof InsecurePublicKeyException.MissingException ? SignatureState.UNSIGNED : SignatureState.INVALID;
            try {
                map = self.getTextures(profile, false);
            } catch (InsecurePublicKeyException again) {
                return MinecraftProfileTextures.EMPTY;
            }
        }
        if (map == null || map.isEmpty()) {
            return MinecraftProfileTextures.EMPTY;
        }
        return new MinecraftProfileTextures(map.get(MinecraftProfileTexture.Type.SKIN), map.get(MinecraftProfileTexture.Type.CAPE),
            map.get(MinecraftProfileTexture.Type.ELYTRA), state);
    }

    /**
     * UserApiService.properties(): 6.0.52's Yggdrasil service itself answers what it fetched first, as 5.0.47's answered what
     * it fetched when built (a failed fetch: the offline properties Minecraft fell back to); any other service (a converted
     * implementation's own properties(), reached through fetchProperties) is asked each time
     */
    public static UserApiService.UserProperties properties(UserApiService service) {
        boolean cached = service.getClass() == YggdrasilUserApiService.class;
        if (cached) {
            UserApiService.UserProperties known = PROPERTIES.get(service);
            if (known != null) {
                return known;
            }
        }
        UserApiService.UserProperties fetched;
        try {
            fetched = service.fetchProperties();
        } catch (AuthenticationException e) {
            fetched = UserApiService.OFFLINE_PROPERTIES;
        }
        if (cached) {
            PROPERTIES.put(service, fetched);
        }
        return fetched;
    }

    /** YggdrasilUserApiService.properties(), also a mod subclass's super.properties() (fetchProperties is guarded) */
    public static UserApiService.UserProperties properties(YggdrasilUserApiService service) {
        return properties((UserApiService) service);
    }
}

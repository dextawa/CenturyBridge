package cb.slib;

import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import net.fabricmc.api.ModInitializer;

/**
 * A 1.20.2-era mod (compiled against authlib 5.0.47) for slib's library bridges of the 1.20.2 -> 1.20.3 step.
 * Each check prints "CB-SLIB ok|FAIL ..."; the last line is "CB-SLIB DONE passed/checks".
 */
public class SlibAuthlib3Test implements ModInitializer {
    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-SLIB " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) FAILS.add(what);
    }

    static void run(String what, Runnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            check(false, what + " threw " + e);
            e.printStackTrace(System.out);
        }
    }

    static final String SKIN = "http://textures.minecraft.net/texture/cbslibskin";
    static final String CAPE = "http://textures.minecraft.net/texture/cbslibcape";

    static Property texturesProperty() {
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + SKIN + "\"}}}";
        return new Property("textures", Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8)));
    }

    static class MyMissing extends InsecurePublicKeyException.MissingException {
    }

    /** a subclass of the Yggdrasil service whose override adds a cape to what super answers */
    static class MySession extends YggdrasilMinecraftSessionService {
        MySession() {
            super(ServicesKeySet.EMPTY, Proxy.NO_PROXY, YggdrasilEnvironment.PROD.getEnvironment());
        }

        @Override
        public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure)
                throws InsecurePublicKeyException {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> m = new HashMap<>(super.getTextures(profile, requireSecure));
            m.put(MinecraftProfileTexture.Type.CAPE, new MinecraftProfileTexture(CAPE, null));
            return m;
        }
    }

    /** a session service of its own: a skin named after the profile, never signed */
    static class MyService implements MinecraftSessionService {
        @Override
        public void joinServer(UUID id, String token, String serverId) throws AuthenticationException {
        }

        @Override
        public ProfileResult hasJoinedServer(String name, String serverId, InetAddress address) {
            return null;
        }

        @Override
        public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure)
                throws InsecurePublicKeyException {
            if (requireSecure) throw new InsecurePublicKeyException.MissingException();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> m = new HashMap<>();
            m.put(MinecraftProfileTexture.Type.SKIN, new MinecraftProfileTexture(SKIN + "-" + profile.getName(), null));
            return m;
        }

        @Override
        public ProfileResult fetchProfile(UUID id, boolean secure) {
            return null;
        }

        @Override
        public String getSecurePropertyValue(Property property) throws InsecurePublicKeyException {
            throw new InsecurePublicKeyException.MissingException();
        }
    }

    static final UserApiService.UserProperties MINE = new UserApiService.UserProperties(Set.of(UserApiService.UserFlag.CHAT_ALLOWED), Map.of());

    static class MyUserApi implements UserApiService {
        @Override
        public UserProperties properties() {
            return MINE;
        }

        @Override
        public boolean isBlockedPlayer(UUID id) {
            return false;
        }

        @Override
        public void refreshBlockList() {
        }

        @Override
        public TelemetrySession newTelemetrySession(Executor executor) {
            return null;
        }

        @Override
        public KeyPairResponse getKeyPair() {
            return null;
        }

        @Override
        public void reportAbuse(AbuseReportRequest request) {
        }

        @Override
        public boolean canSendReports() {
            return false;
        }

        @Override
        public AbuseReportLimits getAbuseReportLimits() {
            return null;
        }
    }

    static Object call(Object target, String name, Class<?> type, Object arg) throws ReflectiveOperationException {
        for (Class<?> c = target.getClass(); c != null; c = c.getSuperclass()) {
            try {
                java.lang.reflect.Method m = c.getMethod(name, type);
                return m.invoke(target, arg);
            } catch (NoSuchMethodException ignored) {
            }
        }
        throw new NoSuchMethodException(name);
    }

    @Override
    public void onInitialize() {
        UUID id = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");
        run("Environment", () -> {
            Environment env = new Environment("https://accounts.example", "https://session.example", "https://services.example", "CUSTOM");
            check("https://accounts.example".equals(env.accountsHost()) && "https://session.example".equals(env.sessionHost())
                && "https://services.example".equals(env.servicesHost()) && "CUSTOM".equals(env.name()), "Environment keeps all four values");
            check("https://api.mojang.com".equals(YggdrasilEnvironment.PROD.getEnvironment().accountsHost()), "PROD's accounts host is 5.0.47's");
        });
        run("MissingException", () -> {
            check("No public key information found".equals(new InsecurePublicKeyException.MissingException().getMessage()), "MissingException() message");
            check("No public key information found".equals(new MyMissing().getMessage()), "a MissingException subclass's message");
        });
        run("Yggdrasil getTextures", () -> {
            MinecraftSessionService svc = new MySession();
            GameProfile bare = new GameProfile(id, "Notch");
            try {
                YggdrasilMinecraftSessionService ygg = new YggdrasilMinecraftSessionService(ServicesKeySet.EMPTY, Proxy.NO_PROXY,
                    YggdrasilEnvironment.PROD.getEnvironment()) {
                };
                check(ygg.getTextures(bare, false).isEmpty(), "no textures property: an empty map");
                GameProfile p = new GameProfile(id, "Notch");
                p.getProperties().put("textures", texturesProperty());
                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> m = ygg.getTextures(p, false);
                check(m.size() == 1 && SKIN.equals(m.get(MinecraftProfileTexture.Type.SKIN).getUrl()), "an unsigned payload decodes when not secure");
                try {
                    ygg.getTextures(p, true);
                    check(false, "secure read of an unsigned property throws");
                } catch (InsecurePublicKeyException.MissingException e) {
                    check(true, "secure read of an unsigned property throws MissingException");
                }
                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> sub = svc.getTextures(p, false);
                check(sub.size() == 2 && SKIN.equals(sub.get(MinecraftProfileTexture.Type.SKIN).getUrl()),
                    "a subclass's super.getTextures decodes the payload and its override adds to it");
                Object packed = call(svc, "getPackedTextures", GameProfile.class, p);
                Object unpacked = call(svc, "unpackTextures", Property.class, packed);
                Object cape = unpacked.getClass().getMethod("cape").invoke(unpacked);
                check(cape != null && CAPE.equals(((MinecraftProfileTexture) cape).getUrl()), "1.20.3's unpack reaches the subclass's override");
            } catch (InsecurePublicKeyException | ReflectiveOperationException e) {
                check(false, "getTextures " + e);
            }
        });
        run("own session service", () -> {
            MyService svc = new MyService();
            GameProfile bare = new GameProfile(id, "Notch");
            try {
                Object packed = call(svc, "getPackedTextures", GameProfile.class, bare);
                check(packed != null, "a profile without textures still gets a packed property from a 5.0.47 service");
                Object unpacked = call(svc, "unpackTextures", Property.class, packed);
                Object skin = unpacked.getClass().getMethod("skin").invoke(unpacked);
                Object state = unpacked.getClass().getMethod("signatureState").invoke(unpacked);
                check(skin != null && (SKIN + "-Notch").equals(((MinecraftProfileTexture) skin).getUrl()), "unpack answers the service's own textures for that profile");
                check("UNSIGNED".equals(String.valueOf(state)), "a MissingException on the secure read is unsigned");
                MinecraftSessionService asIface = svc;
                check((SKIN + "-Notch").equals(asIface.getTextures(bare, false).get(MinecraftProfileTexture.Type.SKIN).getUrl()),
                    "a mod's own call reaches its service's method");
            } catch (InsecurePublicKeyException | ReflectiveOperationException e) {
                check(false, "own service " + e);
            }
        });
        run("UserApiService", () -> {
            check(UserApiService.OFFLINE.properties() == UserApiService.OFFLINE_PROPERTIES, "OFFLINE.properties() is OFFLINE_PROPERTIES");
            MyUserApi api = new MyUserApi();
            UserApiService asIface = api;
            check(asIface.properties() == MINE, "properties() on a mod's own service answers its method");
            try {
                Object fetched = api.getClass().getMethod("fetchProperties").invoke(api);
                check(fetched == MINE, "1.20.3's fetchProperties reaches the mod's properties()");
            } catch (ReflectiveOperationException e) {
                check(false, "fetchProperties " + e);
            }
        });
        System.out.println("CB-SLIB DONE " + (checks - FAILS.size()) + "/" + checks);
    }
}

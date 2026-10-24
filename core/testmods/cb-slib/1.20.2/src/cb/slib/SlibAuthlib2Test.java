package cb.slib;

import com.mojang.authlib.Agent;
import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import com.mojang.util.UUIDTypeAdapter;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;

/**
 * A 1.20.1-era mod (compiled against authlib 4.0.43) for slib's step-1 library bridges. Each check prints
 * "CB-SLIB ok|FAIL ..."; the last line is "CB-SLIB DONE passed/checks".
 */
public class SlibAuthlib2Test implements ModInitializer {
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

    static class Failed implements ProfileLookupCallback {
        GameProfile failed;

        @Override
        public void onProfileLookupSucceeded(GameProfile profile) {
        }

        @Override
        public void onProfileLookupFailed(GameProfile profile, Exception exception) {
            failed = profile;
        }
    }

    @Override
    public void onInitialize() {
        UUID id = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");
        run("GameProfile", () -> {
            GameProfile byName = new GameProfile(null, "Notch");
            check(byName.getId() == null && "Notch".equals(byName.getName()), "a name-only profile keeps a null id");
            check(!byName.isComplete() && new GameProfile(id, "Notch").isComplete(), "isComplete: a real id and a name");
            check(!byName.isLegacy(), "isLegacy is false");
            try {
                new GameProfile(null, " ");
                check(false, "both blank is refused");
            } catch (IllegalArgumentException e) {
                check("Name and ID cannot both be blank".equals(e.getMessage()), "both blank is refused with 4.0.43's message");
            }
        });
        run("Property", () -> {
            Property p = new Property("textures", "v", "s");
            check("textures".equals(p.getName()) && "v".equals(p.getValue()) && "s".equals(p.getSignature()), "Property getters");
        });
        run("UUIDTypeAdapter", () -> {
            String s = UUIDTypeAdapter.fromUUID(id);
            check("069a79f444e94726a5befca90e38aaf5".equals(s) && id.equals(UUIDTypeAdapter.fromString(s)), "fromUUID / fromString");
        });
        run("Environment", () -> {
            Environment env = Environment.create("https://auth.example", "https://accounts.example", "https://session.example",
                "https://services.example", "CUSTOM");
            check("https://auth.example".equals(env.getAuthHost()) && "https://accounts.example".equals(env.getAccountsHost())
                && "https://session.example".equals(env.getSessionHost()) && "https://services.example".equals(env.getServicesHost())
                && "CUSTOM".equals(env.getName()), "Environment.create and its getters");
            check(env.asString().startsWith("authHost='https://auth.example', accountsHost='https://accounts.example'"), "asString");
            check("https://authserver.mojang.com".equals(YggdrasilEnvironment.PROD.getEnvironment().getAuthHost()), "PROD's auth host");
        });
        run("services", () -> {
            YggdrasilAuthenticationService auth = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "token");
            check(auth.getProxy() == Proxy.NO_PROXY, "YggdrasilAuthenticationService(proxy, clientToken)");
            check(Agent.MINECRAFT.getName().equals("Minecraft") && Agent.MINECRAFT.getVersion() == 1, "Agent.MINECRAFT");
        });
        run("profile lookup", () -> {
            GameProfileRepository repo = (names, agent, callback) -> {
                for (String n : names) callback.onProfileLookupFailed(new GameProfile(null, n), new RuntimeException(agent.getName()));
            };
            Failed cb = new Failed();
            repo.findProfilesByNames(new String[] {"Jeb"}, Agent.MINECRAFT, cb);
            check(cb.failed != null && "Jeb".equals(cb.failed.getName()), "a lambda repository and the old failure shape");
            try {
                GameProfileRepository.class.getMethod("findProfilesByNames", String[].class, ProfileLookupCallback.class)
                    .invoke(repo, new String[] {"Dinnerbone"}, cb);
                check("Dinnerbone".equals(cb.failed.getName()), "1.20.2's (names, callback) reaches the lambda");
                ProfileLookupCallback.class.getMethod("onProfileLookupFailed", String.class, Exception.class).invoke(cb, "Grumm", new RuntimeException());
                check("Grumm".equals(cb.failed.getName()) && cb.failed.getId() == null, "1.20.2's (name, exception) reaches the callback");
            } catch (ReflectiveOperationException e) {
                check(false, "new shapes " + e);
            }
        });
        System.out.println("CB-SLIB DONE " + (checks - FAILS.size()) + "/" + checks);
    }
}

package cb.slib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;

/**
 * A 1.21.8-era mod (compiled against authlib 6.0.58) for slib's library bridges of the 1.21.8 -> 1.21.9 step.
 * Each check prints "CB-SLIB ok|FAIL ..."; the last line is "CB-SLIB DONE passed/checks".
 */
public class SlibAuthlibTest implements ModInitializer {
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

    static class MyMap extends PropertyMap {
    }

    static class Callback implements ProfileLookupCallback {
        GameProfile got;

        @Override
        public void onProfileLookupSucceeded(GameProfile profile) {
            got = profile;
        }

        @Override
        public void onProfileLookupFailed(String name, Exception ex) {
        }
    }

    @Override
    public void onInitialize() {
        UUID id = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");
        run("GameProfile accessors", () -> {
            GameProfile p = new GameProfile(id, "Notch");
            check(id.equals(p.getId()) && "Notch".equals(p.getName()), "getId/getName answer the constructor's values");
            p.getProperties().put("textures", new Property("textures", "v", "s"));
            check(p.getProperties().get("textures").size() == 1, "a new profile's properties are mutable");
            GameProfile q = new GameProfile(id, "Notch");
            check(q.getProperties().isEmpty(), "two profiles do not share one property map");
        });
        run("GameProfile null id", () -> {
            try {
                new GameProfile(null, "x");
                check(false, "a null id is refused");
            } catch (NullPointerException e) {
                check("Profile ID must not be null".equals(e.getMessage()), "a null id is refused with 6.0.58's message");
            }
        });
        run("PropertyMap", () -> {
            PropertyMap m = new PropertyMap();
            m.put("a", new Property("a", "1"));
            check(m.size() == 1 && m.containsKey("a"), "new PropertyMap() is mutable");
            MyMap mm = new MyMap();
            mm.put("b", new Property("b", "2"));
            check(mm.size() == 1 && mm instanceof PropertyMap, "a PropertyMap subclass is mutable");
        });
        run("GameProfile.Serializer", () -> {
            Gson gson = new GsonBuilder().registerTypeAdapter(GameProfile.class, new GameProfile.Serializer()).create();
            JsonObject json = gson.toJsonTree(new GameProfile(id, "Notch")).getAsJsonObject();
            check(id.toString().equals(json.get("id").getAsString()) && "Notch".equals(json.get("name").getAsString())
                && json.size() == 2, "serialize writes id and name");
            GameProfile back = gson.fromJson(json, GameProfile.class);
            check(id.equals(back.getId()) && "Notch".equals(back.getName()), "deserialize reads id and name");
            back.getProperties().put("x", new Property("x", "y"));
            check(back.getProperties().size() == 1, "a deserialized profile's properties are mutable");
        });
        run("Environment", () -> {
            Environment env = new Environment("https://session.example", "https://services.example", "CUSTOM");
            check("https://session.example".equals(env.sessionHost()) && "https://services.example".equals(env.servicesHost())
                && "CUSTOM".equals(env.name()), "Environment keeps its hosts and name");
            try {
                Object profiles = env.getClass().getMethod("profilesHost").invoke(env);
                check("https://services.example".equals(profiles), "profile lookups go to the services host (as 6.0.58's repository did)");
            } catch (ReflectiveOperationException e) {
                check(false, "profilesHost " + e);
            }
        });
        run("MinecraftProfilePropertiesResponse.toProfile", () -> {
            PropertyMap props = new PropertyMap();
            props.put("textures", new Property("textures", "v"));
            MinecraftProfilePropertiesResponse r = new MinecraftProfilePropertiesResponse(id, "Notch", props, Set.of());
            GameProfile p = r.toProfile();
            check(id.equals(p.getId()) && "Notch".equals(p.getName()) && p.getProperties().get("textures").size() == 1,
                "toProfile copies id, name and properties");
            p.getProperties().put("more", new Property("more", "1"));
            check(p.getProperties() != r.properties() && r.properties().size() == 1, "toProfile's properties are its own mutable copy");
        });
        run("ProfileLookupCallback", () -> {
            Callback cb = new Callback();
            try {
                cb.getClass().getMethod("onProfileLookupSucceeded", String.class, UUID.class).invoke(cb, "Notch", id);
                check(cb.got != null && id.equals(cb.got.getId()) && "Notch".equals(cb.got.getName()),
                    "the repository's (name, id) call reaches the mod's profile method");
            } catch (ReflectiveOperationException e) {
                check(false, "(name, id) shape " + e);
            }
            GameProfile p = new GameProfile(id, "Notch");
            ProfileLookupCallback asIface = cb;
            asIface.onProfileLookupSucceeded(p);
            check(cb.got == p, "a mod's own call of the profile shape hands the same profile");
        });
        System.out.println("CB-SLIB DONE " + (checks - FAILS.size()) + "/" + checks);
    }
}

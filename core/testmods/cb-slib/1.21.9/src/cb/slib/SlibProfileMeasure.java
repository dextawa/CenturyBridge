package cb.slib;

import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.JsonOps;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;

/**
 * Measures what a 1.21.8-era mod meets when it mutates a property map vanilla made (decoded by vanilla's own
 * ExtraCodecs.PROPERTY_MAP, class_5699.field_40725), next to one it made itself. Prints "CB-SLIB MEASURE ...".
 */
public class SlibProfileMeasure implements ModInitializer {
    static void attempt(String what, Runnable r) {
        try {
            r.run();
            System.out.println("CB-SLIB MEASURE " + what + ": succeeded");
        } catch (Throwable e) {
            System.out.println("CB-SLIB MEASURE " + what + ": threw " + e);
        }
    }

    @Override
    public void onInitialize() {
        PropertyMap vanilla = net.minecraft.class_5699.field_40725
            .parse(JsonOps.INSTANCE, JsonParser.parseString("[{\"name\":\"textures\",\"value\":\"v\"}]")).getOrThrow();
        attempt("put on a vanilla-decoded PropertyMap", () -> vanilla.put("textures", new Property("textures", "w")));
        attempt("removeAll on a vanilla-decoded PropertyMap", () -> vanilla.removeAll("textures"));
        GameProfile own = new GameProfile(UUID.randomUUID(), "Own");
        attempt("put on a mod-made profile's properties", () -> own.getProperties().put("textures", new Property("textures", "w")));
        System.out.println("CB-SLIB DONE measure");
    }
}

package cb.slib;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.KeyDispatchCodec;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ModInitializer;

/**
 * A 1.21.10-era mod (compiled against DFU 8.0.16) for slib's library bridges of the 1.21.10 -> 1.21.11 step.
 * Each check prints "CB-SLIB ok|FAIL ..."; the last line is "CB-SLIB DONE passed/checks".
 */
public class SlibDfu11Test implements ModInitializer {
    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-SLIB " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) FAILS.add(what);
    }

    record Shape(String kind, int size) {
    }

    static class MyDispatch extends KeyDispatchCodec<String, Shape> {
        MyDispatch() {
            super("kind", Codec.STRING, s -> DataResult.success(s.kind()),
                k -> DataResult.success(Codec.INT.fieldOf("size").xmap(n -> new Shape(k, n), Shape::size)));
        }
    }

    @Override
    public void onInitialize() {
        try {
            Codec<String> unit = Codec.unit("fixed");
            check("fixed".equals(unit.parse(JsonOps.INSTANCE, new JsonObject()).result().orElse(null)), "Codec.unit(value) decodes to its value");
            Codec<Integer> sup = Codec.unit(() -> 7);
            check(Integer.valueOf(7).equals(sup.parse(JsonOps.INSTANCE, new JsonObject()).result().orElse(null)), "Codec.unit(supplier)");
            KeyDispatchCodec<String, Shape> kd = new KeyDispatchCodec<>("kind", Codec.STRING, s -> DataResult.success(s.kind()),
                k -> DataResult.success(Codec.INT.fieldOf("size").xmap(n -> new Shape(k, n), Shape::size)));
            Codec<Shape> codec = kd.codec();
            JsonElement enc = codec.encodeStart(JsonOps.INSTANCE, new Shape("box", 3)).result().orElseThrow();
            check(enc.isJsonObject() && "box".equals(enc.getAsJsonObject().get("kind").getAsString())
                && enc.getAsJsonObject().get("size").getAsInt() == 3, "KeyDispatchCodec writes the type under its key");
            JsonObject in = new JsonObject();
            in.add("kind", new JsonPrimitive("ball"));
            in.add("size", new JsonPrimitive(5));
            check(new Shape("ball", 5).equals(codec.parse(JsonOps.INSTANCE, in).result().orElse(null)), "KeyDispatchCodec reads it back");
            Codec<Shape> sub = new MyDispatch().codec();
            check(new Shape("ball", 5).equals(sub.parse(JsonOps.INSTANCE, in).result().orElse(null)), "a KeyDispatchCodec subclass");
        } catch (Throwable e) {
            check(false, "threw " + e);
            e.printStackTrace(System.out);
        }
        System.out.println("CB-SLIB DONE " + (checks - FAILS.size()) + "/" + checks);
    }
}

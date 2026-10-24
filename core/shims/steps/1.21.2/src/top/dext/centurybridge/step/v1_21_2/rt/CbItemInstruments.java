package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.serialization.Lifecycle;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_156;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2370;
import net.minecraft.class_2378;
import net.minecraft.class_2385;
import net.minecraft.class_2561;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_7430;
import net.minecraft.class_7444;
import net.minecraft.class_7445;
import net.minecraft.class_7924;
import net.minecraft.class_9248;
import net.minecraft.class_9334;

/**
 * 1.21.1's instruments on 1.21.2. 1.21.1 registered them in the static BuiltInRegistries.INSTRUMENT (vanilla's goat
 * horns at bootstrap, a mod's where it registered them); 1.21.2 loads them into each world's minecraft:instrument
 * registry from its datapacks (the server's sent to its clients), every Instrument carrying its own description.
 * <ul>
 * <li>BuiltInRegistries.INSTRUMENT reads as a stand-in ({@link #field_41166}): a registry under the same key, with
 *     vanilla's eight goat horns as 1.21.1 registered them, which a 1.21.1 mod registers into where it did. When a
 *     server loads its instrument registry (CbItemInstrumentLoaderMixin), every stand-in entry its datapacks do not
 *     define joins it -- described as 1.21.2's bootstrap describes its own: "instrument.&lt;ns&gt;.&lt;path&gt;".</li>
 * <li>A stand-in holder handed to the game -- the INSTRUMENT component, InstrumentItem.create -- is the world's holder
 *     of the same key (CbItemComponentValues): the server's, or on the client's thread the client's.</li>
 * <li>Instrument(sound, useDuration, range): 1.21.1 counted the use duration in ticks, 1.21.2 in seconds (1.21.2's
 *     InstrumentItem uses it times 20); the description 1.21.2 adds is made when the entry joins a world (above), so
 *     the record built here carries a placeholder. useDuration() answers ticks again.</li>
 * </ul>
 */
public final class CbItemInstruments {
    private CbItemInstruments() {
    }

    private static final class Standin {
        static final class_2370<class_7444> REGISTRY = new class_2370<>(class_7924.field_41275, Lifecycle.stable());

        static {
            bootstrapInto(REGISTRY);
        }
    }

    private static volatile class_2378<class_7444> serverWorld;
    private static volatile class_2378<class_7444> clientWorld;
    private static volatile Thread clientThread;

    /** BuiltInRegistries.INSTRUMENT (fieldGetters) */
    public static class_2378<class_7444> field_41166() {
        return Standin.REGISTRY;
    }

    /** Instruments.GOAT_HORN_RANGE, an int in 1.21.1 */
    public static int field_39125() {
        return (int) class_7445.field_39125;
    }

    /** the placeholder description of a record a 1.21.1 constructor builds (an entry joining a world gets its own) */
    public static class_2561 unnamed() {
        return class_2561.method_43471("instrument.centurybridge.unnamed");
    }

    /** 1.21.1's use duration in ticks as 1.21.2's in seconds */
    public static float seconds(int ticks) {
        return ticks / 20.0F;
    }

    /** Instrument.useDuration(): ticks, as 1.21.1 answered */
    public static int comp_773(class_7444 instrument) {
        return Math.round(instrument.comp_773() * 20.0F);
    }

    /** 1.21.2's description of an instrument registered under a key (Instruments.register) */
    private static class_2561 description(class_5321<class_7444> key) {
        return class_2561.method_43471(class_156.method_646("instrument", key.method_29177()));
    }

    /**
     * Instruments.bootstrap(registry): 1.21.1 registered the eight goat horns (sound variant i, 140 ticks, range 256)
     * and answered the last; their descriptions are 1.21.2's
     */
    public static class_7444 bootstrapInto(class_2378<class_7444> registry) {
        List<class_5321<class_7444>> keys = List.of(class_7445.field_39126, class_7445.field_39127, class_7445.field_39128,
            class_7445.field_39129, class_7445.field_39130, class_7445.field_39131, class_7445.field_39132, class_7445.field_39133);
        class_7444 last = null;
        for (int i = 0; i < keys.size(); i++) {
            @SuppressWarnings("unchecked")
            class_6880<class_3414> sound = (class_6880<class_3414>) class_3417.field_39028.get(i);
            last = class_2378.method_39197(registry, keys.get(i), new class_7444(sound, seconds(140), 256.0F, description(keys.get(i))));
        }
        return last;
    }

    // ---- the worlds ----

    /** a server's instrument registry has loaded its datapacks, before it freezes: the stand-in's other entries join it */
    public static void onServerLoaded(class_2385<class_7444> world) {
        for (Map.Entry<class_5321<class_7444>, class_7444> e : Standin.REGISTRY.method_29722()) {
            if (!world.method_35842(e.getKey())) {
                class_7444 i = e.getValue();
                world.method_10272(e.getKey(), new class_7444(i.comp_772(), i.comp_773(), i.comp_774(), description(e.getKey())),
                    class_9248.field_49136);
            }
        }
        serverWorld = world;
    }

    /** a client's instrument registry has loaded what its server sent */
    public static void onClientLoaded(class_2378<class_7444> world) {
        clientWorld = world;
        clientThread = Thread.currentThread();
    }

    /** the world registry this thread works for: the client's on the client's thread, else the server's */
    private static class_2378<class_7444> world() {
        class_2378<class_7444> c = clientWorld;
        if (c != null && Thread.currentThread() == clientThread) {
            return c;
        }
        class_2378<class_7444> s = serverWorld;
        return s != null ? s : c;
    }

    /** a stand-in holder as the world's holder of its key; any other holder unchanged */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object worldHolder(Object value) {
        if (value instanceof class_6880.class_6883 ref && ref.method_46745(Standin.REGISTRY)) {
            class_2378<class_7444> world = world();
            if (world == null) {
                throw new IllegalStateException("no world's instrument registry is loaded: instrument "
                    + ref.method_40237().method_29177() + " exists in a world's registry once a world has loaded");
            }
            class_5321<class_7444> key = (class_5321<class_7444>) ref.method_40237();
            Optional<class_6880.class_6883<class_7444>> found = world.method_46746(key);
            if (found.isEmpty()) {
                throw new IllegalStateException("the world's instrument registry has no " + key.method_29177());
            }
            return found.get();
        }
        return value;
    }

    /** InstrumentItem.create(item, instrument) */
    @SuppressWarnings("unchecked")
    public static class_1799 create(class_1792 item, class_6880<class_7444> instrument) {
        return class_7430.method_43558(item, (class_6880<class_7444>) worldHolder(instrument));
    }

    /** InstrumentItem.setRandom(stack, tag, random): a random instrument of the world's tag */
    public static void setRandom(class_1799 stack, class_6862<class_7444> tag, class_5819 random) {
        class_2378<class_7444> world = world();
        if (world == null) {
            throw new IllegalStateException("no world's instrument registry is loaded to pick from " + tag.comp_327());
        }
        Optional<class_6880<class_7444>> picked = world.method_56159(tag, random);
        picked.ifPresent(h -> stack.method_57379(class_9334.field_49612, h));
    }
}

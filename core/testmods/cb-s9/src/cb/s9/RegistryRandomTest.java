package cb.s9;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.class_1792;
import net.minecraft.class_2370;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3489;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_8828;

/**
 * 1.21.8 Registry.getRandomElementOf(tag, random) (a default method 1.21.9 dropped, given back as a default of Registry
 * by the layer's interface mixin): a call through Registry, a call inherited through DefaultedRegistry, and a 1.21.8
 * registry class overriding it and reaching the default by super.
 */
final class RegistryRandomTest {
    private RegistryRandomTest() {
    }

    /** a 1.21.8 registry overriding getRandomElementOf and keeping the default's answer by super */
    static final class CountingRegistry<T> extends class_2370<T> {
        int calls;

        CountingRegistry(class_5321<? extends class_2378<T>> key) {
            super(key, Lifecycle.stable());
        }

        @Override
        public Optional<class_6880<T>> method_56159(class_6862<T> tag, class_5819 random) {
            calls++;
            return super.method_56159(tag, random);
        }
    }

    static void run() {
        S9TestMod.run("registry: getRandomElementOf", () -> {
            class_5819 random = class_5819.method_43049(42L);
            class_2378<class_1792> items = class_7923.field_41178;
            Optional<class_6880<class_1792>> viaRegistry = items.method_56159(class_3489.field_15539, random);
            S9TestMod.check(viaRegistry.isPresent() && viaRegistry.get().method_40220(class_3489.field_15539),
                "Registry.getRandomElementOf(LOGS, random) through Registry answers a log, got " + viaRegistry);
        });
        S9TestMod.run("registry: getRandomElementOf inherited through DefaultedRegistry", () -> {
            class_5819 random = class_5819.method_43049(42L);
            Optional<class_6880<class_1792>> inherited = class_7923.field_41178.method_56159(class_3489.field_15539, random);
            S9TestMod.check(inherited.isPresent() && inherited.get().method_40220(class_3489.field_15539),
                "DefaultedRegistry.getRandomElementOf(LOGS, random) (inherited) answers a log, got " + inherited);
        });
        S9TestMod.run("registry: getRandomElementOf override + super", () -> {
            class_5819 random = class_5819.method_43049(42L);
            CountingRegistry<class_1792> own = new CountingRegistry<>(class_5321.method_29180(class_2960.method_60655("cb_s9", "random_test")));
            class_2378<class_1792> asRegistry = own;
            // a fresh registry has no tags bound: 1.21.8's default body (getTag(tag)) throws "Tags not bound" there,
            // as it did on 1.21.8 -- that exception is the proof the super call reached the restored default
            String got;
            try {
                got = String.valueOf(asRegistry.method_56159(class_6862.method_40092(own.method_46765(),
                    class_2960.method_60655("cb_s9", "no_such_tag")), random));
            } catch (IllegalStateException e) {
                got = String.valueOf(e.getMessage());
            }
            S9TestMod.check(own.calls == 1 && got.startsWith("Tags not bound"),
                "a 1.21.8 override of getRandomElementOf is reached through Registry and its super call runs 1.21.8's default "
                    + "body (getTag on an unbound registry: Tags not bound), calls " + own.calls + " got " + got);
        });
        S9TestMod.run("registry: negative case, an unrestored interface member through a subinterface", () -> {
            // ComponentContents.type() (method_54163) died at 1.21.9 and no layer restores it: reached through the
            // subinterface PlainTextContents it must still be tombstoned, not taken for restored by the interface walk
            try {
                Object t = class_8828.field_46625.method_54163();
                S9TestMod.check(false, "PlainTextContents.type() is tombstoned, but it answered " + t);
            } catch (UnsupportedOperationException e) {
                S9TestMod.check(String.valueOf(e.getMessage()).contains("tombstone"),
                    "PlainTextContents.type() is tombstoned, got " + e);
            }
        });
    }
}

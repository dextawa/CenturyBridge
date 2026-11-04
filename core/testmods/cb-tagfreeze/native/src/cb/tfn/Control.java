package cb.tfn;

import com.mojang.serialization.Lifecycle;
import java.util.List;
import net.minecraft.class_2370;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_9248;

/**
 * The native 1.21.2 control, not converted: the same registry built the 1.21.2 way. (1) A tag created for
 * registration and left unbound: freeze() refuses ("Unbound tags"), vanilla's check, with or without the layer.
 * (2) The same after bindAllTagsToEmpty() (vanilla's BuiltInRegistries.freeze order): freeze() passes and the tag is
 * empty; a tag bound earlier through bindTag is emptied too (bindAllTagsToEmpty binds every set). Prints
 * "CB-TFN ok|FAIL ..." and "CB-TFN summary ok=N fail=M".
 */
public final class Control {
    static int ok;
    static int fail;

    static void check(String name, boolean pass, Object detail) {
        if (pass) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-TFN " + (pass ? "ok " : "FAIL ") + name + " " + detail);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static class_2370<String> build(String path, class_6885[] fresh) {
        class_5321 key = class_5321.method_29180(class_2960.method_60655("cbtfn", path));
        class_2370<String> reg = new class_2370<>(key, Lifecycle.stable());
        class_6880 a = reg.method_10272(class_5321.method_29179(key, class_2960.method_60655("cbtfn", "a")), "a", class_9248.field_49136);
        fresh[0] = reg.method_46769().method_46735(class_6862.method_40092(key, class_2960.method_60655("cbtfn", "fresh")));
        reg.method_62681(class_6862.method_40092(key, class_2960.method_60655("cbtfn", "bound")), List.of(a));
        return reg;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void run() {
        try {
            class_6885[] fresh = new class_6885[1];
            class_2370<String> r1 = build("unbound", fresh);
            try {
                r1.method_40276();
                check("vanilla-refuses-unbound", false, "no exception");
            } catch (IllegalStateException e) {
                check("vanilla-refuses-unbound", String.valueOf(e.getMessage()).startsWith("Unbound tags"), e.getMessage());
            }
            class_2370<String> r2 = build("bound", fresh);
            r2.method_62691();
            r2.method_40276();
            check("bind-then-freeze", true, "no exception");
            check("fresh-empty", fresh[0].method_40247() == 0, "size=" + fresh[0].method_40247());
            // vanilla's bindAllTagsToEmpty binds every named set, a bound one included, to nothing: why the converted
            // freeze binds only the sets still unbound (cb-tf's bound-kept)
            class_5321 key2 = class_5321.method_29180(class_2960.method_60655("cbtfn", "bound"));
            class_6885 b = (class_6885) r2.method_46733(class_6862.method_40092(key2, class_2960.method_60655("cbtfn", "bound"))).orElse(null);
            check("bindall-clears-bound", b != null && b.method_40247() == 0, "size=" + (b == null ? "absent" : b.method_40247()));
        } catch (Throwable t) {
            check("control", false, t);
            t.printStackTrace(System.out);
        } finally {
            System.out.println("CB-TFN summary ok=" + ok + " fail=" + fail);
        }
    }

    private Control() {
    }
}

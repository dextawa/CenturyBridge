package cb.tf;

import com.mojang.serialization.Lifecycle;
import java.util.List;
import java.util.Map;
import net.minecraft.class_2370;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_9248;

/**
 * A 1.21.1 mod freezing a registry it built itself (the case of Forge's GameData.freezeData under Kilt): an entry, a
 * tag made through getOrCreateTag and never bound, a tag bound through bindTags, then freeze(). On 1.21.1 a named set
 * starts bound (empty), so freeze() passes. Expected on 1.21.2 after conversion: no "Unbound tags", the fresh tag
 * empty, the bound tag keeping its one holder. Then a refreeze (unfreeze through an accessor, add an entry and a tag,
 * freeze again): passes, the entry present, the new tag empty, the bound tag still holding its holder. Prints "CB-TF ok|FAIL case detail" and "CB-TF summary ok=N fail=M".
 */
public final class Driver {
    static int ok;
    static int fail;

    static void check(String name, boolean pass, Object detail) {
        if (pass) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-TF " + (pass ? "ok " : "FAIL ") + name + " " + detail);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void run() {
        try {
            class_5321 key = class_5321.method_29180(class_2960.method_60655("cbtf", "things"));
            class_2370<String> reg = new class_2370<>(key, Lifecycle.stable());
            class_6880 a = reg.method_10272(class_5321.method_29179(key, class_2960.method_60655("cbtf", "a")), "a", class_9248.field_49136);
            class_6862 fresh = class_6862.method_40092(key, class_2960.method_60655("cbtf", "fresh"));
            class_6862 bound = class_6862.method_40092(key, class_2960.method_60655("cbtf", "bound"));
            class_6885 freshSet = reg.method_40260(fresh);
            reg.method_40257(Map.of(bound, List.of(a)));
            try {
                reg.method_40276();
                check("freeze", true, "no exception");
            } catch (RuntimeException e) {
                check("freeze", false, e);
                return;
            }
            check("fresh-empty", freshSet.method_40247() == 0, "size=" + freshSet.method_40247());
            class_6885 b = (class_6885) reg.method_40266(bound).orElse(null);
            check("bound-kept", b != null && b.method_40247() == 1, "size=" + (b == null ? "absent" : b.method_40247()));
            check("frozen-freeze-returns-self", reg.method_40276() == reg, "a frozen registry's freeze() returns it unchanged");
            // refreeze, as Forge's GameData under Kilt does: unfreeze, add an entry and a tag, freeze again (1.21.1 allowed it)
            ((cb.tf.mixin.RegistryFrozenAccess) (Object) reg).cbtf$setFrozen(false);
            class_2960 cId = class_2960.method_60655("cbtf", "c");
            reg.method_10272(class_5321.method_29179(key, cId), "c", class_9248.field_49136);
            class_6885 lateSet = reg.method_40260(class_6862.method_40092(key, class_2960.method_60655("cbtf", "late")));
            try {
                reg.method_40276();
                check("refreeze", true, "no exception");
            } catch (RuntimeException e) {
                check("refreeze", false, e);
                return;
            }
            check("refreeze-entry", reg.method_10250(cId), "added entry present after refreeze");
            check("refreeze-late-empty", lateSet.method_40247() == 0, "size=" + lateSet.method_40247());
            class_6885 b2 = (class_6885) reg.method_40266(bound).orElse(null);
            check("refreeze-bound-kept", b2 != null && b2.method_40247() == 1, "size=" + (b2 == null ? "absent" : b2.method_40247()));
        } catch (Throwable t) {
            check("driver", false, t);
            t.printStackTrace(System.out);
        } finally {
            System.out.println("CB-TF summary ok=" + ok + " fail=" + fail);
        }
    }

    private Driver() {
    }
}

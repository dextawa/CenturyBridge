package cb.s212n;

import cb.s212n.mixin.NativeDropsAccess;
import java.util.Optional;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_52;
import net.minecraft.class_7924;

/**
 * What 1.21.2 holds in a Properties' drops (cb-s212's driver asks by reflection, after writing it through 1.21.1's
 * field): "default" for the function every new Properties carries ("blocks/&lt;id&gt;"), "none" for an empty
 * Optional, else the fixed key's location.
 */
public final class Drops {
    private Drops() {
    }

    public static String of(Object properties) {
        class_5321<net.minecraft.class_2248> id = class_5321.method_29179(class_7924.field_41254, class_2960.method_60655("cb_s212n", "probe"));
        Optional<class_5321<class_52>> v = ((NativeDropsAccess) properties).cbs212n$drops().get(id);
        if (v.isPresent() && v.get().method_29177().equals(class_2960.method_60655("cb_s212n", "blocks/probe"))) return "default";
        return v.map(k -> k.method_29177().toString()).orElse("none");
    }
}

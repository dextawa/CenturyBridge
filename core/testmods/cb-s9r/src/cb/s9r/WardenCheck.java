package cb.s9r;

import java.util.List;
import net.minecraft.class_310;
import net.minecraft.class_5602;
import net.minecraft.class_630;
import net.minecraft.class_7280;

/**
 * 1.21.8 WardenModel's emissive part lists: builds a WardenModel from the baked warden layer, as a 1.21.8 warden
 * renderer does, and asks its four list getters. cb.s9r.mixin.WardenListsMixin records, at the constructor's RETURN,
 * whether the model's own lists already held 1.21.8's parts in 1.21.8's order (they did on 1.21.8).
 */
public final class WardenCheck {
    /** set by WardenListsMixin: "lists at init: <ok>", and the four lists it saw */
    public static volatile String atInit = "never ran";
    public static volatile List<List<class_630>> seen = List.of();

    private WardenCheck() {
    }

    /** "<at init> | getters same as the constructor's lists: <b> | sizes a,b,c,d" */
    public static String bake() {
        class_7280 model = new class_7280(class_310.method_1551().method_31974().method_32072(class_5602.field_38328));
        List<List<class_630>> got = List.of(model.method_42738(null), model.method_42739(null), model.method_42740(null),
            model.method_42741(null));
        boolean same = seen.size() == 4;
        for (int i = 0; same && i < 4; i++) {
            same = got.get(i) == seen.get(i);
        }
        return atInit + " | getters same as the constructor's lists: " + same + " | sizes " + got.get(0).size() + ","
            + got.get(1).size() + "," + got.get(2).size() + "," + got.get(3).size();
    }
}

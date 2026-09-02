package top.dext.centurybridge.facade.v1_21_4;

import net.minecraft.class_9168;
import net.minecraft.class_9169;

/**
 * 1.20.1 SpawnRestriction.Location (class_1317$class_1319), an enum 1.20.5 replaced by the
 * SpawnLocation interface (class_9168) and its four vanilla constants (class_9169). The constants
 * keep their intermediary field ids so old GETSTATIC sites resolve; bridges unwrap with
 * {@link #cbVanilla()}. classRename: net/minecraft/class_1317$class_1319 -> this enum.
 */
public enum class_1317_class_1319 {
    /** ON_GROUND */
    field_6317(class_9169.field_48745),
    /** IN_WATER */
    field_6318(class_9169.field_48743),
    /** NO_RESTRICTIONS */
    field_19350(class_9169.field_48742),
    /** IN_LAVA */
    field_23221(class_9169.field_48744);

    private final class_9168 vanilla;

    class_1317_class_1319(class_9168 vanilla) {
        this.vanilla = vanilla;
    }

    public class_9168 cbVanilla() {
        return vanilla;
    }

    public static class_1317_class_1319 cbOf(class_9168 location) {
        for (class_1317_class_1319 l : values()) {
            if (l.vanilla == location) {
                return l;
            }
        }
        return field_19350;
    }
}

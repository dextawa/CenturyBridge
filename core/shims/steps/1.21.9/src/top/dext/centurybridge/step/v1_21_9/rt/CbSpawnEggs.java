package top.dext.centurybridge.step.v1_21_9.rt;

import net.minecraft.class_11580;
import net.minecraft.class_1299;
import net.minecraft.class_1826;
import net.minecraft.class_9334;

/**
 * SpawnEggItem.defaultType (field_8917, 1.21.8): 1.21.9 keeps the egg's entity type only in its default ENTITY_DATA
 * component (javap -c: the constructor reads components().get(ENTITY_DATA).type() into BY_ID, as 1.21.8 put
 * defaultType there), so the 1.21.8 read answers that type, null when the item has none (as 1.21.9's own getType).
 */
public final class CbSpawnEggs {
    private CbSpawnEggs() {
    }

    public static class_1299<?> field_8917(class_1826 egg) {
        class_11580<?> data = egg.method_57347().method_58694(class_9334.field_49609);
        return data == null ? null : (class_1299<?>) data.method_72530();
    }
}

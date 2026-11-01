package top.dext.centurybridge.step.v1_21_9.rt;

import net.minecraft.class_1299;
import net.minecraft.class_1542;

/**
 * new ItemEntity(other) (1.21.8's private copy constructor, behind ItemEntity.copy(), which only ItemPickupParticle
 * called): 1.21.9 dropped both (its pickup particle keeps the item's render state). 1.21.8's body, javap -c: the
 * (type, level) constructor of the other's type and level, age 0, pickupDelay 0, health 5 (what 1.21.9's (type, level)
 * constructor sets), a copy of the other's stack, its position, then its age and bobOffs.
 */
public final class CbItemEntityCopy {
    private CbItemEntityCopy() {
    }

    @SuppressWarnings("unchecked")
    public static class_1542 create(class_1542 other) {
        class_1542 e = new class_1542((class_1299<? extends class_1542>) other.method_5864(), other.method_73183());
        e.method_6979(other.method_6983().method_7972());
        e.method_5719(other);
        e.field_7204 = other.field_7204;
        e.field_7203 = other.field_7203;
        return e;
    }
}

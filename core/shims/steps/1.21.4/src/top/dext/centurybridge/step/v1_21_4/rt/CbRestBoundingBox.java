package top.dext.centurybridge.step.v1_21_4.rt;

import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;

/**
 * 1.21.3's Entity.makeBoundingBox(): the entity's box at its current position, overridable. 1.21.4 made it
 * final (it returns makeBoundingBox(position())) and moved the overridable shape to makeBoundingBox(Vec3 pos).
 * A 1.21.3 override is renamed cb$makeBoundingBox (overrideRenames, with {@link Legacy}), and each 1.21.4
 * declaration of makeBoundingBox(pos) (Entity, Interaction, Shulker, AbstractWindCharge: CbRestBoundingBoxMixin*)
 * hands the call to it: at the current position its answer as it is -- 1.21.3's only caller, setPos, asked
 * there -- and at another position (1.21.4's new collision probes) the same box moved there, the shape the
 * override gives the entity. The mod's super call reaches 1.21.4's final makeBoundingBox(), which asks
 * makeBoundingBox(position()) again; the guard lets that nested call run vanilla's body, which is what
 * 1.21.3's super implementation answered.
 */
public final class CbRestBoundingBox {

    private CbRestBoundingBox() {
    }

    public interface Legacy {
        class_238 cb$makeBoundingBox();
    }

    /** the mod override's box at pos, or null to let vanilla's body run */
    public static class_238 legacyBox(Object self, class_243 pos) {
        if (!(self instanceof Legacy legacy) || !Guard.cbEnter(self)) {
            return null;
        }
        try {
            class_238 box = legacy.cb$makeBoundingBox();
            class_243 at = ((class_1297) self).method_19538();
            if (box == null || at.equals(pos)) {
                return box;
            }
            return box.method_997(pos.method_1020(at));
        } finally {
            Guard.cbExit(self);
        }
    }
}

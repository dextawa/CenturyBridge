package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_128;
import net.minecraft.class_129;
import net.minecraft.class_1297;
import net.minecraft.class_148;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2680;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityInsideInvoker;

/**
 * 1.21.1's Entity.tryCheckInsideBlocks() / checkInsideBlocks() on 1.21.2.
 *
 * <p>1.21.1: move() ended with the overridable tryCheckInsideBlocks(), which ran the overridable
 * checkInsideBlocks() and turned a throwable into a crash report; checkInsideBlocks (also called directly by
 * projectiles, boats, minecarts and other ticks) visited every block the bounding box (deflated by 1e-7) overlaps
 * while the entity is alive -- state.entityInside(level, pos, entity) then onInsideBlock(state) -- when the level
 * has those chunks. 1.21.2 replaced both with applyEffectsFromBlocks: blocks along the tick's movement, visited in
 * its private loop. A 1.21.1 body is kept here for the members' calls and super calls; for a mod class overriding
 * either member, CbEntityInsideMixin runs 1.21.1's tryCheckInsideBlocks in place of 1.21.2's loop, so the override
 * sees the blocks where 1.21.2's vanilla handles them.
 */
public final class CbEntityInside {

    /** a mod entity class's own 1.21.1 tryCheckInsideBlocks (adapter hook) */
    public interface LegacyTry {
        void method_36974();
    }

    /** a mod entity class's own 1.21.1 checkInsideBlocks (adapter hook) */
    public interface LegacyCheck {
        void method_5852();
    }

    /** 1.21.1 Entity.tryCheckInsideBlocks(), called */
    public static void tryCheckInsideBlocks(class_1297 entity) {
        if (entity instanceof LegacyTry legacy) {
            legacy.method_36974();
            return;
        }
        superTryCheckInsideBlocks(entity);
    }

    /** 1.21.1 Entity's own tryCheckInsideBlocks body */
    public static void superTryCheckInsideBlocks(class_1297 entity) {
        try {
            checkInsideBlocks(entity);
        } catch (Throwable t) {
            class_128 report = class_128.method_560(t, "Checking entity block collision");
            entity.method_5819(report.method_562("Entity being checked for collision"));
            throw new class_148(report);
        }
    }

    /** 1.21.1 Entity.checkInsideBlocks(), called */
    public static void checkInsideBlocks(class_1297 entity) {
        if (entity instanceof LegacyCheck legacy) {
            legacy.method_5852();
            return;
        }
        superCheckInsideBlocks(entity);
    }

    /** 1.21.1 Entity's own checkInsideBlocks body */
    public static void superCheckInsideBlocks(class_1297 entity) {
        class_238 box = entity.method_5829();
        class_2338 from = class_2338.method_49637(box.field_1323 + 1.0E-7, box.field_1322 + 1.0E-7, box.field_1321 + 1.0E-7);
        class_2338 to = class_2338.method_49637(box.field_1320 - 1.0E-7, box.field_1325 - 1.0E-7, box.field_1324 - 1.0E-7);
        class_1937 level = entity.method_37908();
        if (!level.method_22343(from, to)) {
            return;
        }
        class_2338.class_2339 pos = new class_2338.class_2339();
        for (int x = from.method_10263(); x <= to.method_10263(); x++) {
            for (int y = from.method_10264(); y <= to.method_10264(); y++) {
                for (int z = from.method_10260(); z <= to.method_10260(); z++) {
                    if (!entity.method_5805()) {
                        return;
                    }
                    pos.method_10103(x, y, z);
                    class_2680 state = entity.method_37908().method_8320(pos);
                    try {
                        state.method_26178(entity.method_37908(), pos, entity);
                        ((CbEntityInsideInvoker) entity).cb$onInsideBlock(state);
                    } catch (Throwable t) {
                        class_128 report = class_128.method_560(t, "Colliding entity with block");
                        class_129.method_586(report.method_562("Block being collided with"), entity.method_37908(), pos, state);
                        throw new class_148(report);
                    }
                }
            }
        }
    }

    private CbEntityInside() {
    }
}

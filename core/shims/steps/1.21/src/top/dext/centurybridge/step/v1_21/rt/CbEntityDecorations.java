package top.dext.centurybridge.step.v1_21.rt;

import net.minecraft.class_1297;
import net.minecraft.class_1530;
import net.minecraft.class_1532;
import net.minecraft.class_1533;
import net.minecraft.class_1534;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_3417;
import net.minecraft.class_9209;
import net.minecraft.class_9691;

/**
 * Hanging entities (item frames, paintings) and the fence knot. 1.20.6's HangingEntity sized itself from the
 * abstract getWidth()/getHeight() (in pixels) in an overridable recalculateBoundingBox(); 1.21 put the position
 * and the box in BlockAttachedEntity (also the knot's superclass now), asks the abstract
 * calculateBoundingBox(pos, direction) from a final recalculateBoundingBox(), and dropped the pixel sizes.
 * Wired from s6world.json; CbEntityHangingMixin answers a 1.20.6 override of the final method.
 */
public final class CbEntityDecorations {

    public interface LegacyWidth {
        int method_6897();
    }

    public interface LegacyHeight {
        int method_6891();
    }

    /** a 1.20.6 recalculateBoundingBox() override, renamed (1.21's is final) */
    public interface LegacyRecalculate {
        void method_6895$cb1_21();
    }

    public interface LegacyKnotDirection {
        void method_6892(class_2350 direction);
    }

    public interface LegacyKnotPlacementSound {
        void method_6894();
    }

    // ------------------------------------------------------------------------------------------------ sizes
    /** 1.20.6 hangingEntity.getWidth(): the entity's own (a 1.20.6 class), else 1.20.6's vanilla answer */
    public static int getWidth(class_1530 entity) {
        if (entity instanceof LegacyWidth legacy) {
            return legacy.method_6897();
        }
        if (entity instanceof class_1533) {
            return 12;
        }
        if (entity instanceof class_1534 painting) {
            return CbEntityPaintings.pixels(painting.method_43404().comp_349().comp_2670());
        }
        return extent(entity, true);
    }

    public static int getHeight(class_1530 entity) {
        if (entity instanceof LegacyHeight legacy) {
            return legacy.method_6891();
        }
        if (entity instanceof class_1533) {
            return 12;
        }
        if (entity instanceof class_1534 painting) {
            return CbEntityPaintings.pixels(painting.method_43404().comp_349().comp_2671());
        }
        return extent(entity, false);
    }

    /** 1.20.6 LeashFenceKnotEntity.getWidth()/getHeight(): 9 pixels (a 1.20.6 subclass answers itself) */
    public static int getWidth(class_1532 knot) {
        return knot instanceof LegacyWidth legacy ? legacy.method_6897() : 9;
    }

    public static int getHeight(class_1532 knot) {
        return knot instanceof LegacyHeight legacy ? legacy.method_6891() : 9;
    }

    /**
     * The size of a hanging entity 1.20.6 had no class for: what its box spans, in 1.20.6's pixels (16 to a
     * block) -- along the wall for the width, upwards for the height.
     */
    private static int extent(class_1530 entity, boolean width) {
        class_238 box = entity.method_5829();
        double span = width
            ? (entity.method_5735().method_10166() == class_2350.class_2351.field_11051 ? box.field_1320 - box.field_1323 : box.field_1324 - box.field_1321)
            : box.field_1325 - box.field_1322;
        return (int) Math.round(span * 16.0);
    }

    // a super call landing on 1.20.6's vanilla body
    public static int superGetWidth(class_1533 frame) {
        return 12;
    }

    public static int superGetHeight(class_1533 frame) {
        return 12;
    }

    public static int superGetWidth(class_1534 painting) {
        return CbEntityPaintings.pixels(painting.method_43404().comp_349().comp_2670());
    }

    public static int superGetHeight(class_1534 painting) {
        return CbEntityPaintings.pixels(painting.method_43404().comp_349().comp_2671());
    }

    public static int superGetWidth(class_1532 knot) {
        return 9;
    }

    public static int superGetHeight(class_1532 knot) {
        return 9;
    }

    /**
     * 1.21's calculateBoundingBox(pos, direction) for a 1.20.6 subclass of HangingEntity (abstractFills): 1.20.6's
     * recalculateBoundingBox from its getWidth()/getHeight() -- centred half a block in from the wall it hangs on,
     * shifted half a block along and up for an even size, a pixel thick. 1.21 then puts the entity at the box's
     * centre, where 1.20.6 put it.
     */
    public static class_238 calculateBoundingBox(class_1530 entity, class_2338 pos, class_2350 direction) {
        int widthPixels = getWidth(entity);
        int heightPixels = getHeight(entity);
        double x = pos.method_10263() + 0.5;
        double y = pos.method_10264() + 0.5;
        double z = pos.method_10260() + 0.5;
        double alongOffset = widthPixels % 32 == 0 ? 0.5 : 0.0;
        double upOffset = heightPixels % 32 == 0 ? 0.5 : 0.0;
        x -= direction.method_10148() * 0.46875;
        z -= direction.method_10165() * 0.46875;
        y += upOffset;
        class_2350 along = direction.method_10160();
        x += alongOffset * along.method_10148();
        z += alongOffset * along.method_10165();
        double w = widthPixels;
        double h = heightPixels;
        double d = widthPixels;
        if (direction.method_10166() == class_2350.class_2351.field_11051) {
            d = 1.0;
        } else {
            w = 1.0;
        }
        w /= 32.0;
        h /= 32.0;
        d /= 32.0;
        return new class_238(x - w, y - h, z - d, x + w, y + h, z + d);
    }

    // ------------------------------------------------------------------------------------------------ the knot
    /** 1.20.6 knot.setDirection(direction): a knot has no direction, 1.20.6's body did nothing */
    public static void setDirection(class_1532 knot, class_2350 direction) {
        if (knot instanceof LegacyKnotDirection legacy) {
            legacy.method_6892(direction);
        }
    }

    public static void superSetDirection(class_1532 knot, class_2350 direction) {
    }

    /** 1.20.6 knot.playPlacementSound(): the knot placing sound */
    public static void playPlacementSound(class_1532 knot) {
        if (knot instanceof LegacyKnotPlacementSound legacy) {
            legacy.method_6894();
        } else {
            superPlayPlacementSound(knot);
        }
    }

    public static void superPlayPlacementSound(class_1532 knot) {
        knot.method_5783(class_3417.field_15062, 1.0F, 1.0F);
    }

    // ------------------------------------------------------------------------------------------------ item frames
    /** 1.20.6 frame.getFramedMapId(): the map id of the item it holds -- 1.21's for that item */
    public static class_9209 getFramedMapId(class_1533 frame) {
        return frame.method_43272(frame.method_6940());
    }

    /** getFramedMapId(stack)'s stack for a 1.20.6 override entered directly: the item the frame holds */
    public static class_1799 framedItem(class_1297 frame) {
        return ((class_1533) frame).method_6940();
    }

    private CbEntityDecorations() {
    }
}

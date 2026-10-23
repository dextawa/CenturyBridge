package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.class_1767;
import net.minecraft.class_1792;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2480;

/**
 * Small 1.21.3 statics gone in 1.21.4, each 1.21.3's body over 1.21.4's members:
 * FoliageColor's color getters (1.21.4 has the same colors as constants), ShulkerBoxBlock.getColorFromItem /
 * getColorFromBlock, Shulker.getProgressAabb / getProgressDeltaAabb (1.21.4's take a position and center the
 * box; 1.21.3's box starts at the origin corner), BlockPos.breadthFirstTraversal with a Predicate (1.21.4
 * takes a function answering ACCEPT / SKIP / STOP; 1.21.3's predicate false skipped the position).
 */
public final class CbRestStatics {

    private CbRestStatics() {
    }

    // ---- FoliageColor ----

    public static int method_8342() {
        return -10380959;
    }

    public static int method_8343() {
        return -8345771;
    }

    public static int method_8341() {
        return -12012264;
    }

    public static int method_43717() {
        return -7158200;
    }

    // ---- ShulkerBoxBlock ----

    public static class_1767 method_10527(class_1792 item) {
        return method_10526(class_2248.method_9503(item));
    }

    public static class_1767 method_10526(class_2248 block) {
        return block instanceof class_2480 box ? box.method_10528() : null;
    }

    // ---- Shulker ----

    public static class_238 method_33346(float scale, class_2350 dir, float progress) {
        return method_33347(scale, dir, -1.0F, progress);
    }

    public static class_238 method_33347(float scale, class_2350 dir, float progress0, float progress1) {
        class_238 box = new class_238(0.0, 0.0, 0.0, scale, scale, scale);
        double hi = Math.max(progress0, progress1);
        double lo = Math.min(progress0, progress1);
        return box.method_1012(dir.method_10148() * hi * scale, dir.method_10164() * hi * scale, dir.method_10165() * hi * scale)
            .method_1002(-dir.method_10148() * (1.0 + lo) * scale, -dir.method_10164() * (1.0 + lo) * scale,
                -dir.method_10165() * (1.0 + lo) * scale);
    }

    // ---- BlockPos ----

    public static int method_49925(class_2338 start, int depth, int visitLimit, BiConsumer<class_2338, Consumer<class_2338>> neighbours,
            Predicate<class_2338> visitor) {
        return class_2338.method_49925(start, depth, visitLimit, neighbours,
            pos -> visitor.test(pos) ? class_2338.class_10384.field_55165 : class_2338.class_10384.field_55166);
    }
}

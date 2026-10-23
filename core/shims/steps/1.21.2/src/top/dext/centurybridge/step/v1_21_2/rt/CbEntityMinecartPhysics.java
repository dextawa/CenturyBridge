package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1688;
import net.minecraft.class_1696;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_9878;
import net.minecraft.class_9883;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityMinecartInvoker;

/**
 * 1.21.1's minecart rail physics on 1.21.2.
 *
 * <p>1.21.1 kept it on AbstractMinecart: tick() called the overridable moveAlongTrack(pos, state) for the rail at
 * the minecart, whose body asked the overridable getPos(x, y, z) and ended with the overridable
 * applyNaturalSlowdown(), which slowed getDeltaMovement() in place; getPosOffs(x, y, z, offset) (public, the
 * renderer's) asked getPos. 1.21.2 moved the rail physics to a MinecartBehavior: OldMinecartBehavior is 1.21.1's
 * code (getPos / getPosOffs bodies identical; tick and moveAlongTrack(level) the same steps, the pos and state
 * looked up the way 1.21.1's tick did), and applyNaturalSlowdown(Vec3) answers the slowed vector its caller
 * stores. MinecartFurnace's push alignment, done in 1.21.1 after super.moveAlongTrack, is done in 1.21.2 inside
 * its applyNaturalSlowdown, on the same velocity; its xPush / zPush are 1.21.2's push vector.
 *
 * <p>A mod minecart's 1.21.1 overrides are reached as 1.21.1 reached them: CbEntityMinecartOldMixin dispatches
 * OldMinecartBehavior's moveAlongTrack / getPos / getPosOffs to the minecart's own override (adapter
 * interfaces below, added by the wiring), and the overrides' super calls run OldMinecartBehavior's body once,
 * with the pos and state the override passed. Under NewMinecartBehavior (the experimental minecart
 * improvements, which 1.21.1 did not have) vanilla calls AbstractMinecart.moveAlongTrack(level) and
 * applyNaturalSlowdown(Vec3) itself; the hooks answer those with the old overrides too, and a super
 * moveAlongTrack runs the new behavior's own step.
 */
public final class CbEntityMinecartPhysics {

    /** a mod minecart's own 1.21.1 moveAlongTrack(pos, state) (override hook adapter) */
    public interface LegacyTrack {
        void method_7513(class_2338 pos, class_2680 state);
    }

    /** the super accessors the moveAlongTrack hook adds to such a class */
    public interface TrackSuper {
        void cb$super$method_7513(class_3218 level);
    }

    /** a mod minecart's own 1.21.1 applyNaturalSlowdown() (override hook adapter) */
    public interface LegacySlowdown {
        void method_7525();
    }

    /** the super accessors the applyNaturalSlowdown hook adds to such a class */
    public interface SlowdownSuper {
        class_243 cb$super$method_7525(class_243 velocity);
    }

    /** a mod minecart's own 1.21.1 getPos(x, y, z) (adapter hook) */
    public interface LegacyPos {
        class_243 method_7508(double x, double y, double z);
    }

    /** a mod minecart's own 1.21.1 getPosOffs(x, y, z, offset) (adapter hook) */
    public interface LegacyPosOffs {
        class_243 method_7505(double x, double y, double z, double offset);
    }

    /** the one-shot state CbEntityMinecartOldMixin keeps on an OldMinecartBehavior */
    public interface OldState {
        /** the next moveAlongTrack(level) runs vanilla's body on this pos and state (a super call) */
        void cb$track(class_2338 pos, class_2680 state);

        /** the next getPos / getPosOffs runs vanilla's body (a super call) */
        void cb$skipPos();

        void cb$skipPosOffs();
    }

    // ---- moveAlongTrack ----

    /** 1.21.1 AbstractMinecart.moveAlongTrack(pos, state), called (it was protected: from the minecart's class) */
    public static void moveAlongTrack(class_1688 cart, class_2338 pos, class_2680 state) {
        if (cart instanceof LegacyTrack legacy) {
            legacy.method_7513(pos, state);
            return;
        }
        class_3218 level = serverLevel(cart);
        class_9878 behavior = cart.method_61569();
        if (behavior instanceof OldState old) {
            old.cb$track(pos, state);
        }
        try {
            ((CbEntityMinecartInvoker) cart).cb$moveAlongTrack(level);
        } finally {
            if (behavior instanceof OldState old) {
                old.cb$track(null, null);
            }
        }
    }

    /** 1.21.2's moveAlongTrack(level) of a mod minecart overriding 1.21.1's: the rail at the minecart, as 1.21.1's tick */
    public static void trackBridge(LegacyTrack self, class_3218 level) {
        class_1688 cart = (class_1688) self;
        class_2338 pos = cart.method_62825();
        self.method_7513(pos, level.method_8320(pos));
    }

    /** super.moveAlongTrack(pos, state) of such an override: vanilla's step, on the pos and state passed */
    public static void superMoveAlongTrack(TrackSuper self, class_2338 pos, class_2680 state) {
        class_1688 cart = (class_1688) self;
        class_3218 level = serverLevel(cart);
        class_9878 behavior = cart.method_61569();
        if (behavior instanceof OldState old) {
            old.cb$track(pos, state);
        }
        try {
            self.cb$super$method_7513(level);
        } finally {
            if (behavior instanceof OldState old) {
                old.cb$track(null, null);
            }
        }
    }

    private static class_3218 serverLevel(class_1688 cart) {
        class_1937 level = cart.method_37908();
        if (level instanceof class_3218 server) {
            return server;
        }
        // 1.21.1 ran it from the server tick only; 1.21.2's step needs the server level (its max speed rule)
        throw new IllegalStateException("1.21.2 moves a minecart along its track on the server only");
    }

    // ---- applyNaturalSlowdown ----

    /** 1.21.1 AbstractMinecart.applyNaturalSlowdown(), called: slows getDeltaMovement() in place */
    public static void applyNaturalSlowdown(class_1688 cart) {
        CbEntityMinecartInvoker inv = (CbEntityMinecartInvoker) cart;
        cart.method_18799(inv.cb$applyNaturalSlowdown(cart.method_18798()));
    }

    /**
     * 1.21.2's applyNaturalSlowdown(velocity) of a mod minecart overriding 1.21.1's: the old override slows the
     * delta movement, so it runs on the velocity passed and its result is answered; the minecart's own delta
     * movement is left as it was (NewMinecartBehavior passes a vector of its own).
     */
    public static class_243 slowdownBridge(LegacySlowdown self, class_243 velocity) {
        class_1688 cart = (class_1688) self;
        class_243 saved = cart.method_18798();
        cart.method_18799(velocity);
        try {
            self.method_7525();
            return cart.method_18798();
        } finally {
            cart.method_18799(saved);
        }
    }

    /** super.applyNaturalSlowdown() of such an override: super's 1.21.2 body on the delta movement, stored back */
    public static void superApplyNaturalSlowdown(SlowdownSuper self) {
        class_1688 cart = (class_1688) self;
        cart.method_18799(self.cb$super$method_7525(cart.method_18798()));
    }

    // ---- getPos / getPosOffs ----

    /** 1.21.1 AbstractMinecart.getPos(x, y, z), called (the minecart's own override answers through the behavior) */
    public static class_243 getPos(class_1688 cart, double x, double y, double z) {
        return old(cart).method_61620(x, y, z);
    }

    /** super.getPos of a mod minecart: vanilla's body */
    public static class_243 superGetPos(class_1688 cart, double x, double y, double z) {
        class_9883 old = old(cart);
        ((OldState) old).cb$skipPos();
        return old.method_61620(x, y, z);
    }

    /** 1.21.1 AbstractMinecart.getPosOffs(x, y, z, offset), called */
    public static class_243 getPosOffs(class_1688 cart, double x, double y, double z, double offset) {
        return old(cart).method_61619(x, y, z, offset);
    }

    /** super.getPosOffs of a mod minecart: vanilla's body (its getPos call still reaches the minecart's own) */
    public static class_243 superGetPosOffs(class_1688 cart, double x, double y, double z, double offset) {
        class_9883 old = old(cart);
        ((OldState) old).cb$skipPosOffs();
        return old.method_61619(x, y, z, offset);
    }

    /**
     * The minecart's OldMinecartBehavior; under NewMinecartBehavior a fresh one (its constructor only stores the
     * minecart; getPos / getPosOffs read nothing else, the rail geometry of the minecart's level).
     */
    private static class_9883 old(class_1688 cart) {
        class_9878 behavior = cart.method_61569();
        return behavior instanceof class_9883 old ? old : new class_9883(cart);
    }

    // ---- MinecartFurnace xPush / zPush ----

    public static double getXPush(class_1696 cart) {
        return cart.field_54300.field_1352;
    }

    public static void setXPush(class_1696 cart, double value) {
        class_243 push = cart.field_54300;
        cart.field_54300 = new class_243(value, push.field_1351, push.field_1350);
    }

    public static double getZPush(class_1696 cart) {
        return cart.field_54300.field_1350;
    }

    public static void setZPush(class_1696 cart, double value) {
        class_243 push = cart.field_54300;
        cart.field_54300 = new class_243(push.field_1352, push.field_1351, value);
    }

    private CbEntityMinecartPhysics() {
    }
}

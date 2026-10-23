package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1282;
import net.minecraft.class_1510;
import net.minecraft.class_1521;
import net.minecraft.class_1527;
import net.minecraft.class_1528;
import net.minecraft.class_2338;
import net.minecraft.class_2902;
import net.minecraft.class_3033;
import net.minecraft.class_3218;
import net.minecraft.class_3532;
import net.minecraft.class_9871;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityDragonInvoker;

/**
 * 1.21.1's EnderDragon flight history and WitherBoss head rotations on 1.21.2.
 *
 * <p>1.21.1's EnderDragon kept its last 64 (yRot, y) in the public array positions[64][3] ([2] never written by
 * vanilla) with the public ring pointer posPointer (-1: fill every slot on the next record); getLatencyPos(i,
 * partialTick) interpolated two of them (vanilla asked it with 1.0 from aiStep, and with the frame's partial tick
 * from the renderer and model), and getHeadPartYOffset(i, a, b) answered the model's head part offset from the
 * dragon's phase. 1.21.2 keeps the same ring in a DragonFlightHistory (samples (y, yRot), the same pointer
 * rule), interpolated by get(i, partialTick), and the head offset on the render state. CbEntityDragonHistoryMixin
 * keeps 1.21.1's positions array on the history as the samples' storage -- recorded alongside, copied with it,
 * and answering get(i) -- so a mod's reads and writes of positions are what vanilla reads; a mod dragon's
 * getLatencyPos / getHeadPartYOffset overrides answer the history (and the render state's copy) where 1.21.1's
 * vanilla asked them.
 *
 * <p>1.21.1's WitherBoss.getHeadYRot(i) / getHeadXRot(i) answered its yRotHeads / xRotHeads; 1.21.2 hands the
 * arrays out (getHeadYRots / getHeadXRots) to the renderer, which copies them into its state.
 * CbEntityWitherMixin answers those with a mod wither's own overrides.
 */
public final class CbEntityDragon {

    /** the 1.21.1 storage CbEntityDragonHistoryMixin adds to a DragonFlightHistory */
    public interface History {
        double[][] cb$positions();

        int cb$pointer();

        void cb$pointer(int pointer);

        class_1510 cb$owner();

        void cb$owner(class_1510 owner);
    }

    /** a mod dragon's own 1.21.1 getLatencyPos (adapter hook) */
    public interface LegacyLatency {
        double[] method_6817(int index, float partialTick);
    }

    /** a mod dragon's own 1.21.1 getHeadPartYOffset (adapter hook) */
    public interface LegacyHeadOffset {
        float method_6823(int index, double[] a, double[] b);
    }

    /** a mod wither's own 1.21.1 getHeadYRot / getHeadXRot (adapter hooks) */
    public interface LegacyHeadYRot {
        float method_6879(int head);
    }

    public interface LegacyHeadXRot {
        float method_6887(int head);
    }

    /** the 1.21.2 arrays CbEntityWitherMixin reads past its own dispatch */
    public interface WitherHeads {
        float[] cb$yRotHeads();

        float[] cb$xRotHeads();
    }

    private static History history(class_1510 dragon) {
        return (History) dragon.field_52493;
    }

    // ---- fields ----

    /** 1.21.1 EnderDragon.positions (final): the history's storage itself */
    public static double[][] positions(class_1510 dragon) {
        return history(dragon).cb$positions();
    }

    public static int getPosPointer(class_1510 dragon) {
        return history(dragon).cb$pointer();
    }

    public static void setPosPointer(class_1510 dragon, int pointer) {
        history(dragon).cb$pointer(pointer);
    }

    // ---- getLatencyPos ----

    /** 1.21.1 EnderDragon.getLatencyPos(i, partialTick), called */
    public static double[] getLatencyPos(class_1510 dragon, int index, float partialTick) {
        if (dragon instanceof LegacyLatency legacy) {
            return legacy.method_6817(index, partialTick);
        }
        return superGetLatencyPos(dragon, index, partialTick);
    }

    /** 1.21.1 EnderDragon's own getLatencyPos body, on the history's positions */
    public static double[] superGetLatencyPos(class_1510 dragon, int index, float partialTick) {
        if (dragon.method_31481()) {
            partialTick = 0.0F;
        }
        return latency(history(dragon), index, partialTick);
    }

    /** 1.21.1's interpolation of positions[(p - i) & 63] towards positions[(p - i - 1) & 63] */
    public static double[] latency(History h, int index, float partialTick) {
        double[][] positions = h.cb$positions();
        int pointer = h.cb$pointer();
        float t = 1.0F - partialTick;
        int a = pointer - index & 63;
        int b = pointer - index - 1 & 63;
        double[] out = new double[3];
        double base = positions[a][0];
        double delta = class_3532.method_15338(positions[b][0] - base);
        out[0] = base + delta * (double) t;
        base = positions[a][1];
        delta = positions[b][1] - base;
        out[1] = base + delta * (double) t;
        out[2] = class_3532.method_16436((double) t, positions[a][2], positions[b][2]);
        return out;
    }

    /** a 1.21.1 latency position (yRot, y, _) as 1.21.2's sample (y, yRot) */
    public static class_9871.class_9872 sample(double[] pos) {
        return new class_9871.class_9872(pos[1], (float) pos[0]);
    }

    /** a 1.21.2 sample as the 1.21.1 latency position 1.21.1's vanilla handed getHeadPartYOffset */
    public static double[] position(class_9871.class_9872 sample) {
        return new double[] {(double) sample.comp_2917(), sample.comp_2916(), 0.0};
    }

    // ---- getHeadPartYOffset ----

    /** 1.21.1 EnderDragon.getHeadPartYOffset(i, a, b), called */
    public static float getHeadPartYOffset(class_1510 dragon, int index, double[] a, double[] b) {
        if (dragon instanceof LegacyHeadOffset legacy) {
            return legacy.method_6823(index, a, b);
        }
        return superGetHeadPartYOffset(dragon, index, a, b);
    }

    /** 1.21.1 EnderDragon's own getHeadPartYOffset body */
    public static float superGetHeadPartYOffset(class_1510 dragon, int index, double[] a, double[] b) {
        class_1521 phase = dragon.method_6831().method_6864();
        class_1527<? extends class_1521> type = phase.method_6849();
        double offset;
        if (type == class_1527.field_7067 || type == class_1527.field_7077) {
            class_2338 egg = dragon.method_37908().method_8598(class_2902.class_2903.field_13203,
                class_3033.method_51862(dragon.method_51854()));
            double dist = Math.max(Math.sqrt(egg.method_19770(dragon.method_19538())) / 4.0, 1.0);
            offset = (double) index / dist;
        } else if (phase.method_6848()) {
            offset = (double) index;
        } else if (index == 6) {
            offset = 0.0;
        } else {
            offset = b[1] - a[1];
        }
        return (float) offset;
    }

    // ---- reallyHurt ----

    /** a mod dragon's own 1.21.1 reallyHurt(source, amount) (override hook adapter) */
    public interface LegacyReallyHurt {
        boolean method_6819(class_1282 source, float amount);
    }

    /** the super accessor the reallyHurt hook adds to such a class */
    public interface ReallyHurtSuper {
        void cb$super$method_6819(class_3218 level, class_1282 source, float amount);
    }

    /** the entity whose LivingEntity.hurtServer answer CbEntityHurtResultMixin records, and the answer */
    private static final ThreadLocal<Object[]> HURT_RESULT = new ThreadLocal<>();

    /** from CbEntityHurtResultMixin: LivingEntity.hurtServer of {@code entity} answered {@code result} */
    public static void hurtResult(Object entity, boolean result) {
        Object[] capture = HURT_RESULT.get();
        if (capture != null && capture[0] == entity) {
            capture[1] = result;
        }
    }

    /**
     * 1.21.1 EnderDragon.reallyHurt(source, amount) answered super.hurt (Mob's, i.e. LivingEntity's) -- false on a
     * client level; 1.21.2's reallyHurt(level, source, amount) runs LivingEntity.hurtServer and drops its answer
     * (1.21.1's vanilla caller dropped it too). The answer is recorded while {@code run} runs on the server.
     */
    private static boolean capturing(class_1510 dragon, java.util.function.Consumer<class_3218> run) {
        if (!(dragon.method_37908() instanceof class_3218 level)) {
            return false;
        }
        Object[] outer = HURT_RESULT.get();
        Object[] capture = {dragon, Boolean.FALSE};
        HURT_RESULT.set(capture);
        try {
            run.accept(level);
            return (Boolean) capture[1];
        } finally {
            HURT_RESULT.set(outer);
        }
    }

    /** 1.21.1 EnderDragon.reallyHurt(source, amount), called (protected: from the dragon's class) */
    public static boolean reallyHurt(class_1510 dragon, class_1282 source, float amount) {
        if (dragon instanceof LegacyReallyHurt legacy) {
            return legacy.method_6819(source, amount);
        }
        return capturing(dragon, level -> ((CbEntityDragonInvoker) dragon).cb$reallyHurt(level, source, amount));
    }

    /** 1.21.2's reallyHurt(level, source, amount) of a mod dragon overriding 1.21.1's: its answer is dropped, as vanilla's */
    public static void reallyHurtBridge(LegacyReallyHurt self, class_3218 level, class_1282 source, float amount) {
        self.method_6819(source, amount);
    }

    /** super.reallyHurt(source, amount) of such an override: super's 1.21.2 reallyHurt, answering hurtServer's result */
    public static boolean superReallyHurt(ReallyHurtSuper self, class_1282 source, float amount) {
        return capturing((class_1510) self, level -> self.cb$super$method_6819(level, source, amount));
    }

    // ---- WitherBoss ----

    /** 1.21.1 WitherBoss.getHeadYRot(i), called */
    public static float getHeadYRot(class_1528 wither, int head) {
        if (wither instanceof LegacyHeadYRot legacy) {
            return legacy.method_6879(head);
        }
        return superGetHeadYRot(wither, head);
    }

    public static float superGetHeadYRot(class_1528 wither, int head) {
        return ((WitherHeads) wither).cb$yRotHeads()[head];
    }

    /** 1.21.1 WitherBoss.getHeadXRot(i), called */
    public static float getHeadXRot(class_1528 wither, int head) {
        if (wither instanceof LegacyHeadXRot legacy) {
            return legacy.method_6887(head);
        }
        return superGetHeadXRot(wither, head);
    }

    public static float superGetHeadXRot(class_1528 wither, int head) {
        return ((WitherHeads) wither).cb$xRotHeads()[head];
    }

    private CbEntityDragon() {
    }
}

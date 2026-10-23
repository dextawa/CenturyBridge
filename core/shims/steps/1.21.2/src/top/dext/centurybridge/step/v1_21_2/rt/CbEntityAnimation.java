package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_3532;
import net.minecraft.class_7094;

/**
 * 1.21.1's AnimationState clock on 1.21.2. 1.21.1 kept the animation's time itself: updateTime(ageInTicks,
 * speed), called each frame, advanced an accumulated time by the elapsed milliseconds times the speed, and
 * getAccumulatedTime() answered it. 1.21.2 keeps only the start tick (getTimeInMillis(ageInTicks), no speed).
 * The 1.21.1 clock lives on beside it (CbEntityAnimationStateMixin), started, stopped, fast-forwarded and
 * copied with 1.21.2's state.
 */
public final class CbEntityAnimation {

    /** the 1.21.1 clock fields CbEntityAnimationStateMixin adds */
    public interface Clock {
        long cb$lastTime();

        long cb$accumulatedTime();

        void cb$set(long lastTime, long accumulatedTime);
    }

    /** a mod subclass's own 1.21.1 updateTime / getAccumulatedTime (adapter hooks) */
    public interface LegacyUpdate {
        void method_43686(float ageInTicks, float speed);
    }

    public interface LegacyAccumulated {
        long method_43687();
    }

    /** 1.21.1 AnimationState.updateTime(ageInTicks, speed), called: a mod subclass's own override answers */
    public static void updateTime(class_7094 state, float ageInTicks, float speed) {
        if (state instanceof LegacyUpdate legacy) {
            legacy.method_43686(ageInTicks, speed);
            return;
        }
        superUpdateTime(state, ageInTicks, speed);
    }

    /** 1.21.1 AnimationState's own updateTime body (a mod subclass's super call) */
    public static void superUpdateTime(class_7094 state, float ageInTicks, float speed) {
        if (!state.method_41327()) {
            return;
        }
        Clock c = (Clock) state;
        long now = class_3532.method_15372((double) (ageInTicks * 1000.0F / 20.0F));
        c.cb$set(now, c.cb$accumulatedTime() + (long) ((float) (now - c.cb$lastTime()) * speed));
    }

    /** 1.21.1 AnimationState.getAccumulatedTime(), called */
    public static long getAccumulatedTime(class_7094 state) {
        if (state instanceof LegacyAccumulated legacy) {
            return legacy.method_43687();
        }
        return superGetAccumulatedTime(state);
    }

    /** 1.21.1 AnimationState's own getAccumulatedTime body */
    public static long superGetAccumulatedTime(class_7094 state) {
        return ((Clock) state).cb$accumulatedTime();
    }

    private CbEntityAnimation() {
    }
}

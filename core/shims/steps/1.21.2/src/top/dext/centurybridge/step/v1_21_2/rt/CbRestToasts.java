package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Map;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_368;
import net.minecraft.class_374;

/**
 * 1.21.1's Toast on 1.21.2's.
 *
 * 1.21.1's toast had one abstract method, render(graphics, toastComponent, timeSinceVisible), which drew the
 * toast and answered whether it should stay shown. 1.21.2 split it: update(manager, time) advances the
 * toast's state, getWantedVisibility() answers the visibility, render(graphics, font, time) draws. A 1.21.1
 * toast is asked through 1.21.2's render, where its own render runs (it draws there, as in 1.21.1) and its
 * answer is kept; the kept answer is the visibility the toast manager reads next (CbRestToastInstanceMixin),
 * one frame after it was given (1.21.1's manager read it in the same frame -- the start of the hide
 * animation moves by that frame). Until it has rendered once the toast is shown, as a 1.21.1 toast was
 * when added. Toasts written as a lambda become {@link LambdaToast}.
 */
@Environment(EnvType.CLIENT)
public final class CbRestToasts {

    private CbRestToasts() {
    }

    /** a 1.21.1 toast class: its own render (adapter of the render override hook) */
    public interface LegacyToast {
        class_368.class_369 method_1986(class_332 graphics, class_374 manager, long time);
    }

    /** super's 1.21.2 render and visibility, for a 1.21.1 subclass of a vanilla toast calling super.render */
    public interface ToastSuper {
        void cb$super$method_1986(class_332 graphics, class_327 font, long time);

        class_368.class_369 cb$super$method_61988();
    }

    private static final class State {
        class_374 manager;
        class_368.class_369 visibility;
    }

    private static final Map<Object, State> STATES = new WeakHashMap<>();

    private static State state(Object toast) {
        return STATES.computeIfAbsent(toast, t -> new State());
    }

    private static class_374 manager(Object toast) {
        State s = STATES.get(toast);
        return s != null && s.manager != null ? s.manager : net.minecraft.class_310.method_1551().method_1566();
    }

    /** 1.21.2's render(graphics, font, time) of a 1.21.1 toast: its own render, its answer kept */
    public static void render(LegacyToast toast, class_332 graphics, class_327 font, long time) {
        class_368.class_369 answer = toast.method_1986(graphics, manager(toast), time);
        state(toast).visibility = answer;
    }

    /** 1.21.2's update(manager, time) of a 1.21.1 toast implementing Toast itself: the manager kept for its render */
    public static void update(class_368 toast, class_374 manager, long time) {
        state(toast).manager = manager;
    }

    /** 1.21.2's getWantedVisibility() of a 1.21.1 toast implementing Toast itself: its last answer, shown before */
    public static class_368.class_369 wantedVisibility(class_368 toast) {
        State s = STATES.get(toast);
        return s != null && s.visibility != null ? s.visibility : class_368.class_369.field_2210;
    }

    /** the visibility the toast manager reads: a 1.21.1 toast's last answer where it gave one, else the toast's own */
    public static class_368.class_369 managerVisibility(class_368 toast) {
        if (toast instanceof LegacyToast) {
            State s = STATES.get(toast);
            if (s != null && s.visibility != null) {
                return s.visibility;
            }
        }
        return toast.method_61988();
    }

    /**
     * a 1.21.1 subclass of a vanilla toast calling super.render(graphics, component, time): the vanilla
     * toast's 1.21.2 render (its state already advanced by the manager's update) and its visibility, which is
     * what its 1.21.1 render answered
     */
    public static class_368.class_369 superRender(ToastSuper toast, class_332 graphics, class_374 manager, long time) {
        toast.cb$super$method_1986(graphics, manager.method_1995().field_1772, time);
        return toast.cb$super$method_61988();
    }

    /**
     * toast.render(graphics, component, time) called by a 1.21.1 mod: a 1.21.1 toast's own render; a 1.21.2
     * toast advanced, drawn and asked for its visibility, which together are what 1.21.1's render did
     */
    public static class_368.class_369 callRender(class_368 toast, class_332 graphics, class_374 manager, long time) {
        if (toast instanceof LegacyToast legacy) {
            return legacy.method_1986(graphics, manager, time);
        }
        toast.method_61989(manager, time);
        toast.method_1986(graphics, manager.method_1995().field_1772, time);
        return toast.method_61988();
    }

    /** a 1.21.1 toast lambda (lambdaAdapters) */
    public static class_368 toast(LegacyToast lambda) {
        return new LambdaToast(lambda);
    }

    /** a 1.21.1 lambda toast as a 1.21.2 toast (default width and height, no token, as the lambda had) */
    public static final class LambdaToast implements class_368, LegacyToast {
        private final LegacyToast lambda;
        private class_374 manager;
        private class_368.class_369 visibility = class_368.class_369.field_2210;

        LambdaToast(LegacyToast lambda) {
            this.lambda = lambda;
        }

        @Override
        public class_368.class_369 method_1986(class_332 graphics, class_374 manager, long time) {
            return this.lambda.method_1986(graphics, manager, time);
        }

        @Override
        public class_368.class_369 method_61988() {
            return this.visibility;
        }

        @Override
        public void method_61989(class_374 manager, long time) {
            this.manager = manager;
        }

        @Override
        public void method_1986(class_332 graphics, class_327 font, long time) {
            class_374 m = this.manager != null ? this.manager : net.minecraft.class_310.method_1551().method_1566();
            this.visibility = this.lambda.method_1986(graphics, m, time);
        }
    }
}

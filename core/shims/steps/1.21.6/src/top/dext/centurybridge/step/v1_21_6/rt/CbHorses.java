package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * AbstractHorse.setStanding(boolean) of 1.21.5 (javap): when standing, stop eating; then set the STANDING flag (32) to
 * the argument. 1.21.6's setStanding(int ticks) also starts a stand timer, and its clearStanding resets it, so 1.21.5's
 * flag-only body is run on 1.21.6's members (setEating, setFlag -- the latter opened by the layer's access widener).
 */
public final class CbHorses {

    private CbHorses() {
    }

    /** 1.21.5's restored AbstractHorse jump flag members (CbHorseJumpRestoredMixin), as the route calls them */
    public interface LegacyJump {
        boolean method_6763();

        void method_6758(boolean jumping);
    }

    /** setStanding(boolean) */
    public static void method_6737(net.minecraft.class_1496 horse, boolean standing) {
        if (standing) {
            horse.method_6740(false);
        }
        horse.method_6769(32, standing);
    }
}

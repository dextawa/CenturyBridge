package top.dext.centurybridge.step.v1_20_3.rt;

/**
 * What a restoring layer mixin (at {@link CbVanillaReads#PRIORITY}) shares with the injectors into 1.20.3's own
 * methods that were split off it into a default-priority {@code <name>Hooks} mixin: the state and restored members both
 * use, reached through the restorer (its {@code @Unique} state is the restorer's own; its restored members are called
 * virtually, so a subclass's override still answers).
 */
public final class CbPrioritySplit {
    private CbPrioritySplit() {
    }

    /** CbRealmsWorldSlotButtonMixin: the 1.20.2 tooltip setter a button built with 1.20.2's constructor was given */
    public interface SlotButtonTooltip {
        java.util.function.Consumer<net.minecraft.class_2561> cb$v1_20_3$tooltipSetter();
    }

    /** CbWorldPlayerMixin: 1.20.2's checkMovementStatistics, called virtually */
    public interface PlayerMovementStatistics {
        void cb$v1_20_3$checkMovementStatistics(double dx, double dy, double dz);
    }
}

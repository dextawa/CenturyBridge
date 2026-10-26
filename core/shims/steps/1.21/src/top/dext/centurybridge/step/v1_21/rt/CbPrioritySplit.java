package top.dext.centurybridge.step.v1_21.rt;

/**
 * What a restoring layer mixin (at {@link CbVanillaReads#PRIORITY}) shares with the injectors into 1.21's own methods
 * that were split off it into a default-priority {@code <name>Hooks} mixin: the state and restored members both use,
 * reached through the restorer (its {@code @Unique} state is the restorer's own; its restored members are called
 * virtually from the restorer's own code, so a subclass's override still answers). Method names carry the layer
 * ({@code cb$v1_21$}): a game class carries the mixins of every climbed layer.
 */
public final class CbPrioritySplit {
    private CbPrioritySplit() {
    }

    /** CbClientSubtitleMixin: the location and time 1.20.6 kept, set at construction and refresh, and its getters called virtually */
    public interface Subtitle {
        void cb$v1_21$played(net.minecraft.class_243 location, long time);

        net.minecraft.class_243 cb$v1_21$location();

        long cb$v1_21$time();
    }

    /** CbRenderBufferBuilderMixin: 1.20.6's default color (set, being filled) and its fill */
    public interface BufferDefaultColor {
        boolean cb$v1_21$defaultColorSet();

        boolean cb$v1_21$fillingDefault();

        void cb$v1_21$fillDefaultColor();
    }
}

package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * What a restoring client layer mixin (at {@link CbVanillaReads#PRIORITY}) shares with the injectors into 26.2's own
 * methods that were split off it into a default-priority {@code <name>Hooks} mixin: the state both use, reached through
 * the restorer. Method names carry the layer ({@code cb$v26_2$}): a game class carries the mixins of every climbed layer.
 */
@Environment(EnvType.CLIENT)
public final class CbPrioritySplitClient {
    private CbPrioritySplitClient() {
    }

    /** CbS26RenderBuffersMixin: 26.1.2's three buffer sources, built when the render buffers are constructed */
    public interface RenderBufferSources {
        void cb$v26_2$buildSources();
    }
}

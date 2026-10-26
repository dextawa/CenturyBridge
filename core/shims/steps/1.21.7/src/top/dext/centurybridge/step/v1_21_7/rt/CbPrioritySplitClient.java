package top.dext.centurybridge.step.v1_21_7.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * What a restoring layer mixin (at {@link CbVanillaReads#PRIORITY}) shares with the injectors into 1.21.7's own
 * methods that were split off it into a default-priority {@code <name>Hooks} mixin: the state both use, reached through
 * the restorer. Method names carry the layer ({@code cb$v1_21_7$}): a game class carries the mixins of every climbed
 * layer.
 */
@Environment(EnvType.CLIENT)
public final class CbPrioritySplitClient {
    private CbPrioritySplitClient() {
    }

    /** CbItemStackRenderStateIdentity: the elements recorded since the state was last cleared */
    public interface RenderStateIdentity {
        java.util.List<Object> cb$v1_21_7$identityElements();
    }
}

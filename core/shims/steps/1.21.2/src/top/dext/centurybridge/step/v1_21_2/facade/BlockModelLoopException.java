package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's BlockModel.LoopException (net/minecraft/class_793$class_6246, classRenames), gone in 1.21.2: the
 * RuntimeException 1.21.1 threw for a model whose parent chain loops. 1.21.2's ModelDiscovery resolves parents without
 * it, so none is thrown by the game; a mod's own throw / catch / subclass works as in 1.21.1.
 */
@Environment(EnvType.CLIENT)
public class BlockModelLoopException extends RuntimeException {

    public BlockModelLoopException(String message) {
        super(message);
    }
}

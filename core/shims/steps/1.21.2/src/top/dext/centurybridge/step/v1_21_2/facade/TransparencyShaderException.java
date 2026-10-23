package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's LevelRenderer.TransparencyShaderException (net/minecraft/class_761$class_5347, classRenames), gone in
 * 1.21.2 with the transparency post chain it reported: the same RuntimeException with message and cause.
 */
@Environment(EnvType.CLIENT)
public class TransparencyShaderException extends RuntimeException {

    public TransparencyShaderException(String message, Throwable cause) {
        super(message, cause);
    }
}

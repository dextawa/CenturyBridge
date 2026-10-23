package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.client.rendering;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

/**
 * WorldRenderContext.positionMatrix(), abstract in 1.20.5, filled in a mod's own 1.20.4 implementation
 * (abstractFills): 1.20.4's world render handed its contexts the position matrix as the top of the matrix
 * stack they answer, which is where a 1.20.4 context keeps it.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiRendering {

    private CbFapiRendering() {
    }

    public static org.joml.Matrix4f positionMatrix(WorldRenderContext context) {
        return context.matrixStack().method_23760().method_23761();
    }
}

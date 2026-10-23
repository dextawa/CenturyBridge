package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Camera's culling projection (1.21.11's WorldExtractionContext.cullProjectionMatrix; rt/CbFapiWorldContexts) */
@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public interface CbFapiCameraAccess {

    @Invoker("createProjectionMatrixForCulling")
    Matrix4f cb$cullProjectionMatrix();
}

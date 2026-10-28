package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestReloadShaders;

/**
 * ShaderManager.apply(configs, resourceManager, profiler) returning (class_10151.method_62945, after the reload's
 * compilation cache replaced the previous one): 1.21.1's GameRenderer.reloadShaders(resourceManager) on the game
 * renderer (CbRestGameRendererShadersRestored). Adds a call after 1.21.2's apply; replaces nothing 1.21.2 calls.
 * Before the game renderer exists (Minecraft sets it after registering the reload listeners) there is none to call.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10151.class)
public abstract class CbRestShaderReloadRoute {

    @Inject(method = "method_62945", at = @At("RETURN"))
    private void cb$reloadShaders(net.minecraft.class_10151.class_10153 prepared, net.minecraft.class_3300 resourceManager,
                                  net.minecraft.class_3695 profiler, CallbackInfo ci) {
        net.minecraft.class_757 renderer = net.minecraft.class_310.method_1551().field_1773;
        if (renderer != null) {
            ((CbRestReloadShaders) renderer).cb$reloadShaders(resourceManager);
        }
    }
}

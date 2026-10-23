package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.compat.fapi.renderer.CbFapiRenderer;

/** ItemModelResolver.appendItemLayers: the render state notes the stack it is resolved for (CbFapiItemStateMixin) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10442.class)
public abstract class CbFapiItemResolverMixin {
    @Inject(method = "method_65596(Lnet/minecraft/class_10444;Lnet/minecraft/class_1799;Lnet/minecraft/class_811;Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;I)V",
        at = @At("HEAD"))
    private void cb$noteStack(net.minecraft.class_10444 state, net.minecraft.class_1799 stack, net.minecraft.class_811 displayContext,
        net.minecraft.class_1937 level, net.minecraft.class_1309 entity, int seed, CallbackInfo ci) {
        ((CbFapiRenderer.CbItemStackHolder) state).cb$setStack(stack);
    }
}

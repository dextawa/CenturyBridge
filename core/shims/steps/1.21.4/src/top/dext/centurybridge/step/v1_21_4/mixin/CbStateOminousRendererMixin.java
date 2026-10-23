package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.facade.OminousItemSpawnerRenderState;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderStates;

/**
 * OminousItemSpawnerRenderer makes 1.21.3's state (an ItemClusterRenderState with 1.21.3's item fields) and fills those as
 * 1.21.3's extractRenderState did: the item (a copy) and, unless empty, its model.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_9483.class)
public abstract class CbStateOminousRendererMixin {

    @Inject(method = "method_62496", at = @At("HEAD"), cancellable = true)
    private void cb$legacyState(CallbackInfoReturnable<net.minecraft.class_10428> cir) {
        cir.setReturnValue(new OminousItemSpawnerRenderState());
    }

    @Inject(method = "method_62497", at = @At("TAIL"))
    private void cb$legacyItem(net.minecraft.class_9461 spawner, net.minecraft.class_10428 state, float partialTick, CallbackInfo ci) {
        if (state instanceof OminousItemSpawnerRenderState legacy) {
            legacy.field_53495 = spawner.method_58640().method_7972();
            legacy.field_53494 = legacy.field_53495.method_7960() ? null : CbRenderStates.model(legacy.field_53495, spawner.method_37908(), null, 0);
        }
    }
}

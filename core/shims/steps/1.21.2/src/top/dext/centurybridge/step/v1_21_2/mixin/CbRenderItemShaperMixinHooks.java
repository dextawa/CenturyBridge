package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderItemModels;

/**
 * The injectors into 1.21.2's own ItemModelShaper methods that go with {@link CbRenderItemShaperMixin} (its restored
 * 1.21.1 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.1 injectors can anchor inside them):
 * the model manager kept at construction, the cache rebuilt with 1.21.2's, a registered item's model answered.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_763.class)
public abstract class CbRenderItemShaperMixinHooks {

    @Inject(method = "<init>(Lnet/minecraft/class_1092;)V", at = @At("TAIL"))
    private void cb$keepManager(net.minecraft.class_1092 manager, CallbackInfo ci) {
        ((CbPrioritySplitClient.ItemShaper) (Object) this).cb$v1_21_2$modelManager(manager);
    }

    @Inject(method = "method_64056()V", at = @At("TAIL"))
    private void cb$rebuildWithVanilla(CallbackInfo ci) {
        CbPrioritySplitClient.ItemShaper shaper = (CbPrioritySplitClient.ItemShaper) (Object) this;
        if (shaper.cb$v1_21_2$modelManager() != null) {
            shaper.cb$v1_21_2$rebuildCache();
        }
    }

    @Inject(method = "method_3308(Lnet/minecraft/class_1799;)Lnet/minecraft/class_1087;", at = @At("HEAD"), cancellable = true)
    private void cb$registeredModel(net.minecraft.class_1799 stack, CallbackInfoReturnable<net.minecraft.class_1087> cir) {
        int id = net.minecraft.class_1792.method_7880(stack.method_7909());
        if (((CbRenderItemModels.Shaper) (Object) this).cb$shapes().containsKey(id)) {
            CbPrioritySplitClient.ItemShaper shaper = (CbPrioritySplitClient.ItemShaper) (Object) this;
            net.minecraft.class_1087 m = shaper.cb$v1_21_2$cache().get(id);
            cir.setReturnValue(m == null ? shaper.cb$v1_21_2$modelManager().method_4744() : m);
        }
    }
}

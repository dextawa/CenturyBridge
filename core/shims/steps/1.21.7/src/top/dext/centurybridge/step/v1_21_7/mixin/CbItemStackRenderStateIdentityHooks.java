package top.dext.centurybridge.step.v1_21_7.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_7.rt.CbPrioritySplitClient;

/**
 * The injectors into 1.21.7's own ItemStackRenderState methods that go with {@link CbItemStackRenderStateIdentity} (its
 * restored 1.21.6 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.6 injectors can anchor inside
 * them): a plain state records the elements appended while it is updated, and empties the record when it is cleared.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10444.class)
abstract class CbItemStackRenderStateIdentityHooks {

    @Inject(method = "method_70946", at = @At("HEAD"))
    private void cb$recordModelIdentity(Object element, CallbackInfo ci) {
        ((CbPrioritySplitClient.RenderStateIdentity) (Object) this).cb$v1_21_7$identityElements().add(element);
    }

    @Inject(method = "method_65605", at = @At("HEAD"))
    private void cb$resetModelIdentity(CallbackInfo ci) {
        ((CbPrioritySplitClient.RenderStateIdentity) (Object) this).cb$v1_21_7$identityElements().clear();
    }
}

package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplitClient;

/**
 * The injectors into 1.20.5's own AbstractSelectionList methods that go with {@link CbClientSelectionListMixin} (its
 * restored 1.20.4 member stays there, at {@code CbVanillaReads.PRIORITY}): a list 1.20.4 code switched off skips its
 * backdrop and separators.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_350.class)
public abstract class CbClientSelectionListMixinHooks {

    @Inject(method = "method_57715", at = @At("HEAD"), cancellable = true)
    private void cb$listBackground(net.minecraft.class_332 graphics, CallbackInfo ci) {
        if (!((CbPrioritySplitClient.ListBackground) (Object) this).cb$v1_20_5$renderBackground()) {
            ci.cancel();
        }
    }

    @Inject(method = "method_57713", at = @At("HEAD"), cancellable = true)
    private void cb$listSeparators(net.minecraft.class_332 graphics, CallbackInfo ci) {
        if (!((CbPrioritySplitClient.ListBackground) (Object) this).cb$v1_20_5$renderBackground()) {
            ci.cancel();
        }
    }
}

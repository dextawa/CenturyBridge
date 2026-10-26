package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.rt.CbGuiScroll;

/**
 * The injector into 1.21.4's own AbstractSelectionList.scrollBarX that goes with {@link CbGuiScrollListMixin} (its
 * restored 1.21.3 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.3 injectors can anchor inside
 * them): scrollBarX asks a 1.21.3 override of getDefaultScrollbarPosition.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_350.class)
public abstract class CbGuiScrollListMixinHooks {

    @Inject(method = "method_65507", at = @At("HEAD"), cancellable = true)
    private void cb$legacyDefaultScrollbar(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof CbGuiScroll.CbLegacyDefaultScrollbar legacy && CbGuiScroll.enter(this, CbGuiScroll.DEFAULT_SCROLLBAR)) {
            try {
                cir.setReturnValue(legacy.cb$getDefaultScrollbarPosition());
            } finally {
                CbGuiScroll.exit(this, CbGuiScroll.DEFAULT_SCROLLBAR);
            }
        }
    }
}

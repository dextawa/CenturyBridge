package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;

/**
 * The route into 1.20.5's own Fireball.getItem that goes with {@link CbEntityFireballRawMixin} (its restored 1.20.4
 * getItemRaw stays there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.4 injectors can anchor inside it).
 */
@Mixin(net.minecraft.class_3855.class)
public abstract class CbEntityFireballRawMixinHooks {

    /** 1.20.4's getItem: getItemRaw, or the default item when it is empty (1.20.5's reads the synched stack directly) */
    @Inject(method = "method_7495", at = @At("HEAD"), cancellable = true)
    private void cbRoute$getItem(CallbackInfoReturnable<net.minecraft.class_1799> cir) {
        if (CbRoutes.route(CbRoutes.FIREBALL_GET_ITEM_RAW, this)) {
            net.minecraft.class_1799 raw = ((CbPrioritySplit.ItemRaw) (Object) this).cb$v1_20_5$itemRaw();
            cir.setReturnValue(raw.method_7960() ? new net.minecraft.class_1799(net.minecraft.class_1802.field_8814) : raw);
        }
    }
}

package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/** Fireball: 1.20.4's protected getItemRaw() -- the synched item stack itself, empty when unset -- which 1.20.5 dropped */
@Mixin(net.minecraft.class_3855.class)
public abstract class CbEntityFireballRawMixin {

    @Shadow
    @Final
    private static net.minecraft.class_2940<net.minecraft.class_1799> field_17081;

    /** was getItemRaw() */
    @CbVanillaReads("CbEntityFireballRawMixin (cbRoute$getItem): getItem answers from it as 1.20.4's did (empty -> the default item) when a converted mod mixes it")
    protected net.minecraft.class_1799 method_16938() {
        return ((net.minecraft.class_1297) (Object) this).method_5841().method_12789(field_17081);
    }

    /** 1.20.4's getItem: getItemRaw, or the default item when it is empty (1.20.5's reads the synched stack directly) */
    @Inject(method = "method_7495", at = @At("HEAD"), cancellable = true)
    private void cbRoute$getItem(CallbackInfoReturnable<net.minecraft.class_1799> cir) {
        if (CbRoutes.route(CbRoutes.FIREBALL_GET_ITEM_RAW, this)) {
            net.minecraft.class_1799 raw = this.method_16938();
            cir.setReturnValue(raw.method_7960() ? new net.minecraft.class_1799(net.minecraft.class_1802.field_8814) : raw);
        }
    }
}

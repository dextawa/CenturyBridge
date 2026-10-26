package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/** ThrowableItemProjectile: 1.20.4's protected getItemRaw() -- the synched item stack itself -- which 1.20.5 dropped */
@Mixin(net.minecraft.class_3857.class)
public abstract class CbEntityThrowableItemRawMixin {

    @Shadow
    @Final
    private static net.minecraft.class_2940<net.minecraft.class_1799> field_17082;

    /** was getItemRaw() */
    @CbVanillaReads("CbEntityThrowableItemRawMixin (cbRoute$getItem): getItem answers from it as 1.20.4's did (empty -> the default item) when a converted mod mixes it")
    protected net.minecraft.class_1799 method_16943() {
        return ((net.minecraft.class_1297) (Object) this).method_5841().method_12789(field_17082);
    }

    @Shadow
    protected abstract net.minecraft.class_1792 method_16942();

    /** 1.20.4's getItem: getItemRaw, or the default item when it is empty (1.20.5's reads the synched stack directly) */
    @Inject(method = "method_7495", at = @At("HEAD"), cancellable = true)
    private void cbRoute$getItem(CallbackInfoReturnable<net.minecraft.class_1799> cir) {
        if (CbRoutes.route(CbRoutes.THROWABLE_GET_ITEM_RAW, this)) {
            net.minecraft.class_1799 raw = this.method_16943();
            cir.setReturnValue(raw.method_7960() ? new net.minecraft.class_1799(this.method_16942()) : raw);
        }
    }
}

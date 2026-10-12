package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.4 AbstractHorse.getPassengersRidingOffsetY(dimensions, scale): the height 1.20.4's horse put its
 * rider at (before the rearing offset), which mods override. 1.20.5's horse takes that height from its
 * passenger attachment instead. The cover answers that attachment's height (the first passenger's,
 * 1.20.4's horses carried one), so a vanilla horse answers 1.20.5's own value; where a 1.20.4 override
 * answers another height, 1.20.5's point is moved to it, keeping the rearing offset 1.20.5 adds.
 */
@Mixin(net.minecraft.class_1496.class)
public abstract class CbRestAbstractHorseMixin {

    /** 1.20.4 AbstractHorse.getPassengersRidingOffsetY(EntityDimensions, float) */
    protected float method_52550(net.minecraft.class_4048 dimensions, float scale) {
        return cbRest$attachmentHeight(dimensions);
    }

    private float cbRest$attachmentHeight(net.minecraft.class_4048 dimensions) {
        net.minecraft.class_1297 self = (net.minecraft.class_1297) (Object) this;
        return (float) dimensions.comp_2188().method_55679(net.minecraft.class_9064.field_47743, 0, self.method_36454()).field_1351;
    }

    @Inject(method = "method_52533", at = @At("RETURN"), cancellable = true)
    private void cbRest$ridingHeight(net.minecraft.class_1297 passenger, net.minecraft.class_4048 dimensions, float scale,
                                     CallbackInfoReturnable<net.minecraft.class_243> cir) {
        float base = cbRest$attachmentHeight(dimensions);
        float legacy = this.method_52550(dimensions, scale);
        if (legacy != base) {
            net.minecraft.class_243 r = cir.getReturnValue();
            cir.setReturnValue(new net.minecraft.class_243(r.field_1352, r.field_1351 - base + legacy, r.field_1350));
        }
    }
}

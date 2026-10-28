package top.dext.centurybridge.step.v1_21.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import top.dext.centurybridge.step.v1_21.rt.CbMixedMembers;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * The route that goes with {@link CbNbtTagVisitorRestoredMixin}: visitList's two and visitCompound's one nested visit
 * (method_60585) append the restored 1.20.6 method_59850's answer to the visitor's builder instead, when a converted mod mixes
 * it (CbMixedMembers); otherwise 1.21's call runs. Priority CbVanillaReads.ROUTE_FIRST: the innermost wrapper of the call, so
 * a native 1.21 mod's @WrapOperation of it wraps the route and runs either way.
 */
@Mixin(value = net.minecraft.class_5628.class, priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbNbtTagVisitorRestoredMixinHooks {

    @Shadow
    @Final
    private net.minecraft.class_5250 field_27842;

    @WrapOperation(method = {"method_32298(Lnet/minecraft/class_2499;)V", "method_32292(Lnet/minecraft/class_2487;)V"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_5628;method_60585(Lnet/minecraft/class_2520;Z)V"))
    private void cb$v1_21$nested(net.minecraft.class_5628 self, net.minecraft.class_2520 tag, boolean indent, Operation<Void> original) {
        if (CbMixedMembers.mixed("net/minecraft/class_5628.method_59850(Lnet/minecraft/class_2520;Z)Lnet/minecraft/class_2561;")) {
            this.field_27842.method_10852(((CbPrioritySplit.TagVisitorNested) self).cb$v1_21$visitNested(tag, indent));
        } else {
            original.call(self, tag, indent);
        }
    }
}

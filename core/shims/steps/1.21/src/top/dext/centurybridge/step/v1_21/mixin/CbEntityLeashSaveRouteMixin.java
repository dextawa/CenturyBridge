package top.dext.centurybridge.step.v1_21.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbMixedMembers;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * Leashable (class_9817) statics that go with {@link CbEntityLeashSaveRestoredMixin}:
 * <ul>
 * <li>tickLeash (method_60965): its restoreLeashFromSave call (method_60958) runs the restored 1.20.6 Mob.restoreLeashFromSave
 *     (method_5940) in its place for a Mob when a converted mod mixes it (CbMixedMembers); the mirrors are current (tickLeash
 *     read getLeashData just before), and what the restored body wrote into them goes into LeashData at once. Otherwise
 *     1.21's call.</li>
 * <li>setLeashedTo (method_60957) and the client's holder lookup (method_60966): after LeashData.setLeashHolder, the only
 *     in-place write of a LeashData, a Mob's mirrors take the new holder.</li>
 * </ul>
 * Priority CbVanillaReads.ROUTE_FIRST: the route is the innermost wrapper of the call (a native 1.21 mod's @WrapOperation of
 * it wraps the route and keeps running), and the mirrors are set before other mods' injectors at the same points.
 */
@Mixin(value = net.minecraft.class_9817.class, priority = CbVanillaReads.ROUTE_FIRST)
public interface CbEntityLeashSaveRouteMixin {

    @WrapOperation(method = "method_60965(Lnet/minecraft/class_1297;)V", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_9817;method_60958(Lnet/minecraft/class_1297;Lnet/minecraft/class_9817$class_9818;)V"))
    private static void cb$v1_21$restoreLeash(net.minecraft.class_1297 entity, net.minecraft.class_9817.class_9818 data,
                                              Operation<Void> original) {
        if (entity instanceof CbPrioritySplit.LeashSave mob && CbMixedMembers.mixed("net/minecraft/class_1308.method_5940()V")) {
            mob.cb$v1_21$restoreLeashFromSave();
            mob.cb$v1_21$reconcileLeash();
        } else {
            original.call(entity, data);
        }
    }

    @Inject(method = "method_60957(Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;Z)V", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_9817$class_9818;method_60971(Lnet/minecraft/class_1297;)V", shift = At.Shift.AFTER))
    private static void cb$v1_21$holderSet(net.minecraft.class_1297 entity, net.minecraft.class_1297 holder, boolean broadcast,
                                           CallbackInfo ci) {
        if (entity instanceof CbPrioritySplit.LeashSave mob) {
            mob.cb$v1_21$mirrorLeash();
        }
    }

    @Inject(method = "method_60966(Lnet/minecraft/class_1297;)Lnet/minecraft/class_1297;", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_9817$class_9818;method_60971(Lnet/minecraft/class_1297;)V", shift = At.Shift.AFTER))
    private static void cb$v1_21$holderFound(net.minecraft.class_1297 entity, CallbackInfoReturnable<net.minecraft.class_1297> cir) {
        if (entity instanceof CbPrioritySplit.LeashSave mob) {
            mob.cb$v1_21$mirrorLeash();
        }
    }
}

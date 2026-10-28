package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * The injectors into 1.21's own Mob (class_1308) methods that go with {@link CbEntityLeashSaveRestoredMixin} (its mirrors of
 * LeashData and the restored restoreLeashFromSave stay there, at CbVanillaReads.PRIORITY): setLeashData (method_60960) and
 * readAdditionalSaveData (method_5749), the two writes of Mob.leashData, copy the new LeashData into the mirrors; getLeashData
 * (method_60955), through which 1.21 reads the leash, first takes a mirror a converted mod wrote into LeashData. Priority
 * CbVanillaReads.ROUTE_FIRST: they run before other mods' injectors at the same points, which then see the mirrors and
 * LeashData agree.
 */
@Mixin(value = net.minecraft.class_1308.class, priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbEntityLeashSaveRestoredMixinHooks {

    @Inject(method = "method_60960(Lnet/minecraft/class_9817$class_9818;)V", at = @At("RETURN"))
    private void cb$v1_21$leashDataSet(net.minecraft.class_9817.class_9818 data, CallbackInfo ci) {
        ((CbPrioritySplit.LeashSave) this).cb$v1_21$mirrorLeash();
    }

    @Inject(method = "method_5749(Lnet/minecraft/class_2487;)V", at = @At("RETURN"))
    private void cb$v1_21$leashDataRead(net.minecraft.class_2487 tag, CallbackInfo ci) {
        ((CbPrioritySplit.LeashSave) this).cb$v1_21$mirrorLeash();
    }

    @Inject(method = "method_60955()Lnet/minecraft/class_9817$class_9818;", at = @At("HEAD"))
    private void cb$v1_21$leashDataAsked(CallbackInfoReturnable<net.minecraft.class_9817.class_9818> cir) {
        ((CbPrioritySplit.LeashSave) this).cb$v1_21$reconcileLeash();
    }
}

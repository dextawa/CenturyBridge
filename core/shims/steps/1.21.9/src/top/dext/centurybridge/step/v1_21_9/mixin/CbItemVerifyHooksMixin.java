package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_9323;
import net.minecraft.class_9326;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_9.rt.CbPrioritySplit;

/**
 * 1.21.8's ItemStack calls of Item.verifyComponentsAfterLoad(this) that 1.21.9 dropped (javap -c both; the restored
 * member is CbItemRestoredMixin's, reached virtually): at the end of the private constructor (ItemLike, count,
 * PatchedDataComponentMap) and of applyComponents(DataComponentPatch) / applyComponents(DataComponentMap); and at the
 * end of applyValidatedChanges(patch) only when the patch validated (1.21.8 returned early, without the call, after
 * logging the error and restoring the previous patch -- the restore call marks that path). Default priority: they sit
 * among mods' injectors at 1.21.9's order; 1.21.9's own code runs unchanged before them.
 */
@Mixin(class_1799.class)
public abstract class CbItemVerifyHooksMixin {

    @Unique
    private boolean cb$v1_21_9$patchRejected;

    @Unique
    private void cb$v1_21_9$verify() {
        class_1799 self = (class_1799) (Object) this;
        ((CbPrioritySplit.ItemVerify) self.method_7909()).cb$v1_21_9$verifyComponentsAfterLoad(self);
    }

    @Inject(method = "<init>(Lnet/minecraft/class_1935;ILnet/minecraft/class_9335;)V", at = @At("RETURN"))
    private void cb$v1_21_9$verifyAfterCreate(CallbackInfo ci) {
        cb$v1_21_9$verify();
    }

    @Inject(method = "method_57366(Lnet/minecraft/class_9326;)V", at = @At("RETURN"))
    private void cb$v1_21_9$verifyAfterPatch(class_9326 patch, CallbackInfo ci) {
        cb$v1_21_9$verify();
    }

    @Inject(method = "method_57365(Lnet/minecraft/class_9323;)V", at = @At("RETURN"))
    private void cb$v1_21_9$verifyAfterMap(class_9323 components, CallbackInfo ci) {
        cb$v1_21_9$verify();
    }

    @Inject(method = "method_59692(Lnet/minecraft/class_9326;)V", at = @At("HEAD"))
    private void cb$v1_21_9$validatedStart(class_9326 patch, CallbackInfo ci) {
        this.cb$v1_21_9$patchRejected = false;
    }

    @Inject(method = "method_59692(Lnet/minecraft/class_9326;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9335;method_59772(Lnet/minecraft/class_9326;)V"))
    private void cb$v1_21_9$validatedRejected(class_9326 patch, CallbackInfo ci) {
        this.cb$v1_21_9$patchRejected = true;
    }

    @Inject(method = "method_59692(Lnet/minecraft/class_9326;)V", at = @At("RETURN"))
    private void cb$v1_21_9$verifyAfterValidated(class_9326 patch, CallbackInfo ci) {
        boolean rejected = this.cb$v1_21_9$patchRejected;
        this.cb$v1_21_9$patchRejected = false;
        if (!rejected) {
            cb$v1_21_9$verify();
        }
    }
}

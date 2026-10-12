package top.dext.centurybridge.step.v1_20_5.mixin;

import net.minecraft.class_2945;
import net.minecraft.class_9221;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetEntityData;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetEntityDataAccess;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetEntityDataBuilderAccess;

/**
 * SynchedEntityData.Builder: the data object the entity already reads while its builder is filled
 * (opened by CbNetEntityDataConstructionBridge) is the one build() answers, and build() leaves an
 * allocated id undefined the way 1.20.4 did -- see {@link CbNetEntityData}.
 */
@Mixin(class_2945.class_9222.class)
public abstract class CbNetEntityDataBuilderBridge implements CbNetEntityDataBuilderAccess {

    @Shadow
    @Final
    private class_9221 field_49022;

    @Shadow
    @Final
    @Mutable
    private class_2945.class_2946<?>[] field_49023;

    @Unique
    private class_2945 cb$view;

    @Override
    public class_2945 cb$openView() {
        if (this.cb$view == null) {
            this.cb$view = CbNetEntityDataInvoker.cb$new(this.field_49022, this.field_49023);
            ((CbNetEntityDataAccess) this.cb$view).cb$attachBuilder((class_2945.class_9222) (Object) this);
        }
        return this.cb$view;
    }

    @Override
    public void cb$replaceItems(class_2945.class_2946<?>[] items) {
        this.field_49023 = items;
    }

    /**
     * build(): 1.20.5 refuses an allocated id left undefined; 1.20.4 let an entity define it later or
     * never (a read of it crashed, a value for it was skipped -- CbNetEntityDataBridge keeps both). The
     * data object is the one the entity has read from since its builder was opened, or -- built
     * without one -- a new one over the builder's items, which is what 1.20.5's build() makes.
     */
    @Inject(method = "method_56911", at = @At("HEAD"), cancellable = true)
    private void cb$buildAsDefined(CallbackInfoReturnable<class_2945> cir) {
        class_2945 data = this.cb$view;
        if (data == null) {
            data = CbNetEntityDataInvoker.cb$new(this.field_49022, this.field_49023);
        } else {
            ((CbNetEntityDataAccess) data).cb$detachBuilder();
        }
        cir.setReturnValue(data);
    }
}

package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_1936;
import net.minecraft.class_2195;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2513;
import net.minecraft.class_2680;
import net.minecraft.class_4538;
import net.minecraft.class_4970;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldStems;

/**
 * An AttachedStemBlock built through the 1.20.2 constructor (see CbWorldStems) reverts to its fruit's stem
 * and picks its seed through the 1.20.2 fruit and seed supplier.
 */
@Mixin(class_2195.class)
public abstract class CbWorldAttachedStemMixin {

    @Unique
    private CbWorldStems.LegacyStem cb$legacy;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$takeLegacy(class_5321<?> stem, class_5321<?> fruit, class_5321<?> seed,
                               class_4970.class_2251 properties, CallbackInfo ci) {
        this.cb$legacy = CbWorldStems.takePending();
    }

    /**
     * 1.20.2 AttachedStemBlock.updateShape: the fruit gone from the faced side turns this back into the
     * fruit's full-grown stem. Every other update goes on to 1.20.3's code, which for such a stem (its
     * key fields name nothing) does what 1.20.2's did: defer to BushBlock.
     */
    @Inject(method = "method_9559", at = @At("HEAD"), cancellable = true)
    private void cb$legacyUpdateShape(class_2680 state, class_2350 direction, class_2680 neighborState, class_1936 level,
                                      class_2338 pos, class_2338 neighborPos, CallbackInfoReturnable<class_2680> cir) {
        if (this.cb$legacy != null && !neighborState.method_27852(this.cb$legacy.fruit())
                && direction == state.method_11654(class_2195.field_9873)) {
            cir.setReturnValue(this.cb$legacy.fruit().method_10679().method_9564().method_11657(class_2513.field_11584, 7));
        }
    }

    /** 1.20.2: the seed the supplier names */
    @Inject(method = "method_9574", at = @At("HEAD"), cancellable = true)
    private void cb$legacySeed(class_4538 level, class_2338 pos, class_2680 state, CallbackInfoReturnable<class_1799> cir) {
        if (this.cb$legacy != null) {
            cir.setReturnValue(new class_1799(this.cb$legacy.seed().get()));
        }
    }
}

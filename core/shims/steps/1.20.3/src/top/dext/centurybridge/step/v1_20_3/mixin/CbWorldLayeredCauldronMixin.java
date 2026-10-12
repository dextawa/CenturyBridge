package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3611;
import net.minecraft.class_3612;
import net.minecraft.class_4970;
import net.minecraft.class_5556;
import net.minecraft.class_5620;
import net.minecraft.class_5712;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldBlocks;

/**
 * A layered cauldron built through a 1.20.2 constructor fills the way its 1.20.2 predicate says, takes
 * stalactite water when that predicate was LayeredCauldronBlock.RAIN, and handles a burning entity
 * the way its 1.20.2 class did: LayeredCauldronBlock lowered the level, PowderSnowCauldronBlock melted
 * into a water cauldron (1.20.3 decides both by its one precipitation, see CbWorldBlocks.legacyLayeredFill).
 * Cauldrons vanilla 1.20.3 builds itself carry no 1.20.2 predicate and are untouched.
 */
@Mixin(class_5556.class)
public abstract class CbWorldLayeredCauldronMixin {

    @Unique
    private CbWorldBlocks.LegacyCauldron cb$legacy;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$takeLegacy(class_1959.class_1963 precipitation, class_5620.class_8821 interactions,
                               class_4970.class_2251 properties, CallbackInfo ci) {
        this.cb$legacy = CbWorldBlocks.takePendingCauldron();
    }

    /** 1.20.2 LayeredCauldronBlock.handlePrecipitation, with the predicate in place of 1.20.3's one precipitation */
    @Inject(method = "method_9504", at = @At("HEAD"), cancellable = true)
    private void cb$legacyPrecipitation(class_2680 state, class_1937 level, class_2338 pos,
                                        class_1959.class_1963 precipitation, CallbackInfo ci) {
        if (this.cb$legacy == null) {
            return;
        }
        ci.cancel();
        if (!CbWorldCauldronBlockAccess.cb$shouldHandlePrecipitation(level, precipitation) || state.method_11654(class_5556.field_27206) == 3
                || !this.cb$legacy.fill().test(precipitation)) {
            return;
        }
        class_2680 next = (class_2680) state.method_28493(class_5556.field_27206);
        level.method_8501(pos, next);
        level.method_43276(class_5712.field_28733, pos, class_5712.class_7397.method_43287(next));
    }

    /** 1.20.2: water drips in when the fill predicate is LayeredCauldronBlock.RAIN itself */
    @Inject(method = "method_32765", at = @At("HEAD"), cancellable = true)
    private void cb$legacyStalactiteDrip(class_3611 fluid, CallbackInfoReturnable<Boolean> cir) {
        if (this.cb$legacy != null) {
            cir.setReturnValue(fluid == class_3612.field_15910 && this.cb$legacy.fill() == CbWorldBlocks.LAYERED_RAIN);
        }
    }

    /** 1.20.2 LayeredCauldronBlock lowered the level; only PowderSnowCauldronBlock melted into water */
    @Inject(method = "method_36994", at = @At("HEAD"), cancellable = true)
    private void cb$legacyFire(class_2680 state, class_1937 level, class_2338 pos, CallbackInfo ci) {
        if (this.cb$legacy != null && !this.cb$legacy.powderSnow()) {
            ci.cancel();
            class_5556.method_31650(state, level, pos);
        }
    }
}

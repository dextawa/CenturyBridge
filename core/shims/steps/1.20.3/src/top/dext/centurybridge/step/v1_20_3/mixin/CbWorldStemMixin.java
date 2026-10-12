package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2383;
import net.minecraft.class_2513;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_3481;
import net.minecraft.class_4538;
import net.minecraft.class_4970;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_3.facade.StemGrownBlock;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldStems;

/**
 * A StemBlock built through the 1.20.2 constructor (see CbWorldStems) grows its fruit, attaches and
 * picks its seed through the 1.20.2 fruit and seed supplier, and answers 1.20.2's getFruit().
 */
@Mixin(class_2513.class)
public abstract class CbWorldStemMixin {

    @Unique
    private CbWorldStems.LegacyStem cb$legacy;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$takeLegacy(class_5321<?> fruit, class_5321<?> attachedStem, class_5321<?> seed,
                               class_4970.class_2251 properties, CallbackInfo ci) {
        this.cb$legacy = CbWorldStems.takePending();
    }

    /** 1.20.2 StemBlock.randomTick: the fruit and its attached stem come from the 1.20.2 fruit */
    @Inject(method = "method_9514", at = @At("HEAD"), cancellable = true)
    private void cb$legacyRandomTick(class_2680 state, class_3218 level, class_2338 pos, class_5819 random, CallbackInfo ci) {
        if (this.cb$legacy == null) {
            return;
        }
        ci.cancel();
        if (level.method_22335(pos, 0) < 9) {
            return;
        }
        float speed = CbWorldCropBlockAccess.cb$getGrowthSpeed((class_2513) (Object) this, level, pos);
        if (random.method_43048((int) (25.0F / speed) + 1) != 0) {
            return;
        }
        int age = state.method_11654(class_2513.field_11584);
        if (age < 7) {
            level.method_8652(pos, state.method_11657(class_2513.field_11584, age + 1), 2);
            return;
        }
        class_2350 dir = class_2350.class_2353.field_11062.method_10183(random);
        class_2338 at = pos.method_10093(dir);
        class_2680 below = level.method_8320(at.method_10074());
        if (level.method_8320(at).method_26215() && (below.method_27852(class_2246.field_10362) || below.method_26164(class_3481.field_29822))) {
            StemGrownBlock fruit = this.cb$legacy.fruit();
            level.method_8501(at, fruit.method_9564());
            level.method_8501(pos, fruit.method_10680().method_9564().method_11657(class_2383.field_11177, dir));
        }
    }

    /** 1.20.2: the seed the supplier names */
    @Inject(method = "method_9574", at = @At("HEAD"), cancellable = true)
    private void cb$legacySeed(class_4538 level, class_2338 pos, class_2680 state, CallbackInfoReturnable<class_1799> cir) {
        if (this.cb$legacy != null) {
            cir.setReturnValue(new class_1799(this.cb$legacy.seed().get()));
        }
    }

    /**
     * 1.20.2 StemBlock.getFruit(). 1.20.3's own stems name their fruit by key, and that fruit (Blocks.MELON,
     * Blocks.PUMPKIN) is a plain Block, not a StemGrownBlock: there is nothing of the 1.20.2 type to return.
     */
    public StemGrownBlock method_10694() {
        if (this.cb$legacy != null) {
            return this.cb$legacy.fruit();
        }
        throw new IllegalStateException("StemBlock.getFruit(): " + this + " is a 1.20.3 stem; its fruit is a plain Block,"
            + " not a StemGrownBlock");
    }
}

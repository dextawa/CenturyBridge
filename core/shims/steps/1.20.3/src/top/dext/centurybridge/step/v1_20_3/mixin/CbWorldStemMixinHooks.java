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
import net.minecraft.class_5819;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_3.facade.StemGrownBlock;
import top.dext.centurybridge.step.v1_20_3.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldStems;

/**
 * The injectors into 1.20.3's own StemBlock methods that go with {@link CbWorldStemMixin} (its restored 1.20.2 getFruit
 * stays there, at {@code CbVanillaReads.PRIORITY}): a stem built through the 1.20.2 constructor grows, attaches and
 * picks its seed through the 1.20.2 fruit and seed supplier.
 */
@Mixin(class_2513.class)
public abstract class CbWorldStemMixinHooks {

    /** 1.20.2 StemBlock.randomTick: the fruit and its attached stem come from the 1.20.2 fruit */
    @Inject(method = "method_9514", at = @At("HEAD"), cancellable = true)
    private void cb$legacyRandomTick(class_2680 state, class_3218 level, class_2338 pos, class_5819 random, CallbackInfo ci) {
        CbWorldStems.LegacyStem legacy = ((CbPrioritySplit.StemLegacy) (Object) this).cb$v1_20_3$legacyStem();
        if (legacy == null) {
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
            StemGrownBlock fruit = legacy.fruit();
            level.method_8501(at, fruit.method_9564());
            level.method_8501(pos, fruit.method_10680().method_9564().method_11657(class_2383.field_11177, dir));
        }
    }

    /** 1.20.2: the seed the supplier names */
    @Inject(method = "method_9574", at = @At("HEAD"), cancellable = true)
    private void cb$legacySeed(class_4538 level, class_2338 pos, class_2680 state, CallbackInfoReturnable<class_1799> cir) {
        CbWorldStems.LegacyStem legacy = ((CbPrioritySplit.StemLegacy) (Object) this).cb$v1_20_3$legacyStem();
        if (legacy != null) {
            cir.setReturnValue(new class_1799(legacy.seed().get()));
        }
    }
}

package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_5483;
import net.minecraft.class_6010;
import net.minecraft.class_6012;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_5.rt.CbSpawnWeights;

/**
 * Every 1.21.5 WeightedList, once built, tells rt/CbSpawnWeights the weight of each SpawnerData it holds: 1.21.5 keeps a
 * SpawnerData's weight in the list's Weighted entry, 1.21.4's SpawnerData carried it (getWeight()), and a converted mod
 * reads it from the SpawnerData wherever it got one. Observes only: reads the built list, changes nothing, takes none of
 * the constructor's arguments. Default priority: an observer, not a restorer (mixin_priority.py).
 */
@Mixin(class_6012.class)
public abstract class CbWeightedListMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$spawnWeights(CallbackInfo ci) {
        for (class_6010<?> w : ((class_6012<?>) (Object) this).method_34994()) {
            if (w.comp_2542() instanceof class_5483.class_1964 data) {
                CbSpawnWeights.record(data, w.comp_2543());
            }
        }
    }
}

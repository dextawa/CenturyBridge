package cb.smx4c.mixin;

import cb.smx4c.Counters;
import net.minecraft.class_350;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.21.3's AbstractSelectionList.getMaxScroll(): RETURN records the value per list class. */
@Mixin(class_350.class)
public abstract class MaxScrollMixin {
    @Inject(method = "method_25331", at = @At("RETURN"))
    private void cb$max(CallbackInfoReturnable<Integer> cir) {
        String k = this.getClass().getName();
        Counters.MAX_CALLS.merge(k, 1, Integer::sum);
        Counters.MAX_VALUE.put(k, cir.getReturnValue());
    }
}

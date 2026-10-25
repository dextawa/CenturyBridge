package cb.s7gear.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_1860;
import net.minecraft.class_1863;
import net.minecraft.class_1937;
import net.minecraft.class_2371;
import net.minecraft.class_3956;
import net.minecraft.class_7923;
import net.minecraft.class_9695;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * a 1.21.1 mod mixin on RecipeManager.getRemainingItemsFor: an empty bucket left behind comes back filled with water
 * (GearTestMod.remainingReads)
 */
@Mixin(class_1863.class)
public abstract class RemainingItemsMixin {

    @Inject(method = "method_8128", at = @At("RETURN"))
    private <I extends class_9695, T extends class_1860<I>> void cbs7gear$waterBuckets(class_3956<T> type, I input, class_1937 level,
            CallbackInfoReturnable<class_2371<class_1799>> cir) {
        class_2371<class_1799> out = cir.getReturnValue();
        for (int i = 0; i < out.size(); i++) {
            if ("bucket".equals(class_7923.field_41178.method_10221(out.get(i).method_7909()).method_12832())) {
                out.set(i, new class_1799(class_7923.field_41178.method_10223(net.minecraft.class_2960.method_60656("water_bucket"))));
            }
        }
    }
}

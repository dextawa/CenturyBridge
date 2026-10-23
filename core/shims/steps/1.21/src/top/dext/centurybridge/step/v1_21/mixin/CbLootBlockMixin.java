package top.dext.centurybridge.step.v1_21.mixin;

import java.util.function.BiConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbLoot;

/**
 * BlockLootSubProvider.generate(consumer): the provider whose tables are being built, for the 1.20.6 statics
 * a subclass's generate() calls (HAS_SILK_TOUCH, createSilkTouchDispatchTable...), which 1.21 answers from
 * this provider's registries (CbLoot).
 */
@Mixin(net.minecraft.class_7788.class)
public abstract class CbLootBlockMixin {

    @Inject(method = "method_10399(Ljava/util/function/BiConsumer;)V", at = @At("HEAD"))
    private void cbLoot$enter(BiConsumer<?, ?> consumer, CallbackInfo ci) {
        CbLoot.enterBlock((net.minecraft.class_7788) (Object) this);
    }

    @Inject(method = "method_10399(Ljava/util/function/BiConsumer;)V", at = @At("RETURN"))
    private void cbLoot$exit(BiConsumer<?, ?> consumer, CallbackInfo ci) {
        CbLoot.exitBlock((net.minecraft.class_7788) (Object) this);
    }
}

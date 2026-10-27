package cb.smx6c.mixin;

import cb.smx6c.Counters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.6's ModelBakery blockstate helpers that 1.21 moved, same bodies, into BlockStateModelLoader (step wiring s121.json
 * mixinMoves): predicate (method_4725, private static), getValueHelper (method_4724, static) and registerModelGroup
 * (method_21603, private instance; the handler does not read this). HEAD counts each call.
 */
@Mixin(net.minecraft.class_1088.class)
public abstract class ModelBakeryMoveMixin {

    @Inject(method = "method_4725", at = @At("HEAD"))
    private static void cb$predicate(CallbackInfoReturnable<?> cir) {
        Counters.PREDICATE.incrementAndGet();
    }

    @Inject(method = "method_4724", at = @At("HEAD"))
    private static void cb$value(CallbackInfoReturnable<?> cir) {
        Counters.VALUE.incrementAndGet();
    }

    @Inject(method = "method_21603", at = @At("HEAD"))
    private void cb$group(CallbackInfo ci) {
        Counters.GROUP.incrementAndGet();
    }
}

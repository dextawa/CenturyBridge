package cb.skspawnn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla 1.20.5's own read of the placement type: SpawnPlacements.getPlacementType (method_6159) reads
 * Data.comp_2254 with GETFIELD. This mixin at that field read must fire whether or not CenturyBridge's layer is loaded
 * (the step's routes must leave vanilla's path untouched).
 */
@Mixin(net.minecraft.class_1317.class)
public abstract class NativeFieldReadMixin {
    @Inject(method = "method_6159(Lnet/minecraft/class_1299;)Lnet/minecraft/class_9168;",
        at = @At(value = "FIELD", target = "Lnet/minecraft/class_1317$class_1318;comp_2254:Lnet/minecraft/class_9168;", opcode = 180),
        remap = false)
    private static void cbskspawnn$fieldRead(net.minecraft.class_1299<?> type, CallbackInfoReturnable<net.minecraft.class_9168> cir) {
        cb.skspawnn.Driver.fieldReads++;
    }
}

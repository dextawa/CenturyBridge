package cb.skspawnn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** counts calls of 1.20.5 SpawnPlacements.Data.placementType() (comp_2254) */
@Mixin(targets = "net.minecraft.class_1317$class_1318")
public abstract class NativePlacementMixin {
    @Inject(method = "comp_2254", at = @At("HEAD"), remap = false)
    private void cbskspawnn$count(CallbackInfoReturnable<net.minecraft.class_9168> cir) {
        cb.skspawnn.Driver.placementReads++;
    }
}

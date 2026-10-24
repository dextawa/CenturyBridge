package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipes;
import top.dext.centurybridge.step.v26_1.rt.CbWallSpawnEggs;

/**
 * 1.21.11's SpawnEggItem let subclasses override requiredFeatures(); 26.1's Item made it final. A 1.21.11
 * subclass's override is renamed (overrideRenames, its adapter) and answers here; its own super call reaches
 * the final member again, which then runs 26.1's body (the reentry rule of rt/CbWallRecipes.enterOverride).
 */
@Mixin(Item.class)
public abstract class CbWallItemFeaturesMixin {

    @Inject(method = "requiredFeatures", at = @At("HEAD"), cancellable = true)
    private void cb$requiredFeatures(CallbackInfoReturnable<FeatureFlagSet> cir) {
        if (this instanceof CbWallSpawnEggs.LegacyRequiredFeatures legacy
            && CbWallRecipes.enterOverride("requiredFeatures", "cb$legacyRequiredFeatures", this)) {
            try {
                cir.setReturnValue(legacy.cb$legacyRequiredFeatures());
            } finally {
                CbWallRecipes.exitOverride("requiredFeatures", this);
            }
        }
    }
}

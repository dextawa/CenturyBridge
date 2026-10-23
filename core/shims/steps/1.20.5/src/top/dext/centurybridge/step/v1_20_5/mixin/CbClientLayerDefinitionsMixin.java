package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientModels;

/**
 * LayerDefinitions.createRoots(): 1.20.4 registered ModelLayers.BREEZE_WIND and BREEZE_EYES with their own
 * definitions; 1.20.5 has one breeze layer. The two locations (rt/CbClientModels) go back in with 1.20.4's
 * definitions, so a 1.20.4 mod's EntityModelSet.bakeLayer of either finds what it found in 1.20.4.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_5600.class)
public abstract class CbClientLayerDefinitionsMixin {

    @Inject(method = "method_32073", at = @At("RETURN"), cancellable = true)
    private static void cb$legacyBreezeLayers(CallbackInfoReturnable<java.util.Map<net.minecraft.class_5601, net.minecraft.class_5607>> cir) {
        com.google.common.collect.ImmutableMap.Builder<net.minecraft.class_5601, net.minecraft.class_5607> all = com.google.common.collect.ImmutableMap.builder();
        all.putAll(cir.getReturnValue());
        all.putAll(CbClientModels.legacyLayers());
        cir.setReturnValue(all.build());
    }
}

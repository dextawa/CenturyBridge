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
 * <p>
 * Only where the release's own roots lack the location: 1.21.9 registers breeze#wind and breeze#eyes again itself
 * (javap 1.21.9 LayerDefinitions.createRoots: ModelLayers field_61685 / field_61689 with 1.21.9's definitions), and a
 * second entry for one location fails the whole resource load (ImmutableMap duplicate key, climbed 1.21.11 client).
 * There the release's definition stands, for its own renderers and for a 1.20.4 mod's bake alike.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_5600.class)
public abstract class CbClientLayerDefinitionsMixin {

    @Inject(method = "method_32073", at = @At("RETURN"), cancellable = true)
    private static void cb$legacyBreezeLayers(CallbackInfoReturnable<java.util.Map<net.minecraft.class_5601, net.minecraft.class_5607>> cir) {
        com.google.common.collect.ImmutableMap.Builder<net.minecraft.class_5601, net.minecraft.class_5607> all = com.google.common.collect.ImmutableMap.builder();
        java.util.Map<net.minecraft.class_5601, net.minecraft.class_5607> roots = cir.getReturnValue();
        all.putAll(roots);
        CbClientModels.legacyLayers().forEach((location, definition) -> {
            if (!roots.containsKey(location)) {
                all.put(location, definition);
            }
        });
        cir.setReturnValue(all.build());
    }
}

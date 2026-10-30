package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
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
 * <p>
 * A @ModifyReturnValue, not a cancellable @Inject + setReturnValue: other layers add their own locations at the same
 * RETURN (26.1's wall layer, 26.2's bed and sign layers), and a cancelled callback returns at once, so every injector
 * applied after the first one was skipped (climbed 26.2 client: "No model for layer minecraft:bed_head#main").
 * ModifyReturnValue handlers chain, each receiving the previous one's map.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_5600.class)
public abstract class CbClientLayerDefinitionsMixin {

    @ModifyReturnValue(method = "method_32073", at = @At("RETURN"))
    private static java.util.Map<net.minecraft.class_5601, net.minecraft.class_5607> cb$legacyBreezeLayers(
            java.util.Map<net.minecraft.class_5601, net.minecraft.class_5607> roots) {
        com.google.common.collect.ImmutableMap.Builder<net.minecraft.class_5601, net.minecraft.class_5607> all = com.google.common.collect.ImmutableMap.builder();
        all.putAll(roots);
        CbClientModels.legacyLayers().forEach((location, definition) -> {
            if (!roots.containsKey(location)) {
                all.put(location, definition);
            }
        });
        return all.build();
    }
}

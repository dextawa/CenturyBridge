package top.dext.centurybridge.step.v26_1.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.dext.centurybridge.step.v26_1.rt.CbWallModelLayers;

/**
 * 26.1's model roots with 1.21.11's baby layers 26.1 dropped (rt/CbWallModelLayers). A @ModifyReturnValue chains
 * with every other layer's injector at the same RETURN; a cancellable @Inject with setReturnValue returned at once
 * and skipped those applied after it (the 26.2 layer's bed and sign layers: "No model for layer bed_head#main").
 */
@Environment(EnvType.CLIENT)
@Mixin(LayerDefinitions.class)
public abstract class CbWallLayerDefinitionsMixin {

    @ModifyReturnValue(method = "createRoots()Ljava/util/Map;", at = @At("RETURN"))
    private static Map<ModelLayerLocation, LayerDefinition> cb$legacyBabyLayers(Map<ModelLayerLocation, LayerDefinition> original) {
        return CbWallModelLayers.withLegacy(original);
    }
}

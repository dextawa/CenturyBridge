package top.dext.centurybridge.step.v26_2.mixin;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.dext.centurybridge.step.v26_2.rt.CbS26SignsClient;

/**
 * 26.1.2's LayerDefinitions.createRoots defined, for every WoodType.values(), the standing and wall sign layers
 * (StandingSignRenderer.createSignLayer(true / false)) and one hanging sign layer per attachment
 * (HangingSignRenderer.createHangingSignLayer) under ModelLayers.create*SignModelName (javap); 26.2's defines none.
 * They are added to 26.2's map once it is built (rt/CbS26SignsClient.addLayers).
 */
@Environment(EnvType.CLIENT)
@Mixin(LayerDefinitions.class)
public abstract class CbS26SignLayersMixin {

    // chained, never cancelled: a cancelled RETURN callback returns at once, so every later createRoots RETURN
    // injector (Fabric API's, the other layers') would be skipped and its layers never defined
    @ModifyReturnValue(method = "createRoots", at = @At("RETURN"))
    private static Map<ModelLayerLocation, LayerDefinition> cbS26$signLayers(Map<ModelLayerLocation, LayerDefinition> original) {
        ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> roots = ImmutableMap.builder();
        roots.putAll(original);
        CbS26SignsClient.addLayers(roots);
        return roots.buildOrThrow();
    }
}

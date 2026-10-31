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
import top.dext.centurybridge.step.v26_2.facade.BedRenderer;
import top.dext.centurybridge.step.v26_2.rt.CbS26BedsClient;

/**
 * 26.1.2's LayerDefinitions.createRoots also defined the bed layers (BED_FOOT -> BedRenderer.createFootLayer,
 * BED_HEAD -> createHeadLayer, javap); 26.2's defines everything else. The two are added to 26.2's map once it is built
 * and checked (26.2 checks only that its own known layers are defined).
 */
@Environment(EnvType.CLIENT)
@Mixin(LayerDefinitions.class)
public abstract class CbS26BedLayersMixin {

    // chained, never cancelled: a cancelled RETURN callback returns at once, so every later createRoots RETURN
    // injector (Fabric API's, the other layers') would be skipped and its layers never defined
    @ModifyReturnValue(method = "createRoots", at = @At("RETURN"))
    private static Map<ModelLayerLocation, LayerDefinition> cbS26$bedLayers(Map<ModelLayerLocation, LayerDefinition> original) {
        ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> roots = ImmutableMap.builder();
        roots.putAll(original);
        roots.put(CbS26BedsClient.BED_FOOT(), BedRenderer.createFootLayer());
        roots.put(CbS26BedsClient.BED_HEAD(), BedRenderer.createHeadLayer());
        return roots.buildOrThrow();
    }
}

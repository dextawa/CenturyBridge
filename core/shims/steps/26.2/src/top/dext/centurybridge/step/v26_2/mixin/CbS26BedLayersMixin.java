package top.dext.centurybridge.step.v26_2.mixin;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
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

    @Inject(method = "createRoots", at = @At("RETURN"), cancellable = true)
    private static void cbS26$bedLayers(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> cir) {
        ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> roots = ImmutableMap.builder();
        roots.putAll(cir.getReturnValue());
        roots.put(CbS26BedsClient.BED_FOOT(), BedRenderer.createFootLayer());
        roots.put(CbS26BedsClient.BED_HEAD(), BedRenderer.createHeadLayer());
        cir.setReturnValue(roots.buildOrThrow());
    }
}

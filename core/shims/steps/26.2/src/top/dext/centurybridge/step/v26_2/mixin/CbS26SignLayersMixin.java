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

    @Inject(method = "createRoots", at = @At("RETURN"), cancellable = true)
    private static void cbS26$signLayers(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> cir) {
        ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> roots = ImmutableMap.builder();
        roots.putAll(cir.getReturnValue());
        CbS26SignsClient.addLayers(roots);
        cir.setReturnValue(roots.buildOrThrow());
    }
}

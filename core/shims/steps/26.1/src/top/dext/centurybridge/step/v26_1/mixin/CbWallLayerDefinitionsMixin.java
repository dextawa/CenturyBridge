package top.dext.centurybridge.step.v26_1.mixin;

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
import top.dext.centurybridge.step.v26_1.rt.CbWallModelLayers;

/** 26.1's model roots with 1.21.11's baby layers 26.1 dropped (rt/CbWallModelLayers) */
@Environment(EnvType.CLIENT)
@Mixin(LayerDefinitions.class)
public abstract class CbWallLayerDefinitionsMixin {

    @Inject(method = "createRoots()Ljava/util/Map;", at = @At("RETURN"), cancellable = true)
    private static void cb$legacyBabyLayers(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> cir) {
        cir.setReturnValue(CbWallModelLayers.withLegacy(cir.getReturnValue()));
    }
}

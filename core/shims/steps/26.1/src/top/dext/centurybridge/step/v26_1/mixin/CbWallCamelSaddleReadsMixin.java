package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.animal.camel.CamelModel;
import net.minecraft.client.model.animal.camel.CamelSaddleModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.CamelRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.animal.camel.Camel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_1.rt.CbMixedMembers;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacySplit;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * 26.1's CamelRenderer constructor builds the saddle layer with the static createCamelSaddleLayer(context, this,
 * layerType, layer); 1.21.11's called its own createCamelSaddleLayer(context) (CbWallCamelSaddleMixin). The restored
 * method builds it when a converted mod mixes it (rt/CbMixedMembers) or this renderer's class overrides it, as 1.21.11's
 * constructor would have reached them; otherwise 26.1's call runs as it is. Default priority: injector into a 26.1
 * method.
 */
@Environment(EnvType.CLIENT)
@Mixin(CamelRenderer.class)
public abstract class CbWallCamelSaddleReadsMixin {

    /** the restored member, as rt/CbMixedMembers names it */
    @Unique
    private static final String CB_SADDLE = "net/minecraft/client/renderer/entity/CamelRenderer.createCamelSaddleLayer"
        + "(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)Lnet/minecraft/client/renderer/entity/layers/SimpleEquipmentLayer;";

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/CamelRenderer;"
        + "createCamelSaddleLayer(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;"
        + "Lnet/minecraft/client/renderer/entity/MobRenderer;Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;"
        + "Lnet/minecraft/client/model/geom/ModelLayerLocation;)Lnet/minecraft/client/renderer/entity/layers/SimpleEquipmentLayer;"))
    private SimpleEquipmentLayer<CamelRenderState, CamelModel, CamelSaddleModel> cb$saddleLayer(
            EntityRendererProvider.Context context, MobRenderer<Camel, CamelRenderState, CamelModel> renderer,
            EquipmentClientInfo.LayerType layerType, ModelLayerLocation layer) {
        if (CbMixedMembers.mixed(CB_SADDLE)
            || CbWallLegacySplit.overrides(getClass(), CamelRenderer.class, "createCamelSaddleLayer", EntityRendererProvider.Context.class)) {
            return ((CbWallLegacyCalls.CamelSaddleLegacy) this).createCamelSaddleLayer(context);
        }
        return CamelRenderer.createCamelSaddleLayer(context, renderer, layerType, layer);
    }
}

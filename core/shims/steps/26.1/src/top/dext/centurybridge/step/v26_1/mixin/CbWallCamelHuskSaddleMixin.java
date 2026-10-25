package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.animal.camel.CamelModel;
import net.minecraft.client.model.animal.camel.CamelSaddleModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.CamelHuskRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.animal.camel.Camel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/**
 * 1.21.11's CamelHuskRenderer.createCamelSaddleLayer(context) (class_12296.method_76299), its override of
 * CamelRenderer's with the husk saddle. 26.1's CamelHuskRenderer extends MobRenderer and calls CamelRenderer's static
 * createCamelSaddleLayer(context, this, CAMEL_HUSK_SADDLE, CAMEL_HUSK_SADDLE) from its constructor; that call is
 * redirected to the restored method, as in CbWallCamelSaddleMixin. Same known difference: no baby saddle model in 26.1
 * (ModelLayers.CAMEL_HUSK_BABY_SADDLE is gone).
 */
@Environment(EnvType.CLIENT)
@Mixin(CamelHuskRenderer.class)
public abstract class CbWallCamelHuskSaddleMixin {

    /** 1.21.11 CamelHuskRenderer.createCamelSaddleLayer (class_12296.method_76299) */
    @CbVanillaReads("CbWallCamelHuskSaddleMixin: the constructor's CamelRenderer.createCamelSaddleLayer(context, this, "
        + "layerType, layer) builds the saddle layer through this method")
    public SimpleEquipmentLayer<CamelRenderState, CamelModel, CamelSaddleModel> createCamelSaddleLayer(
            EntityRendererProvider.Context context) {
        return new SimpleEquipmentLayer<>((CamelHuskRenderer) (Object) this, context.getEquipmentRenderer(),
            EquipmentClientInfo.LayerType.CAMEL_HUSK_SADDLE, state -> state.saddle,
            new CamelSaddleModel(context.bakeLayer(ModelLayers.CAMEL_HUSK_SADDLE)), null);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/CamelRenderer;"
        + "createCamelSaddleLayer(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;"
        + "Lnet/minecraft/client/renderer/entity/MobRenderer;Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;"
        + "Lnet/minecraft/client/model/geom/ModelLayerLocation;)Lnet/minecraft/client/renderer/entity/layers/SimpleEquipmentLayer;"))
    private SimpleEquipmentLayer<CamelRenderState, CamelModel, CamelSaddleModel> cb$saddleLayer(
            EntityRendererProvider.Context context, MobRenderer<Camel, CamelRenderState, CamelModel> renderer,
            EquipmentClientInfo.LayerType layerType, ModelLayerLocation layer) {
        return createCamelSaddleLayer(context);
    }
}

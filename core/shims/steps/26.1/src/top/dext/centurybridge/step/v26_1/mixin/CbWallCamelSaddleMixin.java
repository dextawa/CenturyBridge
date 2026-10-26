package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.animal.camel.CamelModel;
import net.minecraft.client.model.animal.camel.CamelSaddleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.CamelRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * 1.21.11's CamelRenderer.createCamelSaddleLayer(context) (class_7763.method_76299): the saddle layer, built by an
 * overridable instance method the constructor called (CamelHuskRenderer overrode it with the husk saddle). 26.1 made
 * it a static taking the renderer, the layer type and the model layer, called from both constructors.
 *
 * Vanilla's path goes through the restored method: the constructor's static call is redirected to
 * this.createCamelSaddleLayer(context), so an override in a subclass, or a mod's injector in the body, builds the
 * layer as on 1.21.11. The body is 1.21.11's with 26.1's models.
 *
 * Known difference: 26.1 has no baby saddle layer (ModelLayers.CAMEL_BABY_SADDLE is gone, baby camels carry no
 * saddle model), so the baby model is null as in 26.1, and the body has one NEW CamelSaddleModel / bakeLayer less
 * than 1.21.11's; selectors on the baby ones read as anchors gone.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = CamelRenderer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallCamelSaddleMixin implements CbWallLegacyCalls.CamelSaddleLegacy {

    /** 1.21.11 CamelRenderer.createCamelSaddleLayer (class_7763.method_76299) */
    @CbVanillaReads("CbWallCamelSaddleReadsMixin: the constructor's createCamelSaddleLayer(context, this, layerType, "
        + "layer) builds the saddle layer through this method")
    @Override
    public SimpleEquipmentLayer<CamelRenderState, CamelModel, CamelSaddleModel> createCamelSaddleLayer(
            EntityRendererProvider.Context context) {
        return new SimpleEquipmentLayer<>((CamelRenderer) (Object) this, context.getEquipmentRenderer(),
            EquipmentClientInfo.LayerType.CAMEL_SADDLE, state -> state.saddle,
            new CamelSaddleModel(context.bakeLayer(ModelLayers.CAMEL_SADDLE)), null);
    }
}

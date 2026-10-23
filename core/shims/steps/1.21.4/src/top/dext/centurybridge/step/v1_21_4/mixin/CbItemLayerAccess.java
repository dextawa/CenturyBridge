package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ItemStackRenderState.LayerRenderState's tint colours (private tintLayers) and baked model */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10444.class_10446.class)
public interface CbItemLayerAccess {
    @Accessor("field_55349")
    int[] cb$tints();

    /** the layer's baked model (package-private "model") */
    @Accessor("field_55346")
    net.minecraft.class_1087 cb$model();

    /** the layer's special renderer and its argument (private specialRenderer / argumentForSpecialRendering) */
    @Accessor("field_55350")
    net.minecraft.class_10515<Object> cb$special();

    @Accessor("field_55351")
    Object cb$specialArgument();
}

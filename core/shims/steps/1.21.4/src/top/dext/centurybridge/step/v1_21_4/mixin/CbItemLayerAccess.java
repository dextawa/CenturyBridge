package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ItemStackRenderState.LayerRenderState's tint colours (private tintLayers) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10444.class_10446.class)
public interface CbItemLayerAccess {
    @Accessor("field_55349")
    int[] cb$tints();
}

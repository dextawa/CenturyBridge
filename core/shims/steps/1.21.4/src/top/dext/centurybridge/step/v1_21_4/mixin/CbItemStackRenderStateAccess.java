package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ItemStackRenderState's layers (private activeLayerCount / layers): read for the tints a definition gave */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10444.class)
public interface CbItemStackRenderStateAccess {
    @Accessor("field_55339")
    int cb$activeLayerCount();

    @Accessor("field_55340")
    net.minecraft.class_10444.class_10446[] cb$layers();
}

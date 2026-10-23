package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** NativeImage's pixel address, where 1.20.4's copyFromFont rasterized a glyph (rt/CbClientRender) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1011.class)
public interface CbClientNativeImageAccessor {

    /** pixels */
    @Accessor("field_4988")
    long cb$pixels();
}

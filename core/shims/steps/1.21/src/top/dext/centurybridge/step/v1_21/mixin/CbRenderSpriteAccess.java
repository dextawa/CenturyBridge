package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * SpriteCoordinateExpander's delegate and sprite: 1.20.6's expander forwarded endVertex and defaultColor to what it wraps, and its whole vertex with the sprite's UVs (CbRenderWrappers, CbRenderLegacySprite).
 */
@Mixin(net.minecraft.class_4723.class)
public interface CbRenderSpriteAccess {

    @Accessor("field_21730")
    net.minecraft.class_4588 cb$delegate();

    @Accessor("field_21731")
    net.minecraft.class_1058 cb$sprite();
}

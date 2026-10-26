package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** 1.21.1 EmptyGlyph.render(...): draws nothing (1.21.1's empty override of BakedGlyph.render) */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_384.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderEmptyGlyphMixin {

    public void method_2025(boolean italic, float x, float y, org.joml.Matrix4f matrix, net.minecraft.class_4588 consumer, float r, float g, float b,
                            float a, int light) {
    }
}

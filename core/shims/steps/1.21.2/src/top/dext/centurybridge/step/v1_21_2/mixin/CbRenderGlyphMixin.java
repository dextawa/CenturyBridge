package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 BakedGlyph.render(italic, x, y, matrix, consumer, r, g, b, a, light): the glyph's quad, 1.21.1's body on the
 * same eight fields 1.21.2 keeps (u0/u1/v0/v1, left/right/up/down). 1.21.2 renders a GlyphInstance (styled, packed
 * colour) instead; the 1.21.1 call draws the quad as 1.21.1 did, with the float colour.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_382.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderGlyphMixin {

    @Shadow
    @Final
    private float field_2276;

    @Shadow
    @Final
    private float field_2275;

    @Shadow
    @Final
    private float field_2274;

    @Shadow
    @Final
    private float field_2273;

    @Shadow
    @Final
    private float field_2272;

    @Shadow
    @Final
    private float field_2280;

    @Shadow
    @Final
    private float field_2279;

    @Shadow
    @Final
    private float field_2278;

    public void method_2025(boolean italic, float x, float y, org.joml.Matrix4f matrix, net.minecraft.class_4588 consumer, float r, float g, float b,
                            float a, int light) {
        float x0 = x + this.field_2272;
        float x1 = x + this.field_2280;
        float y0 = y + this.field_2279;
        float y1 = y + this.field_2278;
        float s0 = italic ? 1.0F - 0.25F * this.field_2279 : 0.0F;
        float s1 = italic ? 1.0F - 0.25F * this.field_2278 : 0.0F;
        consumer.method_22918(matrix, x0 + s0, y0, 0.0F).method_22915(r, g, b, a).method_22913(this.field_2276, this.field_2274).method_60803(light);
        consumer.method_22918(matrix, x0 + s1, y1, 0.0F).method_22915(r, g, b, a).method_22913(this.field_2276, this.field_2273).method_60803(light);
        consumer.method_22918(matrix, x1 + s1, y1, 0.0F).method_22915(r, g, b, a).method_22913(this.field_2275, this.field_2273).method_60803(light);
        consumer.method_22918(matrix, x1 + s0, y0, 0.0F).method_22915(r, g, b, a).method_22913(this.field_2275, this.field_2274).method_60803(light);
    }
}

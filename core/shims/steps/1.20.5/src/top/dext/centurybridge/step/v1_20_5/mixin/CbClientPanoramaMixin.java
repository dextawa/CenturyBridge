package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * PanoramaRenderer.render(delta, alpha): 1.20.4's -- the spin and bob advanced by the frame delta scaled by
 * the panorama-speed option, the cube map drawn at that alpha. 1.20.5's render(graphics, width, height, fade,
 * partialTick) does the same and then draws the panorama overlay texture over it; 1.20.4's drew no overlay.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_766.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientPanoramaMixin {

    @Shadow
    @Final
    private net.minecraft.class_310 field_4139;

    @Shadow
    @Final
    private net.minecraft.class_751 field_4141;

    @Shadow
    private float field_40510;

    @Shadow
    private float field_40511;

    @Shadow
    private static float method_45780(float value, float max) {
        throw new AssertionError();
    }

    /** 1.20.4 PanoramaRenderer.render(delta, alpha) */
    public void method_3317(float delta, float alpha) {
        float speed = (float) ((double) delta * this.field_4139.field_1690.method_45581().method_41753());
        this.field_40510 = method_45780(this.field_40510 + speed * 0.1F, 360.0F);
        this.field_40511 = method_45780(this.field_40511 + speed * 0.001F, (float) (Math.PI * 2));
        this.field_4141.method_3156(this.field_4139, 10.0F, -this.field_40510, alpha);
    }
}

package cb.mixintest.mixin;

import cb.mixintest.Probes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(net.minecraft.class_1309.class)
public abstract class LivingEntityMixin implements Probes.LivingProbe {
    /** lyHeadRot: renamed in 1.20.2 (step 1 fieldRenames) */
    @Shadow
    protected double field_6242;

    @Override
    public double cbtest$shadowLyHeadRot() {
        return this.field_6242;
    }

    @Override
    public void cbtest$shadowSetLyHeadRot(double v) {
        this.field_6242 = v;
    }
}

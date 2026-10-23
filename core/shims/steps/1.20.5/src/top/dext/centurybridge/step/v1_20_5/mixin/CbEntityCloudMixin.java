package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbPotions;

/**
 * AreaEffectCloud: 1.20.4's potion and color members, which 1.20.5 folded into one PotionContents (potion,
 * custom color, custom effects) and a particle that carries the color. Added back so a 1.20.4 call, super
 * call and subclass override all have the member again (1.20.5 itself never calls them; 1.20.4's lingering
 * potion did, 1.20.5's sets the contents). A fixed color is 1.20.5's custom color.
 */
@Mixin(net.minecraft.class_1295.class)
public abstract class CbEntityCloudMixin {

    @Shadow
    private net.minecraft.class_1844 field_49209;

    @Shadow
    public abstract void method_57280(net.minecraft.class_1844 contents);

    @Shadow
    public abstract net.minecraft.class_2394 method_5600();

    /** was setPotion(potion): the contents' potion, its color and effects kept */
    public void method_5612(net.minecraft.class_1842 potion) {
        net.minecraft.class_1844 c = this.field_49209;
        @SuppressWarnings("unchecked")
        java.util.Optional<net.minecraft.class_6880<net.minecraft.class_1842>> holder = CbPotions.holder(potion);
        this.method_57280(new net.minecraft.class_1844(holder, c.comp_2379(), c.comp_2380()));
    }

    /** was getPotion(): the empty potion for none */
    public net.minecraft.class_1842 method_35048() {
        return CbPotions.potion(this.field_49209.comp_2378());
    }

    /** was setFixedColor(color): the contents' custom color */
    public void method_5602(int color) {
        net.minecraft.class_1844 c = this.field_49209;
        this.method_57280(new net.minecraft.class_1844(c.comp_2378(), java.util.Optional.of(color), c.comp_2380()));
    }

    /**
     * was getColor() (the synched color, read on both sides): the color the cloud's particle carries, as
     * 1.20.4's RGB; a particle that carries none -- the server's contents' color then, as 1.20.5 computes it
     * for the particle (0 for no contents, as 1.20.4's default).
     */
    public int method_5606() {
        if (this.method_5600() instanceof net.minecraft.class_9381 p) {
            return Math.round(p.method_58259() * 255.0F) << 16 | Math.round(p.method_58263() * 255.0F) << 8
                | Math.round(p.method_58264() * 255.0F);
        }
        net.minecraft.class_1844 c = this.field_49209;
        return c.equals(net.minecraft.class_1844.field_49274) ? 0 : c.method_8064() & 0xFFFFFF;
    }
}

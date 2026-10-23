package top.dext.centurybridge.step.v1_20_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's GustDustParticle (classRenames), gone in 1.20.5 with its particle type: a texture-sheet particle
 * drifting outward in the horizontal plane, its colour fading from grey to white over its 18-21 tick life.
 * 1.20.4's class on 1.20.5's TextureSheetParticle, member for member.
 */
@Environment(EnvType.CLIENT)
public class GustDustParticle extends net.minecraft.class_4003 {

    /** START_COLOR */
    public final org.joml.Vector3f field_47451 = new org.joml.Vector3f(0.5F, 0.5F, 0.5F);
    /** END_COLOR */
    public final org.joml.Vector3f field_47452 = new org.joml.Vector3f(1.0F, 1.0F, 1.0F);

    public GustDustParticle(net.minecraft.class_638 level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z);
        this.field_3862 = false;
        this.field_3852 = xd + (double) net.minecraft.class_3532.method_32750(this.field_3840, -0.4F, 0.4F);
        this.field_3850 = zd + (double) net.minecraft.class_3532.method_32750(this.field_3840, -0.4F, 0.4F);
        double speed = Math.random() * 2.0D;
        double length = Math.sqrt(this.field_3852 * this.field_3852 + this.field_3869 * this.field_3869 + this.field_3850 * this.field_3850);
        this.field_3852 = this.field_3852 / length * speed * (double) 0.4F;
        this.field_3850 = this.field_3850 / length * speed * (double) 0.4F;
        this.field_17867 *= 2.5F;
        this.field_3852 *= (double) 0.08F;
        this.field_3850 *= (double) 0.08F;
        this.field_3847 = 18 + this.field_3840.method_43048(4);
    }

    @Override
    public void method_3074(net.minecraft.class_4588 buffer, net.minecraft.class_4184 camera, float partialTick) {
        this.method_55241(partialTick);
        super.method_3074(buffer, camera, partialTick);
    }

    /** lerpColors(partialTick) */
    public void method_55241(float partialTick) {
        float t = ((float) this.field_3866 + partialTick) / (float) (this.field_3847 + 1);
        org.joml.Vector3f color = new org.joml.Vector3f(this.field_47451).lerp(this.field_47452, t);
        this.field_3861 = color.x();
        this.field_3842 = color.y();
        this.field_3859 = color.z();
    }

    @Override
    public net.minecraft.class_3999 method_18122() {
        return net.minecraft.class_3999.field_17829;
    }

    @Override
    public void method_3070() {
        if (this.field_3866++ >= this.field_3847) {
            this.method_3085();
        } else {
            this.field_3858 = this.field_3874;
            this.field_3856 = this.field_3871;
            this.method_3069(this.field_3852, 0.0D, this.field_3850);
            this.field_3852 *= 0.99D;
            this.field_3850 *= 0.99D;
        }
    }

    /** 1.20.4 GustDustParticle.GustDustParticleProvider */
    @Environment(EnvType.CLIENT)
    public static class GustDustParticleProvider implements net.minecraft.class_707<net.minecraft.class_2400> {

        public final net.minecraft.class_4002 field_47453;

        public GustDustParticleProvider(net.minecraft.class_4002 sprites) {
            this.field_47453 = sprites;
        }

        public net.minecraft.class_703 method_55242(net.minecraft.class_2400 type, net.minecraft.class_638 level, double x, double y, double z,
                                                    double xd, double yd, double zd) {
            GustDustParticle particle = new GustDustParticle(level, x, y, z, xd, yd, zd);
            particle.method_18140(this.field_47453);
            return particle;
        }

        @Override
        public net.minecraft.class_703 method_3090(net.minecraft.class_2400 type, net.minecraft.class_638 level, double x, double y, double z,
                                                   double xd, double yd, double zd) {
            return this.method_55242(type, level, x, y, z, xd, yd, zd);
        }
    }
}

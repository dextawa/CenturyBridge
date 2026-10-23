package top.dext.centurybridge.step.v1_20_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's SpellParticle.AmbientMobProvider (classRenames): the ambient (beacon) effect particle provider,
 * gone in 1.20.5 where ambient effect particles are coloured particle options. A spell particle with the
 * velocity it is given, that velocity as its colour, at alpha 0.15 -- 1.20.4's body.
 */
@Environment(EnvType.CLIENT)
public class SpellParticleAmbientMobProvider implements net.minecraft.class_707<net.minecraft.class_2400> {

    public final net.minecraft.class_4002 field_17871;

    public SpellParticleAmbientMobProvider(net.minecraft.class_4002 sprites) {
        this.field_17871 = sprites;
    }

    public net.minecraft.class_703 method_3096(net.minecraft.class_2400 type, net.minecraft.class_638 level, double x, double y, double z,
                                               double xd, double yd, double zd) {
        net.minecraft.class_711 particle = top.dext.centurybridge.step.v1_20_5.mixin.CbClientSpellParticleInvoker
            .cb$newSpell(level, x, y, z, xd, yd, zd, this.field_17871);
        ((top.dext.centurybridge.step.v1_20_5.mixin.CbClientParticleInvoker) particle).cb$setAlpha(0.15F);
        particle.method_3084((float) xd, (float) yd, (float) zd);
        return particle;
    }

    @Override
    public net.minecraft.class_703 method_3090(net.minecraft.class_2400 type, net.minecraft.class_638 level, double x, double y, double z,
                                               double xd, double yd, double zd) {
        return this.method_3096(type, level, x, y, z, xd, yd, zd);
    }
}

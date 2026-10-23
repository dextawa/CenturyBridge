package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** SpellParticle(level, x, y, z, xd, yd, zd, sprites), package-private: 1.20.4's spell particle providers build it (facade/SpellParticleAmbientMobProvider, rt/CbClientParticles) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_711.class)
public interface CbClientSpellParticleInvoker {

    @Invoker("<init>")
    static net.minecraft.class_711 cb$newSpell(net.minecraft.class_638 level, double x, double y, double z, double xd, double yd, double zd,
                                               net.minecraft.class_4002 sprites) {
        throw new AssertionError();
    }
}

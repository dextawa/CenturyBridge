package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Particle.setAlpha(alpha), protected: 1.20.4's ambient spell provider set it on the particle it built */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_703.class)
public interface CbClientParticleInvoker {

    @Invoker("method_3083")
    void cb$setAlpha(float alpha);
}

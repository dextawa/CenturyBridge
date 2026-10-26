package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetGame;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * ClientboundExplodePacket: 1.20.4's getExplosionSound() (the sound event; 1.20.5 answers its holder) beside
 * 1.20.5's, reading the packet's field; and 1.20.4's public writeParticle(buf, particle), which 1.20.4's write
 * called for both particles: 1.20.5's writer calls it again where it encodes them (CbNetExplodePacketMixinHooks,
 * default priority), so a 1.20.4 subclass that overrides it still writes its particles its way.
 */
@Mixin(value = net.minecraft.class_2664.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbNetExplodePacketMixin implements CbPrioritySplit.ExplodeParticles {

    @Shadow
    @Final
    private net.minecraft.class_6880<net.minecraft.class_3414> field_47518;

    /** was getExplosionSound() */
    public net.minecraft.class_3414 method_55331() {
        return this.field_47518.comp_349();
    }

    /** was writeParticle(buf, particle) */
    public void method_55628(net.minecraft.class_2540 buf, net.minecraft.class_2394 particle) {
        CbNetGame.writeParticle(buf, particle);
    }

    /** 1.20.4's writeParticle called virtually, for 1.20.5's write (CbNetExplodePacketMixinHooks) */
    @Override
    public void cb$v1_20_5$writeParticle(net.minecraft.class_2540 buf, net.minecraft.class_2394 particle) {
        this.method_55628(buf, particle);
    }
}

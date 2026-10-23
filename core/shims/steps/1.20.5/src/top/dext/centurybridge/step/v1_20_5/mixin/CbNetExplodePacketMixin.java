package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetGame;

/**
 * ClientboundExplodePacket: 1.20.4's getExplosionSound() (the sound event; 1.20.5 answers its holder) beside
 * 1.20.5's, reading the packet's field; and 1.20.4's public writeParticle(buf, particle), which 1.20.4's write
 * called for both particles: 1.20.5's writer calls it again where it encodes them, so a 1.20.4 subclass that
 * overrides it still writes its particles its way.
 */
@Mixin(net.minecraft.class_2664.class)
public abstract class CbNetExplodePacketMixin {

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

    /** write(RegistryFriendlyByteBuf): each particle through this.writeParticle, as 1.20.4's write did */
    @Redirect(method = "method_55877", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_9139;encode(Ljava/lang/Object;Ljava/lang/Object;)V"))
    private void cbS4net$particle(net.minecraft.class_9139<Object, Object> codec, Object buf, Object value) {
        if (codec == (Object) net.minecraft.class_2398.field_48456) {
            this.method_55628((net.minecraft.class_2540) buf, (net.minecraft.class_2394) value);
        } else {
            codec.encode(buf, value);
        }
    }
}

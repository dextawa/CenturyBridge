package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;

/**
 * The injector into 1.20.5's own ClientboundExplodePacket.write that goes with {@link CbNetExplodePacketMixin} (its
 * restored 1.20.4 members stay there, at {@code CbVanillaReads.PRIORITY}): each particle through this.writeParticle,
 * as 1.20.4's write did.
 */
@Mixin(net.minecraft.class_2664.class)
public abstract class CbNetExplodePacketMixinHooks {

    /** write(RegistryFriendlyByteBuf): each particle through this.writeParticle, as 1.20.4's write did */
    @Redirect(method = "method_55877", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_9139;encode(Ljava/lang/Object;Ljava/lang/Object;)V"))
    private void cbS4net$particle(net.minecraft.class_9139<Object, Object> codec, Object buf, Object value) {
        if (codec == (Object) net.minecraft.class_2398.field_48456) {
            ((CbPrioritySplit.ExplodeParticles) (Object) this).cb$v1_20_5$writeParticle((net.minecraft.class_2540) buf, (net.minecraft.class_2394) value);
        } else {
            codec.encode(buf, value);
        }
    }
}

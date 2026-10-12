package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_20_3.rt.CbNet;

/**
 * Connection.send: a score packet 1.20.2 code made as REMOVE goes out as 1.20.3's reset packet, the one a
 * 1.20.3 client understands as the removal ({@link CbNet#cbOutgoing}).
 */
@Mixin(net.minecraft.class_2535.class)
public abstract class CbConnectionMixin {

    @ModifyVariable(method = "method_52906", at = @At("HEAD"), argsOnly = true)
    private net.minecraft.class_2596<?> cb$legacyRemove(net.minecraft.class_2596<?> packet) {
        return CbNet.cbOutgoing(packet);
    }
}

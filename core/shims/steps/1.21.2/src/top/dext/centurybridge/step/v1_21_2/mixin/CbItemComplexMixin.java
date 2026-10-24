package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_2596;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemSmall;

/**
 * 1.21.1's ServerPlayer sent, every tick, each complex item stack in the inventory what its item's
 * getUpdatePacket(stack, level, player) answered (a map's update; null for nothing). 1.21.2 synchronises an
 * inventory stack by its map id instead. For a 1.21.1 complex item (CbItemSmall.complex: a ComplexItem facade
 * subclass, or a MapItem subclass declaring getUpdatePacket) the item's own packet is sent, as 1.21.1 did; any other
 * stack keeps 1.21.2's synchronisation.
 */
@Mixin(class_3222.class)
public abstract class CbItemComplexMixin {

    @Inject(method = "method_64134(Lnet/minecraft/class_1799;)V", at = @At("HEAD"), cancellable = true)
    private void cb$complexItem(class_1799 stack, CallbackInfo ci) {
        CbItemSmall.Legacy_getUpdatePacket complex = CbItemSmall.complex(stack.method_7909());
        if (complex != null) {
            class_3222 self = (class_3222) (Object) this;
            class_2596<?> packet = complex.method_7757(stack, self.method_37908(), self);
            if (packet != null) {
                self.field_13987.method_14364(packet);
            }
            ci.cancel();
        }
    }
}

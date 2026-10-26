package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbPrioritySplit;

/**
 * The injectors into 1.20.3's own Player.travel that go with {@link CbWorldPlayerMixin} (its restored 1.20.2 member
 * stays there, at {@code CbVanillaReads.PRIORITY}): the travel of a player that is not a ServerPlayer calls 1.20.2's
 * checkMovementStatistics with the move, as 1.20.2's travel did.
 */
@Mixin(class_1657.class)
public abstract class CbWorldPlayerMixinHooks {

    @Unique
    private double cb$travelX;
    @Unique
    private double cb$travelY;
    @Unique
    private double cb$travelZ;

    @Inject(method = "method_6091", at = @At("HEAD"))
    private void cb$travelFrom(class_243 input, CallbackInfo ci) {
        class_1657 self = (class_1657) (Object) this;
        this.cb$travelX = self.method_23317();
        this.cb$travelY = self.method_23318();
        this.cb$travelZ = self.method_23321();
    }

    @Inject(method = "method_6091", at = @At("RETURN"))
    private void cb$travelStatistics(class_243 input, CallbackInfo ci) {
        if (!((Object) this instanceof class_3222)) {
            class_1657 self = (class_1657) (Object) this;
            ((CbPrioritySplit.PlayerMovementStatistics) (Object) this).cb$v1_20_3$checkMovementStatistics(self.method_23317() - this.cb$travelX,
                self.method_23318() - this.cb$travelY, self.method_23321() - this.cb$travelZ);
        }
    }
}

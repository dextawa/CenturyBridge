package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldEntities;

/**
 * 1.20.2's Player.checkMovementStatistics(dx, dy, dz), which every player's travel called; 1.20.3 has it on
 * ServerPlayer only (same code, called from ServerPlayer.travel). It comes back: on a ServerPlayer it is the
 * 1.20.3 method, on any other player 1.20.2's body (CbWorldEntities.legacyMovementStatistics), and the travel
 * of a player that is not a ServerPlayer calls it with the move, as 1.20.2's travel did.
 */
@Mixin(class_1657.class)
public abstract class CbWorldPlayerMixin {

    @Unique
    private double cb$travelX;
    @Unique
    private double cb$travelY;
    @Unique
    private double cb$travelZ;

    public void method_7282(double dx, double dy, double dz) {
        if ((Object) this instanceof class_3222 server) {
            server.method_54720(dx, dy, dz);
        } else {
            CbWorldEntities.legacyMovementStatistics((class_1657) (Object) this, dx, dy, dz);
        }
    }

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
            this.method_7282(self.method_23317() - this.cb$travelX, self.method_23318() - this.cb$travelY,
                self.method_23321() - this.cb$travelZ);
        }
    }
}

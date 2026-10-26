package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1657;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_3.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldEntities;

/**
 * 1.20.2's Player.checkMovementStatistics(dx, dy, dz), which every player's travel called; 1.20.3 has it on
 * ServerPlayer only (same code, called from ServerPlayer.travel). It comes back: on a ServerPlayer it is the
 * 1.20.3 method, on any other player 1.20.2's body (CbWorldEntities.legacyMovementStatistics), and the travel
 * of a player that is not a ServerPlayer calls it with the move, as 1.20.2's travel did (CbWorldPlayerMixinHooks,
 * default priority).
 */
@Mixin(value = class_1657.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWorldPlayerMixin implements CbPrioritySplit.PlayerMovementStatistics {

    public void method_7282(double dx, double dy, double dz) {
        if ((Object) this instanceof class_3222 server) {
            server.method_54720(dx, dy, dz);
        } else {
            CbWorldEntities.legacyMovementStatistics((class_1657) (Object) this, dx, dy, dz);
        }
    }

    /** 1.20.2's checkMovementStatistics called virtually, for 1.20.3's travel (CbWorldPlayerMixinHooks) */
    @Override
    public void cb$v1_20_3$checkMovementStatistics(double dx, double dy, double dz) {
        this.method_7282(dx, dy, dz);
    }
}

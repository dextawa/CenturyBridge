package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_2643;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbEntityPortals;

/** TheEndGatewayBlockEntity: the teleport cooldown 1.20.6's teleportEntity set (CbEntityPortals.teleportEntity) */
@Mixin(class_2643.class)
public abstract class CbEntityGatewayMixin implements CbEntityPortals.GatewayCooldown {

    @Shadow
    private int field_12130;

    @Override
    public void cb$setTeleportCooldown(int ticks) {
        this.field_12130 = ticks;
    }
}

package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * ClientLevel: 1.20.4's setScoreboard(scoreboard). 1.20.4 kept the client's scoreboard on the level and its
 * packet listener handled scoreboard packets into level.getScoreboard(); 1.20.5 keeps it on the packet listener
 * and the level answers that one. Replacing the listener's (CbFinClientConnectionAccess) is replacing the one
 * both the level and the scoreboard packets use, as 1.20.4's setter did.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_638.class)
public abstract class CbFinClientLevelMixin {

    @Shadow
    @Final
    private net.minecraft.class_634 field_3727;

    /** was setScoreboard(scoreboard) */
    public void method_2944(net.minecraft.class_269 scoreboard) {
        ((CbFinClientConnectionAccess) this.field_3727).cbS4$setScoreboard(scoreboard);
    }
}

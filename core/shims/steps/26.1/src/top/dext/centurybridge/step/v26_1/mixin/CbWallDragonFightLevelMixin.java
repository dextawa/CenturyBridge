package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.facade.EndDragonFightData;
import top.dext.centurybridge.step.v26_1.rt.CbWallDragonFight;

/**
 * The world data's dragon fight record as 1.21.11 used it (rt/CbWallDragonFight): a record a mod set before
 * the End existed is the one the End's fight starts from, and the End's save stores its fight's own state
 * over whatever the world data held.
 */
@Mixin(ServerLevel.class)
public abstract class CbWallDragonFightLevelMixin {

    @Inject(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/level/dimension/end/EnderDragonFight;init(Lnet/minecraft/server/level/ServerLevel;JLnet/minecraft/core/BlockPos;)V",
        shift = At.Shift.AFTER))
    private void cb$fightFromWorldData(CallbackInfo ci) {
        ServerLevel self = (ServerLevel) (Object) this;
        EndDragonFightData set = CbWallDragonFight.takeForNewEnd(self.getServer().getWorldData());
        if (set != null) {
            EnderDragonFight fight = CbWallDragonFight.fromData(set);
            self.getDataStorage().set(EnderDragonFight.TYPE, fight);
            self.setDragonFight(fight);
            fight.init(self, self.getSeed(), BlockPos.ZERO);
        }
    }

    /** 1.21.11's saveLevelData stored the fight's saveData() into the world data first */
    @Inject(method = "saveLevelData", at = @At("HEAD"))
    private void cb$endSaved(boolean flush, CallbackInfo ci) {
        ServerLevel self = (ServerLevel) (Object) this;
        if (self.getDragonFight() != null) {
            CbWallDragonFight.endSaved(self.getServer().getWorldData());
        }
    }
}

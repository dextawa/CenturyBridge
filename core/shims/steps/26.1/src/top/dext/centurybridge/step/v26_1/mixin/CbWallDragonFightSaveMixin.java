package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.level.dimension.end.EnderDragonFight;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v26_1.rt.CbWallDragonFight;

/** a 1.21.11 fight class overriding saveData() is saved as its record says (rt/CbWallDragonFight.toSave) */
@Mixin(SavedDataStorage.class)
public abstract class CbWallDragonFightSaveMixin {

    @ModifyVariable(method = "encodeUnchecked", at = @At("HEAD"), argsOnly = true)
    private SavedData cb$fightAsRecord(SavedData data) {
        return data instanceof EnderDragonFight fight ? CbWallDragonFight.toSave(fight) : data;
    }
}

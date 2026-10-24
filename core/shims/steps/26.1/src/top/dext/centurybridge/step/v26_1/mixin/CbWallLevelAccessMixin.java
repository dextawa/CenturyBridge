package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLevelAccess;

/** keeps rt/CbWallLevelAccess's set of open level storage accesses: opened by the constructor, closed by close() */
@Mixin(LevelStorageSource.LevelStorageAccess.class)
public abstract class CbWallLevelAccessMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cb$opened(CallbackInfo ci) {
        CbWallLevelAccess.opened((LevelStorageSource.LevelStorageAccess) (Object) this);
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void cb$closed(CallbackInfo ci) {
        CbWallLevelAccess.closed((LevelStorageSource.LevelStorageAccess) (Object) this);
    }
}

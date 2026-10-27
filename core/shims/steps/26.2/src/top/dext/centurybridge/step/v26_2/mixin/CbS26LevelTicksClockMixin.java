package top.dext.centurybridge.step.v26_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26LevelTicks;

import java.util.function.BooleanSupplier;

/**
 * Keeps the restored LevelRenderer.ticks (mixin/CbS26LevelTicksMixin) equal to (int) getGameTime() of the level the
 * client draws, where 26.2 changes it: after tickTime() in ClientLevel.tick (where 26.1.2's LevelRenderer.tick did
 * ticks++, under the same runsNormally check) and after setTimeFromServer (rt/CbS26LevelTicks). Injections only.
 */
@Environment(EnvType.CLIENT)
@Mixin(ClientLevel.class)
public abstract class CbS26LevelTicksClockMixin {
    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;tickTime()V", shift = At.Shift.AFTER))
    private void cbS26$ticksAfterTickTime(BooleanSupplier haveTime, CallbackInfo ci) {
        this.cbS26$syncTicks();
    }

    @Inject(method = "setTimeFromServer(J)V", at = @At("RETURN"))
    private void cbS26$ticksAfterServerTime(long gameTime, CallbackInfo ci) {
        this.cbS26$syncTicks();
    }

    @Unique
    private void cbS26$syncTicks() {
        ClientLevel self = (ClientLevel) (Object) this;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == self) {
            ((CbS26LevelTicks) minecraft.levelRenderer).cbS26$setTicks((int) self.getGameTime());
        }
    }
}

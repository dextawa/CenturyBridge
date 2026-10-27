package top.dext.centurybridge.step.v26_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbPrioritySplitClient;

/**
 * The injector into 26.2's own Minecraft constructor that goes with {@link CbS26VersionTypeMixin} (its restored
 * getVersionType stays there, at {@code CbVanillaReads.PRIORITY}): right after the constructor stores launchedVersion,
 * where 26.1.2's constructor stored versionType from the same GameConfig.game, the value is handed to the restorer.
 * Default priority; only adds.
 */
@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class CbS26VersionTypeMixinHooks {

    @Inject(method = "<init>(Lnet/minecraft/client/main/GameConfig;)V",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;launchedVersion:Ljava/lang/String;",
            opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void cbS26$versionType(GameConfig config, CallbackInfo ci) {
        ((CbPrioritySplitClient.VersionType) (Object) this).cb$v26_2$versionType(config.game.versionType);
    }
}

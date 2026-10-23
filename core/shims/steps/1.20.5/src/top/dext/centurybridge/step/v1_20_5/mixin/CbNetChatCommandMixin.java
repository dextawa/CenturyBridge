package top.dext.centurybridge.step.v1_20_5.mixin;

import java.time.Instant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetGame;

/**
 * ServerboundChatCommandPacket: when it was made, which 1.20.4's packet carried as its timestamp (1.20.4's
 * client stamped Instant.now() as it built the packet; 1.20.5's unsigned command carries none). Both
 * constructors (the command's and the decoding one) reach this.
 */
@Mixin(net.minecraft.class_7472.class)
public abstract class CbNetChatCommandMixin implements CbNetGame.ChatCommandSlot {

    @Unique
    private Instant cbS4net$timeStamp;

    @Inject(method = "<init>(Ljava/lang/String;)V", at = @At("RETURN"))
    private void cbS4net$made(String command, CallbackInfo ci) {
        this.cbS4net$timeStamp = Instant.now();
    }

    @Override
    public Instant cbS4net$timeStamp() {
        return this.cbS4net$timeStamp;
    }
}

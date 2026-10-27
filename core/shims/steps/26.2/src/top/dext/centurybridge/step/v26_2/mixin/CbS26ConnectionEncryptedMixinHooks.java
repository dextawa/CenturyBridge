package top.dext.centurybridge.step.v26_2.mixin;

import javax.crypto.Cipher;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbPrioritySplit;

/**
 * The injector into 26.2's own Connection.setEncryptionKey that goes with {@link CbS26ConnectionEncryptedMixin} (its
 * restored isEncrypted stays there, at {@code CbVanillaReads.PRIORITY}): marks the connection encrypted at the head of
 * setEncryptionKey, where 26.1.2 set its field. Default priority, so it takes its place among mods' HEAD injectors.
 */
@Mixin(Connection.class)
public abstract class CbS26ConnectionEncryptedMixinHooks {

    @Inject(method = "setEncryptionKey(Ljavax/crypto/Cipher;Ljavax/crypto/Cipher;)V", at = @At("HEAD"))
    private void cb$markEncrypted(Cipher decryptCipher, Cipher encryptCipher, CallbackInfo ci) {
        ((CbPrioritySplit.ConnectionEncrypted) (Object) this).cb$v26_2$markEncrypted();
    }
}

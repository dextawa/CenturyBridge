package top.dext.centurybridge.step.v26_2.mixin;

import javax.crypto.Cipher;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * Connection.isEncrypted(), which 26.2 dropped with its {@code encrypted} field, restored with 26.1.2's meaning (javap
 * of both): true once setEncryptionKey ran on this connection. 26.1.2 set the field first thing in setEncryptionKey and
 * then added the decrypt/encrypt handlers; 26.2's setEncryptionKey adds the same handlers, so the flag is set at its
 * head here, as 26.1.2 did. Only adds: 26.2's own setEncryptionKey and every other mixin on it run unchanged.
 * <p>26.1.2's vanilla read it twice (PlayerTabOverlay's head column, ClientPacketListener's key-pair preparation);
 * 26.2 reads ClientPacketListener.onlineMode() there instead, a value of the login, not of this connection, so a mod's
 * override of isEncrypted no longer steers those two readers. Calls and super calls link as on 26.1.2.
 */
@Mixin(value = Connection.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26ConnectionEncryptedMixin {

    @Unique
    private volatile boolean cb$encrypted;

    @Inject(method = "setEncryptionKey(Ljavax/crypto/Cipher;Ljavax/crypto/Cipher;)V", at = @At("HEAD"))
    private void cb$markEncrypted(Cipher decryptCipher, Cipher encryptCipher, CallbackInfo ci) {
        this.cb$encrypted = true;
    }

    /** 26.1.2 Connection.isEncrypted() */
    public boolean isEncrypted() {
        return this.cb$encrypted;
    }
}

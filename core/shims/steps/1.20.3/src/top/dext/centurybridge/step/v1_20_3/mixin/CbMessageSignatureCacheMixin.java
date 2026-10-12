package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;

/**
 * MessageSignatureCache.push(PlayerChatMessage) (method_46286), 1.20.2's: the message's body (its last-seen
 * signatures) and its own signature, which is 1.20.3's push(SignedMessageBody, MessageSignature).
 */
@Mixin(net.minecraft.class_7561.class)
public abstract class CbMessageSignatureCacheMixin {

    public void method_46286(net.minecraft.class_7471 message) {
        ((net.minecraft.class_7561) (Object) this).method_46286(message.comp_928(), message.comp_1084());
    }
}

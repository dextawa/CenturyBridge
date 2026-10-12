package top.dext.centurybridge.shims.v1_20_2;

import java.util.function.Function;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.dext.centurybridge.compat.fapi.attachment.v1.AttachmentTarget;

/**
 * A block entity's update packet does not carry its attachments (1.20.1's
 * BlockEntityUpdateS2CPacketMixin): the attachment compound is taken out of the NBT the packet is
 * made from.
 */
@Mixin(net.minecraft.class_2622.class)
abstract class CbFapiBlockEntityUpdatePacket {

    @ModifyArg(method = "method_38585", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2622;method_39026(Lnet/minecraft/class_2586;Ljava/util/function/Function;)Lnet/minecraft/class_2622;"))
    private static Function<net.minecraft.class_2586, net.minecraft.class_2487> cb$stripPersistentAttachmentData(
            Function<net.minecraft.class_2586, net.minecraft.class_2487> getter) {
        return blockEntity -> {
            net.minecraft.class_2487 nbt = getter.apply(blockEntity);
            nbt.method_10551(AttachmentTarget.NBT_ATTACHMENT_KEY);
            return nbt;
        };
    }
}

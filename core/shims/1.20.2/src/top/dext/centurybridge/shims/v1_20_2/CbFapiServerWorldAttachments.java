package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.compat.fapi.impl.attachment.AttachmentPersistentState;

/**
 * A server world's attachments are persistent world data (1.20.1's ServerWorldMixin): the world gets
 * its attachment state from its persistent state manager when it is created, which reads the saved
 * attachments back. 1.20.2 asks for persistent state by a PersistentState.Type (constructor, reader,
 * no data fixer) where 1.20.1 took the reader and constructor directly.
 */
@Mixin(net.minecraft.class_3218.class)
abstract class CbFapiServerWorldAttachments {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cb$createAttachmentsPersistentState(CallbackInfo ci) {
        net.minecraft.class_3218 world = (net.minecraft.class_3218) (Object) this;
        world.method_17983().method_17924(new net.minecraft.class_18.class_8645<>(
            () -> new AttachmentPersistentState(world), nbt -> AttachmentPersistentState.read(world, nbt), null),
            AttachmentPersistentState.ID);
    }
}

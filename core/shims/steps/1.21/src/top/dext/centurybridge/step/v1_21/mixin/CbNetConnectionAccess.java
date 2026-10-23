package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Connection's own disconnect details, what its getDisconnectionDetails() answers (CbNet.cbSuperDisconnectedReason) */
@Mixin(net.minecraft.class_2535.class)
public interface CbNetConnectionAccess {

    @Accessor("field_52180")
    net.minecraft.class_9812 cb$disconnectionDetails();
}

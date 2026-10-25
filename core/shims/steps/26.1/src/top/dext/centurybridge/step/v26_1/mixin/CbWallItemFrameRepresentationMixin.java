package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallEntityRepresentation;

/**
 * 1.21.11's ItemFrame kept its item's entity representation (CbWallItemStackRepresentationMixin): onItemChanged set
 * the frame on a non-empty item another frame did not hold, removeFramedMap cleared it last.
 */
@Mixin(ItemFrame.class)
public abstract class CbWallItemFrameRepresentationMixin {

    @Inject(method = "onItemChanged", at = @At("HEAD"))
    private void cb$frameItem(ItemStack item, CallbackInfo ci) {
        CbWallEntityRepresentation r = (CbWallEntityRepresentation) (Object) item;
        if (!item.isEmpty() && r.getFrame() != (Object) this) {
            r.setEntityRepresentation((ItemFrame) (Object) this);
        }
    }

    @Inject(method = "removeFramedMap", at = @At("TAIL"))
    private void cb$unframeItem(ItemStack item, CallbackInfo ci) {
        ((CbWallEntityRepresentation) (Object) item).setEntityRepresentation(null);
    }
}

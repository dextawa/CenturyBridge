package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallEntityRepresentation;

/**
 * 1.21.11's ItemDisplay set itself as its item's entity representation (CbWallItemStackRepresentationMixin) first
 * thing in updateRenderSubState.
 */
@Mixin(Display.ItemDisplay.class)
public abstract class CbWallItemDisplayRepresentationMixin {

    @Shadow
    private ItemStack getItemStack() {
        throw new AssertionError();
    }

    @Inject(method = "updateRenderSubState", at = @At("HEAD"))
    private void cb$representItem(boolean interpolate, float partialTicks, CallbackInfo ci) {
        ((CbWallEntityRepresentation) (Object) getItemStack()).setEntityRepresentation((Display.ItemDisplay) (Object) this);
    }
}

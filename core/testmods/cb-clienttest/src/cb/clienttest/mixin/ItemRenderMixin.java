package cb.clienttest.mixin;

import cb.clienttest.ModelProbe;
import net.minecraft.class_1087;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_811;
import net.minecraft.class_918;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A 1.20.1 mod's injector at ItemRenderer.render(ItemStack, ItemDisplayContext, boolean, PoseStack,
 * MultiBufferSource, int, int, BakedModel) HEAD (smx4 client check 3b, as 1.20.1 has it): one call per item drawn.
 */
@Mixin(class_918.class)
public abstract class ItemRenderMixin {
    @Inject(method = "method_23179(Lnet/minecraft/class_1799;Lnet/minecraft/class_811;ZLnet/minecraft/class_4587;Lnet/minecraft/class_4597;IILnet/minecraft/class_1087;)V",
        at = @At("HEAD"))
    private void cbClientTest$render(class_1799 stack, class_811 context, boolean leftHand, class_4587 pose,
            class_4597 buffers, int light, int overlay, class_1087 model, CallbackInfo ci) {
        ModelProbe.itemRenders++;
    }
}

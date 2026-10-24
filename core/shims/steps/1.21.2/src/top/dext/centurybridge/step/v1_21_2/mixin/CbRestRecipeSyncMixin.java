package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2596;
import net.minecraft.class_2788;
import net.minecraft.class_3244;
import net.minecraft.class_7648;
import net.minecraft.class_8609;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeNet;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeSync;

/**
 * Before a ClientboundUpdateRecipesPacket goes to a player, that player receives the recipes it stood for in
 * 1.21.1 ({@link CbRestRecipeSync}), so they arrive before the packet, as 1.21.1's packet carried them.
 */
@Mixin(class_8609.class)
public abstract class CbRestRecipeSyncMixin {

    @Inject(method = "method_52391", at = @At("HEAD"))
    private void cb$recipes(class_2596<?> packet, class_7648 listener, CallbackInfo ci) {
        if (CbRestRecipeNet.outgoing(packet) instanceof class_2788 recipes && (Object) this instanceof class_3244 game && game.field_14140 != null) {
            CbRestRecipeSync.beforeSend(game, recipes);
        }
    }
}

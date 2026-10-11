package top.dext.centurybridge.shims.v1_21_11;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** CraftingScreenHandler.updateResult is protected static: Statics reaches it through here. */
@Mixin(net.minecraft.class_1714.class)
public interface CraftingScreenHandlerInvoker {

    @Invoker("method_17399")
    static void cb$updateResult(net.minecraft.class_1703 handler, net.minecraft.class_3218 world,
            net.minecraft.class_1657 player, net.minecraft.class_8566 input, net.minecraft.class_1731 result,
            net.minecraft.class_8786<net.minecraft.class_3955> recipe) {
        throw new AssertionError();
    }
}

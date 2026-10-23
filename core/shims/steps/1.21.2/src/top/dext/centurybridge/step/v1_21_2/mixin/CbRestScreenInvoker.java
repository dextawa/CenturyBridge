package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Screen.renderBlurredBackground() (protected) for CbRestGuiClient.renderBlurredBackground */
@Environment(EnvType.CLIENT)
@Mixin(class_437.class)
public interface CbRestScreenInvoker {

    @Invoker("method_57734")
    void cb$renderBlurredBackground();
}

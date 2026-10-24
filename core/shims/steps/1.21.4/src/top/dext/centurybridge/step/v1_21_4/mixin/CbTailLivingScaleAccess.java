package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderTail;

/** LivingEntityRenderer.scale (protected): what a 1.21.3 call of a renderer's typed scale reaches (CbRenderTail) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_922.class)
public interface CbTailLivingScaleAccess extends CbRenderTail.CbLivingScale {
    @Override
    @Invoker("method_4042")
    void cb$scale(net.minecraft.class_10042 state, net.minecraft.class_4587 pose);
}

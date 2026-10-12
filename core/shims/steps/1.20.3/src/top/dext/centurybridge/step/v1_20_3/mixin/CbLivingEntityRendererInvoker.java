package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_20_3.rt.CbBatRender;

/** LivingEntityRenderer's protected scale / setupRotations, called virtually (CbBatRender) */
@Mixin(net.minecraft.class_922.class)
public interface CbLivingEntityRendererInvoker extends CbBatRender.LivingRendererAccess {

    @Override
    @Invoker("method_4042")
    void cb$scale(net.minecraft.class_1309 entity, net.minecraft.class_4587 pose, float partialTick);

    @Override
    @Invoker("method_4058")
    void cb$setupRotations(net.minecraft.class_1309 entity, net.minecraft.class_4587 pose, float bob, float yRot,
        float partialTick);
}

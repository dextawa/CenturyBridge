package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderItemInHand;

/** 1.21.1 ItemInHandLayer.renderArmWithItem(entity, stack, ...) put back with 1.21.1's body (see CbRenderItemInHand) */
@SuppressWarnings("rawtypes")
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_989.class)
public abstract class CbRenderItemInHandMixin implements CbRenderItemInHand.Legacy {

    @Override
    public void method_4192(net.minecraft.class_1309 entity, net.minecraft.class_1799 stack, net.minecraft.class_811 displayContext,
                            net.minecraft.class_1306 arm, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light) {
        CbRenderItemInHand.render((net.minecraft.class_989) (Object) this, entity, stack, displayContext, arm, poseStack, buffers, light);
    }
}

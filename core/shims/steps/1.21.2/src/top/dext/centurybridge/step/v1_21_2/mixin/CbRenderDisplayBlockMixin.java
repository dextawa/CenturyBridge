package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderDisplays;

/** 1.21.1 DisplayRenderer.BlockDisplayRenderer's getSubState / renderInner (see CbRenderDisplays) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_8138.class_8139.class)
public abstract class CbRenderDisplayBlockMixin {

    /** 1.21.1 getSubState(display): the display's blockRenderState() */
    protected net.minecraft.class_8113.class_8115.class_8225 method_49922(net.minecraft.class_8113.class_8115 display) {
        return display.method_49778();
    }

    /** javac's 1.21.1 bridge method_49921(Display) to it */
    public Object method_49921(net.minecraft.class_8113 display) {
        return this.method_49922((net.minecraft.class_8113.class_8115) display);
    }

    /** 1.21.1 renderInner(display, subState, ...) */
    public void method_49054(net.minecraft.class_8113.class_8115 display, net.minecraft.class_8113.class_8115.class_8225 subState, net.minecraft.class_4587 poseStack,
                             net.minecraft.class_4597 buffers, int light, float interpolationProgress) {
        ((CbRenderDisplays.Legacy) this).method_49052(display, subState, poseStack, buffers, light, interpolationProgress);
    }
}

package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderDisplays;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** 1.21.1 DisplayRenderer.TextDisplayRenderer's getSubState / renderInner (see CbRenderDisplays) */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_8138.class_8141.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderDisplayTextMixin {

    /** 1.21.1 getSubState(display): the display's textRenderState() */
    protected net.minecraft.class_8113.class_8123.class_8230 method_49924(net.minecraft.class_8113.class_8123 display) {
        return display.method_49784();
    }

    /** javac's 1.21.1 bridge method_49921(Display) to it */
    public Object method_49921(net.minecraft.class_8113 display) {
        return this.method_49924((net.minecraft.class_8113.class_8123) display);
    }

    /** 1.21.1 renderInner(display, subState, ...) */
    public void method_49056(net.minecraft.class_8113.class_8123 display, net.minecraft.class_8113.class_8123.class_8230 subState, net.minecraft.class_4587 poseStack,
                             net.minecraft.class_4597 buffers, int light, float interpolationProgress) {
        ((CbRenderDisplays.Legacy) this).method_49052(display, subState, poseStack, buffers, light, interpolationProgress);
    }
}

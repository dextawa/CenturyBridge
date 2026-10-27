package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderDisplays;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** 1.21.1 DisplayRenderer.ItemDisplayRenderer's getSubState / renderInner (see CbRenderDisplays) */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_8138.class_8140.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderDisplayItemMixin {

    /** 1.21.1 getSubState(display): the display's itemRenderState() */
    protected net.minecraft.class_8113.class_8122.class_8226 method_49923(net.minecraft.class_8113.class_8122 display) {
        return display.method_49782();
    }

    /** javac's 1.21.1 bridge method_49921(Display) to it */
    public Object method_49921(net.minecraft.class_8113 display) {
        return this.method_49923((net.minecraft.class_8113.class_8122) display);
    }

    /** 1.21.1 renderInner(display, subState, ...) */
    public void method_49055(net.minecraft.class_8113.class_8122 display, net.minecraft.class_8113.class_8122.class_8226 subState, net.minecraft.class_4587 poseStack,
                             net.minecraft.class_4597 buffers, int light, float interpolationProgress) {
        ((CbRenderDisplays.Legacy) this).method_49052(display, subState, poseStack, buffers, light, interpolationProgress);
    }
}

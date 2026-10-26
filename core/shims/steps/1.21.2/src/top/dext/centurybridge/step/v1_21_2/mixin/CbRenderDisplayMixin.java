package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderDisplays;
import top.dext.centurybridge.step.v1_21_2.rt.Guard;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** 1.21.1 DisplayRenderer.getSubState / renderInner(display, subState, ...) on 1.21.2's (see CbRenderDisplays) */
@SuppressWarnings({"unchecked", "rawtypes"})
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_8138.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderDisplayMixin implements CbRenderDisplays.Legacy {

    @Shadow
    protected abstract void method_49052(net.minecraft.class_10011 state, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light,
                                         float interpolationProgress);

    /** 1.21.1 getSubState(display): abstract there; each vanilla subclass answers its display's accessor */
    @Override
    public Object method_49921(net.minecraft.class_8113 display) {
        throw new AbstractMethodError(this.getClass().getName() + ".getSubState(Display): abstract in 1.21.1");
    }

    /** 1.21.1 renderInner(display, subState, ...): 1.21.2's on a state of the display carrying that sub-state, as vanilla's */
    @Override
    public void method_49052(net.minecraft.class_8113 display, Object subState, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                             int light, float interpolationProgress) {
        net.minecraft.class_10011 state = CbRenderDisplays.state((net.minecraft.class_8138) (Object) this, display, subState);
        boolean in = Guard.cbEnter(this);
        try {
            this.method_49052(state, poseStack, buffers, light, interpolationProgress);
        } finally {
            if (in) {
                Guard.cbExit(this);
            }
        }
    }
}

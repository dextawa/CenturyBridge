package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelBase;
import top.dext.centurybridge.step.v1_21_2.rt.Guard;

/**
 * 1.21.1 Model.renderToBuffer (abstract there) and HierarchicalModel.root() over 1.21.2's root-built Model (see
 * CbRenderModelBase).
 * - renderToBuffer(pose, consumer, light, overlay, color), for a 1.21.2 model or a super call reaching Model: 1.21.2's
 *   final renderToBuffer, which draws the root.
 * - 1.21.2's renderToBuffer on a model whose class draws itself (declares 1.21.1's renderToBuffer) runs that one instead,
 *   once (the guard: its own super call, reaching Model's put-back one, draws the root as 1.21.2 does).
 * - root(): every 1.21.1 vanilla HierarchicalModel answered its root part, which is the 1.21.2 model's root; a 1.21.1
 *   model drawing itself (the HierarchicalModel facade's subclasses) declares its own.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_3879.class)
public abstract class CbRenderModelMixin implements CbRenderModelBase.Legacy {

    @Shadow
    public abstract void method_62100(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color);

    @Shadow
    public abstract net.minecraft.class_630 method_63512();

    /** 1.21.1 Model.renderToBuffer */
    @Override
    public void method_2828(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color) {
        this.method_62100(pose, consumer, light, overlay, color);
    }

    /** 1.21.1 HierarchicalModel.root() of a vanilla model */
    public net.minecraft.class_630 method_32008() {
        return this.method_63512();
    }

    @Inject(method = "method_62100(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;III)V", at = @At("HEAD"), cancellable = true)
    private void cb$drawItself(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color, CallbackInfo ci) {
        if (CbRenderModelBase.drawsItself(this) && Guard.cbEnter(this)) {
            try {
                this.method_2828(pose, consumer, light, overlay, color);
            } finally {
                Guard.cbExit(this);
            }
            ci.cancel();
        }
    }
}

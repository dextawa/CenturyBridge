package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21.1 BookModel.render(pose, consumer, light, overlay, color): its root part drawn with the colour; 1.21.2's
 * Model.renderToBuffer draws the same root the same way.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_557.class)
public abstract class CbRenderModelBookMixin {

    public void method_24184(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color) {
        ((net.minecraft.class_3879) (Object) this).method_62100(pose, consumer, light, overlay, color);
    }
}

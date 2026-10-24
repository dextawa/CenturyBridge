package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.21.1 EntityRendererProvider.Context.getItemInHandRenderer(): 1.21.1's dispatcher built its context with its own
 * ItemInHandRenderer, which 1.21.2's dispatcher still has.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_5617.class_5618.class)
public abstract class CbRenderContextMixin {

    @Shadow
    public abstract net.minecraft.class_898 method_32166();

    public net.minecraft.class_759 method_43338() {
        return this.method_32166().method_43336();
    }
}

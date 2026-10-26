package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The injector into 1.21.2's own RenderTarget.blitToScreen that goes with {@link CbRenderTargetMixin} (its restored
 * 1.21.1 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.1 injectors can anchor inside them):
 * the blit draws with the shader a mod set in GameRenderer.blitShader, as 1.21.1's did.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_276.class)
public abstract class CbRenderTargetMixinHooks {

    @Shadow
    protected int field_1475;

    @Redirect(method = "method_1233", require = 1, at = @At(value = "INVOKE",
        target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Lnet/minecraft/class_10156;)Lnet/minecraft/class_5944;"))
    private net.minecraft.class_5944 cb$blitShader(net.minecraft.class_10156 program) {
        net.minecraft.class_757 renderer = net.minecraft.class_310.method_1551().field_1773;
        net.minecraft.class_5944 legacy = renderer == null ? null : ((top.dext.centurybridge.step.v1_21_2.rt.CbRenderGame.Blit) renderer).cb$blitShader();
        if (legacy == null) {
            return com.mojang.blaze3d.systems.RenderSystem.setShader(program);
        }
        com.mojang.blaze3d.systems.RenderSystem.setShader(legacy);
        legacy.method_62899("DiffuseSampler", this.field_1475);
        return legacy;
    }
}

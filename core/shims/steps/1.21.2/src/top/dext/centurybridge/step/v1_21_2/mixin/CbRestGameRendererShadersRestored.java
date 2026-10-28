package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestReloadShaders;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's GameRenderer.reloadShaders(provider) (class_757.method_34538), gone in 1.21.2, restored on GameRenderer.
 * <p>
 * 1.21.1's GameRenderer reload listener called it on every resource reload, with the resource manager, to compile
 * every core shader; a 1.21.1 mixin at its TAIL ran once the core shaders of that reload were ready (the 1.20.5 layer's
 * CbClientGameRendererShadersMixinHooks loads 1.20.4's two removed core shaders there). 1.21.2 compiles core shaders
 * in ShaderManager (class_10151), a reload listener of its own, whose apply step (method_62945) builds that reload's
 * compilation cache (javap). The restored body does nothing of its own: 1.21.2's ShaderManager has already compiled
 * the shaders 1.21.1's body compiled. It is called at the end of each ShaderManager apply (CbRestShaderReloadRoute), so
 * a 1.21.1 mixin into it runs where 1.21.1 ran it.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_757.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestGameRendererShadersRestored implements CbRestReloadShaders {

    @CbVanillaReads("CbRestShaderReloadRoute: ShaderManager's apply step calls it on the game renderer once it built the"
        + " reload's compilation cache, with the reload's resource manager, as 1.21.1's GameRenderer reload listener did")
    private void method_34538(net.minecraft.class_5912 provider) {
    }

    @Override
    public void cb$reloadShaders(net.minecraft.class_5912 provider) {
        method_34538(provider);
    }
}

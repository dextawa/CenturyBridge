package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderShaders;

/**
 * The injectors into 1.21.2's own CompiledShaderProgram methods that go with {@link CbRenderShaderMixin} (its restored
 * 1.21.1 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.1 injectors can anchor inside them):
 * a linked program's two Programs, and 1.21.1's kept samplers bound at the head of apply().
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_5944.class)
public abstract class CbRenderShaderMixinHooks {

    @Shadow
    public abstract void method_62899(String name, int textureId);

    /** 1.21.2 linkShader(vertex, fragment, format): the linked program's Programs, over the two compiled shaders */
    @Inject(method = "method_62896(Lnet/minecraft/class_10141;Lnet/minecraft/class_10141;Lnet/minecraft/class_293;)Lnet/minecraft/class_5944;",
        at = @At("RETURN"))
    private static void cb$linkedPrograms(net.minecraft.class_10141 vertex, net.minecraft.class_10141 fragment, net.minecraft.class_293 format,
                                          CallbackInfoReturnable<net.minecraft.class_5944> cir) {
        CbPrioritySplitClient.ShaderState linked = (CbPrioritySplitClient.ShaderState) (Object) cir.getReturnValue();
        linked.cb$v1_21_2$programs(
            new top.dext.centurybridge.step.v1_21_2.facade.Program(top.dext.centurybridge.step.v1_21_2.facade.Program.Type.field_1530,
                vertex.method_62883(), CbRenderShaders.legacyName(vertex.method_62881())),
            new top.dext.centurybridge.step.v1_21_2.facade.Program(top.dext.centurybridge.step.v1_21_2.facade.Program.Type.field_1531,
                fragment.method_62883(), CbRenderShaders.legacyName(fragment.method_62881())));
    }

    @Inject(method = "method_34586()V", at = @At("HEAD"))
    private void cb$bindLegacySamplers(CallbackInfo ci) {
        for (Map.Entry<String, Object> e : ((CbPrioritySplitClient.ShaderState) (Object) this).cb$v1_21_2$samplers().entrySet()) {
            int id = CbRenderShaders.textureId(e.getValue());
            if (id != -1) {
                this.method_62899(e.getKey(), id);
            }
        }
    }
}

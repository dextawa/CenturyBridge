package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderShaders;

/**
 * 1.21.1 ShaderInstance members on 1.21.2's compiled program (the same class, class_5944; see CbRenderShaders):
 * - setSampler(name, Object): 1.21.1 kept the object (a RenderTarget, an AbstractTexture, or a texture id) and turned
 *   it into a texture id at every apply(); kept likewise and bound (1.21.2's bindSampler) at the head of apply().
 * - markDirty(): 1.21.1 set a flag nothing ever read (field_29495: written by markDirty and apply, read nowhere), so it
 *   had no effect; it has none here.
 * - getName() / getVertexFormat(): 1.21.2's program keeps neither; the ShaderManager hands them over when it compiles
 *   the program for a key (CbRenderShaderManagerMixin), in 1.21.1's form (a core shader's bare name, another's
 *   namespace:name).
 * - the Shader interface (facade) again, as 1.21.1's ShaderInstance implemented it: getId (1.21.2's own) and markDirty;
 *   getVertexProgram / getFragmentProgram / attachToProgram stay unimplemented (ledger: no Program objects in 1.21.2).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_5944.class)
public abstract class CbRenderShaderMixin implements CbRenderShaders.Program, top.dext.centurybridge.step.v1_21_2.facade.Shader {

    @Unique
    private final Map<String, Object> cb$samplers = new LinkedHashMap<>();

    @Unique
    private String cb$name;

    @Unique
    private net.minecraft.class_293 cb$format;

    @Shadow
    public abstract void method_62899(String name, int textureId);

    /** 1.21.1 ShaderInstance.markDirty() */
    @Override
    public void method_1279() {
    }

    /** 1.21.1 ShaderInstance.setSampler(name, texture) */
    public void method_34583(String name, Object texture) {
        this.cb$samplers.put(name, texture);
    }

    /** 1.21.1 ShaderInstance.getVertexFormat() */
    public net.minecraft.class_293 method_35786() {
        return this.cb$format;
    }

    /** 1.21.1 ShaderInstance.getName() */
    public String method_35787() {
        return this.cb$name;
    }

    @Override
    public void cb$compiledFor(net.minecraft.class_10156 key) {
        this.cb$name = CbRenderShaders.legacyName(key.comp_3113());
        this.cb$format = key.comp_3114();
    }

    @Inject(method = "method_34586()V", at = @At("HEAD"))
    private void cb$bindLegacySamplers(CallbackInfo ci) {
        for (Map.Entry<String, Object> e : this.cb$samplers.entrySet()) {
            int id = CbRenderShaders.textureId(e.getValue());
            if (id != -1) {
                this.method_62899(e.getKey(), id);
            }
        }
    }
}

package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderShaders;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 ShaderInstance members on 1.21.2's compiled program (the same class, class_5944; see CbRenderShaders):
 * - setSampler(name, Object): 1.21.1 kept the object (a RenderTarget, an AbstractTexture, or a texture id) and turned
 *   it into a texture id at every apply(); kept likewise and bound (1.21.2's bindSampler) at the head of apply().
 * - markDirty(): 1.21.1 set a flag nothing ever read (field_29495: written by markDirty and apply, read nowhere), so it
 *   had no effect; it has none here.
 * - getName() / getVertexFormat(): 1.21.2's program keeps neither; the ShaderManager hands them over when it compiles
 *   the program for a key (CbRenderShaderManagerMixin), in 1.21.1's form (a core shader's bare name, another's
 *   namespace:name).
 * - the Shader interface (facade) again, as 1.21.1's ShaderInstance implemented it: getId (1.21.2's own), markDirty,
 *   and getVertexProgram / getFragmentProgram / attachToProgram over the program's two shaders: a program 1.21.1's
 *   constructor built (CbRenderShaderLoad) keeps the Programs it compiled; one 1.21.2 links (linkShader, from the
 *   ShaderManager's compiled vertex and fragment shaders) gets a Program for each of them, named as 1.21.1 named the
 *   shader (a vanilla shader's bare name, another's namespace:name) and not entered in Program.Type's cache, which
 *   1.21.1 filled from its own compiles only. attachToProgram attaches both again, as 1.21.1's did.
 * The injectors into 1.21.2's linkShader and apply are in CbRenderShaderMixinHooks (default priority).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_5944.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderShaderMixin implements CbRenderShaders.Program, top.dext.centurybridge.step.v1_21_2.rt.CbRenderShaderLoad.Legacy,
    top.dext.centurybridge.step.v1_21_2.facade.Shader, CbPrioritySplitClient.ShaderState {

    @Unique
    private final Map<String, Object> cb$samplers = new LinkedHashMap<>();

    @Unique
    private String cb$name;

    @Unique
    private net.minecraft.class_293 cb$format;

    @Unique
    private top.dext.centurybridge.step.v1_21_2.facade.Program cb$vertex;

    @Unique
    private top.dext.centurybridge.step.v1_21_2.facade.Program cb$fragment;

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

    @Override
    public void cb$legacy(String name, net.minecraft.class_293 format, top.dext.centurybridge.step.v1_21_2.facade.Program vertex,
                          top.dext.centurybridge.step.v1_21_2.facade.Program fragment) {
        this.cb$name = name;
        this.cb$format = format;
        this.cb$vertex = vertex;
        this.cb$fragment = fragment;
    }

    /** 1.21.1 ShaderInstance.getVertexProgram() */
    @Override
    public top.dext.centurybridge.step.v1_21_2.facade.Program method_1274() {
        return this.cb$vertex;
    }

    /** 1.21.1 ShaderInstance.getFragmentProgram() */
    @Override
    public top.dext.centurybridge.step.v1_21_2.facade.Program method_1278() {
        return this.cb$fragment;
    }

    /** 1.21.1 ShaderInstance.attachToProgram() */
    @Override
    public void method_34418() {
        this.cb$fragment.method_1281(this);
        this.cb$vertex.method_1281(this);
    }

    @Override
    public Map<String, Object> cb$v1_21_2$samplers() {
        return this.cb$samplers;
    }

    @Override
    public void cb$v1_21_2$programs(top.dext.centurybridge.step.v1_21_2.facade.Program vertex, top.dext.centurybridge.step.v1_21_2.facade.Program fragment) {
        this.cb$vertex = vertex;
        this.cb$fragment = fragment;
    }
}

package top.dext.centurybridge.step.v1_21_2.facade;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's EffectProgram (net/minecraft/class_5937, classRenames), gone in 1.21.2: a compiled vertex or fragment
 * shader shared by the EffectInstances that attach to it, closed when the last one lets go. 1.21.1's bodies (its
 * preprocessor answers every #moj_import with an error line, as 1.21.1's did).
 */
@Environment(EnvType.CLIENT)
public class EffectProgram extends Program {

    public static final net.minecraft.class_5913 field_29329 = new net.minecraft.class_5913() {
        @Override
        public String method_34233(boolean local, String name) {
            return "#error Import statement not supported";
        }
    };

    public int field_29330;

    public EffectProgram(Program.Type type, int id, String name) {
        super(type, id, name);
    }

    /** attachToEffect(effect) */
    public void method_34414(Effect effect) {
        RenderSystem.assertOnRenderThread();
        this.field_29330++;
        this.method_1281(effect);
    }

    /** close(): the shader is deleted once no effect uses it */
    @Override
    public void method_1282() {
        RenderSystem.assertOnRenderThread();
        this.field_29330--;
        if (this.field_29330 <= 0) {
            super.method_1282();
        }
    }

    /** compileShader(type, name, stream, sourceName) */
    public static EffectProgram method_34415(Program.Type type, String name, InputStream stream, String sourceName) throws IOException {
        RenderSystem.assertOnRenderThread();
        int id = Program.method_34416(type, name, stream, sourceName, field_29329);
        EffectProgram program = new EffectProgram(type, id, name);
        type.method_1289().put(name, program);
        return program;
    }
}

package top.dext.centurybridge.step.v1_21_2.facade;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's Program (net/minecraft/class_281, classRenames) and Program.Type (class_281$class_282), gone in 1.21.2 (its
 * shaders compile through CompiledShader inside the ShaderManager): one compiled GL vertex or fragment shader, cached
 * per type by name. 1.21.1's fields (public: an access widener never reaches a CenturyBridge class) and bodies, on the
 * GlStateManager calls 1.21.2 keeps (glShaderSource takes the joined source there, as 1.21.1's handed LWJGL the parts).
 */
@Environment(EnvType.CLIENT)
public class Program {

    /** MAX_LOG_LENGTH */
    public static final int field_32037 = 32768;

    /** type */
    public final Type field_1524;

    /** name */
    public final String field_1525;

    /** id */
    public int field_1523;

    public Program(Type type, int id, String name) {
        this.field_1524 = type;
        this.field_1523 = id;
        this.field_1525 = name;
    }

    /** attachToShader(shader) */
    public void method_1281(Shader shader) {
        RenderSystem.assertOnRenderThread();
        GlStateManager.glAttachShader(shader.method_1270(), this.method_34417());
    }

    /** close() */
    public void method_1282() {
        if (this.field_1523 != -1) {
            RenderSystem.assertOnRenderThread();
            GlStateManager.glDeleteShader(this.field_1523);
            this.field_1523 = -1;
            this.field_1524.method_1289().remove(this.field_1525);
        }
    }

    /** getName() */
    public String method_1280() {
        return this.field_1525;
    }

    /** compileShader(type, name, stream, sourceName, preprocessor) */
    public static Program method_1283(Type type, String name, InputStream stream, String sourceName, net.minecraft.class_5913 preprocessor)
        throws IOException {
        RenderSystem.assertOnRenderThread();
        int id = method_34416(type, name, stream, sourceName, preprocessor);
        Program program = new Program(type, id, name);
        type.method_1289().put(name, program);
        return program;
    }

    /** compileShaderInternal(type, name, stream, sourceName, preprocessor): the GL shader id */
    public static int method_34416(Type type, String name, InputStream stream, String sourceName, net.minecraft.class_5913 preprocessor)
        throws IOException {
        String source = org.apache.commons.io.IOUtils.toString(stream, StandardCharsets.UTF_8);
        if (source == null) {
            throw new IOException("Could not load program " + type.method_1286());
        }
        int id = GlStateManager.glCreateShader(type.method_1287());
        GlStateManager.glShaderSource(id, String.join("", preprocessor.method_34229(source)));
        GlStateManager.glCompileShader(id);
        if (GlStateManager.glGetShaderi(id, 35713) == 0) {
            String log = org.apache.commons.lang3.StringUtils.trim(GlStateManager.glGetShaderInfoLog(id, 32768));
            throw new IOException("Couldn't compile " + type.method_1286() + " program (" + sourceName + ", " + name + ") : " + log);
        }
        return id;
    }

    /** getId() */
    public int method_34417() {
        return this.field_1523;
    }

    /**
     * 1.21.1's Program.Type, an enum there (VERTEX, FRAGMENT). A class here: its constants keep their intermediary
     * field names, and a Java enum's name() would be the field's, where 1.21.1's answered "VERTEX" / "FRAGMENT" (the
     * names valueOf takes); name / ordinal / values / valueOf are 1.21.1's.
     */
    @Environment(EnvType.CLIENT)
    public static final class Type {
        /** VERTEX */
        public static final Type field_1530 = new Type("VERTEX", 0, "vertex", ".vsh", 35633);

        /** FRAGMENT */
        public static final Type field_1531 = new Type("FRAGMENT", 1, "fragment", ".fsh", 35632);

        private static final Type[] VALUES = {field_1530, field_1531};

        private final String constant;

        private final int ordinal;

        /** name */
        public final String field_1526;

        /** extension */
        public final String field_1528;

        /** glType */
        public final int field_1529;

        /** programs */
        public final Map<String, Program> field_1527 = new HashMap<>();

        public Type(String constant, int ordinal, String name, String extension, int glType) {
            this.constant = constant;
            this.ordinal = ordinal;
            this.field_1526 = name;
            this.field_1528 = extension;
            this.field_1529 = glType;
        }

        public static Type[] values() {
            return VALUES.clone();
        }

        public static Type valueOf(String name) {
            for (Type t : VALUES) {
                if (t.constant.equals(name)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("No enum constant Program.Type." + name);
        }

        public String name() {
            return this.constant;
        }

        public int ordinal() {
            return this.ordinal;
        }

        @Override
        public String toString() {
            return this.constant;
        }

        /** getName() */
        public String method_1286() {
            return this.field_1526;
        }

        /** getExtension() */
        public String method_1284() {
            return this.field_1528;
        }

        /** getGlType() */
        public int method_1287() {
            return this.field_1529;
        }

        /** getPrograms() */
        public Map<String, Program> method_1289() {
            return this.field_1527;
        }
    }
}

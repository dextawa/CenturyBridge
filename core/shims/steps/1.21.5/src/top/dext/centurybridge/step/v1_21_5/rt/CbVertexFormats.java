package top.dext.centurybridge.step.v1_21_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.4's VertexFormat (class_293) members 1.21.5 dropped or changed, on 1.21.5's com/mojang/blaze3d/vertex/VertexFormat
 * (javap -c -p 1.21.4 class_293 / class_293$class_5595 / $class_5596 / class_5944, 1.21.5 VertexFormat, GlConst,
 * class_10869$class_10870, class_5944). The members that only changed their names are methodRenames
 * (segments/steps/1.21.4__1.21.5.json.d/s205d-vertexformat.json).
 * <ul>
 * <li>setupBufferState(): 1.21.4 enabled each element's attribute array and set its pointer by the element's usage;
 *     1.21.5 does exactly that in GlDevice's vertex array setup (class_10869$class_10870.method_68430(format, true),
 *     opened by the layer's access widener) -- same per-usage pointer calls, normalized flags and offsets.</li>
 * <li>clearBufferState(): 1.21.4 disabled each element's attribute array (GL20.glDisableVertexAttribArray on the render
 *     thread); 1.21.5's GlStateManager lost the wrapper, the GL call is the same.</li>
 * <li>IndexType.asGLType / Mode.asGLMode (int fields): 1.21.5 computes them in GlConst.toGl(IndexType / Mode), with the
 *     same values per constant (1.21.4 constructors' constants vs 1.21.5 toGl's switch, constant order unchanged).</li>
 * <li>CompiledShaderProgram.link(vertex, fragment, format): 1.21.5's link takes a debug label too (log lines only): the
 *     vertex shader's id, as 1.21.4 named none.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbVertexFormats {

    private CbVertexFormats() {
    }

    /** VertexFormat.setupBufferState() of 1.21.4 */
    public static void method_22649(com.mojang.blaze3d.vertex.VertexFormat format) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        net.minecraft.class_10869.class_10870.method_68430(format, true);
    }

    /** VertexFormat.clearBufferState() of 1.21.4 */
    public static void method_22651(com.mojang.blaze3d.vertex.VertexFormat format) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        for (int i = 0; i < format.getElements().size(); i++) {
            org.lwjgl.opengl.GL20.glDisableVertexAttribArray(i);
        }
    }

    /**
     * VertexFormatElement.setupBufferState(index, offset, stride) of 1.21.4: the element's usage's setter (Usage's
     * setupBufferState lambdas, javap -v) -- POSITION / GENERIC a float pointer, NORMAL / COLOR a normalized one, UV a float
     * pointer for float elements and an integer pointer otherwise; no attribute array enabled (1.21.4's did not either).
     * The same per-usage calls as 1.21.5's vertex array setup (class_10869$class_10870.method_68430).
     */
    public static void method_22652(com.mojang.blaze3d.vertex.VertexFormatElement element, int index, long offset, int stride) {
        int count = element.count();
        int glType = com.mojang.blaze3d.opengl.GlConst.toGl(element.type());
        switch (element.usage()) {
            case NORMAL, COLOR -> com.mojang.blaze3d.opengl.GlStateManager._vertexAttribPointer(index, count, glType, true, stride, offset);
            case UV -> {
                if (glType == org.lwjgl.opengl.GL11.GL_FLOAT) {
                    com.mojang.blaze3d.opengl.GlStateManager._vertexAttribPointer(index, count, glType, false, stride, offset);
                } else {
                    com.mojang.blaze3d.opengl.GlStateManager._vertexAttribIPointer(index, count, glType, stride, offset);
                }
            }
            default -> com.mojang.blaze3d.opengl.GlStateManager._vertexAttribPointer(index, count, glType, false, stride, offset);
        }
    }

    /** VertexFormatElement.Type.getGlType() of 1.21.4: GlConst.toGl(type), the same GL constant per type (javap of both) */
    public static int method_1390(com.mojang.blaze3d.vertex.VertexFormatElement.Type type) {
        return com.mojang.blaze3d.opengl.GlConst.toGl(type);
    }

    /** VertexFormat.IndexType.asGLType of 1.21.4 */
    public static int field_27374(com.mojang.blaze3d.vertex.VertexFormat.class_5595 type) {
        return com.mojang.blaze3d.opengl.GlConst.toGl(type);
    }

    /** VertexFormat.Mode.asGLMode of 1.21.4 */
    public static int field_27383(com.mojang.blaze3d.vertex.VertexFormat.class_5596 mode) {
        return com.mojang.blaze3d.opengl.GlConst.toGl(mode);
    }

    /** CompiledShaderProgram.link(vertex, fragment, format) of 1.21.4 */
    public static net.minecraft.class_5944 link(net.minecraft.class_10141 vertex, net.minecraft.class_10141 fragment,
                                                com.mojang.blaze3d.vertex.VertexFormat format) throws net.minecraft.class_10151.class_10152 {
        return net.minecraft.class_5944.method_62896(vertex, fragment, format, String.valueOf(vertex.method_62881()));
    }
}

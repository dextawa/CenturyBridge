package top.dext.centurybridge.step.v1_21.rt;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6's position_color_tex vertex format and shader, and ShaderInstance.parseBlendNode, gone in 1.21.
 * <ul>
 * <li>DefaultVertexFormat.POSITION_COLOR_TEX: Position, Color, UV0, in that order (1.21 keeps only
 *     POSITION_TEX_COLOR).</li>
 * <li>GameRenderer.getPositionColorTexShader(): 1.20.6's position_color_tex program computed exactly what 1.21's
 *     position_tex_color does (position through ModelViewMat and ProjMat; texture times vertex color, discarded
 *     below alpha 0.1, times ColorModulator); only the attribute order differed. It is 1.21's position_tex_color
 *     program, linked for the POSITION_COLOR_TEX attribute order -- a ShaderInstance of its own, made with the
 *     core shaders (CbRenderGameRendererMixin: after reloadShaders, closed by shutdownShaders).</li>
 * <li>parseBlendNode(json): 1.20.6's body (func / srcrgb / dstrgb / srcalpha / dstalpha, the default blend when
 *     the node is absent or names the default), over 1.21's BlendMode, which kept the parsers and constructors.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbRenderShaders {

    private CbRenderShaders() {
    }

    /** DefaultVertexFormat.POSITION_COLOR_TEX */
    public static final net.minecraft.class_293 field_20887 = net.minecraft.class_293.method_60833()
        .method_60842("Position", net.minecraft.class_296.field_52107)
        .method_60842("Color", net.minecraft.class_296.field_52108)
        .method_60842("UV0", net.minecraft.class_296.field_52109)
        .method_60840();

    private static net.minecraft.class_5944 positionColorTex;

    /** GameRenderer.getPositionColorTexShader() */
    public static net.minecraft.class_5944 method_34541() {
        return positionColorTex;
    }

    /** the core shaders were (re)loaded: position_color_tex with them */
    public static void cbShadersLoaded(net.minecraft.class_5912 provider) {
        cbShadersClosed();
        try {
            positionColorTex = new net.minecraft.class_5944(provider, "position_tex_color", field_20887);
        } catch (java.io.IOException e) {
            throw new RuntimeException("could not reload shaders: position_color_tex", e);
        }
    }

    /** the core shaders are being shut down: position_color_tex with them */
    public static void cbShadersClosed() {
        if (positionColorTex != null) {
            positionColorTex.close();
            positionColorTex = null;
        }
    }

    /** ShaderInstance.parseBlendNode(json) of 1.20.6 */
    public static net.minecraft.class_277 method_34581(JsonObject json) {
        if (json == null) {
            return new net.minecraft.class_277();
        }
        int func = 32774;
        int srcColor = 1;
        int dstColor = 0;
        int srcAlpha = 1;
        int dstAlpha = 0;
        boolean isDefault = true;
        boolean separate = false;
        if (net.minecraft.class_3518.method_15289(json, "func")) {
            func = net.minecraft.class_277.method_1247(json.get("func").getAsString());
            if (func != 32774) {
                isDefault = false;
            }
        }
        if (net.minecraft.class_3518.method_15289(json, "srcrgb")) {
            srcColor = net.minecraft.class_277.method_1243(json.get("srcrgb").getAsString());
            if (srcColor != 1) {
                isDefault = false;
            }
        }
        if (net.minecraft.class_3518.method_15289(json, "dstrgb")) {
            dstColor = net.minecraft.class_277.method_1243(json.get("dstrgb").getAsString());
            if (dstColor != 0) {
                isDefault = false;
            }
        }
        if (net.minecraft.class_3518.method_15289(json, "srcalpha")) {
            srcAlpha = net.minecraft.class_277.method_1243(json.get("srcalpha").getAsString());
            if (srcAlpha != 1) {
                isDefault = false;
            }
            separate = true;
        }
        if (net.minecraft.class_3518.method_15289(json, "dstalpha")) {
            dstAlpha = net.minecraft.class_277.method_1243(json.get("dstalpha").getAsString());
            if (dstAlpha != 0) {
                isDefault = false;
            }
            separate = true;
        }
        if (isDefault) {
            return new net.minecraft.class_277();
        }
        return separate ? new net.minecraft.class_277(srcColor, dstColor, srcAlpha, dstAlpha, func)
            : new net.minecraft.class_277(srcColor, dstColor, func);
    }
}

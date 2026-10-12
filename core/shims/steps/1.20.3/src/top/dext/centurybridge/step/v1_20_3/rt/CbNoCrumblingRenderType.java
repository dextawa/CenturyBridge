package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.2's RenderType.translucentNoCrumbling(): "translucent_no_crumbling", block vertex format, quads,
 * 262144-byte buffer, no crumbling, sorted on upload, with translucentState(its shader) -- lightmap, the
 * mipped block atlas, translucent transparency, the translucent target -- and the composite state's
 * defaults for the rest, set up and cleared in the composite state's order. 1.20.3's composite render
 * type and its factory are private, so this subclass builds the same type through RenderType's public
 * constructor; its outline is the one the composite state gives it (the block atlas, culled), which is
 * also translucent()'s.
 */
@Environment(EnvType.CLIENT)
public final class CbNoCrumblingRenderType extends net.minecraft.class_1921 {

    /** was RenderStateShard.RENDERTYPE_TRANSLUCENT_NO_CRUMBLING_SHADER (field_29448): the shader {@link CbRender} loads */
    public static final net.minecraft.class_4668.class_5942 field_29448 = new net.minecraft.class_4668.class_5942(CbRender::method_34500);


    private static final List<net.minecraft.class_4668> STATES = List.of(
        field_21376,                // texture: BLOCK_SHEET_MIPPED
        field_29448,                // shader
        field_21370,                // transparency: TRANSLUCENT_TRANSPARENCY
        field_21348,                // depth test: LEQUAL_DEPTH_TEST (default)
        field_21344,                // cull: CULL (default)
        field_21383,                // lightmap: LIGHTMAP
        field_21386,                // overlay: NO_OVERLAY (default)
        field_21352,                // layering: NO_LAYERING (default)
        field_25280,                // output: TRANSLUCENT_TARGET
        field_21379,                // texturing: DEFAULT_TEXTURING (default)
        field_21349,                // write mask: COLOR_DEPTH_WRITE (default)
        field_21360,                // line: DEFAULT_LINE (default)
        field_44815);               // colour logic: NO_COLOR_LOGIC (default)

    CbNoCrumblingRenderType() {
        super("translucent_no_crumbling", net.minecraft.class_290.field_1590, net.minecraft.class_293.class_5596.field_27382,
            262144, false, true,
            () -> STATES.forEach(net.minecraft.class_4668::method_23516),
            () -> STATES.forEach(net.minecraft.class_4668::method_23518));
    }

    @Override
    public Optional<net.minecraft.class_1921> method_24296() {
        return net.minecraft.class_1921.method_23583().method_24296();
    }
}

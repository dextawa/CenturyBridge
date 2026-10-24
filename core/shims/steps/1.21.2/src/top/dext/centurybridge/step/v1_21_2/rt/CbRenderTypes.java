package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The two core shaders 1.21.2 deleted, and the render types and shader shards 1.21.1 built on them.
 *
 * <p>rendertype_entity_glint_direct: 1.21.1's json, vsh and fsh are 1.21.1's rendertype_entity_glint ones under
 * another name (the three files differ only in the program names; compared in the 1.21.1 client jar), so its program
 * is 1.21.2's CoreShaders.RENDERTYPE_ENTITY_GLINT.
 *
 * <p>rendertype_entity_translucent_cull: 1.21.1's rendertype_entity_translucent without the overlay (no Sampler1, no
 * overlay mix; the lightmap and the 0.1 alpha cutout the same). 1.21.2 builds every entity shader from core/entity
 * with defines, NO_OVERLAY among them; its rendertype_entity_translucent config is core/entity with ALPHA_CUTOUT 0.1,
 * so the program is that config with NO_OVERLAY -- a ShaderProgram key the ShaderManager compiles on first use like
 * any CoreShaders key. (1.21.1 multiplied the lightmap per vertex, 1.21.2 per fragment: the same product,
 * interpolated in another order.)
 *
 * <p>The render types are 1.21.1's (RenderType.entityTranslucentCull(texture), memoized per texture as 1.21.1 did, and
 * RenderType.entityGlintDirect()), built with 1.21.2's states of the same names; a texture's 1.21.1 blur flag is
 * 1.21.2's TriState of the same value. This class extends RenderStateShard only to read its protected states.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderTypes extends net.minecraft.class_4668 {

    /** rendertype_entity_translucent_cull: core/rendertype_entity_translucent with NO_OVERLAY */
    public static final net.minecraft.class_10156 ENTITY_TRANSLUCENT_CULL = new net.minecraft.class_10156(
        net.minecraft.class_2960.method_60656("core/rendertype_entity_translucent"), net.minecraft.class_290.field_1580,
        net.minecraft.class_10149.method_62926().method_62932("NO_OVERLAY").method_62931());

    /** RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER */
    private static final net.minecraft.class_4668.class_5942 TRANSLUCENT_CULL_SHARD = new net.minecraft.class_4668.class_5942(ENTITY_TRANSLUCENT_CULL);

    /** RenderStateShard.RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER */
    private static final net.minecraft.class_4668.class_5942 GLINT_DIRECT_SHARD = new net.minecraft.class_4668.class_5942(net.minecraft.class_10142.field_53852);

    private static final Function<net.minecraft.class_2960, net.minecraft.class_1921> ENTITY_TRANSLUCENT_CULL_TYPES = net.minecraft.class_156.method_34866(texture ->
        net.minecraft.class_1921.method_24049("entity_translucent_cull", net.minecraft.class_290.field_1580, net.minecraft.class_293.class_5596.field_27382, 1536, true, true,
            net.minecraft.class_1921.class_4688.method_23598()
                .method_34578(TRANSLUCENT_CULL_SHARD)
                .method_34577(new net.minecraft.class_4668.class_4683(texture, net.minecraft.class_9851.field_52395, false))
                .method_23615(field_21370)
                .method_23608(field_21383)
                .method_23611(field_21385)
                .method_23617(true)));

    private static net.minecraft.class_1921 entityGlintDirect;

    private CbRenderTypes() {
        super("centurybridge_render_types", () -> {
        }, () -> {
        });
    }

    /** the program of the two 1.21.1 core shader names 1.21.2 deleted, or null (GameRenderer.getShader(name)) */
    public static net.minecraft.class_10156 byName(String name) {
        return switch (name) {
            case "rendertype_entity_translucent_cull" -> ENTITY_TRANSLUCENT_CULL;
            case "rendertype_entity_glint_direct" -> net.minecraft.class_10142.field_53852;
            default -> null;
        };
    }

    /** 1.21.1 GameRenderer.getRendertypeEntityTranslucentCullShader() */
    public static net.minecraft.class_5944 method_34507() {
        return CbRenderShaders.program(ENTITY_TRANSLUCENT_CULL);
    }

    /** 1.21.1 GameRenderer.getRendertypeEntityGlintDirectShader() */
    public static net.minecraft.class_5944 method_34528() {
        return CbRenderShaders.program(net.minecraft.class_10142.field_53852);
    }

    /** 1.21.1 RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER */
    public static net.minecraft.class_4668.class_5942 field_29406() {
        return TRANSLUCENT_CULL_SHARD;
    }

    /** 1.21.1 RenderStateShard.RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER */
    public static net.minecraft.class_4668.class_5942 field_29425() {
        return GLINT_DIRECT_SHARD;
    }

    /** 1.21.1 RenderType.entityTranslucentCull(texture) */
    public static net.minecraft.class_1921 method_23689(net.minecraft.class_2960 texture) {
        return ENTITY_TRANSLUCENT_CULL_TYPES.apply(texture);
    }

    /** 1.21.1 RenderType.entityGlintDirect(): one render type, built on first use */
    public static synchronized net.minecraft.class_1921 method_29707() {
        if (entityGlintDirect == null) {
            entityGlintDirect = net.minecraft.class_1921.method_24048("entity_glint_direct", net.minecraft.class_290.field_1585, net.minecraft.class_293.class_5596.field_27382, 1536,
                net.minecraft.class_1921.class_4688.method_23598()
                    .method_34578(GLINT_DIRECT_SHARD)
                    .method_34577(new net.minecraft.class_4668.class_4683(net.minecraft.class_918.field_43086, net.minecraft.class_9851.field_52394, false))
                    .method_23616(field_21350)
                    .method_23603(field_21345)
                    .method_23604(field_21347)
                    .method_23615(field_21368)
                    .method_23614(field_21382)
                    .method_23617(false));
        }
        return entityGlintDirect;
    }
}

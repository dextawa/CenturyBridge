package top.dext.centurybridge.step.v1_21_5.rt;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.LogicOp;
import com.mojang.blaze3d.platform.SourceFactor;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_5.facade.CoreShaders;
import top.dext.centurybridge.step.v1_21_5.facade.RenderStateShard$class_4671;
import top.dext.centurybridge.step.v1_21_5.facade.RenderStateShard$class_4672;
import top.dext.centurybridge.step.v1_21_5.facade.RenderStateShard$class_4685;
import top.dext.centurybridge.step.v1_21_5.facade.RenderStateShard$class_4686;
import top.dext.centurybridge.step.v1_21_5.facade.RenderStateShard$class_5942;
import top.dext.centurybridge.step.v1_21_5.facade.RenderStateShard$class_8559;

/**
 * The RenderStateShard constants 1.21.5 dropped from net/minecraft/class_4668 (damage: 80 get-static-protected rows),
 * given back as fieldGetters (s205k-rendertype.json), one shared instance each as 1.21.4 had. Built as 1.21.4's
 * &lt;clinit&gt; built them (javap -c -p 1.21.4 class_4668, its lambda bodies; facts D:/tmp/s205i/shardmap.txt):
 * <ul>
 * <li>shader shards: new ShaderStateShard(CoreShaders.X) for the program 1.21.4 passed; NO_SHADER = the no-program form;
 * <li>transparency: setup enableBlend + the blend function 1.21.4 set, clear disableBlend + defaultBlendFunc
 * (no_transparency: setup disableBlend, clear nothing); each carries the same blend as a 1.21.5 BlendFunction (a
 * constant where 1.21.5 has one with those factors, else new BlendFunction with them);
 * <li>depth test, cull, write mask: their 1.21.4 constructors (always/==/&lt;=/&gt;; cull on/off; colour+depth, colour,
 * depth);
 * <li>polygon_offset_layering (-1, -10) and world_border_layering (-3, -3): 1.21.5 LayeringStateShards (class_4675 is
 * still there) whose runnables set 1.21.4's polygon offset; the offset is also recorded ({@link #depthBias}) so a
 * render type's pipeline carries it (1.21.5's command encoder sets polygon offset from the pipeline at every draw);
 * <li>clouds_target: a 1.21.5 OutputStateShard over LevelRenderer.getCloudsTarget, the main target when null (1.21.4's
 * setup bound the same, 1.21.5's OutputStateShard takes the target as a supplier);
 * <li>colour logic: no_color_logic (disableColorLogicOp; LogicOp.NONE), or_reverse (enableColorLogicOp +
 * logicOp(OR_REVERSE), clear disableColorLogicOp; LogicOp.OR_REVERSE).
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbRenderStates {

    private CbRenderStates() {
    }

    private static final Map<net.minecraft.class_4668.class_4675, float[]> DEPTH_BIAS = Collections.synchronizedMap(new WeakHashMap<>());

    /** the depth bias (scale factor, constant) 1.21.4's layering shard set, null when it set none this layer knows of */
    public static float[] depthBias(net.minecraft.class_4668.class_4675 layering) {
        return layering == null ? null : DEPTH_BIAS.get(layering);
    }

    // ---- transparency (GL factors: ZERO 0, ONE 1, SRC_COLOR 768, ONE_MINUS_SRC_COLOR 769, SRC_ALPHA 770,
    // ONE_MINUS_SRC_ALPHA 771, DST_COLOR 774, ONE_MINUS_DST_COLOR 775) ----

    private static void clearBlend() {
        CbGlRenderSystem.disableBlend();
        CbGlRenderSystem.defaultBlendFunc();
    }

    private static RenderStateShard$class_4685 blend2(String name, int src, int dst, BlendFunction f) {
        return new RenderStateShard$class_4685(name, () -> {
            CbGlRenderSystem.enableBlend();
            CbShardRenderSystem.blendFunc(src, dst);
        }, CbRenderStates::clearBlend, Optional.of(f));
    }

    private static RenderStateShard$class_4685 blend4(String name, int sc, int dc, int sa, int da, BlendFunction f) {
        return new RenderStateShard$class_4685(name, () -> {
            CbGlRenderSystem.enableBlend();
            CbShardRenderSystem.blendFuncSeparate(sc, dc, sa, da);
        }, CbRenderStates::clearBlend, Optional.of(f));
    }

    private static final RenderStateShard$class_4685 NO_TRANSPARENCY = new RenderStateShard$class_4685("no_transparency",
        CbGlRenderSystem::disableBlend, () -> {
        }, Optional.empty());
    private static final RenderStateShard$class_4685 ADDITIVE = blend2("additive_transparency", 1, 1, BlendFunction.ADDITIVE);
    private static final RenderStateShard$class_4685 LIGHTNING = blend2("lightning_transparency", 770, 1, BlendFunction.LIGHTNING);
    private static final RenderStateShard$class_4685 GLINT = blend4("glint_transparency", 768, 1, 0, 1, BlendFunction.GLINT);
    private static final RenderStateShard$class_4685 CRUMBLING = blend4("crumbling_transparency", 774, 768, 1, 0,
        new BlendFunction(SourceFactor.DST_COLOR, DestFactor.SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO));
    private static final RenderStateShard$class_4685 OVERLAY = blend4("overlay_transparency", 770, 1, 1, 0, BlendFunction.OVERLAY);
    private static final RenderStateShard$class_4685 TRANSLUCENT = blend4("translucent_transparency", 770, 771, 1, 771, BlendFunction.TRANSLUCENT);
    private static final RenderStateShard$class_4685 VIGNETTE = blend2("vignette_transparency", 0, 769,
        new BlendFunction(SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_COLOR));
    private static final RenderStateShard$class_4685 CROSSHAIR = blend4("crosshair_transparency", 775, 769, 1, 0,
        new BlendFunction(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO));
    private static final RenderStateShard$class_4685 MOJANG_LOGO = blend2("mojang_logo_transparency", 770, 1,
        new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE));
    private static final RenderStateShard$class_4685 NAUSEA_OVERLAY = blend4("nausea_overlay_transparency", 1, 1, 1, 1,
        new BlendFunction(SourceFactor.ONE, DestFactor.ONE, SourceFactor.ONE, DestFactor.ONE));

    public static RenderStateShard$class_4685 field_21364() { return NO_TRANSPARENCY; }
    public static RenderStateShard$class_4685 field_21366() { return ADDITIVE; }
    public static RenderStateShard$class_4685 field_21367() { return LIGHTNING; }
    public static RenderStateShard$class_4685 field_21368() { return GLINT; }
    public static RenderStateShard$class_4685 field_21369() { return CRUMBLING; }
    public static RenderStateShard$class_4685 field_55137() { return OVERLAY; }
    public static RenderStateShard$class_4685 field_21370() { return TRANSLUCENT; }
    public static RenderStateShard$class_4685 field_53124() { return VIGNETTE; }
    public static RenderStateShard$class_4685 field_53125() { return CROSSHAIR; }
    public static RenderStateShard$class_4685 field_53126() { return MOJANG_LOGO; }
    public static RenderStateShard$class_4685 field_53127() { return NAUSEA_OVERLAY; }

    // ---- depth test, cull, write mask ----

    private static final RenderStateShard$class_4672 NO_DEPTH_TEST = new RenderStateShard$class_4672("always", 519);
    private static final RenderStateShard$class_4672 EQUAL_DEPTH_TEST = new RenderStateShard$class_4672("==", 514);
    private static final RenderStateShard$class_4672 LEQUAL_DEPTH_TEST = new RenderStateShard$class_4672("<=", 515);
    private static final RenderStateShard$class_4672 GREATER_DEPTH_TEST = new RenderStateShard$class_4672(">", 516);

    public static RenderStateShard$class_4672 field_21346() { return NO_DEPTH_TEST; }
    public static RenderStateShard$class_4672 field_21347() { return EQUAL_DEPTH_TEST; }
    public static RenderStateShard$class_4672 field_21348() { return LEQUAL_DEPTH_TEST; }
    public static RenderStateShard$class_4672 field_44814() { return GREATER_DEPTH_TEST; }

    private static final RenderStateShard$class_4671 CULL = new RenderStateShard$class_4671(true);
    private static final RenderStateShard$class_4671 NO_CULL = new RenderStateShard$class_4671(false);

    public static RenderStateShard$class_4671 field_21344() { return CULL; }
    public static RenderStateShard$class_4671 field_21345() { return NO_CULL; }

    private static final RenderStateShard$class_4686 COLOR_DEPTH_WRITE = new RenderStateShard$class_4686(true, true);
    private static final RenderStateShard$class_4686 COLOR_WRITE = new RenderStateShard$class_4686(true, false);
    private static final RenderStateShard$class_4686 DEPTH_WRITE = new RenderStateShard$class_4686(false, true);

    public static RenderStateShard$class_4686 field_21349() { return COLOR_DEPTH_WRITE; }
    public static RenderStateShard$class_4686 field_21350() { return COLOR_WRITE; }
    public static RenderStateShard$class_4686 field_21351() { return DEPTH_WRITE; }

    // ---- layering with polygon offset ----

    private static net.minecraft.class_4668.class_4675 offsetLayering(String name, float factor, float units) {
        net.minecraft.class_4668.class_4675 s = new net.minecraft.class_4668.class_4675(name, () -> {
            CbShardRenderSystem.polygonOffset(factor, units);
            CbShardRenderSystem.enablePolygonOffset();
        }, () -> {
            CbShardRenderSystem.polygonOffset(0.0F, 0.0F);
            CbShardRenderSystem.disablePolygonOffset();
        });
        DEPTH_BIAS.put(s, new float[] {factor, units});
        return s;
    }

    private static final net.minecraft.class_4668.class_4675 POLYGON_OFFSET_LAYERING = offsetLayering("polygon_offset_layering", -1.0F, -10.0F);
    private static final net.minecraft.class_4668.class_4675 WORLD_BORDER_LAYERING = offsetLayering("world_border_layering", -3.0F, -3.0F);

    public static net.minecraft.class_4668.class_4675 field_21353() { return POLYGON_OFFSET_LAYERING; }
    public static net.minecraft.class_4668.class_4675 field_55136() { return WORLD_BORDER_LAYERING; }

    // ---- clouds target ----

    private static final net.minecraft.class_4668.class_4678 CLOUDS_TARGET = new net.minecraft.class_4668.class_4678("clouds_target", () -> {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        net.minecraft.class_276 t = mc.field_1769.method_29364();
        return t != null ? t : mc.method_1522();
    });

    public static net.minecraft.class_4668.class_4678 field_25283() { return CLOUDS_TARGET; }

    // ---- colour logic ----

    private static final RenderStateShard$class_8559 NO_COLOR_LOGIC = new RenderStateShard$class_8559("no_color_logic",
        CbShardRenderSystem::disableColorLogicOp, () -> {
        }, LogicOp.NONE);
    private static final RenderStateShard$class_8559 OR_REVERSE = new RenderStateShard$class_8559("or_reverse", () -> {
        CbShardRenderSystem.enableColorLogicOp();
        CbGlRenderSystem.logicOp(top.dext.centurybridge.step.v1_21_5.facade.GlStateManager$class_1030.OR_REVERSE);
    }, CbShardRenderSystem::disableColorLogicOp, LogicOp.OR_REVERSE);

    public static RenderStateShard$class_8559 field_44815() { return NO_COLOR_LOGIC; }
    public static RenderStateShard$class_8559 field_44816() { return OR_REVERSE; }

    // ---- shader shards (1.21.4 field -> CoreShaders constant, generated from 1.21.4's <clinit>) ----

    private static final RenderStateShard$class_5942 NO_SHADER = new RenderStateShard$class_5942();

    public static RenderStateShard$class_5942 field_29434() { return NO_SHADER; }
    /** position_color_lightmap */
    private static final RenderStateShard$class_5942 S_field_29437 = new RenderStateShard$class_5942(CoreShaders.field_53877);
    /** position */
    private static final RenderStateShard$class_5942 S_field_29438 = new RenderStateShard$class_5942(CoreShaders.field_53875);
    /** position_tex */
    private static final RenderStateShard$class_5942 S_field_29440 = new RenderStateShard$class_5942(CoreShaders.field_53879);
    /** position_color_tex_lightmap */
    private static final RenderStateShard$class_5942 S_field_29441 = new RenderStateShard$class_5942(CoreShaders.field_53878);
    /** position_color */
    private static final RenderStateShard$class_5942 S_field_29442 = new RenderStateShard$class_5942(CoreShaders.field_53876);
    /** position_tex_color */
    private static final RenderStateShard$class_5942 S_field_53128 = new RenderStateShard$class_5942(CoreShaders.field_53880);
    /** particle */
    private static final RenderStateShard$class_5942 S_field_55138 = new RenderStateShard$class_5942(CoreShaders.field_53874);
    /** rendertype_solid */
    private static final RenderStateShard$class_5942 S_field_29443 = new RenderStateShard$class_5942(CoreShaders.field_53881);
    /** rendertype_cutout_mipped */
    private static final RenderStateShard$class_5942 S_field_29444 = new RenderStateShard$class_5942(CoreShaders.field_53882);
    /** rendertype_cutout */
    private static final RenderStateShard$class_5942 S_field_29445 = new RenderStateShard$class_5942(CoreShaders.field_53883);
    /** rendertype_translucent */
    private static final RenderStateShard$class_5942 S_field_29446 = new RenderStateShard$class_5942(CoreShaders.field_53884);
    /** rendertype_translucent_moving_block */
    private static final RenderStateShard$class_5942 S_field_29447 = new RenderStateShard$class_5942(CoreShaders.field_53885);
    /** rendertype_armor_cutout_no_cull */
    private static final RenderStateShard$class_5942 S_field_29449 = new RenderStateShard$class_5942(CoreShaders.field_53886);
    /** rendertype_armor_translucent */
    private static final RenderStateShard$class_5942 S_field_54961 = new RenderStateShard$class_5942(CoreShaders.field_54960);
    /** rendertype_entity_solid */
    private static final RenderStateShard$class_5942 S_field_29450 = new RenderStateShard$class_5942(CoreShaders.field_53887);
    /** rendertype_entity_cutout */
    private static final RenderStateShard$class_5942 S_field_29451 = new RenderStateShard$class_5942(CoreShaders.field_53888);
    /** rendertype_entity_cutout_no_cull */
    private static final RenderStateShard$class_5942 S_field_29452 = new RenderStateShard$class_5942(CoreShaders.field_53889);
    /** rendertype_entity_cutout_no_cull_z_offset */
    private static final RenderStateShard$class_5942 S_field_29404 = new RenderStateShard$class_5942(CoreShaders.field_53890);
    /** rendertype_item_entity_translucent_cull */
    private static final RenderStateShard$class_5942 S_field_29405 = new RenderStateShard$class_5942(CoreShaders.field_53891);
    /** rendertype_entity_translucent */
    private static final RenderStateShard$class_5942 S_field_29407 = new RenderStateShard$class_5942(CoreShaders.field_53892);
    /** rendertype_entity_translucent_emissive */
    private static final RenderStateShard$class_5942 S_field_38344 = new RenderStateShard$class_5942(CoreShaders.field_53893);
    /** rendertype_entity_smooth_cutout */
    private static final RenderStateShard$class_5942 S_field_29408 = new RenderStateShard$class_5942(CoreShaders.field_53894);
    /** rendertype_beacon_beam */
    private static final RenderStateShard$class_5942 S_field_29409 = new RenderStateShard$class_5942(CoreShaders.field_53895);
    /** rendertype_entity_decal */
    private static final RenderStateShard$class_5942 S_field_29410 = new RenderStateShard$class_5942(CoreShaders.field_53896);
    /** rendertype_entity_no_outline */
    private static final RenderStateShard$class_5942 S_field_29411 = new RenderStateShard$class_5942(CoreShaders.field_53897);
    /** rendertype_entity_shadow */
    private static final RenderStateShard$class_5942 S_field_29412 = new RenderStateShard$class_5942(CoreShaders.field_53842);
    /** rendertype_entity_alpha */
    private static final RenderStateShard$class_5942 S_field_29413 = new RenderStateShard$class_5942(CoreShaders.field_53843);
    /** rendertype_eyes */
    private static final RenderStateShard$class_5942 S_field_29414 = new RenderStateShard$class_5942(CoreShaders.field_53844);
    /** rendertype_energy_swirl */
    private static final RenderStateShard$class_5942 S_field_29415 = new RenderStateShard$class_5942(CoreShaders.field_53845);
    /** rendertype_leash */
    private static final RenderStateShard$class_5942 S_field_29416 = new RenderStateShard$class_5942(CoreShaders.field_53846);
    /** rendertype_water_mask */
    private static final RenderStateShard$class_5942 S_field_29417 = new RenderStateShard$class_5942(CoreShaders.field_53847);
    /** rendertype_outline */
    private static final RenderStateShard$class_5942 S_field_29418 = new RenderStateShard$class_5942(CoreShaders.field_53848);
    /** rendertype_armor_entity_glint */
    private static final RenderStateShard$class_5942 S_field_29420 = new RenderStateShard$class_5942(CoreShaders.field_53849);
    /** rendertype_glint_translucent */
    private static final RenderStateShard$class_5942 S_field_29421 = new RenderStateShard$class_5942(CoreShaders.field_53850);
    /** rendertype_glint */
    private static final RenderStateShard$class_5942 S_field_29422 = new RenderStateShard$class_5942(CoreShaders.field_53851);
    /** rendertype_entity_glint */
    private static final RenderStateShard$class_5942 S_field_29424 = new RenderStateShard$class_5942(CoreShaders.field_53852);
    /** rendertype_crumbling */
    private static final RenderStateShard$class_5942 S_field_29426 = new RenderStateShard$class_5942(CoreShaders.field_53865);
    /** rendertype_text */
    private static final RenderStateShard$class_5942 S_field_29427 = new RenderStateShard$class_5942(CoreShaders.field_53853);
    /** rendertype_text_background */
    private static final RenderStateShard$class_5942 S_field_42517 = new RenderStateShard$class_5942(CoreShaders.field_53854);
    /** rendertype_text_intensity */
    private static final RenderStateShard$class_5942 S_field_33628 = new RenderStateShard$class_5942(CoreShaders.field_53855);
    /** rendertype_text_see_through */
    private static final RenderStateShard$class_5942 S_field_29428 = new RenderStateShard$class_5942(CoreShaders.field_53856);
    /** rendertype_text_background_see_through */
    private static final RenderStateShard$class_5942 S_field_42518 = new RenderStateShard$class_5942(CoreShaders.field_53857);
    /** rendertype_text_intensity_see_through */
    private static final RenderStateShard$class_5942 S_field_33629 = new RenderStateShard$class_5942(CoreShaders.field_53858);
    /** rendertype_lightning */
    private static final RenderStateShard$class_5942 S_field_29429 = new RenderStateShard$class_5942(CoreShaders.field_53859);
    /** rendertype_tripwire */
    private static final RenderStateShard$class_5942 S_field_29430 = new RenderStateShard$class_5942(CoreShaders.field_53860);
    /** rendertype_end_portal */
    private static final RenderStateShard$class_5942 S_field_29431 = new RenderStateShard$class_5942(CoreShaders.field_53861);
    /** rendertype_end_gateway */
    private static final RenderStateShard$class_5942 S_field_29432 = new RenderStateShard$class_5942(CoreShaders.field_53862);
    /** rendertype_clouds */
    private static final RenderStateShard$class_5942 S_field_48949 = new RenderStateShard$class_5942(CoreShaders.field_53863);
    /** rendertype_lines */
    private static final RenderStateShard$class_5942 S_field_29433 = new RenderStateShard$class_5942(CoreShaders.field_53864);
    /** rendertype_gui */
    private static final RenderStateShard$class_5942 S_field_44817 = new RenderStateShard$class_5942(CoreShaders.field_53866);
    /** rendertype_gui_overlay */
    private static final RenderStateShard$class_5942 S_field_44818 = new RenderStateShard$class_5942(CoreShaders.field_53867);
    /** rendertype_gui_text_highlight */
    private static final RenderStateShard$class_5942 S_field_44819 = new RenderStateShard$class_5942(CoreShaders.field_53869);
    /** rendertype_gui_ghost_recipe_overlay */
    private static final RenderStateShard$class_5942 S_field_44820 = new RenderStateShard$class_5942(CoreShaders.field_53870);
    /** rendertype_breeze_wind */
    private static final RenderStateShard$class_5942 S_field_47463 = new RenderStateShard$class_5942(CoreShaders.field_53871);

    public static RenderStateShard$class_5942 field_29437() { return S_field_29437; }
    public static RenderStateShard$class_5942 field_29438() { return S_field_29438; }
    public static RenderStateShard$class_5942 field_29440() { return S_field_29440; }
    public static RenderStateShard$class_5942 field_29441() { return S_field_29441; }
    public static RenderStateShard$class_5942 field_29442() { return S_field_29442; }
    public static RenderStateShard$class_5942 field_53128() { return S_field_53128; }
    public static RenderStateShard$class_5942 field_55138() { return S_field_55138; }
    public static RenderStateShard$class_5942 field_29443() { return S_field_29443; }
    public static RenderStateShard$class_5942 field_29444() { return S_field_29444; }
    public static RenderStateShard$class_5942 field_29445() { return S_field_29445; }
    public static RenderStateShard$class_5942 field_29446() { return S_field_29446; }
    public static RenderStateShard$class_5942 field_29447() { return S_field_29447; }
    public static RenderStateShard$class_5942 field_29449() { return S_field_29449; }
    public static RenderStateShard$class_5942 field_54961() { return S_field_54961; }
    public static RenderStateShard$class_5942 field_29450() { return S_field_29450; }
    public static RenderStateShard$class_5942 field_29451() { return S_field_29451; }
    public static RenderStateShard$class_5942 field_29452() { return S_field_29452; }
    public static RenderStateShard$class_5942 field_29404() { return S_field_29404; }
    public static RenderStateShard$class_5942 field_29405() { return S_field_29405; }
    public static RenderStateShard$class_5942 field_29407() { return S_field_29407; }
    public static RenderStateShard$class_5942 field_38344() { return S_field_38344; }
    public static RenderStateShard$class_5942 field_29408() { return S_field_29408; }
    public static RenderStateShard$class_5942 field_29409() { return S_field_29409; }
    public static RenderStateShard$class_5942 field_29410() { return S_field_29410; }
    public static RenderStateShard$class_5942 field_29411() { return S_field_29411; }
    public static RenderStateShard$class_5942 field_29412() { return S_field_29412; }
    public static RenderStateShard$class_5942 field_29413() { return S_field_29413; }
    public static RenderStateShard$class_5942 field_29414() { return S_field_29414; }
    public static RenderStateShard$class_5942 field_29415() { return S_field_29415; }
    public static RenderStateShard$class_5942 field_29416() { return S_field_29416; }
    public static RenderStateShard$class_5942 field_29417() { return S_field_29417; }
    public static RenderStateShard$class_5942 field_29418() { return S_field_29418; }
    public static RenderStateShard$class_5942 field_29420() { return S_field_29420; }
    public static RenderStateShard$class_5942 field_29421() { return S_field_29421; }
    public static RenderStateShard$class_5942 field_29422() { return S_field_29422; }
    public static RenderStateShard$class_5942 field_29424() { return S_field_29424; }
    public static RenderStateShard$class_5942 field_29426() { return S_field_29426; }
    public static RenderStateShard$class_5942 field_29427() { return S_field_29427; }
    public static RenderStateShard$class_5942 field_42517() { return S_field_42517; }
    public static RenderStateShard$class_5942 field_33628() { return S_field_33628; }
    public static RenderStateShard$class_5942 field_29428() { return S_field_29428; }
    public static RenderStateShard$class_5942 field_42518() { return S_field_42518; }
    public static RenderStateShard$class_5942 field_33629() { return S_field_33629; }
    public static RenderStateShard$class_5942 field_29429() { return S_field_29429; }
    public static RenderStateShard$class_5942 field_29430() { return S_field_29430; }
    public static RenderStateShard$class_5942 field_29431() { return S_field_29431; }
    public static RenderStateShard$class_5942 field_29432() { return S_field_29432; }
    public static RenderStateShard$class_5942 field_48949() { return S_field_48949; }
    public static RenderStateShard$class_5942 field_29433() { return S_field_29433; }
    public static RenderStateShard$class_5942 field_44817() { return S_field_44817; }
    public static RenderStateShard$class_5942 field_44818() { return S_field_44818; }
    public static RenderStateShard$class_5942 field_44819() { return S_field_44819; }
    public static RenderStateShard$class_5942 field_44820() { return S_field_44820; }
    public static RenderStateShard$class_5942 field_47463() { return S_field_47463; }
}

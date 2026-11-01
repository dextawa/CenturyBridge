package top.dext.centurybridge.step.v1_21_5.facade;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10149;
import net.minecraft.class_2960;
import net.minecraft.class_290;

/**
 * 1.21.4's net.minecraft.client.renderer.CoreShaders (net/minecraft/class_10142), gone in 1.21.5 with ShaderProgram
 * (facade/ShaderProgram): its 56 constants, in 1.21.4's order, each built as 1.21.4's method_62903 built it (javap -c -p
 * class_10142): id = ResourceLocation.withDefaultNamespace("core/" + name) (class_2960.method_60656), the
 * DefaultVertexFormat field 1.21.4 passed (class_290 keeps every one of those fields in 1.21.5, javap), defines
 * ShaderDefines.EMPTY (class_10149.field_53930), added to the list method_62901 answers. Converted references name this
 * class (classRenames). Members public (access wideners never reach a CenturyBridge class).
 * BLIT_SCREEN and LIGHTMAP passed DefaultVertexFormat.BLIT_SCREEN (field_29336), gone at 1.21.9 (javap 1.21.8 / 1.21.9
 * class_290); 1.21.5 builds it as builder().add("Position", VertexFormatElement.POSITION).build(), exactly as POSITION
 * (field_1592, kept to 26.2), and VertexFormat.equals compares elements mask, vertex size, names and offsets (javap -c 1.21.5), so
 * POSITION is the equal value and the layer never names the dying field.
 */
@Environment(EnvType.CLIENT)
public class CoreShaders {
    /** PROGRAMS */
    public static final List<ShaderProgram> field_53872 = new ArrayList<>();
    /** BLIT_SCREEN */
    public static final ShaderProgram field_53868 = method_62902("blit_screen", class_290.field_1592);
    /** LIGHTMAP */
    public static final ShaderProgram field_53873 = method_62902("lightmap", class_290.field_1592);
    /** PARTICLE */
    public static final ShaderProgram field_53874 = method_62902("particle", class_290.field_1584);
    /** POSITION */
    public static final ShaderProgram field_53875 = method_62902("position", class_290.field_1592);
    /** POSITION_COLOR */
    public static final ShaderProgram field_53876 = method_62902("position_color", class_290.field_1576);
    /** POSITION_COLOR_LIGHTMAP */
    public static final ShaderProgram field_53877 = method_62902("position_color_lightmap", class_290.field_21468);
    /** POSITION_COLOR_TEX_LIGHTMAP */
    public static final ShaderProgram field_53878 = method_62902("position_color_tex_lightmap", class_290.field_20888);
    /** POSITION_TEX */
    public static final ShaderProgram field_53879 = method_62902("position_tex", class_290.field_1585);
    /** POSITION_TEX_COLOR */
    public static final ShaderProgram field_53880 = method_62902("position_tex_color", class_290.field_1575);
    /** RENDERTYPE_SOLID */
    public static final ShaderProgram field_53881 = method_62902("rendertype_solid", class_290.field_1590);
    /** RENDERTYPE_CUTOUT_MIPPED */
    public static final ShaderProgram field_53882 = method_62902("rendertype_cutout_mipped", class_290.field_1590);
    /** RENDERTYPE_CUTOUT */
    public static final ShaderProgram field_53883 = method_62902("rendertype_cutout", class_290.field_1590);
    /** RENDERTYPE_TRANSLUCENT */
    public static final ShaderProgram field_53884 = method_62902("rendertype_translucent", class_290.field_1590);
    /** RENDERTYPE_TRANSLUCENT_MOVING_BLOCK */
    public static final ShaderProgram field_53885 = method_62902("rendertype_translucent_moving_block", class_290.field_1590);
    /** RENDERTYPE_ARMOR_CUTOUT_NO_CULL */
    public static final ShaderProgram field_53886 = method_62902("rendertype_armor_cutout_no_cull", class_290.field_1580);
    /** RENDERTYPE_ARMOR_TRANSLUCENT */
    public static final ShaderProgram field_54960 = method_62902("rendertype_armor_translucent", class_290.field_1580);
    /** RENDERTYPE_ENTITY_SOLID */
    public static final ShaderProgram field_53887 = method_62902("rendertype_entity_solid", class_290.field_1580);
    /** RENDERTYPE_ENTITY_CUTOUT */
    public static final ShaderProgram field_53888 = method_62902("rendertype_entity_cutout", class_290.field_1580);
    /** RENDERTYPE_ENTITY_CUTOUT_NO_CULL */
    public static final ShaderProgram field_53889 = method_62902("rendertype_entity_cutout_no_cull", class_290.field_1580);
    /** RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET */
    public static final ShaderProgram field_53890 = method_62902("rendertype_entity_cutout_no_cull_z_offset", class_290.field_1580);
    /** RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL */
    public static final ShaderProgram field_53891 = method_62902("rendertype_item_entity_translucent_cull", class_290.field_1580);
    /** RENDERTYPE_ENTITY_TRANSLUCENT */
    public static final ShaderProgram field_53892 = method_62902("rendertype_entity_translucent", class_290.field_1580);
    /** RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE */
    public static final ShaderProgram field_53893 = method_62902("rendertype_entity_translucent_emissive", class_290.field_1580);
    /** RENDERTYPE_ENTITY_SMOOTH_CUTOUT */
    public static final ShaderProgram field_53894 = method_62902("rendertype_entity_smooth_cutout", class_290.field_1580);
    /** RENDERTYPE_BEACON_BEAM */
    public static final ShaderProgram field_53895 = method_62902("rendertype_beacon_beam", class_290.field_1590);
    /** RENDERTYPE_ENTITY_DECAL */
    public static final ShaderProgram field_53896 = method_62902("rendertype_entity_decal", class_290.field_1580);
    /** RENDERTYPE_ENTITY_NO_OUTLINE */
    public static final ShaderProgram field_53897 = method_62902("rendertype_entity_no_outline", class_290.field_1580);
    /** RENDERTYPE_ENTITY_SHADOW */
    public static final ShaderProgram field_53842 = method_62902("rendertype_entity_shadow", class_290.field_1580);
    /** RENDERTYPE_ENTITY_ALPHA */
    public static final ShaderProgram field_53843 = method_62902("rendertype_entity_alpha", class_290.field_1580);
    /** RENDERTYPE_EYES */
    public static final ShaderProgram field_53844 = method_62902("rendertype_eyes", class_290.field_1580);
    /** RENDERTYPE_ENERGY_SWIRL */
    public static final ShaderProgram field_53845 = method_62902("rendertype_energy_swirl", class_290.field_1580);
    /** RENDERTYPE_LEASH */
    public static final ShaderProgram field_53846 = method_62902("rendertype_leash", class_290.field_21468);
    /** RENDERTYPE_WATER_MASK */
    public static final ShaderProgram field_53847 = method_62902("rendertype_water_mask", class_290.field_1592);
    /** RENDERTYPE_OUTLINE */
    public static final ShaderProgram field_53848 = method_62902("rendertype_outline", class_290.field_1575);
    /** RENDERTYPE_ARMOR_ENTITY_GLINT */
    public static final ShaderProgram field_53849 = method_62902("rendertype_armor_entity_glint", class_290.field_1585);
    /** RENDERTYPE_GLINT_TRANSLUCENT */
    public static final ShaderProgram field_53850 = method_62902("rendertype_glint_translucent", class_290.field_1585);
    /** RENDERTYPE_GLINT */
    public static final ShaderProgram field_53851 = method_62902("rendertype_glint", class_290.field_1585);
    /** RENDERTYPE_ENTITY_GLINT */
    public static final ShaderProgram field_53852 = method_62902("rendertype_entity_glint", class_290.field_1585);
    /** RENDERTYPE_TEXT */
    public static final ShaderProgram field_53853 = method_62902("rendertype_text", class_290.field_20888);
    /** RENDERTYPE_TEXT_BACKGROUND */
    public static final ShaderProgram field_53854 = method_62902("rendertype_text_background", class_290.field_21468);
    /** RENDERTYPE_TEXT_INTENSITY */
    public static final ShaderProgram field_53855 = method_62902("rendertype_text_intensity", class_290.field_20888);
    /** RENDERTYPE_TEXT_SEE_THROUGH */
    public static final ShaderProgram field_53856 = method_62902("rendertype_text_see_through", class_290.field_20888);
    /** RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH */
    public static final ShaderProgram field_53857 = method_62902("rendertype_text_background_see_through", class_290.field_21468);
    /** RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH */
    public static final ShaderProgram field_53858 = method_62902("rendertype_text_intensity_see_through", class_290.field_20888);
    /** RENDERTYPE_LIGHTNING */
    public static final ShaderProgram field_53859 = method_62902("rendertype_lightning", class_290.field_1576);
    /** RENDERTYPE_TRIPWIRE */
    public static final ShaderProgram field_53860 = method_62902("rendertype_tripwire", class_290.field_1590);
    /** RENDERTYPE_END_PORTAL */
    public static final ShaderProgram field_53861 = method_62902("rendertype_end_portal", class_290.field_1592);
    /** RENDERTYPE_END_GATEWAY */
    public static final ShaderProgram field_53862 = method_62902("rendertype_end_gateway", class_290.field_1592);
    /** RENDERTYPE_CLOUDS */
    public static final ShaderProgram field_53863 = method_62902("rendertype_clouds", class_290.field_1576);
    /** RENDERTYPE_LINES */
    public static final ShaderProgram field_53864 = method_62902("rendertype_lines", class_290.field_29337);
    /** RENDERTYPE_CRUMBLING */
    public static final ShaderProgram field_53865 = method_62902("rendertype_crumbling", class_290.field_1590);
    /** RENDERTYPE_GUI */
    public static final ShaderProgram field_53866 = method_62902("rendertype_gui", class_290.field_1576);
    /** RENDERTYPE_GUI_OVERLAY */
    public static final ShaderProgram field_53867 = method_62902("rendertype_gui_overlay", class_290.field_1576);
    /** RENDERTYPE_GUI_TEXT_HIGHLIGHT */
    public static final ShaderProgram field_53869 = method_62902("rendertype_gui_text_highlight", class_290.field_1576);
    /** RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY */
    public static final ShaderProgram field_53870 = method_62902("rendertype_gui_ghost_recipe_overlay", class_290.field_1576);
    /** RENDERTYPE_BREEZE_WIND */
    public static final ShaderProgram field_53871 = method_62902("rendertype_breeze_wind", class_290.field_1580);

    /** register(String, VertexFormat) */
    public static ShaderProgram method_62902(String name, com.mojang.blaze3d.vertex.VertexFormat format) {
        return method_62903(name, format, class_10149.field_53930);
    }

    /** register(String, VertexFormat, ShaderDefines) */
    public static ShaderProgram method_62903(String name, com.mojang.blaze3d.vertex.VertexFormat format, class_10149 defines) {
        ShaderProgram p = new ShaderProgram(class_2960.method_60656("core/" + name), format, defines);
        field_53872.add(p);
        return p;
    }

    /** getProgramsToPreload() */
    public static List<ShaderProgram> method_62901() {
        return field_53872;
    }
}

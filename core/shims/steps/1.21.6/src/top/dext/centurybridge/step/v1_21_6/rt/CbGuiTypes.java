package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5's GUI render types (RenderType.gui*, vignette, crosshair, mojang_logo) and the blit / fill calls that took
 * one, on 1.21.6, whose GuiGraphics takes the RenderPipeline itself.
 *
 * <p>The render types are rebuilt as 1.21.5 built them -- RenderType.create(name, bufferSize, pipeline, a composite
 * state with the texture), memoized per texture -- on 1.21.6's RenderType classes (1.21.6's TextureStateShard
 * dropped the blur TriState; 1.21.5 passed FALSE or DEFAULT, neither of which blurs). Three of their pipelines are
 * gone in 1.21.6: gui_overlay, gui_textured_overlay and gui_ghost_recipe_overlay were 1.21.5's gui / gui_textured
 * pipelines with another depth test (NO_DEPTH_TEST, GREATER_DEPTH_TEST; javap of RenderPipelines.&lt;clinit&gt;), and
 * 1.21.6's GUI has no depth buffer at all -- what is drawn later is on top -- so they are 1.21.6's gui / gui_textured.
 *
 * <p>A blit / fill of 1.21.5 handed GuiGraphics a function from the texture to a render type (RenderType::guiTextured)
 * or a render type: its pipeline is what 1.21.6 takes ({@link #pipeline}).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiTypes {

    private CbGuiTypes() {
    }

    private static net.minecraft.class_1921 textured(String name, int size, com.mojang.blaze3d.pipeline.RenderPipeline pipeline,
            net.minecraft.class_2960 texture) {
        return net.minecraft.class_1921.method_24048(name, size, pipeline, net.minecraft.class_1921.class_4688.method_23598()
            .method_34577(new net.minecraft.class_4668.class_4683(texture, false)).method_23617(false));
    }

    private static net.minecraft.class_1921 plain(String name, int size, com.mojang.blaze3d.pipeline.RenderPipeline pipeline) {
        return net.minecraft.class_1921.method_24048(name, size, pipeline,
            net.minecraft.class_1921.class_4688.method_23598().method_23617(false));
    }

    private static final java.util.function.Function<net.minecraft.class_2960, net.minecraft.class_1921> GUI_TEXTURED_OVERLAY =
        net.minecraft.class_156.method_34866(t -> textured("gui_textured_overlay", 1536, net.minecraft.class_10799.field_56883, t));
    private static final java.util.function.Function<net.minecraft.class_2960, net.minecraft.class_1921> GUI_OPAQUE_TEXTURED_BACKGROUND =
        net.minecraft.class_156.method_34866(t -> textured("gui_opaque_textured_background", 786432, net.minecraft.class_10799.field_56887, t));
    private static final java.util.function.Function<net.minecraft.class_2960, net.minecraft.class_1921> GUI_TEXTURED =
        net.minecraft.class_156.method_34866(t -> textured("gui_textured", 786432, net.minecraft.class_10799.field_56883, t));
    private static final java.util.function.Function<net.minecraft.class_2960, net.minecraft.class_1921> VIGNETTE =
        net.minecraft.class_156.method_34866(t -> textured("vignette", 786432, net.minecraft.class_10799.field_56889, t));
    private static final java.util.function.Function<net.minecraft.class_2960, net.minecraft.class_1921> CROSSHAIR =
        net.minecraft.class_156.method_34866(t -> textured("crosshair", 786432, net.minecraft.class_10799.field_56890, t));

    private static net.minecraft.class_1921 gui;
    private static net.minecraft.class_1921 guiOverlay;
    private static net.minecraft.class_1921 nausea;
    private static net.minecraft.class_1921 textHighlight;
    private static net.minecraft.class_1921 ghostRecipe;
    private static net.minecraft.class_1921 mojangLogo;

    // RenderType's 1.21.5 GUI statics (names as 1.21.5 declared them)

    /** guiTexturedOverlay(texture) */
    public static net.minecraft.class_1921 method_62275(net.minecraft.class_2960 texture) {
        return GUI_TEXTURED_OVERLAY.apply(texture);
    }

    /** guiOpaqueTexturedBackground(texture) */
    public static net.minecraft.class_1921 method_62276(net.minecraft.class_2960 texture) {
        return GUI_OPAQUE_TEXTURED_BACKGROUND.apply(texture);
    }

    /** guiTextured(texture) */
    public static net.minecraft.class_1921 method_62277(net.minecraft.class_2960 texture) {
        return GUI_TEXTURED.apply(texture);
    }

    /** vignette(texture) */
    public static net.minecraft.class_1921 method_62279(net.minecraft.class_2960 texture) {
        return VIGNETTE.apply(texture);
    }

    /** crosshair(texture) */
    public static net.minecraft.class_1921 method_62280(net.minecraft.class_2960 texture) {
        return CROSSHAIR.apply(texture);
    }

    /** gui() */
    public static synchronized net.minecraft.class_1921 method_51784() {
        return gui != null ? gui : (gui = plain("gui", 786432, net.minecraft.class_10799.field_56879));
    }

    /** guiOverlay() */
    public static synchronized net.minecraft.class_1921 method_51785() {
        return guiOverlay != null ? guiOverlay : (guiOverlay = plain("gui_overlay", 1536, net.minecraft.class_10799.field_56879));
    }

    /** guiNauseaOverlay() */
    public static synchronized net.minecraft.class_1921 method_62284() {
        return nausea != null ? nausea
            : (nausea = textured("gui_nausea_overlay", 1536, net.minecraft.class_10799.field_56888, net.minecraft.class_329.field_52771));
    }

    /** guiTextHighlight() */
    public static synchronized net.minecraft.class_1921 method_51786() {
        return textHighlight != null ? textHighlight
            : (textHighlight = plain("gui_text_highlight", 1536, net.minecraft.class_10799.field_56881));
    }

    /** guiGhostRecipeOverlay() */
    public static synchronized net.minecraft.class_1921 method_51787() {
        return ghostRecipe != null ? ghostRecipe
            : (ghostRecipe = plain("gui_ghost_recipe_overlay", 1536, net.minecraft.class_10799.field_56879));
    }

    /** mojangLogo() */
    public static synchronized net.minecraft.class_1921 method_62287() {
        return mojangLogo != null ? mojangLogo
            : (mojangLogo = textured("mojang_logo", 786432, net.minecraft.class_10799.field_56839, net.minecraft.class_425.field_2483));
    }

    // ---- what 1.21.6's GuiGraphics takes ----

    /** the pipeline a 1.21.5 render type drew with */
    public static com.mojang.blaze3d.pipeline.RenderPipeline pipeline(net.minecraft.class_1921 type) {
        if (type instanceof net.minecraft.class_1921.class_4687 composite) {
            return composite.field_56922;
        }
        throw new IllegalArgumentException("CenturyBridge: render type " + type
            + " is not a composite render type; 1.21.6's GUI draws only with a pipeline");
    }

    /** blit(Function&lt;texture, RenderType&gt;, texture, ...): the pipeline of the type the function gives the texture */
    public static com.mojang.blaze3d.pipeline.RenderPipeline pipeline(java.util.function.Function<net.minecraft.class_2960, net.minecraft.class_1921> types,
            net.minecraft.class_2960 texture) {
        return pipeline(types.apply(texture));
    }

    /** blitSprite(Function, sprite, ...): the sprite's atlas is the texture the function was given in 1.21.5 */
    public static com.mojang.blaze3d.pipeline.RenderPipeline pipeline(java.util.function.Function<net.minecraft.class_2960, net.minecraft.class_1921> types,
            net.minecraft.class_1058 sprite) {
        return pipeline(types.apply(sprite.method_45852()));
    }
}

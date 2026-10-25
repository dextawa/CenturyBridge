package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5's GuiGraphics.fill(RenderType, x1, y1, x2, y2, z) on 1.21.6. 1.21.5 (javap) put a position-only quad into the
 * render type's buffer: what it drew was the type's pipeline over the textures its texture state bound (the end portal
 * screen's RenderType.endPortal(): the end sky and the portal textures). 1.21.6 draws such a quad with
 * fill(RenderPipeline, TextureSetup, x1, y1, x2, y2), and its own end portal screen does exactly that with the same two
 * textures (ReceivingLevelScreen, javap). So the pipeline is the type's, the texture setup is the type's textures in
 * order -- one for a TextureStateShard, the list of a MultiTextureStateShard (kept by CbTextureMultiMixin, since 1.21.6's
 * shard keeps only the first), none for an empty one. The z is dropped: 1.21.6's GUI has no depth.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiTextures {

    private CbGuiTextures() {
    }

    /** a MultiTextureStateShard's textures, in the order it bound them */
    public interface Multi {
        java.util.List<net.minecraft.class_2960> cb$textures();
    }

    /** fill(RenderType, x1, y1, x2, y2, z) */
    public static void method_57709(net.minecraft.class_332 g, net.minecraft.class_1921 type, int x1, int y1, int x2, int y2, int z) {
        g.method_51739(CbGuiTypes.pipeline(type), setup(type), x1, y1, x2, y2);
    }

    /** the textures a 1.21.5 render type bound, as 1.21.6's GUI takes them */
    public static net.minecraft.class_11231 setup(net.minecraft.class_1921 type) {
        if (!(type instanceof net.minecraft.class_1921.class_4687 composite)) {
            throw new IllegalArgumentException("CenturyBridge: render type " + type
                + " is not a composite render type; 1.21.6's GUI draws only with a pipeline");
        }
        net.minecraft.class_4668.class_5939 state = composite.field_21403.field_21406;
        java.util.List<net.minecraft.class_2960> ids;
        if (state instanceof Multi multi) {
            ids = multi.cb$textures();
        } else {
            ids = state.method_23564().map(java.util.List::of).orElse(java.util.List.of());
        }
        net.minecraft.class_1060 textures = net.minecraft.class_310.method_1551().method_1531();
        return switch (ids.size()) {
            case 0 -> net.minecraft.class_11231.method_70899();
            case 1 -> net.minecraft.class_11231.method_70900(textures.method_4619(ids.get(0)).method_71659());
            case 2 -> net.minecraft.class_11231.method_70901(textures.method_4619(ids.get(0)).method_71659(),
                textures.method_4619(ids.get(1)).method_71659());
            default -> new net.minecraft.class_11231(textures.method_4619(ids.get(0)).method_71659(),
                textures.method_4619(ids.get(1)).method_71659(), textures.method_4619(ids.get(2)).method_71659());
        };
    }
}

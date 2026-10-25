package cb.clienttest;

/** What the model and item-render mixins saw since the game started (outside the mixin package). */
public final class ModelProbe {
    /** BlockModel.bake(ModelBaker, BlockModel, Function, ModelState, ResourceLocation, boolean) returns */
    public static int bakes;
    /** getMaterial answers the mixin replaced for minecraft:block/stone(_mirrored) */
    public static int stoneMaterials;
    /** ItemRenderer.render(ItemStack, ItemDisplayContext, boolean, PoseStack, MultiBufferSource, int, int, BakedModel) heads */
    public static int itemRenders;

    static final String[] STONE_MODELS = {"minecraft:block/stone", "minecraft:block/stone_mirrored"};

    private ModelProbe() {
    }

    /** the model's name (1.20.1: minecraft:block/stone; a file-style name is accepted too) */
    public static boolean isStone(String modelName) {
        if (modelName == null) {
            return false;
        }
        String n = modelName.replace("models/", "").replace(".json", "");
        for (String s : STONE_MODELS) {
            if (s.equals(n)) {
                return true;
            }
        }
        return false;
    }
}

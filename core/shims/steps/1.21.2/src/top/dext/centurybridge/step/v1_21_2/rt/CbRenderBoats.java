package top.dext.centurybridge.step.v1_21_2.rt;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.facade.BoatType;

/**
 * 1.21.1's boat models over 1.21.2's.
 *
 * <p>1.21.1: BoatModel / RaftModel were ListModels drawing a parts list (createPartsBuilder(root)), the chest variants
 * ChestBoatModel / ChestRaftModel subclasses adding the chest parts; the boat model also held its water patch (the
 * "water_patch" part of the boat mesh). 1.21.2: one BoatModel / RaftModel class drawing its whole root, the chest
 * variants baked from the chest layers (createChestBoatModel / createChestRaftModel), the water patch a layer of its own
 * (ModelLayers.BOAT_WATER_PATCH, the same box and pose as 1.21.1's part: compared in both jars). ChestBoatModel /
 * ChestRaftModel are facades over the 1.21.2 classes (classRenames), and the model layer names are 1.21.1's (boat/,
 * chest_boat/, raft/, chest_raft/ + the type's name, layer "main": 1.21.2 names its boat layers the same way).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderBoats {

    private CbRenderBoats() {
    }

    /** the chest parts of a chest boat / raft root (1.21.1's ChestBoatModel / ChestRaftModel createPartsBuilder), none for another */
    public static void chestParts(ImmutableList.Builder<net.minecraft.class_630> builder, net.minecraft.class_630 root) {
        if (root.method_41919("chest_bottom")) {
            builder.add(root.method_32086("chest_bottom"), root.method_32086("chest_lid"), root.method_32086("chest_lock"));
        }
    }

    /** 1.21.1 BoatModel.createPartsBuilder(root): the boat parts and paddles, and the chest parts of a chest boat root */
    public static ImmutableList.Builder<net.minecraft.class_630> boatParts(net.minecraft.class_630 root) {
        ImmutableList.Builder<net.minecraft.class_630> builder = new ImmutableList.Builder<>();
        builder.add(root.method_32086("bottom"), root.method_32086("back"), root.method_32086("front"), root.method_32086("right"),
            root.method_32086("left"), root.method_32086("left_paddle"), root.method_32086("right_paddle"));
        chestParts(builder, root);
        return builder;
    }

    /** 1.21.1 RaftModel.createPartsBuilder(root): the raft and paddles, and the chest parts of a chest raft root */
    public static ImmutableList.Builder<net.minecraft.class_630> raftParts(net.minecraft.class_630 root) {
        ImmutableList.Builder<net.minecraft.class_630> builder = new ImmutableList.Builder<>();
        builder.add(root.method_32086("bottom"), root.method_32086("left_paddle"), root.method_32086("right_paddle"));
        chestParts(builder, root);
        return builder;
    }

    /** a boat model's 1.21.1 water patch part: 1.21.2's water patch layer's part */
    public static net.minecraft.class_630 waterPatch() {
        return net.minecraft.class_310.method_1551().method_31974().method_32072(net.minecraft.class_5602.field_53021).method_32086("water_patch");
    }

    private static net.minecraft.class_5601 layer(String path) {
        return new net.minecraft.class_5601(net.minecraft.class_2960.method_60656(path), "main");
    }

    /** ModelLayers.createRaftModelName(type) */
    public static net.minecraft.class_5601 method_45717(BoatType type) {
        return layer("raft/" + type.method_7559());
    }

    /** ModelLayers.createChestRaftModelName(type) */
    public static net.minecraft.class_5601 method_45718(BoatType type) {
        return layer("chest_raft/" + type.method_7559());
    }

    /** ModelLayers.createBoatModelName(type) */
    public static net.minecraft.class_5601 method_32077(BoatType type) {
        return layer("boat/" + type.method_7559());
    }

    /** ModelLayers.createChestBoatModelName(type) */
    public static net.minecraft.class_5601 method_42582(BoatType type) {
        return layer("chest_boat/" + type.method_7559());
    }

    // ---- 1.21.1 BoatRenderer(context, chest) ----

    /** a 1.21.1 boat renderer's model and render type for one variant */
    public record Variant(net.minecraft.class_583<net.minecraft.class_10004> model, net.minecraft.class_1921 renderType, boolean raft) {
    }

    /** BoatRenderer's 1.21.1 per-variant models (CbRenderBoatRendererMixin) */
    public interface Legacy {
        void cb$variants(java.util.Map<BoatType, Variant> variants);
    }

    /** the boat EntityRenderer.render is drawing on this thread (CbRenderBoatDrawMixin) */
    private static final ThreadLocal<net.minecraft.class_1297> DRAWING = new ThreadLocal<>();

    public static void drawing(net.minecraft.class_10004 state) {
        DRAWING.set(CbRenderStateEntity.entityOf(state));
    }

    public static void drawn() {
        DRAWING.remove();
    }

    /** the variant of the boat being drawn (1.21.1 Boat.getVariant()), or null */
    public static BoatType drawingVariant() {
        return DRAWING.get() instanceof net.minecraft.class_10255 boat ? CbEntityBoats.getVariant(boat) : null;
    }

    /** new BoatRenderer(context, chest): 1.21.2's constructor takes one layer -- the oak one, its models replaced per variant afterwards */
    public static net.minecraft.class_5601 legacyLayer(boolean chest) {
        return chest ? method_42582(BoatType.OAK) : method_32077(BoatType.OAK);
    }

    /**
     * after new BoatRenderer(context, chest): 1.21.1's renderer held a model and texture per variant -- bamboo a raft
     * (chest raft) model, the others a boat (chest boat) model, baked from that variant's layer, and the texture
     * textures/entity/(chest_)boat/&lt;variant&gt;.png -- and drew each boat with its variant's (CbRenderBoatRendererMixin)
     */
    @SuppressWarnings("unchecked")
    public static void legacyRenderer(net.minecraft.class_881 renderer, net.minecraft.class_5617.class_5618 context, boolean chest) {
        java.util.Map<BoatType, Variant> variants = new java.util.EnumMap<>(BoatType.class);
        for (BoatType type : BoatType.values()) {
            boolean raft = type == BoatType.BAMBOO;
            net.minecraft.class_5601 layer = raft ? (chest ? method_45718(type) : method_45717(type)) : (chest ? method_42582(type) : method_32077(type));
            net.minecraft.class_630 root = context.method_32167(layer);
            net.minecraft.class_583<net.minecraft.class_10004> model = raft ? new net.minecraft.class_7754(root) : new net.minecraft.class_554(root);
            net.minecraft.class_2960 texture = net.minecraft.class_2960.method_60656("textures/entity/" + (chest ? "chest_boat/" : "boat/") + type.method_7559() + ".png");
            variants.put(type, new Variant(model, model.method_23500(texture), raft));
        }
        ((Legacy) renderer).cb$variants(variants);
    }

    /**
     * BoatModel.createChildren(root): 1.21.2's (private, opened by the layer's access widener) adds the same parts 1.21.1's
     * did except the water patch, which 1.21.2 moved to a layer of its own; 1.21.1's added it to the boat mesh -- added
     * here with 1.21.1's box and pose (compared in both jars)
     */
    public static void method_45703(net.minecraft.class_5610 root) {
        net.minecraft.class_554.method_45703(root);
        root.method_32117("water_patch", net.minecraft.class_5606.method_32108().method_32101(0, 0).method_32097(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
            net.minecraft.class_5603.method_32091(0.0F, -3.0F, 1.0F, 1.5707964F, 0.0F, 0.0F));
    }
}

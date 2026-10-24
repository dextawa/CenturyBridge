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
}

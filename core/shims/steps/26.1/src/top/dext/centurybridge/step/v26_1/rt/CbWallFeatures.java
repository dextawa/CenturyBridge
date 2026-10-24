package top.dext.centurybridge.step.v26_1.rt;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import top.dext.centurybridge.step.v26_1.facade.BlockBlobFeature;
import top.dext.centurybridge.step.v26_1.facade.IceSpikeFeature;
import top.dext.centurybridge.step.v26_1.facade.RandomPatchConfiguration;
import top.dext.centurybridge.step.v26_1.facade.RandomPatchFeature;

/**
 * The features 26.1 removed: flower, no_bonemeal_flower and random_patch (RandomPatchFeature), ice_spike
 * (IceSpikeFeature), forest_rock (BlockBlobFeature of a BlockStateConfiguration). 1.21.11's Feature constants
 * are these layer's classes, registered under 1.21.11's ids when Feature registers 26.1's
 * (mixin/CbWallFeatureRegisterMixin), so a 1.21.11 mod's configured features -- made in code or read from its
 * worldgen data, "type": "minecraft:random_patch" -- resolve and place as they did. 1.21.11's bone meal
 * grew a biome's configured features of type FLOWER; 26.1's grows those tagged can_spawn_from_bone_meal, so
 * a biome's FLOWER features are added to 26.1's list (mixin/CbWallBoneMealFlowersMixin).
 */
public final class CbWallFeatures {

    // made while Feature registers 26.1's (Feature's initialization runs register(), see field_21219)
    private static RandomPatchFeature flower;
    private static RandomPatchFeature noBonemealFlower;
    private static RandomPatchFeature randomPatch;
    private static IceSpikeFeature iceSpike;
    private static BlockBlobFeature forestRock;

    private CbWallFeatures() {
    }

    /** at the end of Feature's registrations: 1.21.11's removed features under their 1.21.11 ids */
    public static void register() {
        flower = register("flower", new RandomPatchFeature(RandomPatchConfiguration.field_24902));
        noBonemealFlower = register("no_bonemeal_flower", new RandomPatchFeature(RandomPatchConfiguration.field_24902));
        randomPatch = register("random_patch", new RandomPatchFeature(RandomPatchConfiguration.field_24902));
        iceSpike = register("ice_spike", new IceSpikeFeature(NoneFeatureConfiguration.CODEC));
        forestRock = register("forest_rock", new BlockBlobFeature(BlockStateConfiguration.CODEC));
    }

    private static <F extends Feature<?>> F register(String id, F feature) {
        return Registry.register(BuiltInRegistries.FEATURE, Identifier.withDefaultNamespace(id), feature);
    }

    /** Feature initialized (which registers this layer's features) before one is handed out */
    private static void ready() {
        java.util.Objects.requireNonNull(Feature.NO_OP);
    }

    // ---- 1.21.11's Feature constants

    public static Feature<?> field_21219() {
        ready();
        return flower;
    }

    public static Feature<?> field_26361() {
        ready();
        return noBonemealFlower;
    }

    public static Feature<?> field_21220() {
        ready();
        return randomPatch;
    }

    public static Feature<?> field_13562() {
        ready();
        return iceSpike;
    }

    public static Feature<?> field_13584() {
        ready();
        return forestRock;
    }

    // ---- place(context) called through the 1.21.11 classes: a plain call dispatches, a super call is the body

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean place(RandomPatchFeature feature, FeaturePlaceContext context) {
        return feature.place(context);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean superPlace(RandomPatchFeature feature, FeaturePlaceContext context) {
        return feature.cb$place(context);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean place(IceSpikeFeature feature, FeaturePlaceContext context) {
        return feature.place(context);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean superPlace(IceSpikeFeature feature, FeaturePlaceContext context) {
        return feature.cb$place(context);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean place(BlockBlobFeature feature, FeaturePlaceContext context) {
        return feature.place(context);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean superPlace(BlockBlobFeature feature, FeaturePlaceContext context) {
        return feature.cb$place(context);
    }

    /**
     * 26.1's bone meal features of a biome and 1.21.11's: its configured features of type FLOWER (1.21.11's
     * BiomeGenerationSettings.getFlowerFeatures), those not listed already
     */
    public static List<ConfiguredFeature<?, ?>> withFlowers(List<ConfiguredFeature<?, ?>> boneMeal, List<HolderSet<PlacedFeature>> features) {
        List<ConfiguredFeature<?, ?>> flowers = features.stream().flatMap(HolderSet::stream).map(Holder::value)
            .flatMap(PlacedFeature::getFeatures).map(Holder::value)
            .filter(f -> f.feature() == flower && !boneMeal.contains(f)).toList();
        if (flowers.isEmpty()) {
            return boneMeal;
        }
        List<ConfiguredFeature<?, ?>> out = new java.util.ArrayList<>(boneMeal);
        out.addAll(flowers);
        return List.copyOf(out);
    }
}

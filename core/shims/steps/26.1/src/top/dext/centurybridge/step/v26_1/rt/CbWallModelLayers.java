package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.resources.Identifier;

/**
 * 1.21.11's baby model layers 26.1 dropped (its babies are models of their own, with no saddle or armour layer, and
 * the cold chicken and pig babies share the temperate ones): the locations 1.21.11's ModelLayers gave them, and the
 * definitions 1.21.11's LayerDefinitions.createRoots registered under them (1.21.11's meshes, CbWallModelMeshes),
 * added to 26.1's roots so a 1.21.11 mod's bakeLayer(..) of one finds it.
 */
@Environment(EnvType.CLIENT)
public final class CbWallModelLayers {

    private CbWallModelLayers() {
    }

    /** the locations, built when first read (1.21.11's ModelLayers.register(name[, layer])) */
    @Environment(EnvType.CLIENT)
    public static final class Locations {

        private Locations() {
        }

        static final ModelLayerLocation CAMEL_BABY_SADDLE = location("camel_baby", "saddle");
        static final ModelLayerLocation CAMEL_HUSK_BABY_SADDLE = location("camel_husk_baby", "saddle");
        static final ModelLayerLocation COLD_CHICKEN_BABY = location("cold_chicken_baby", "main");
        static final ModelLayerLocation COLD_PIG_BABY = location("cold_pig_baby", "main");
        static final ModelLayerLocation DONKEY_BABY_SADDLE = location("donkey_baby", "saddle");
        static final ModelLayerLocation HORSE_BABY_ARMOR = location("horse_armor_baby", "main");
        static final ModelLayerLocation HORSE_BABY_SADDLE = location("horse_baby", "saddle");
        static final ModelLayerLocation MULE_BABY_SADDLE = location("mule_baby", "saddle");
        static final ModelLayerLocation PIG_BABY_SADDLE = location("pig_baby", "saddle");
        static final ModelLayerLocation SHEEP_BABY_WOOL_UNDERCOAT = location("sheep_baby", "wool_undercoat");
        static final ModelLayerLocation SKELETON_HORSE_BABY_SADDLE = location("skeleton_horse_baby", "saddle");
        static final ModelLayerLocation STRIDER_BABY_SADDLE = location("strider_baby", "saddle");
        static final ModelLayerLocation UNDEAD_HORSE_BABY_ARMOR = location("undead_horse_baby_armor", "main");
        static final ModelLayerLocation WOLF_BABY_ARMOR = location("wolf_baby_armor", "main");
        static final ModelLayerLocation ZOMBIE_HORSE_BABY_SADDLE = location("zombie_horse_baby", "saddle");

        private static ModelLayerLocation location(String name, String layer) {
            return new ModelLayerLocation(Identifier.withDefaultNamespace(name), layer);
        }
    }

    // ---- 1.21.11 ModelLayers fields (fieldGetters)

    public static ModelLayerLocation camelBabySaddle() {
        return Locations.CAMEL_BABY_SADDLE;
    }

    public static ModelLayerLocation camelHuskBabySaddle() {
        return Locations.CAMEL_HUSK_BABY_SADDLE;
    }

    public static ModelLayerLocation coldChickenBaby() {
        return Locations.COLD_CHICKEN_BABY;
    }

    public static ModelLayerLocation coldPigBaby() {
        return Locations.COLD_PIG_BABY;
    }

    public static ModelLayerLocation donkeyBabySaddle() {
        return Locations.DONKEY_BABY_SADDLE;
    }

    public static ModelLayerLocation horseBabyArmor() {
        return Locations.HORSE_BABY_ARMOR;
    }

    public static ModelLayerLocation horseBabySaddle() {
        return Locations.HORSE_BABY_SADDLE;
    }

    public static ModelLayerLocation muleBabySaddle() {
        return Locations.MULE_BABY_SADDLE;
    }

    public static ModelLayerLocation pigBabySaddle() {
        return Locations.PIG_BABY_SADDLE;
    }

    public static ModelLayerLocation sheepBabyWoolUndercoat() {
        return Locations.SHEEP_BABY_WOOL_UNDERCOAT;
    }

    public static ModelLayerLocation skeletonHorseBabySaddle() {
        return Locations.SKELETON_HORSE_BABY_SADDLE;
    }

    public static ModelLayerLocation striderBabySaddle() {
        return Locations.STRIDER_BABY_SADDLE;
    }

    public static ModelLayerLocation undeadHorseBabyArmor() {
        return Locations.UNDEAD_HORSE_BABY_ARMOR;
    }

    public static ModelLayerLocation wolfBabyArmor() {
        return Locations.WOLF_BABY_ARMOR;
    }

    public static ModelLayerLocation zombieHorseBabySaddle() {
        return Locations.ZOMBIE_HORSE_BABY_SADDLE;
    }

    /**
     * 26.1's roots plus 1.21.11's definitions of these layers, as 1.21.11's createRoots built them (the shared
     * equine baby saddle, 1.21.11's local "horse baby saddle", built once and put under each of its locations)
     */
    public static Map<ModelLayerLocation, LayerDefinition> withLegacy(Map<ModelLayerLocation, LayerDefinition> roots) {
        LayerDefinition camelSaddle = CbWallModelMeshes.CamelSaddleModel.createSaddleLayer();
        LayerDefinition equineBabySaddle = CbWallModelMeshes.EquineSaddleModel.createSaddleLayer(true);
        MeshTransformer horseScale = MeshTransformer.scaling(1.1F);
        ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> b = ImmutableMap.builder();
        b.putAll(roots);
        b.put(Locations.CAMEL_BABY_SADDLE, camelSaddle.apply(CbWallModelMeshes.CamelModel.BABY_TRANSFORMER));
        b.put(Locations.CAMEL_HUSK_BABY_SADDLE, camelSaddle.apply(CbWallModelMeshes.CamelModel.BABY_TRANSFORMER));
        b.put(Locations.COLD_CHICKEN_BABY,
            CbWallModelMeshes.ColdChickenModel.createBodyLayer().apply(CbWallModelMeshes.ChickenModel.BABY_TRANSFORMER));
        b.put(Locations.COLD_PIG_BABY,
            CbWallModelMeshes.ColdPigModel.createBodyLayer(CubeDeformation.NONE).apply(CbWallModelMeshes.PigModel.BABY_TRANSFORMER));
        b.put(Locations.DONKEY_BABY_SADDLE, CbWallModelMeshes.DonkeyModel.createSaddleLayer(0.87F, true));
        b.put(Locations.HORSE_BABY_ARMOR,
            LayerDefinition.create(CbWallModelMeshes.AbstractEquineModel.createBabyMesh(new CubeDeformation(0.1F)), 64, 64)
                .apply(horseScale));
        b.put(Locations.HORSE_BABY_SADDLE, equineBabySaddle.apply(horseScale));
        b.put(Locations.MULE_BABY_SADDLE, CbWallModelMeshes.DonkeyModel.createSaddleLayer(0.92F, true));
        b.put(Locations.PIG_BABY_SADDLE, CbWallModelMeshes.PigModel.createBodyLayer(new CubeDeformation(0.5F))
            .apply(CbWallModelMeshes.PigModel.BABY_TRANSFORMER));
        b.put(Locations.SHEEP_BABY_WOOL_UNDERCOAT,
            CbWallModelMeshes.SheepModel.createBodyLayer().apply(CbWallModelMeshes.SheepModel.BABY_TRANSFORMER));
        b.put(Locations.SKELETON_HORSE_BABY_SADDLE, equineBabySaddle);
        b.put(Locations.STRIDER_BABY_SADDLE,
            CbWallModelMeshes.StriderModel.createBodyLayer().apply(CbWallModelMeshes.StriderModel.BABY_TRANSFORMER));
        b.put(Locations.UNDEAD_HORSE_BABY_ARMOR,
            LayerDefinition.create(CbWallModelMeshes.AbstractEquineModel.createBabyMesh(new CubeDeformation(0.1F)), 64, 64));
        b.put(Locations.WOLF_BABY_ARMOR,
            LayerDefinition.create(CbWallModelMeshes.WolfModel.createMeshDefinition(new CubeDeformation(0.2F)), 64, 32)
                .apply(CbWallModelMeshes.WolfModel.BABY_TRANSFORMER));
        b.put(Locations.ZOMBIE_HORSE_BABY_SADDLE, equineBabySaddle);
        return b.buildOrThrow();
    }
}

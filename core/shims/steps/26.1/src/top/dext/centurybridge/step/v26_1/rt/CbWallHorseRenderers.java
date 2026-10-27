package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.DonkeyRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;

/**
 * 1.21.11's DonkeyRenderer(context, type) and UndeadHorseRenderer(context, type) took one Type naming everything the
 * renderer baked: texture, adult and baby model layers, saddle layer type and adult / baby saddle model layers
 * (javap class_9991 / class_961 and their Type enums: DONKEY, MULE / SKELETON, ZOMBIE). 26.1's take (context, saddle
 * layer type, saddle model layer, adult Type, baby Type), with a baby Type of its own per animal (26.1 draws babies
 * with their own models and textures). A 1.21.11 Type maps to the arguments 26.1's EntityRenderers passes for that
 * animal (javap of its donkey / mule / skeleton horse / zombie horse providers), so a 1.21.11 renderer draws the
 * animal as 26.1's own renderer for it does.
 */
@Environment(EnvType.CLIENT)
public final class CbWallHorseRenderers {
    private CbWallHorseRenderers() {
    }

    // ---- DonkeyRenderer ----

    /** new DonkeyRenderer(context, type) */
    public static DonkeyRenderer<?> donkey(EntityRendererProvider.Context context, DonkeyRenderer.Type type) {
        return new DonkeyRenderer<>(context, donkeySaddleLayer(type), donkeySaddleModel(type), donkeyAdult(type), donkeyBaby(type));
    }

    public static EquipmentClientInfo.LayerType donkeySaddleLayer(DonkeyRenderer.Type type) {
        return donkeyAdult(type) == DonkeyRenderer.Type.MULE ? EquipmentClientInfo.LayerType.MULE_SADDLE : EquipmentClientInfo.LayerType.DONKEY_SADDLE;
    }

    public static ModelLayerLocation donkeySaddleModel(DonkeyRenderer.Type type) {
        return donkeyAdult(type) == DonkeyRenderer.Type.MULE ? ModelLayers.MULE_SADDLE : ModelLayers.DONKEY_SADDLE;
    }

    public static DonkeyRenderer.Type donkeyAdult(DonkeyRenderer.Type type) {
        return switch (type) {
            case DONKEY, DONKEY_BABY -> DonkeyRenderer.Type.DONKEY;
            case MULE, MULE_BABY -> DonkeyRenderer.Type.MULE;
        };
    }

    public static DonkeyRenderer.Type donkeyBaby(DonkeyRenderer.Type type) {
        return donkeyAdult(type) == DonkeyRenderer.Type.MULE ? DonkeyRenderer.Type.MULE_BABY : DonkeyRenderer.Type.DONKEY_BABY;
    }

    // ---- UndeadHorseRenderer ----

    /** new UndeadHorseRenderer(context, type) */
    public static UndeadHorseRenderer undeadHorse(EntityRendererProvider.Context context, UndeadHorseRenderer.Type type) {
        return new UndeadHorseRenderer(context, undeadSaddleLayer(type), undeadSaddleModel(type), undeadAdult(type), undeadBaby(type));
    }

    public static EquipmentClientInfo.LayerType undeadSaddleLayer(UndeadHorseRenderer.Type type) {
        return undeadAdult(type) == UndeadHorseRenderer.Type.ZOMBIE
            ? EquipmentClientInfo.LayerType.ZOMBIE_HORSE_SADDLE : EquipmentClientInfo.LayerType.SKELETON_HORSE_SADDLE;
    }

    public static ModelLayerLocation undeadSaddleModel(UndeadHorseRenderer.Type type) {
        return undeadAdult(type) == UndeadHorseRenderer.Type.ZOMBIE ? ModelLayers.ZOMBIE_HORSE_SADDLE : ModelLayers.SKELETON_HORSE_SADDLE;
    }

    public static UndeadHorseRenderer.Type undeadAdult(UndeadHorseRenderer.Type type) {
        return switch (type) {
            case SKELETON, SKELETON_BABY -> UndeadHorseRenderer.Type.SKELETON;
            case ZOMBIE, ZOMBIE_BABY -> UndeadHorseRenderer.Type.ZOMBIE;
        };
    }

    public static UndeadHorseRenderer.Type undeadBaby(UndeadHorseRenderer.Type type) {
        return undeadAdult(type) == UndeadHorseRenderer.Type.ZOMBIE ? UndeadHorseRenderer.Type.ZOMBIE_BABY : UndeadHorseRenderer.Type.SKELETON_BABY;
    }
}

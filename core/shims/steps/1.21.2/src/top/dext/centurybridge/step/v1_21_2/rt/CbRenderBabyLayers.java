package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Baby model layers for 1.21.1 renderer constructors that 1.21.2 gave a baby layer each.
 *
 * <p>1.21.1 drew a baby with the adult model, scaled by the model itself (AgeableListModel's young head/body scales,
 * HumanoidModel: scaleHead, babyYHeadOffset 16, babyZHeadOffset 0, babyHeadScale 2, babyBodyScale 2, bodyYOffset 24).
 * 1.21.2 draws it with a separate model baked from a baby layer, which LayerDefinitions makes from the adult layer with
 * the model family's BABY_TRANSFORMER -- HumanoidModel's is a BabyModelTransform with exactly those numbers. A 1.21.1
 * mod hands only the adult layer; the baby layer handed on is that layer under the same transformer 1.21.2's
 * LayerDefinitions applies for the renderer's own baby layer (ZOMBIE_BABY / ZOMBIE_BABY_INNER_ARMOR / ... :
 * HumanoidModel.BABY_TRANSFORMER, LLAMA_BABY: LlamaModel.BABY_TRANSFORMER; read off 1.21.2's LayerDefinitions).
 *
 * <p>The derived location is a ModelLayerLocation of its own (the adult's id, its layer name with a suffix naming the
 * transformer); EntityModelSet.bakeLayer bakes it from the adult's definition, transformed (CbRenderBabyLayersMixin).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderBabyLayers {

    /** an adult layer and the transformer that makes the baby layer from it */
    public record Derived(net.minecraft.class_5601 adult, net.minecraft.class_9953 transformer) {
    }

    private static final Map<net.minecraft.class_5601, Derived> DERIVED = new ConcurrentHashMap<>();

    private CbRenderBabyLayers() {
    }

    private static net.minecraft.class_5601 derive(net.minecraft.class_5601 adult, String family, net.minecraft.class_9953 transformer) {
        net.minecraft.class_5601 baby = new net.minecraft.class_5601(adult.comp_2995(), adult.comp_2996() + "/centurybridge_baby_" + family);
        DERIVED.putIfAbsent(baby, new Derived(adult, transformer));
        return baby;
    }

    /** the derivation of a location this class made, or null */
    public static Derived derived(net.minecraft.class_5601 location) {
        return DERIVED.get(location);
    }

    /** a humanoid adult layer's baby layer (HumanoidModel.BABY_TRANSFORMER) */
    public static net.minecraft.class_5601 humanoidBaby(net.minecraft.class_5601 adult) {
        return derive(adult, "humanoid", net.minecraft.class_572.field_52918);
    }

    /** a llama adult layer's baby layer (LlamaModel.BABY_TRANSFORMER) */
    public static net.minecraft.class_5601 llamaBaby(net.minecraft.class_5601 adult) {
        return derive(adult, "llama", net.minecraft.class_578.field_52920);
    }

    /** a camel adult layer's baby layer (CamelModel.BABY_TRANSFORMER) */
    public static net.minecraft.class_5601 camelBaby(net.minecraft.class_5601 adult) {
        return derive(adult, "camel", net.minecraft.class_7751.field_52883);
    }

    /**
     * an equine adult layer's baby layer (AbstractEquineModel.BABY_TRANSFORMER): 1.21.1 drew baby horses with the adult
     * mesh under AgeableListModel's young scaling, which that transformer applies to the mesh
     */
    public static net.minecraft.class_5601 equineBaby(net.minecraft.class_5601 adult) {
        return derive(adult, "equine", net.minecraft.class_549.field_52854);
    }
}

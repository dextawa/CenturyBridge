package top.dext.centurybridge.step.v1_21_4.rt;

import com.google.common.collect.MapMaker;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_10191;
import net.minecraft.class_10394;
import net.minecraft.class_1767;
import net.minecraft.class_1792;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_8054;

/**
 * 1.21.3's equipment model ids on 1.21.4's equipment assets. 1.21.3 named an armor's (or carpet's, elytra's)
 * model by a ResourceLocation (EquipmentModels.IRON = minecraft:iron); 1.21.4 by a ResourceKey in the
 * equipment_asset registry with that same location (EquipmentAssets.IRON). {@link #asset} and {@link #id}
 * convert between them, adaptively (a value already of the wanted kind passes through), so they undo each
 * other.
 * <p>
 * TrimMaterial lost itemModelIndex (the trim item model predicate value; 1.21.4 picks trim item models by
 * the material's asset name): the value a 1.21.3 constructor or create(..) gave is kept for the material
 * object and answered by itemModelIndex(); a material 1.21.4 built answers 0, the value of 1.21.3's
 * predicate for an untrimmed item.
 */
public final class CbRestEquipment {

    private CbRestEquipment() {
    }

    // ---- ids <-> asset keys ----

    @SuppressWarnings("unchecked")
    public static class_5321<class_10394> asset(class_2960 id) {
        return id == null ? null : class_5321.method_29179((class_5321<? extends net.minecraft.class_2378<class_10394>>) class_10191.field_55214, id);
    }

    public static class_2960 id(class_5321<class_10394> key) {
        return key == null ? null : key.method_29177();
    }

    /** a map keyed by 1.21.3 model ids as 1.21.4's asset keys (adaptive) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map assetKeys(Map map) {
        if (map == null) {
            return null;
        }
        Map out = new HashMap();
        map.forEach((k, v) -> out.put(k instanceof class_2960 id ? asset(id) : k, v));
        return Map.copyOf(out);
    }

    /** a map keyed by 1.21.4's asset keys as 1.21.3 model ids (adaptive) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map ids(Map map) {
        if (map == null) {
            return null;
        }
        Map out = new HashMap();
        map.forEach((k, v) -> out.put(k instanceof class_5321<?> key ? ((class_5321<?>) key).method_29177() : k, v));
        return Map.copyOf(out);
    }

    /** an Optional model id as an Optional asset key (adaptive) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static java.util.Optional optionalAsset(java.util.Optional o) {
        return o == null ? null : o.map(v -> v instanceof class_2960 id ? asset(id) : v);
    }

    /** an Optional asset key as an Optional model id (adaptive) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static java.util.Optional optionalId(java.util.Optional o) {
        return o == null ? null : o.map(v -> v instanceof class_5321<?> k ? ((class_5321<?>) k).method_29177() : v);
    }

    // ---- ArmorMaterial / Equippable.Builder ----

    /** ArmorMaterial.modelId(): the asset key's location */
    public static class_2960 modelId(class_5321<class_10394> key) {
        return id(key);
    }

    // ---- TrimMaterial ----

    private static final Map<class_8054, Float> ITEM_MODEL_INDEX = new MapMaker().weakKeys().makeMap();

    /** 1.21.3 new TrimMaterial(assetName, ingredient, itemModelIndex, overrideArmorMaterials, description) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_8054 trimMaterial(String assetName, class_6880<class_1792> ingredient, float itemModelIndex, Map overrides,
            class_2561 description) {
        class_8054 material = new class_8054(assetName, ingredient, assetKeys(overrides), description);
        ITEM_MODEL_INDEX.put(material, itemModelIndex);
        return material;
    }

    /** 1.21.3 TrimMaterial.create(assetName, ingredient, itemModelIndex, description, overrideArmorMaterials) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_8054 method_48438(String assetName, class_1792 ingredient, float itemModelIndex, class_2561 description, Map overrides) {
        class_8054 material = class_8054.method_48438(assetName, ingredient, description, assetKeys(overrides));
        ITEM_MODEL_INDEX.put(material, itemModelIndex);
        return material;
    }

    /** 1.21.3 TrimMaterial.itemModelIndex() */
    public static float comp_1210(class_8054 material) {
        Float f = ITEM_MODEL_INDEX.get(material);
        return f == null ? 0.0F : f;
    }

    // ---- EquipmentModels fields ----

    /** EquipmentModels.CARPETS of 1.21.3: dye color -> model id */
    public static Map<class_1767, class_2960> carpets(Map<class_1767, class_5321<class_10394>> keys) {
        Map<class_1767, class_2960> out = new EnumMap<>(class_1767.class);
        keys.forEach((c, k) -> out.put(c, k.method_29177()));
        return Map.copyOf(out);
    }
}

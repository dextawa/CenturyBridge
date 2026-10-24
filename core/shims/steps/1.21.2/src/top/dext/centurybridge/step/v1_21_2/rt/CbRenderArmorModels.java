package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import top.dext.centurybridge.step.v1_21_2.facade.ArmorMaterial;

/**
 * Equipment models for 1.21.1 armour materials (the armour equipment-model synthesis).
 *
 * <p>1.21.1 drew humanoid armour from the material's layers: for each layer the texture
 * textures/models/armor/&lt;asset&gt;_layer_&lt;1|2&gt;&lt;suffix&gt;.png of the asset's namespace (1 for the outer
 * model, 2 for the leggings), tinted when the layer was dyeable with stack.is(#dyeable) ? opaque(DyedItemColor or the
 * default leather colour -6265536) : white, white when not (class_970.method_4169); the glint drawn once over the
 * piece. 1.21.2 draws it from an equipment model json (models/equipment/&lt;id&gt;.json, EquipmentAssetManager) named by
 * the item's Equippable component; a 1.21.1 material becomes a 1.21.2 one whose model id is the first layer's asset
 * (CbItemArmor, s7item's), and a 1.21.1 mod ships no such json.
 *
 * <p>Here: when the asset manager has no model for an id, and a 1.21.1 material of the layer's ARMOR_MATERIAL registry
 * has that model id (asked of CbItemArmor.equipmentModelId, the one rule that names the 1.21.2 material's model; 1.21.1's vanilla
 * materials map to 1.21.2's own models and are skipped), a model is synthesized from its layers: one humanoid and one
 * humanoid_leggings layer per 1.21.1 layer, in order. Each synthesized layer draws 1.21.1's texture
 * (CbRenderEquipmentLayerMixin: Layer.getTextureLocation answers layer.texture(leggings)) with 1.21.1's tint
 * (CbRenderEquipmentColorMixin). 1.21.2 draws the glint with the first layer, 1.21.1 once after all: the same single
 * glint pass over the piece. The cache is dropped on every resource reload.
 *
 * <p>Common code (EquipmentModel and its Layer are common classes, CbRenderEquipmentLayerMixin applies on both sides);
 * only the client asks for a texture or a tint.
 */
public final class CbRenderArmorModels {

    /** the 1.21.1 layer and model part a synthesized 1.21.2 layer stands for */
    private record Legacy(ArmorMaterial.Layer layer, boolean leggings) {
    }

    private static final Map<net.minecraft.class_2960, Optional<net.minecraft.class_10186>> MODELS = new HashMap<>();
    private static final Map<net.minecraft.class_10186.class_10189, Legacy> LAYERS = Collections.synchronizedMap(new IdentityHashMap<>());

    /** the stack whose layers EquipmentLayerRenderer is drawing (render thread) */
    private static net.minecraft.class_1799 drawing;

    private CbRenderArmorModels() {
    }

    public static synchronized void reload() {
        MODELS.clear();
        LAYERS.clear();
    }

    /** the synthesized model for an id the asset manager has none for, or null */
    public static synchronized net.minecraft.class_10186 model(net.minecraft.class_2960 id) {
        return MODELS.computeIfAbsent(id, CbRenderArmorModels::synthesize).orElse(null);
    }

    private static Optional<net.minecraft.class_10186> synthesize(net.minecraft.class_2960 id) {
        net.minecraft.class_2378<ArmorMaterial> registry = CbItemArmor.field_48976();
        for (Map.Entry<net.minecraft.class_5321<ArmorMaterial>, ArmorMaterial> e : registry.method_29722()) {
            if (e.getKey().method_29177().method_12836().equals("minecraft")) {
                continue; // 1.21.1's vanilla materials: 1.21.2's own models
            }
            ArmorMaterial m = e.getValue();
            // s7item's id for the material (CbItemArmor.equipmentModelId: the model id its 1.21.2 material and so its EQUIPPABLE carry)
            net.minecraft.class_2960 model = CbItemArmor.equipmentModelId(registry.method_47983(m));
            if (!model.equals(id)) {
                continue;
            }
            List<net.minecraft.class_10186.class_10189> humanoid = new ArrayList<>();
            List<net.minecraft.class_10186.class_10189> leggings = new ArrayList<>();
            for (ArmorMaterial.Layer layer : m.comp_2302()) {
                humanoid.add(layer(layer, false));
                leggings.add(layer(layer, true));
            }
            Map<net.minecraft.class_10186.class_10190, List<net.minecraft.class_10186.class_10189>> layers = new EnumMap<>(net.minecraft.class_10186.class_10190.class);
            layers.put(net.minecraft.class_10186.class_10190.field_54125, List.copyOf(humanoid));
            layers.put(net.minecraft.class_10186.class_10190.field_54126, List.copyOf(leggings));
            return Optional.of(new net.minecraft.class_10186(layers));
        }
        return Optional.empty();
    }

    private static net.minecraft.class_10186.class_10189 layer(ArmorMaterial.Layer layer, boolean leggings) {
        // dyeable: 1.21.2 skips a layer whose colour is 0, so the tint is answered by CbRenderEquipmentColorMixin
        Optional<net.minecraft.class_10186.class_10188> dyeable = layer.method_56692()
            ? Optional.of(new net.minecraft.class_10186.class_10188(Optional.empty())) : Optional.empty();
        net.minecraft.class_10186.class_10189 w = new net.minecraft.class_10186.class_10189(layer.cb$assetName(), dyeable, false);
        LAYERS.put(w, new Legacy(layer, leggings));
        return w;
    }

    /** the 1.21.1 texture of a synthesized layer, or null for any other */
    public static net.minecraft.class_2960 texture(net.minecraft.class_10186.class_10189 layer) {
        Legacy l = LAYERS.get(layer);
        return l == null ? null : l.layer().method_56693(l.leggings());
    }

    public static void drawing(net.minecraft.class_1799 stack) {
        drawing = stack;
    }

    /** the 1.21.1 tint of a synthesized layer for the stack being drawn, or null for any other layer */
    public static Integer color(net.minecraft.class_10186.class_10189 layer) {
        Legacy l = LAYERS.get(layer);
        if (l == null) {
            return null;
        }
        net.minecraft.class_1799 stack = drawing;
        if (!l.layer().method_56692() || stack == null || !stack.method_31573(net.minecraft.class_3489.field_48803)) {
            return -1;
        }
        return net.minecraft.class_9848.method_61334(net.minecraft.class_9282.method_57470(stack, -6265536));
    }
}

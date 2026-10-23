package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Lifecycle;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.class_1304;
import net.minecraft.class_1738;
import net.minecraft.class_1740;
import net.minecraft.class_1741;
import net.minecraft.class_1792;
import net.minecraft.class_1856;
import net.minecraft.class_2357;
import net.minecraft.class_2370;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_7876;
import net.minecraft.class_7923;
import net.minecraft.class_8051;
import net.minecraft.class_9890;
import net.minecraft.class_10205;
import top.dext.centurybridge.step.v1_21_2.facade.ArmorMaterial;
import top.dext.centurybridge.step.v1_21_2.facade.ArmorMaterials;

/**
 * 1.21.1's ArmorItem and ArmorMaterial on 1.21.2.
 *
 * <p>1.21.1 built an ArmorItem from a registered ArmorMaterial holder (facade ArmorMaterial: defense per type,
 * enchantment value, equip sound, repair ingredient, texture layers, toughness, knockback resistance) and
 * answered its questions from it (getDefense, getToughness, getEquipmentSlot, getEquipSound, the enchantment
 * value, the repair check, the attribute modifiers). The item's durability was whatever its Properties said.
 * 1.21.2 builds the item from a different ArmorMaterial record (durability, a repair tag, an equipment model
 * id) by putting the answers into its components (ArmorMaterial.humanoidProperties: MAX_DAMAGE, ATTRIBUTE_MODIFIERS,
 * ENCHANTABLE, EQUIPPABLE, REPAIRABLE). Here:
 * <ul>
 * <li>{@link #toW}: a 1.21.1 material as a 1.21.2 one, once per material; such a material is <em>legacy</em> and
 *     humanoidProperties (CbItemArmorMaterialMixin) leaves the Properties' durability alone, puts ENCHANTABLE only
 *     for a positive value (1.21.1's 0 was "not at the table"; 1.21.2's Enchantable refuses it) and makes
 *     REPAIRABLE the items of 1.21.1's ingredient, read when first asked as 1.21.1 read its supplier. Attribute
 *     modifiers: 1.21.2's body is 1.21.1's (armor, toughness, knockback resistance when positive, id
 *     armor.&lt;type&gt;). The equipment model id is the matching vanilla material's, else the first layer's asset
 *     (1.21.1's textures were named after it), else the registry id.</li>
 * <li>{@link #fromW}: the 1.21.1 holder behind a 1.21.2 material: the one it was made from, the 1.21.1 vanilla
 *     material for a 1.21.2 vanilla one, else one derived from it (layer = model id, ingredient = its tag).</li>
 * <li>1.21.1's ArmorItem members, on any item the layer knows the material and type of (ArmorFields: 1.21.2's
 *     ArmorItem, CbItemArmorMixin), each a call that reaches a 1.21.1 class's own override (the Legacy*
 *     interfaces, adapterHooks) and a super call that is 1.21.1's body.</li>
 * <li>1.21.1's ARMOR_MATERIAL registry: the layer's own, with 1.21.1's vanilla materials first (facade
 *     ArmorMaterials). It is not in the root registry (1.21.2's root has no such entry and is frozen).</li>
 * </ul>
 */
public final class CbItemArmor {

    /** Registries.ARMOR_MATERIAL */
    public static final class_5321<class_2378<ArmorMaterial>> KEY = class_5321.method_29180(class_2960.method_60656("armor_material"));

    private static final class_2370<ArmorMaterial> REGISTRY = new class_2370<>(KEY, Lifecycle.stable());

    /** 1.21.1 material -> its 1.21.2 counterpart (legacy) */
    private static final Map<ArmorMaterial, class_1741> TO_W = new IdentityHashMap<>();
    /** legacy 1.21.2 material -> the 1.21.1 holder it was made from */
    private static final Map<class_1741, class_6880<ArmorMaterial>> LEGACY = new IdentityHashMap<>();
    /** a 1.21.2 material no 1.21.1 one was made from -> the 1.21.1 holder derived from it */
    private static final Map<class_1741, class_6880<ArmorMaterial>> DERIVED = new IdentityHashMap<>();

    // ---- the registry ----

    /** the registry itself, for facade ArmorMaterials' registration */
    public static class_2378<ArmorMaterial> registry() {
        return REGISTRY;
    }

    /** BuiltInRegistries.ARMOR_MATERIAL: 1.21.1's vanilla materials registered first (class init of ArmorMaterials) */
    public static class_2378<ArmorMaterial> field_48976() {
        ArmorMaterials.method_56697(REGISTRY);
        return REGISTRY;
    }

    /** Registries.ARMOR_MATERIAL */
    public static class_5321<class_2378<ArmorMaterial>> field_48977() {
        return KEY;
    }

    // ---- 1.21.1 material <-> 1.21.2 material ----

    /** 1.21.1's vanilla material holders and 1.21.2's materials, the same field names */
    private static Map<class_1741, class_6880<ArmorMaterial>> vanillaCache;

    private static Map<class_1741, class_6880<ArmorMaterial>> vanillaMap() {
        if (vanillaCache == null) {
            Map<class_1741, class_6880<ArmorMaterial>> m = new IdentityHashMap<>();
            m.put(class_1740.field_7897, ArmorMaterials.field_7897);
            m.put(class_1740.field_7887, ArmorMaterials.field_7887);
            m.put(class_1740.field_7892, ArmorMaterials.field_7892);
            m.put(class_1740.field_7895, ArmorMaterials.field_7895);
            m.put(class_1740.field_7889, ArmorMaterials.field_7889);
            m.put(class_1740.field_7890, ArmorMaterials.field_7890);
            m.put(class_1740.field_21977, ArmorMaterials.field_21977);
            m.put(class_1740.field_48846, ArmorMaterials.field_48846);
            vanillaCache = m;
        }
        return vanillaCache;
    }

    /** the 1.21.2 vanilla material a 1.21.1 vanilla one became, null for any other */
    private static class_1741 vanillaW(ArmorMaterial m) {
        for (Map.Entry<class_1741, class_6880<ArmorMaterial>> e : vanillaMap().entrySet()) {
            if (e.getValue().comp_349() == m) {
                return e.getKey();
            }
        }
        return null;
    }

    /** the 1.21.2 material an ArmorItem is built from for a 1.21.1 material holder (one per material) */
    public static synchronized class_1741 toW(class_6880<ArmorMaterial> holder) {
        ArmorMaterial m = holder.comp_349();
        class_1741 w = TO_W.get(m);
        if (w == null) {
            class_1741 same = vanillaW(m);
            class_2960 model = same != null ? same.comp_3168() : modelId(holder, m);
            // durability and the repair tag are unused for a legacy material (CbItemArmorMaterialMixin)
            w = new class_1741(0, m.comp_2298(), m.comp_2299(), m.comp_2300(), m.comp_2303(), m.comp_2304(), null, model);
            TO_W.put(m, w);
            LEGACY.put(w, holder);
        }
        return w;
    }

    private static class_2960 modelId(class_6880<ArmorMaterial> holder, ArmorMaterial m) {
        if (!m.comp_2302().isEmpty()) {
            return m.comp_2302().get(0).cb$assetName();
        }
        Optional<class_5321<ArmorMaterial>> key = holder.method_40230();
        if (key.isPresent()) {
            return key.get().method_29177();
        }
        // a direct material without layers: 1.21.1 drew nothing for it, an id with no equipment model draws nothing
        return class_2960.method_60655("centurybridge", "no_armor_layers");
    }

    /** whether a 1.21.2 material was made from a 1.21.1 one */
    public static synchronized boolean legacy(class_1741 w) {
        return LEGACY.containsKey(w);
    }

    /** the 1.21.1 holder behind a 1.21.2 material */
    public static synchronized class_6880<ArmorMaterial> fromW(class_1741 w) {
        class_6880<ArmorMaterial> h = LEGACY.get(w);
        if (h != null) {
            return h;
        }
        h = DERIVED.get(w);
        if (h != null) {
            return h;
        }
        h = vanillaMap().get(w);
        if (h != null) {
            return h;
        }
        EnumMap<class_8051, Integer> defense = new EnumMap<>(class_8051.class);
        defense.putAll(w.comp_2298());
        class_6862<class_1792> tag = w.comp_2301();
        Supplier<class_1856> repair = () -> class_1856.method_8106(class_7923.field_41178.method_46735(tag));
        h = class_6880.method_40223(new ArmorMaterial(defense, w.comp_3167(), w.comp_2300(), repair,
            List.of(new ArmorMaterial.Layer(w.comp_3168())), w.comp_2303(), w.comp_2304()));
        DERIVED.put(w, h);
        return h;
    }

    /** REPAIRABLE for a legacy material: the items of 1.21.1's repair ingredient */
    public static class_9890 repairable(class_1741 w) {
        class_6880<ArmorMaterial> h;
        synchronized (CbItemArmor.class) {
            h = LEGACY.get(w);
        }
        return new class_9890(new IngredientItems(() -> h.comp_349().comp_2301().get()));
    }

    /**
     * The items of a 1.21.1 repair ingredient as a holder set, the supplier read on first use: 1.21.1's ArmorItem
     * asked material.repairIngredient().get() when the anvil asked, after every item was registered.
     */
    static final class IngredientItems implements class_6885<class_1792> {
        private final Supplier<class_1856> ingredient;
        private volatile class_6885<class_1792> items;

        IngredientItems(Supplier<class_1856> ingredient) {
            this.ingredient = ingredient;
        }

        private class_6885<class_1792> items() {
            class_6885<class_1792> s = items;
            if (s == null) {
                s = class_6885.method_40242(ingredient.get().method_8105());
                items = s;
            }
            return s;
        }

        @Override
        public Stream<class_6880<class_1792>> method_40239() {
            return items().method_40239();
        }

        @Override
        public int method_40247() {
            return items().method_40247();
        }

        @Override
        public boolean method_62680() {
            return items().method_62680();
        }

        @Override
        public Either<class_6862<class_1792>, List<class_6880<class_1792>>> method_40248() {
            return items().method_40248();
        }

        @Override
        public Optional<class_6880<class_1792>> method_40243(class_5819 random) {
            return items().method_40243(random);
        }

        @Override
        public class_6880<class_1792> method_40240(int i) {
            return items().method_40240(i);
        }

        @Override
        public boolean method_40241(class_6880<class_1792> holder) {
            return items().method_40241(holder);
        }

        @Override
        public boolean method_46768(class_7876<class_1792> owner) {
            return items().method_46768(owner);
        }

        @Override
        public Optional<class_6862<class_1792>> method_45925() {
            return items().method_45925();
        }

        @Override
        public Iterator<class_6880<class_1792>> iterator() {
            return items().iterator();
        }
    }

    // ---- ArmorItem ----

    /** an item whose 1.21.2 armor material and type the layer knows (CbItemArmorMixin on 1.21.2's ArmorItem) */
    public interface ArmorFields {
        class_1741 cb$armorMaterial();

        class_8051 cb$armorType();
    }

    /** 1.21.2's ArmorItem carries its material and type (CbItemArmorMixin) */
    private static ArmorFields fields(class_1738 item) {
        return (ArmorFields) (Object) item;
    }

    /** new ArmorItem(material, type, properties): 1.21.2's ArmorItem built from the 1.21.1 material */
    public static class_1738 newArmorItem(class_6880<ArmorMaterial> material, class_8051 type, class_1792.class_1793 props) {
        return new class_1738(toW(material), type, props);
    }

    // the members a 1.21.1 ArmorItem subclass overrides (adapterHooks)

    public interface LegacyType {
        class_8051 method_48398();
    }

    public interface LegacyMaterial {
        class_6880<ArmorMaterial> method_7686();
    }

    public interface LegacyDefense {
        int method_7687();
    }

    public interface LegacyToughness {
        float method_26353();
    }

    public interface LegacyEquipmentSlot {
        class_1304 method_7685();
    }

    public interface LegacyEquipSound {
        class_6880<class_3414> method_31570();
    }

    /** getType() */
    public static class_8051 method_48398(class_1738 item) {
        return item instanceof LegacyType l ? l.method_48398() : super_method_48398(item);
    }

    public static class_8051 super_method_48398(class_1738 item) {
        return fields(item).cb$armorType();
    }

    /** getMaterial() */
    public static class_6880<ArmorMaterial> method_7686(class_1738 item) {
        return item instanceof LegacyMaterial l ? l.method_7686() : super_method_7686(item);
    }

    public static class_6880<ArmorMaterial> super_method_7686(class_1738 item) {
        return fromW(fields(item).cb$armorMaterial());
    }

    /** getDefense(): the material's defense for the item's type (1.21.1 read its fields, not getMaterial()) */
    public static int method_7687(class_1738 item) {
        return item instanceof LegacyDefense l ? l.method_7687() : super_method_7687(item);
    }

    public static int super_method_7687(class_1738 item) {
        ArmorFields f = fields(item);
        return fromW(f.cb$armorMaterial()).comp_349().method_48403(f.cb$armorType());
    }

    /** getToughness() */
    public static float method_26353(class_1738 item) {
        return item instanceof LegacyToughness l ? l.method_26353() : super_method_26353(item);
    }

    public static float super_method_26353(class_1738 item) {
        return fromW(fields(item).cb$armorMaterial()).comp_349().comp_2303();
    }

    /** getEquipmentSlot(): the type's slot */
    public static class_1304 method_7685(class_1738 item) {
        return item instanceof LegacyEquipmentSlot l ? l.method_7685() : super_method_7685(item);
    }

    public static class_1304 super_method_7685(class_1738 item) {
        return fields(item).cb$armorType().method_48399();
    }

    /** getEquipSound(): getMaterial().value().equipSound() -- through the class's getMaterial() as 1.21.1's did */
    public static class_6880<class_3414> method_31570(class_1738 item) {
        return item instanceof LegacyEquipSound l ? l.method_31570() : super_method_31570(item);
    }

    public static class_6880<class_3414> super_method_31570(class_1738 item) {
        return method_7686(item).comp_349().comp_2300();
    }

    /** the protected field type: the type the item was built with */
    public static class_8051 field_41933(class_1738 item) {
        return fields(item).cb$armorType();
    }

    /** the protected field material: the holder the item was built with */
    public static class_6880<ArmorMaterial> field_7881(class_1738 item) {
        return fromW(fields(item).cb$armorMaterial());
    }

    /**
     * ArmorItem.DISPENSE_ITEM_BEHAVIOR: 1.21.2's EquipmentDispenseItemBehavior, its successor (dispenseEquipment
     * onto a living entity in front that can wear the item, else the default dispense)
     */
    public static class_2357 field_7879() {
        return class_10205.field_54201;
    }

    // ---- ArmorItem.Type (1.21.2's ArmorType) ----

    /** Type.hasTrims(): the four humanoid types, not BODY */
    public static boolean method_56691(class_8051 type) {
        return type == class_8051.field_41934 || type == class_8051.field_41935 || type == class_8051.field_41936
            || type == class_8051.field_41937;
    }

    private CbItemArmor() {
    }
}

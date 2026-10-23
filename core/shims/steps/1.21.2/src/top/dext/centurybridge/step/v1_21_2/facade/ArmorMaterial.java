package top.dext.centurybridge.step.v1_21_2.facade;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.class_1856;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_6880;
import net.minecraft.class_8051;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemArmor;

/**
 * 1.21.1's ArmorMaterial (net/minecraft/class_1741, classRenames): a registered record of the defense per
 * armor type, enchantment value, equip sound, repair ingredient, texture layers, toughness and knockback
 * resistance. 1.21.2 reused the name for a different record (durability and a repair tag added, the layers
 * replaced by an equipment model id, no registry), which an ArmorItem is built from now: CbItemArmor turns
 * one into the other where 1.21.1's ArmorItem constructor took this one. The armor type is 1.21.2's
 * ArmorType (1.21.1's ArmorItem.Type, renamed; the same constants and members).
 *
 * <p>Written out as the record 1.21.1 compiled (final, its component fields and accessors under the
 * intermediary names 1.21.1 bytecode uses, value equality, the record's toString), the fields public: an access
 * widener never reaches a CenturyBridge class, so a member standing in for one a mod could widen must be.
 */
public final class ArmorMaterial {

    /** CODEC: 1.21.1's ARMOR_MATERIAL registry's holder-by-name codec (the layer's registry, CbItemArmor) */
    public static final Codec<class_6880<ArmorMaterial>> field_48840 = CbItemArmor.field_48976().method_40294();

    public final Map<class_8051, Integer> comp_2298;
    public final int comp_2299;
    public final class_6880<class_3414> comp_2300;
    public final Supplier<class_1856> comp_2301;
    public final List<Layer> comp_2302;
    public final float comp_2303;
    public final float comp_2304;

    public ArmorMaterial(Map<class_8051, Integer> defense, int enchantmentValue, class_6880<class_3414> equipSound,
                         Supplier<class_1856> repairIngredient, List<Layer> layers, float toughness, float knockbackResistance) {
        this.comp_2298 = defense;
        this.comp_2299 = enchantmentValue;
        this.comp_2300 = equipSound;
        this.comp_2301 = repairIngredient;
        this.comp_2302 = layers;
        this.comp_2303 = toughness;
        this.comp_2304 = knockbackResistance;
    }

    /** getDefense(type): the defense map's value, 0 for a type it lacks */
    public int method_48403(class_8051 type) {
        return comp_2298.getOrDefault(type, 0);
    }

    /** defense() */
    public Map<class_8051, Integer> comp_2298() {
        return comp_2298;
    }

    /** enchantmentValue() */
    public int comp_2299() {
        return comp_2299;
    }

    /** equipSound() */
    public class_6880<class_3414> comp_2300() {
        return comp_2300;
    }

    /** repairIngredient() */
    public Supplier<class_1856> comp_2301() {
        return comp_2301;
    }

    /** layers() */
    public List<Layer> comp_2302() {
        return comp_2302;
    }

    /** toughness() */
    public float comp_2303() {
        return comp_2303;
    }

    /** knockbackResistance() */
    public float comp_2304() {
        return comp_2304;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof ArmorMaterial m && comp_2299 == m.comp_2299
            && Float.compare(comp_2303, m.comp_2303) == 0 && Float.compare(comp_2304, m.comp_2304) == 0
            && Objects.equals(comp_2298, m.comp_2298) && Objects.equals(comp_2300, m.comp_2300)
            && Objects.equals(comp_2301, m.comp_2301) && Objects.equals(comp_2302, m.comp_2302);
    }

    @Override
    public int hashCode() {
        int h = Objects.hashCode(comp_2298);
        h = 31 * h + Integer.hashCode(comp_2299);
        h = 31 * h + Objects.hashCode(comp_2300);
        h = 31 * h + Objects.hashCode(comp_2301);
        h = 31 * h + Objects.hashCode(comp_2302);
        h = 31 * h + Float.hashCode(comp_2303);
        return 31 * h + Float.hashCode(comp_2304);
    }

    @Override
    public String toString() {
        return "ArmorMaterial[defense=" + comp_2298 + ", enchantmentValue=" + comp_2299 + ", equipSound=" + comp_2300
            + ", repairIngredient=" + comp_2301 + ", layers=" + comp_2302 + ", toughness=" + comp_2303
            + ", knockbackResistance=" + comp_2304 + "]";
    }

    /**
     * 1.21.1's ArmorMaterial.Layer (net/minecraft/class_1741$class_9196, classRenames): one texture layer of the
     * humanoid armor model, textures/models/armor/&lt;asset path&gt;_layer_&lt;1|2&gt;&lt;suffix&gt;.png
     * in the asset's namespace (2 for the inner model), dyeable or not. 1.21.1's body; its members public under
     * their intermediary names (see above).
     */
    public static final class Layer {
        /** assetName */
        public final class_2960 field_48841;
        /** suffix */
        public final String field_48842;
        /** dyeable */
        public final boolean field_48843;
        /** innerTexture */
        public final class_2960 field_48844;
        /** outerTexture */
        public final class_2960 field_48845;

        public Layer(class_2960 assetName, String suffix, boolean dyeable) {
            this.field_48841 = assetName;
            this.field_48842 = suffix;
            this.field_48843 = dyeable;
            this.field_48844 = method_56695(true);
            this.field_48845 = method_56695(false);
        }

        public Layer(class_2960 assetName) {
            this(assetName, "", false);
        }

        /** resolveTexture(inner) */
        public class_2960 method_56695(boolean inner) {
            return field_48841.method_45134(path -> "textures/models/armor/" + field_48841.method_12832() + "_layer_"
                + (inner ? 2 : 1) + field_48842 + ".png");
        }

        /** texture(inner) */
        public class_2960 method_56693(boolean inner) {
            return inner ? field_48844 : field_48845;
        }

        /** dyeable() */
        public boolean method_56692() {
            return field_48843;
        }

        /** the asset id the textures are named after (the item's equipment model id on 1.21.2, CbItemArmor) */
        public class_2960 cb$assetName() {
            return field_48841;
        }
    }
}

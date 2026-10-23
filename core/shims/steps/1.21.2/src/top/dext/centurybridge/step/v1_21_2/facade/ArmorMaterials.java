package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.class_1802;
import net.minecraft.class_1856;
import net.minecraft.class_1935;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_6880;
import net.minecraft.class_8051;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemArmor;

/**
 * 1.21.1's ArmorMaterials (net/minecraft/class_1740, classRenames): the vanilla armor materials, registered in
 * 1.21.1's ARMOR_MATERIAL registry (CbItemArmor's) and held as holders. 1.21.2's ArmorMaterials is an
 * interface of its own materials (a different record, CbItemArmor maps between them). The values are
 * 1.21.1's bootstrap, transcribed from its bytecode: defense per type (boots, leggings, chestplate, helmet,
 * body), enchantment value, equip sound, toughness, knockback resistance, repair item, layers.
 */
public class ArmorMaterials {

    public static final class_6880<ArmorMaterial> field_7897 = method_56699("leather", defense(1, 2, 3, 1, 3), 15,
        class_3417.field_14581, 0.0F, 0.0F, () -> class_1856.method_8091(new class_1935[] {class_1802.field_8745}),
        List.of(new ArmorMaterial.Layer(class_2960.method_60656("leather"), "", true),
            new ArmorMaterial.Layer(class_2960.method_60656("leather"), "_overlay", false)));
    public static final class_6880<ArmorMaterial> field_7887 = method_56698("chainmail", defense(1, 4, 5, 2, 4), 12,
        class_3417.field_15191, 0.0F, 0.0F, () -> class_1856.method_8091(new class_1935[] {class_1802.field_8620}));
    public static final class_6880<ArmorMaterial> field_7892 = method_56698("iron", defense(2, 5, 6, 2, 5), 9,
        class_3417.field_14862, 0.0F, 0.0F, () -> class_1856.method_8091(new class_1935[] {class_1802.field_8620}));
    public static final class_6880<ArmorMaterial> field_7895 = method_56698("gold", defense(1, 3, 5, 2, 7), 25,
        class_3417.field_14761, 0.0F, 0.0F, () -> class_1856.method_8091(new class_1935[] {class_1802.field_8695}));
    public static final class_6880<ArmorMaterial> field_7889 = method_56698("diamond", defense(3, 6, 8, 3, 11), 10,
        class_3417.field_15103, 2.0F, 0.0F, () -> class_1856.method_8091(new class_1935[] {class_1802.field_8477}));
    public static final class_6880<ArmorMaterial> field_7890 = method_56698("turtle", defense(2, 5, 6, 2, 5), 9,
        class_3417.field_14684, 0.0F, 0.0F, () -> class_1856.method_8091(new class_1935[] {class_1802.field_8161}));
    public static final class_6880<ArmorMaterial> field_21977 = method_56698("netherite", defense(3, 6, 8, 3, 11), 15,
        class_3417.field_21866, 3.0F, 0.1F, () -> class_1856.method_8091(new class_1935[] {class_1802.field_22020}));
    public static final class_6880<ArmorMaterial> field_48846 = method_56698("armadillo", defense(3, 6, 8, 3, 11), 10,
        class_3417.field_47721, 0.0F, 0.0F, () -> class_1856.method_8091(new class_1935[] {class_1802.field_47830}));

    public ArmorMaterials() {
    }

    /** bootstrap(registry): 1.21.1 registered the constants on class init and answered LEATHER */
    public static class_6880<ArmorMaterial> method_56697(class_2378<ArmorMaterial> registry) {
        return field_7897;
    }

    private static EnumMap<class_8051, Integer> defense(int boots, int leggings, int chestplate, int helmet, int body) {
        EnumMap<class_8051, Integer> m = new EnumMap<>(class_8051.class);
        m.put(class_8051.field_41937, boots);
        m.put(class_8051.field_41936, leggings);
        m.put(class_8051.field_41935, chestplate);
        m.put(class_8051.field_41934, helmet);
        m.put(class_8051.field_48838, body);
        return m;
    }

    /** register(name, defense, ...): one layer named after the material (public: the facade's members are) */
    public static class_6880<ArmorMaterial> method_56698(String name, EnumMap<class_8051, Integer> defense, int enchantmentValue,
                                                         class_6880<class_3414> equipSound, float toughness, float knockbackResistance,
                                                         Supplier<class_1856> repairIngredient) {
        return method_56699(name, defense, enchantmentValue, equipSound, toughness, knockbackResistance, repairIngredient,
            List.of(new ArmorMaterial.Layer(class_2960.method_60656(name))));
    }

    /** 1.21.1's register: the defense copied for every type in order, the material registered under minecraft:name */
    public static class_6880<ArmorMaterial> method_56699(String name, EnumMap<class_8051, Integer> defense, int enchantmentValue,
                                                         class_6880<class_3414> equipSound, float toughness, float knockbackResistance,
                                                         Supplier<class_1856> repairIngredient, List<ArmorMaterial.Layer> layers) {
        EnumMap<class_8051, Integer> copy = new EnumMap<>(class_8051.class);
        for (class_8051 type : class_8051.values()) {
            copy.put(type, defense.get(type));
        }
        return class_2378.method_47985(CbItemArmor.registry(), class_2960.method_60656(name),
            new ArmorMaterial(copy, enchantmentValue, equipSound, repairIngredient, layers, toughness, knockbackResistance));
    }
}

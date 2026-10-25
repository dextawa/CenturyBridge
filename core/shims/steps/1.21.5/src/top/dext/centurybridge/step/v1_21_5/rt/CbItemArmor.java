package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_10192;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1741;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_3414;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_8051;
import net.minecraft.class_9334;
import top.dext.centurybridge.step.v1_21_5.facade.AnimalArmorItem;
import top.dext.centurybridge.step.v1_21_5.facade.ArmorItem;

/**
 * 1.21.4's armor items on 1.21.5 (item area). 1.21.5 removed ArmorItem and AnimalArmorItem (its armor pieces are Items
 * built from Item.Properties.humanoidArmor / wolfArmor / horseArmor) and ArmorMaterial's property appliers
 * humanoidProperties / animalProperties. Each applier here is 1.21.4's body on 1.21.5's API (javap V class_1741: the
 * same components with the same values; humanoidProperties is exactly 1.21.5's Item.Properties.humanoidArmor).
 * Type tests and casts against the removed classes (instanceOfRedirects / checkCastRedirects; the types are renamed to
 * Item): a converted class (under the facade) or one of 1.21.4's vanilla instances -- the 25 humanoid armor pieces and
 * the wolf armor and 4 horse armors, read off 1.21.4's Items.&lt;clinit&gt; (each field whose factory builds its item
 * with {@code new ArmorItem} / {@code new AnimalArmorItem}).
 */
public final class CbItemArmor {

    private CbItemArmor() {
    }

    /** ArmorMaterial.humanoidProperties(props, type) */
    public static class_1792.class_1793 method_63991(class_1741 material, class_1792.class_1793 props, class_8051 type) {
        return props.method_66332(material, type);
    }

    /** ArmorMaterial.animalProperties(props, allowedEntities) */
    public static class_1792.class_1793 method_63992(class_1741 material, class_1792.class_1793 props, class_6885<class_1299<?>> allowed) {
        return props.method_7895(class_8051.field_48838.method_56690(material.comp_3166()))
            .method_57348(material.method_63993(class_8051.field_48838))
            .method_61647(material.comp_2301())
            .method_57349(class_9334.field_54196, class_10192.method_64202(class_1304.field_48824)
                .method_64205(material.comp_2300()).method_64204(material.comp_3168()).method_64206(allowed).method_64203());
    }

    /** ArmorMaterial.animalProperties(props, equipSound, damageOnHurt, allowedEntities) */
    public static class_1792.class_1793 method_64503(class_1741 material, class_1792.class_1793 props, class_6880<class_3414> equipSound,
            boolean damageOnHurt, class_6885<class_1299<?>> allowed) {
        return props.method_7895(class_8051.field_48838.method_56690(material.comp_3166()))
            .method_61647(material.comp_2301())
            .method_57348(material.method_63993(class_8051.field_48838))
            .method_57349(class_9334.field_54196, class_10192.method_64202(class_1304.field_48824)
                .method_64205(equipSound).method_64204(material.comp_3168()).method_64206(allowed).method_64210(damageOnHurt)
                .method_64203());
    }

    /** Equippable's equipOnInteract for a 1.21.4 {@code new Equippable(..)}: 1.21.4 had none (only its mobs' own interactions equipped) */
    public static boolean noEquipOnInteract() {
        return false;
    }

    /** {@code new ArmorItem(material, type, props)} (ctorRedirects) */
    public static class_1792 newArmorItem(class_1741 material, class_8051 type, class_1792.class_1793 props) {
        return new ArmorItem(material, type, props);
    }

    /** {@code new AnimalArmorItem(material, bodyType, props)} (ctorRedirects) */
    public static class_1792 newAnimalArmorItem(class_1741 material, AnimalArmorItem.BodyType bodyType, class_1792.class_1793 props) {
        return new AnimalArmorItem(material, bodyType, props);
    }

    /** {@code new AnimalArmorItem(material, bodyType, equipSound, damageOnHurt, props)} (ctorRedirects) */
    public static class_1792 newAnimalArmorItemSound(class_1741 material, AnimalArmorItem.BodyType bodyType, class_6880<class_3414> equipSound,
            boolean damageOnHurt, class_1792.class_1793 props) {
        return new AnimalArmorItem(material, bodyType, equipSound, damageOnHurt, props);
    }

    /** {@code o instanceof ArmorItem} */
    public static boolean isArmorItem(Object o) {
        return o instanceof ArmorItem || o == class_1802.field_8090 || o == class_1802.field_8267 || o == class_1802.field_8577
            || o == class_1802.field_8570 || o == class_1802.field_8370 || o == class_1802.field_8283 || o == class_1802.field_8873
            || o == class_1802.field_8218 || o == class_1802.field_8313 || o == class_1802.field_8743 || o == class_1802.field_8523
            || o == class_1802.field_8396 || o == class_1802.field_8660 || o == class_1802.field_8805 || o == class_1802.field_8058
            || o == class_1802.field_8348 || o == class_1802.field_8285 || o == class_1802.field_8862 || o == class_1802.field_8678
            || o == class_1802.field_8416 || o == class_1802.field_8753 || o == class_1802.field_22027 || o == class_1802.field_22028
            || o == class_1802.field_22029 || o == class_1802.field_22030;
    }

    /** {@code (ArmorItem) o} */
    public static Object castArmorItem(Object o) {
        return check(o, isArmorItem(o), "ArmorItem");
    }

    /** {@code o instanceof AnimalArmorItem} */
    public static boolean isAnimalArmorItem(Object o) {
        return o instanceof AnimalArmorItem || o == class_1802.field_47831 || o == class_1802.field_8578 || o == class_1802.field_8560
            || o == class_1802.field_8807 || o == class_1802.field_18138;
    }

    /** {@code (AnimalArmorItem) o} */
    public static Object castAnimalArmorItem(Object o) {
        return check(o, isAnimalArmorItem(o), "AnimalArmorItem");
    }

    private static Object check(Object o, boolean is, String type) {
        if (o == null || is) {
            return o;
        }
        throw new ClassCastException("class " + o.getClass().getName() + " cannot be cast to 1.21.4's " + type);
    }
}

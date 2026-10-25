package top.dext.centurybridge.step.v1_21_5.facade;

import net.minecraft.class_1299;
import net.minecraft.class_1741;
import net.minecraft.class_1792;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import top.dext.centurybridge.step.v1_21_5.rt.CbItemArmor;

/**
 * 1.21.4's AnimalArmorItem (net/minecraft/class_4059), gone in 1.21.5 (wolf and horse armor are Items built from
 * Item.Properties.wolfArmor / horseArmor). Superclass of a converted subclass (superRenames) and what a 1.21.4
 * {@code new AnimalArmorItem(..)} builds (ctorRedirects); other references name Item (classRenames), and a type test
 * asks CbItemArmor. 1.21.4's bodies: the constructors apply ArmorMaterial.animalProperties (CbItemArmor) for the body
 * type's entities; getBreakingSound answers the body type's (it overrides the member restored on Item, which decides
 * the sound LivingEntity.breakItem plays, CbItemBreakSoundMixin).
 */
public class AnimalArmorItem extends class_1792 {

    private final BodyType bodyType;

    public AnimalArmorItem(class_1741 material, BodyType bodyType, class_1792.class_1793 properties) {
        super(CbItemArmor.method_63992(material, properties, bodyType.field_54108));
        this.bodyType = bodyType;
    }

    public AnimalArmorItem(class_1741 material, BodyType bodyType, class_6880<class_3414> equipSound, boolean damageOnHurt,
            class_1792.class_1793 properties) {
        super(CbItemArmor.method_64503(material, properties, equipSound, damageOnHurt, bodyType.field_54108));
        this.bodyType = bodyType;
    }

    /** getBreakingSound(): the body type's (overrides the member CbItemMembersRestoredMixin restores on Item) */
    public class_3414 method_57336() {
        return bodyType.field_49256;
    }

    /**
     * 1.21.4's AnimalArmorItem.BodyType (net/minecraft/class_4059$class_9076, classRenames): the break sound and the
     * entities the armor fits. The constants keep 1.21.4's names (values / valueOf / name) and are also reachable under
     * their intermediary field names, which a converted get-static names.
     */
    public enum BodyType {
        EQUESTRIAN(class_3417.field_15075.comp_349(), class_1299.field_6139),
        CANINE(class_3417.field_49174.comp_349(), class_1299.field_6055);

        /** EQUESTRIAN */
        public static final BodyType field_47825 = EQUESTRIAN;
        /** CANINE */
        public static final BodyType field_47826 = CANINE;

        /** breakingSound (1.21.4: package-private, kept under its intermediary name) */
        public final class_3414 field_49256;
        /** allowedEntities */
        public final class_6885<class_1299<?>> field_54108;

        BodyType(class_3414 field_49256, class_1299<?>... types) {
            this.field_49256 = field_49256;
            this.field_54108 = class_6885.method_40245(class_1299::method_40124, types);
        }
    }
}

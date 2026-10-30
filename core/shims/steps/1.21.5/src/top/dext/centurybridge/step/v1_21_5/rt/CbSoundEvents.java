package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_10823;
import net.minecraft.class_10824;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_6880;

/**
 * 1.21.4 SoundEvents constants 1.21.5 changed (javap -p 1.21.4 / 1.21.5 class_3417, getters for the step's fieldGetters):
 * - registered as Holder.Reference in 1.21.5 where 1.21.4 held the SoundEvent: the holder's value, the same registered
 *   event (LLAMA_SWAG was already a Holder in 1.21.4: the reference itself);
 * - the wolf's sounds moved into 1.21.5's wolf sound variants (SoundEvents.WOLF_SOUNDS, class_10823 records per
 *   WolfSoundVariant.SoundSet): 1.21.4's entity.wolf.ambient / death / growl / hurt / pant / whine are the CLASSIC set's
 *   (its identifier is "", javap -c 1.21.5 class_10824$class_10825 &lt;clinit&gt; and class_3417.method_68125: entity.wolf +
 *   identifier + .ambient ...);
 * - WOLF_HOWL (entity.wolf.howl): 1.21.5 registers no such event; the event 1.21.4 registered under that name,
 *   createVariableRangeEvent(minecraft:entity.wolf.howl), unregistered (a client plays it where its sounds define it).
 */
public final class CbSoundEvents {
    private CbSoundEvents() {
    }

    private static class_10823 classicWolf() {
        return class_3417.field_57074.get(class_10824.class_10825.field_57087);
    }

    /** CAMEL_SADDLE */
    public static class_3414 field_40078() {
        return class_3417.field_40078.comp_349();
    }

    /** HORSE_SADDLE */
    public static class_3414 field_14704() {
        return class_3417.field_14704.comp_349();
    }

    /** ITEM_BREAK */
    public static class_3414 field_15075() {
        return class_3417.field_15075.comp_349();
    }

    /** PIG_SADDLE */
    public static class_3414 field_14824() {
        return class_3417.field_14824.comp_349();
    }

    /** SHIELD_BLOCK */
    public static class_3414 field_15150() {
        return class_3417.field_15150.comp_349();
    }

    /** SHIELD_BREAK */
    public static class_3414 field_15239() {
        return class_3417.field_15239.comp_349();
    }

    /** STRIDER_SADDLE */
    public static class_3414 field_23798() {
        return class_3417.field_23798.comp_349();
    }

    /** WOLF_ARMOR_BREAK */
    public static class_3414 field_49174() {
        return class_3417.field_49174.comp_349();
    }

    /** LLAMA_SWAG (1.21.4: Holder) */
    public static class_6880<class_3414> field_14554() {
        return class_3417.field_14554;
    }

    /** WOLF_AMBIENT: the CLASSIC wolf sound set's */
    public static class_3414 field_14724() {
        return classicWolf().comp_3779().comp_349();
    }

    /** WOLF_DEATH: the CLASSIC wolf sound set's */
    public static class_3414 field_14659() {
        return classicWolf().comp_3780().comp_349();
    }

    /** WOLF_GROWL: the CLASSIC wolf sound set's */
    public static class_3414 field_14575() {
        return classicWolf().comp_3781().comp_349();
    }

    /** WOLF_HURT: the CLASSIC wolf sound set's */
    public static class_3414 field_15218() {
        return classicWolf().comp_3782().comp_349();
    }

    /** WOLF_PANT: the CLASSIC wolf sound set's */
    public static class_3414 field_14922() {
        return classicWolf().comp_3783().comp_349();
    }

    /** WOLF_WHINE: the CLASSIC wolf sound set's */
    public static class_3414 field_14807() {
        return classicWolf().comp_3784().comp_349();
    }

    private static final class_3414 WOLF_HOWL = class_3414.method_47908(class_2960.method_60656("entity.wolf.howl"));

    /** WOLF_HOWL */
    public static class_3414 field_15117() {
        return WOLF_HOWL;
    }
}

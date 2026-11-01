package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11677;
import net.minecraft.class_572;
import net.minecraft.class_970;

/**
 * HumanoidArmorLayer's 1.21.8 models (javap -c both code jars): innerModel / outerModel (field_4830 / field_4831) and
 * the baby pair (field_53218 / field_53219) became ArmorModelSets (field_61804 adult, field_61805 baby); 1.21.8's
 * getArmorModel answered inner for LEGS and outer for HEAD / CHEST / FEET, which is how CbClimbClient.armorSet builds
 * the set from a 1.21.8 pair. A read answers the set's legs (inner) or head (outer); a write (advanced-skills makes
 * them mutable and swaps them per frame) rebuilds the set with the new model in the slots 1.21.8 drew it in.
 */
@Environment(EnvType.CLIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbArmorLayerFields {
    private CbArmorLayerFields() {
    }

    private static class_11677 inner(class_11677 s, class_572 m) {
        return new class_11677(s.comp_4542(), s.comp_4543(), m, s.comp_4545());
    }

    private static class_11677 outer(class_11677 s, class_572 m) {
        return new class_11677(m, m, s.comp_4544(), m);
    }

    public static class_572 field_4830(class_970 l) {
        return (class_572) l.field_61804.comp_4544();
    }

    public static void field_4830(class_970 l, class_572 m) {
        l.field_61804 = inner(l.field_61804, m);
    }

    public static class_572 field_4831(class_970 l) {
        return (class_572) l.field_61804.comp_4542();
    }

    public static void field_4831(class_970 l, class_572 m) {
        l.field_61804 = outer(l.field_61804, m);
    }

    public static class_572 field_53218(class_970 l) {
        return (class_572) l.field_61805.comp_4544();
    }

    public static void field_53218(class_970 l, class_572 m) {
        l.field_61805 = inner(l.field_61805, m);
    }

    public static class_572 field_53219(class_970 l) {
        return (class_572) l.field_61805.comp_4542();
    }

    public static void field_53219(class_970 l, class_572 m) {
        l.field_61805 = outer(l.field_61805, m);
    }
}

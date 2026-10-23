package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_2960;
import net.minecraft.class_6880;
import net.minecraft.class_8053;
import net.minecraft.class_8054;
import net.minecraft.class_8056;
import top.dext.centurybridge.step.v1_21_2.facade.ArmorMaterial;

/**
 * 1.21.1's ArmorTrim members 1.21.2's record dropped: pattern() / material() (the record's components) and the
 * humanoid armor trim textures for a 1.21.1 armor material, by 1.21.1's formula --
 * {@code <pattern asset namespace>:trims/models/armor/<pattern asset path>[_leggings]_<material suffix>}, the suffix
 * being the trim material's override for that armor material, else its asset name. 1.21.2's trim material keys its
 * overrides by equipment model id: the armor material's (CbItemArmor.toW: the vanilla material's id for a 1.21.1
 * vanilla material, which is what 1.21.2's vanilla trim materials key, iron_darker and the like).
 */
public final class CbItemTrims {

    /** pattern() */
    public static class_6880<class_8056> method_48424(class_8053 trim) {
        return trim.comp_3180();
    }

    /** material() */
    public static class_6880<class_8054> method_48431(class_8053 trim) {
        return trim.comp_3179();
    }

    private static String suffix(class_8053 trim, class_6880<ArmorMaterial> armorMaterial) {
        class_8054 material = trim.comp_3179().comp_349();
        String override = material.comp_1237().get(CbItemArmor.toW(armorMaterial).comp_3168());
        return override != null ? override : material.comp_1208();
    }

    /** innerTexture(armorMaterial): the leggings layer */
    public static class_2960 method_48434(class_8053 trim, class_6880<ArmorMaterial> armorMaterial) {
        String suffix = suffix(trim, armorMaterial);
        return trim.comp_3180().comp_349().comp_1213().method_45134(p -> "trims/models/armor/" + p + "_leggings_" + suffix);
    }

    /** outerTexture(armorMaterial) */
    public static class_2960 method_48436(class_8053 trim, class_6880<ArmorMaterial> armorMaterial) {
        String suffix = suffix(trim, armorMaterial);
        return trim.comp_3180().comp_349().comp_1213().method_45134(p -> "trims/models/armor/" + p + "_" + suffix);
    }

    private CbItemTrims() {
    }
}

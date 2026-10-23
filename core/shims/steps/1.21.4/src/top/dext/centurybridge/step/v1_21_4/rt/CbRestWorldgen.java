package top.dext.centurybridge.step.v1_21_4.rt;

import net.minecraft.class_6554;
import net.minecraft.class_7891;
import net.minecraft.class_7924;
import net.minecraft.class_8197;
import net.minecraft.class_8198;
import top.dext.centurybridge.step.v1_21_4.facade.OverworldBiomeModifier;

/**
 * 1.21.3's Winter Drop world generation hooks on 1.21.4, where the Winter Drop is vanilla:
 * MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD_WINTER_DROP is 1.21.4's OVERWORLD preset (it places the
 * pale garden as 1.21.3's Winter Drop preset did), MultiNoiseBiomeSourceParameterLists.winterDrop(context) is
 * 1.21.3's body over it, new OverworldBiomeBuilder(modifier) is 1.21.4's builder (see OverworldBiomeModifier).
 */
public final class CbRestWorldgen {

    private CbRestWorldgen() {
    }

    /** Preset.OVERWORLD_WINTER_DROP (fieldRedirects, its own holder) */
    public static final class Presets {
        private Presets() {
        }

        public static final class_8197.class_5305 field_54706 = class_8197.class_5305.field_34499;
    }

    /** MultiNoiseBiomeSourceParameterLists.winterDrop(context) */
    public static void method_64761(class_7891<class_8197> context) {
        context.method_46838(class_8198.field_42992, new class_8197(class_8197.class_5305.field_34499, context.method_46799(class_7924.field_41236)));
    }

    /** new OverworldBiomeBuilder(modifier) */
    public static class_6554 overworldBuilder(OverworldBiomeModifier modifier) {
        return new class_6554();
    }
}

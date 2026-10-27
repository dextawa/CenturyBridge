package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_4388;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's RealmsConfigureWorldScreen layout columns, leftX (field_19795) and rightX (field_19796), which 1.21.6 dropped
 * when it rebuilt the screen as tabs (javap of both). 1.21.5's init set them first, from the screen width:
 * width / 2 - 187 and width / 2 + 190. Its tooltip placement and buttons read them. They are given back under their
 * names and set the same way at init's head (CbRealmsConfigureWorldRestoredMixinHooks, at the default priority), for
 * the 1.20.2 layer's CbNetRealmsConfigureWorldBridge (its restored renderMousehoverTooltip flips a tooltip that would
 * pass rightX) and for 1.21.5 mods' shadows and accessors. Nothing in 1.21.6 reads them. Priority below every mod's, so
 * the fields exist before any mixin naming them is applied.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(value = class_4388.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRealmsConfigureWorldRestoredMixin implements CbPrioritySplit.RealmsConfigureWorldLayout {

    /** leftX */
    private int field_19795;

    /** rightX */
    private int field_19796;

    @Override
    public void cb$v1_21_6$layout(int width) {
        this.field_19795 = width / 2 - 187;
        this.field_19796 = width / 2 + 190;
    }
}

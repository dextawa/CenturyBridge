package top.dext.centurybridge.step.v1_21_11.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_11.facade.DimensionSpecialEffects;

/** a ClientLevel with 1.21.10's effects() restored (CbClientLevelEffectsMixin): the restored member's answer */
@Environment(EnvType.CLIENT)
public interface CbEffectsLevel {
    /** ClientLevel.effects() (method_28103), called through the restored member so a mod's mixin on it answers */
    DimensionSpecialEffects cb$levelEffects();

    /** sets the restored 1.21.10 field ClientLevel.effects (field_24606), as 1.21.10's constructor did (CbClientLevelEffectsMixinHooks) */
    void cb$v1_21_11$setLevelEffects(DimensionSpecialEffects effects);
}

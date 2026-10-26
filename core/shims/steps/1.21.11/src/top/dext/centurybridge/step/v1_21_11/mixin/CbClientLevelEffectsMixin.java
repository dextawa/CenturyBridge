package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_11.facade.DimensionSpecialEffects;
import top.dext.centurybridge.step.v1_21_11.rt.CbEffectsLevel;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10's ClientLevel.effects() (method_28103) and its field (field_24606), restored: the DimensionSpecialEffects for
 * the level's dimension type, chosen once when the level is made (CbClientLevelEffectsMixinHooks sets the field at the
 * end of the constructor, as 1.21.10's constructor did). 1.21.11's own drawing points ask it through
 * rt/CbDimensionEffectsRoutes (only for a mod's effects object or a mod's mixin; otherwise 1.21.11's values stand).
 * Restored members only (CbVanillaReads.PRIORITY); the injectors into 1.21.11's methods are in the Hooks mixin.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = class_638.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientLevelEffectsMixin implements CbEffectsLevel {
    /** 1.21.10's ClientLevel.effects field (field_24606), restored under its name: effects() reads it as 1.21.10's did */
    private DimensionSpecialEffects field_24606;

    @CbVanillaReads("CbClientLevelEffectsMixinHooks (with CbDimEffectsLightingMixin, CbDimEffectsLightTextureMixin, "
        + "CbDimEffectsFogMixin, CbDimEffectsSkyMixin; rt/CbDimensionEffectsRoutes): when a converted mod mixes effects(), "
        + "1.21.11's shade, lighting, end flash, lightmap, fog and sky points take the effects it answers")
    public DimensionSpecialEffects method_28103() {
        return this.field_24606;
    }

    @Override
    public DimensionSpecialEffects cb$levelEffects() {
        return this.method_28103();
    }

    @Override
    public void cb$v1_21_11$setLevelEffects(DimensionSpecialEffects effects) {
        this.field_24606 = effects;
    }
}

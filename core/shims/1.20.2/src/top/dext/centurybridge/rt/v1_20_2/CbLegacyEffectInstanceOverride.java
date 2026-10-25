package top.dext.centurybridge.rt.v1_20_2;

/**
 * Marks a converted MobEffectInstance class overriding 1.20.1's applyEffect(entity)
 * (method_5589): 1.20.2's tick applies the effect inline, so for such an instance the tick
 * calls the override again (CbEffectInstanceLegacyHooks).
 */
public interface CbLegacyEffectInstanceOverride {

    /** 1.20.1 applyEffect(entity) */
    void method_5589(net.minecraft.class_1309 entity);
}

package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 SuspiciousEffectHolder: one effect and its duration (getSuspiciousEffect /
 * getEffectDuration). 1.20.2's holder answers a list of effect entries (getSuspiciousEffects).
 * A converted holder (an implementor of the 1.20.1 interface, or a FlowerBlock subclass
 * overriding either getter) gets this interface and the 1.20.2 method synthesized
 * ({@link CbEffects#suspiciousEffects}); FlowerBlock answers the 1.20.1 getters again from its
 * 1.20.2 list (CbFlowerBlockLegacy).
 */
public interface CbLegacySuspiciousEffectHolder {

    /** getSuspiciousEffect() */
    net.minecraft.class_1291 method_10188();

    /** getEffectDuration() */
    int method_10187();
}

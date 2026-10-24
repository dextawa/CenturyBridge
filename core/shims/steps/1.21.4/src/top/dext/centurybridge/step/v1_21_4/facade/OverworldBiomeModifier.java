package top.dext.centurybridge.step.v1_21_4.facade;

/**
 * 1.21.3's OverworldBiomeBuilder.Modifier (net/minecraft/class_6554$class_10317, classRenames): NONE or
 * WINTER_DROP (the pale garden placed in the overworld). 1.21.4's builder always places it, as WINTER_DROP did;
 * NONE has no 1.21.4 counterpart (ledgered), CbRestWorldgen.overworldBuilder gives 1.21.4's builder for both.
 */
public enum OverworldBiomeModifier {
    NONE,
    WINTER_DROP;

    /** NONE */
    public static final OverworldBiomeModifier field_54709 = NONE;
    /** WINTER_DROP */
    public static final OverworldBiomeModifier field_54710 = WINTER_DROP;
}

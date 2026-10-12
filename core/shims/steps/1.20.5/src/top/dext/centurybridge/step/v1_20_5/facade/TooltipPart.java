package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * ItemStack.TooltipPart (gone in 1.20.5, which keeps a tooltip flag on each component instead): the
 * parts of a stack's tooltip 1.20.4's HideFlags hid, one bit each in declaration order. Constants
 * carry 1.20.4's intermediary names, as 1.20.4 production code saw them.
 */
public enum TooltipPart {
    /** ENCHANTMENTS */
    field_25768,
    /** MODIFIERS */
    field_25769,
    /** UNBREAKABLE */
    field_25770,
    /** CAN_DESTROY */
    field_25771,
    /** CAN_PLACE */
    field_25772,
    /** ADDITIONAL */
    field_25773,
    /** DYE */
    field_25774,
    /** UPGRADES */
    field_41945;

    /** the HideFlags bit (1.20.4: mask = 1 << ordinal) */
    public final int field_25775 = 1 << ordinal();

    /** getMask() */
    public int method_30269() {
        return field_25775;
    }
}

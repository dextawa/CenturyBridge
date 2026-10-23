package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4 attributes as values, 1.20.5 as registry holders: the conversions AttributeMap's 1.20.4 members
 * (CbRestAttributeMapMixin) and their override hooks use.
 */
public final class CbRestAttributes {

    /** an attribute's holder: its registered reference (BuiltInRegistries.ATTRIBUTE.wrapAsHolder); null for null */
    public static net.minecraft.class_6880<net.minecraft.class_1320> holder(net.minecraft.class_1320 attribute) {
        return attribute == null ? null : net.minecraft.class_7923.field_41190.method_47983(attribute);
    }

    /** the attribute a holder holds; null for null */
    public static net.minecraft.class_1320 value(net.minecraft.class_6880<net.minecraft.class_1320> holder) {
        return holder == null ? null : holder.comp_349();
    }

    private CbRestAttributes() {
    }
}

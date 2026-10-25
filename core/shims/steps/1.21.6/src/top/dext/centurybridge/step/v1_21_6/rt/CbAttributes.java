package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * AttributeInstance's 1.21.5 CompoundTag save / load, which 1.21.6 replaced by pack() / apply(Packed) and the Packed
 * codec. 1.21.5 (javap): save wrote "id" (the attribute holder through BuiltInRegistries.ATTRIBUTE's holder-by-name codec),
 * "base" and, when there were any, "modifiers" (AttributeModifier.CODEC list of the permanent modifiers); 1.21.6's pack()
 * holds exactly those three (List.copyOf of the permanent modifiers). load read "base" (0 when absent) and put each
 * listed modifier into the instance as a permanent one, replacing one of the same id, then marked it dirty -- 1.21.6's
 * setBaseValue, removeModifier(id) and addPermanentModifier do the same to the instance.
 */
public final class CbAttributes {

    private static final com.mojang.serialization.Codec<net.minecraft.class_6880<net.minecraft.class_1320>> ATTRIBUTE =
        net.minecraft.class_7923.field_41190.method_40294();

    private CbAttributes() {
    }

    /** AttributeInstance.ID_FIELD */
    public static String field_52007() {
        return "id";
    }

    /** AttributeInstance.TYPE_CODEC */
    public static com.mojang.serialization.Codec<net.minecraft.class_6880<net.minecraft.class_1320>> field_57596() {
        return ATTRIBUTE;
    }

    /** save() */
    public static net.minecraft.class_2487 method_26839(net.minecraft.class_1324 instance) {
        net.minecraft.class_1324.class_11345 packed = instance.method_71379();
        net.minecraft.class_2487 tag = new net.minecraft.class_2487();
        tag.method_67494("id", ATTRIBUTE, packed.comp_4214());
        tag.method_10549("base", packed.comp_4215());
        if (!packed.comp_4216().isEmpty()) {
            tag.method_67494("modifiers", net.minecraft.class_1322.field_46247.listOf(), packed.comp_4216());
        }
        return tag;
    }

    /** load(CompoundTag) */
    public static void method_26833(net.minecraft.class_1324 instance, net.minecraft.class_2487 tag) {
        instance.method_6192(tag.method_68563("base", 0.0D));
        for (net.minecraft.class_1322 modifier : tag.method_67491("modifiers", net.minecraft.class_1322.field_46247.listOf())
                .orElse(java.util.List.of())) {
            if (instance.method_6196(modifier.comp_2447())) {
                instance.method_6200(modifier.comp_2447());
            }
            instance.method_26837(modifier);
        }
    }
}

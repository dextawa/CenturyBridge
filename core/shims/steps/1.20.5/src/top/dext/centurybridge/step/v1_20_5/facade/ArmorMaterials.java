package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4's ArmorMaterials enum (class_1740; classRenames): the seven vanilla materials, in 1.20.4's order
 * with 1.20.4's names (name() / valueOf / getSerializedName / CODEC), under their 1.20.4 intermediary field
 * names too. 1.20.5 keeps the same materials as registered records (ArmorMaterials' holders, same names,
 * same defense per piece, enchantability, sounds, toughness, knockback resistance and repair items -- read
 * from them here); the durability multipliers, which 1.20.5 moved into each item's properties, are 1.20.4's.
 */
public enum ArmorMaterials implements ArmorMaterial, net.minecraft.class_3542 {
    LEATHER("leather", 5),
    CHAIN("chainmail", 15),
    IRON("iron", 15),
    GOLD("gold", 7),
    DIAMOND("diamond", 33),
    TURTLE("turtle", 25),
    NETHERITE("netherite", 37);

    public static final ArmorMaterials field_7897 = LEATHER;
    public static final ArmorMaterials field_7887 = CHAIN;
    public static final ArmorMaterials field_7892 = IRON;
    public static final ArmorMaterials field_7895 = GOLD;
    public static final ArmorMaterials field_7889 = DIAMOND;
    public static final ArmorMaterials field_7890 = TURTLE;
    public static final ArmorMaterials field_21977 = NETHERITE;

    /** CODEC: by serialized name */
    public static final com.mojang.serialization.Codec<ArmorMaterials> field_41941 =
        net.minecraft.class_3542.method_28140(ArmorMaterials::values);

    private final String name;
    private final int durabilityMultiplier;

    ArmorMaterials(String name, int durabilityMultiplier) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
    }

    /** the 1.20.5 holder of this material (ArmorMaterials' field of the same name) */
    public net.minecraft.class_6880<net.minecraft.class_1741> cbHolder() {
        return switch (this) {
            case LEATHER -> net.minecraft.class_1740.field_7897;
            case CHAIN -> net.minecraft.class_1740.field_7887;
            case IRON -> net.minecraft.class_1740.field_7892;
            case GOLD -> net.minecraft.class_1740.field_7895;
            case DIAMOND -> net.minecraft.class_1740.field_7889;
            case TURTLE -> net.minecraft.class_1740.field_7890;
            case NETHERITE -> net.minecraft.class_1740.field_21977;
        };
    }

    private net.minecraft.class_1741 record() {
        return cbHolder().comp_349();
    }

    /** getDurabilityForType(type): the piece's base durability times 1.20.4's multiplier */
    @Override
    public int method_48402(net.minecraft.class_1738.class_8051 type) {
        return type.method_56690(durabilityMultiplier);
    }

    @Override
    public int method_48403(net.minecraft.class_1738.class_8051 type) {
        return record().method_48403(type);
    }

    @Override
    public int method_7699() {
        return record().comp_2299();
    }

    @Override
    public net.minecraft.class_3414 method_7698() {
        return record().comp_2300().comp_349();
    }

    @Override
    public net.minecraft.class_1856 method_7695() {
        return record().comp_2301().get();
    }

    @Override
    public String method_7694() {
        return name;
    }

    @Override
    public float method_7700() {
        return record().comp_2303();
    }

    @Override
    public float method_24355() {
        return record().comp_2304();
    }

    /** getSerializedName() */
    @Override
    public String method_15434() {
        return name;
    }
}

package top.dext.centurybridge.step.v1_21.rt;

/**
 * Small shape changes of 1.21's item, effect, data and tag classes, each value converted exactly:
 * <ul>
 * <li>DyeColor.getTextureDiffuseColors: 1.20.6's three 0-1 components of the dye's texture colour, 1.21's
 *     one opaque ARGB int of the same colour;</li>
 * <li>CustomData.read(encoder, value): 1.20.6 always encoded through NbtOps.INSTANCE, which 1.21 takes as
 *     an argument;</li>
 * <li>attribute modifiers named by a UUID in 1.20.6, by a ResourceLocation in 1.21: MobEffect's
 *     addAttributeModifier took the UUID as a string, Item's BASE_ATTACK_DAMAGE_UUID / BASE_ATTACK_SPEED_UUID
 *     are 1.21's BASE_ATTACK_DAMAGE_ID / BASE_ATTACK_SPEED_ID -- both through CbEntityAttributes' mapping
 *     (1.21's own data fixer table), so a modifier added under a UUID is found under it again;</li>
 * <li>TagManager.getTagDir(registry): the directory 1.21 reads that registry's tags from
 *     (Registries.tagsDirPath -- 1.21 names them in the singular, tags/block where 1.20.6 read tags/blocks);</li>
 * <li>BlockMath.getUVLockTransform(transformation, direction, warning): 1.21 dropped the warning
 *     supplier and answers the identity where 1.20.6 logged the warning and answered a transformation of
 *     zero scale (a transformation that cannot be inverted); that case keeps 1.20.6's answer.</li>
 * </ul>
 */
public final class CbItemMisc {

    private CbItemMisc() {
    }

    /** DyeColor.getTextureDiffuseColors (callReshapes ret): 1.21's ARGB as 1.20.6's {r, g, b} in 0..1 */
    public static float[] rgbComponents(int argb) {
        return new float[] {((argb >> 16) & 0xFF) / 255.0F, ((argb >> 8) & 0xFF) / 255.0F, (argb & 0xFF) / 255.0F};
    }

    /** CustomData.read's ops: NbtOps.INSTANCE, what 1.20.6's encoded through (callReshapes push) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static com.mojang.serialization.DynamicOps nbtOps() {
        return net.minecraft.class_2509.field_11560;
    }

    /** MobEffect.addAttributeModifier's 1.20.6 uuid string as 1.21's modifier id (callReshapes conv) */
    public static net.minecraft.class_2960 idOfUuid(String uuid) {
        return CbEntityAttributes.idOf(java.util.UUID.fromString(uuid));
    }

    /** the override side: 1.21's modifier id as the uuid string a 1.20.6 addAttributeModifier override takes */
    public static String uuidStringOf(net.minecraft.class_2960 id) {
        return CbEntityAttributes.uuidOf(id).toString();
    }

    /** Item.BASE_ATTACK_DAMAGE_UUID (fieldGetter): the uuid 1.21's BASE_ATTACK_DAMAGE_ID stands for */
    public static java.util.UUID field_8006() {
        return CbEntityAttributes.uuidOf(net.minecraft.class_1792.field_8006);
    }

    /** Item.BASE_ATTACK_SPEED_UUID (fieldGetter): the uuid 1.21's BASE_ATTACK_SPEED_ID stands for */
    public static java.util.UUID field_8001() {
        return CbEntityAttributes.uuidOf(net.minecraft.class_1792.field_8001);
    }

    /** TagManager.getTagDir(registry) (staticRedirect): where 1.21 reads that registry's tags */
    public static String method_40099(net.minecraft.class_5321<? extends net.minecraft.class_2378<?>> registry) {
        return net.minecraft.class_7924.method_60916(registry);
    }

    /** BlockMath.getUVLockTransform(transformation, direction, warning) (staticRedirect) */
    public static net.minecraft.class_4590 method_23221(net.minecraft.class_4590 transformation, net.minecraft.class_2350 direction,
            java.util.function.Supplier<String> warning) {
        if (transformation.method_22935() == null) {
            org.slf4j.LoggerFactory.getLogger(net.minecraft.class_4609.class).warn(warning.get());
            return new net.minecraft.class_4590(null, null, new org.joml.Vector3f(0.0F, 0.0F, 0.0F), null);
        }
        return net.minecraft.class_4609.method_23221(transformation, direction);
    }
}

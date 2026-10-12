package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 static helpers whose 1.20.2 counterparts changed shape, with their 1.20.1 bodies ported
 * onto the 1.20.2 API.
 */
public final class CbMisc {

    // ---- CommonLinks.extendRealms: 1.20.2 takes the profile as a UUID; 1.20.1 took the string it put in the link

    /** extendRealms(subscriptionId, profileId) */
    public static String method_49719(String subscriptionId, String profileId) {
        return "https://aka.ms/ExtendJavaRealms?subscriptionId=" + subscriptionId + "&profileId=" + profileId;
    }

    /** extendRealms(subscriptionId, profileId, trial) */
    public static String method_49720(String subscriptionId, String profileId, boolean trial) {
        return method_49719(subscriptionId, profileId) + "&ref=" + (trial ? "expiredTrial" : "expiredRealm");
    }

    // ---- PlayerPublicKey.verifyAndDecode(validator, uuid, data, gracePeriod): 1.20.2 dropped the expiry
    // check this did first; the signature check and construction are 1.20.2's own

    /** verifyAndDecode(validator, uuid, data, gracePeriod) */
    public static net.minecraft.class_7428 method_43550(net.minecraft.class_7500 validator, java.util.UUID uuid,
            net.minecraft.class_7428.class_7443 data, java.time.Duration gracePeriod)
            throws net.minecraft.class_7428.class_7652 {
        if (data.method_45103(gracePeriod)) {
            throw new net.minecraft.class_7428.class_7652(net.minecraft.class_7428.field_39954);
        }
        return net.minecraft.class_7428.method_43550(validator, uuid, data);
    }

    // ---- DispenserBehavior.setEntityPosition(pointer, entity, side): 1.20.2 computes the position from the
    // entity type's size and returns it; 1.20.1 placed the entity itself, by its own size

    /** setEntityPosition(pointer, entity, side), 1.20.1 body */
    public static void method_27042(net.minecraft.class_2342 pointer, net.minecraft.class_1297 entity,
            net.minecraft.class_2350 side) {
        net.minecraft.class_243 center = pointer.method_53906();
        double width = entity.method_17681();
        double height = entity.method_17682();
        entity.method_5814(
            center.field_1352 + side.method_10148() * (0.5000099999997474 - width / 2.0),
            center.field_1351 + side.method_10164() * (0.5000099999997474 - height / 2.0) - height / 2.0,
            center.field_1350 + side.method_10165() * (0.5000099999997474 - width / 2.0));
    }

    // ---- SuspiciousStewItem.addEffectToStew(stew, effect, duration): 1.20.2 keeps stew effects as a list of
    // (effect, duration) entries under "effects"; appending one is 1.20.2's appendEffects

    /** addEffectToStew(stew, effect, duration): appends, as 1.20.1 did */
    public static void method_8021(net.minecraft.class_1799 stew, net.minecraft.class_1291 effect, int duration) {
        net.minecraft.class_1830.method_53209(stew,
            java.util.List.of(new net.minecraft.class_7917.class_8751(effect, duration)));
    }

    /** SuspiciousStewItem.EFFECT_ID_KEY (1.20.1 ConstantValue) */
    public static String field_30924() {
        return "EffectId";
    }

    /** SuspiciousStewItem.EFFECT_DURATION_KEY (1.20.1 ConstantValue) */
    public static String field_30925() {
        return "EffectDuration";
    }

    /** a 1.20.1 trim pattern is not a decal (1.20.2 added the flag) */
    public static boolean noDecal() {
        return false;
    }

    /** ArmorTrim.getTrim(registries, stack): 1.20.1 always reported decoding problems */
    public static boolean reportTrimErrors() {
        return false;
    }

    private CbMisc() {
    }
}

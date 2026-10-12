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

    /** ArmorTrim.getTrim(registries, stack): 1.20.1 always logged decoding problems -- 1.20.2's suppressErrors flag off */
    public static boolean reportTrimErrors() {
        return false;
    }

    // ---- UUIDUtil.getOrCreatePlayerUUID(profile): gone in 1.20.2 (authlib 5 profiles always carry an id)

    /** getOrCreatePlayerUUID(profile), 1.20.1 body: the profile's id, else the offline-mode id of its name */
    public static java.util.UUID method_43343(com.mojang.authlib.GameProfile profile) {
        java.util.UUID id = profile.getId();
        return id != null ? id : net.minecraft.class_4844.method_43344(profile.getName());
    }

    // ---- ExtraCodecs.instantCodec(formatter): gone in 1.20.2

    /** instantCodec(formatter), 1.20.1 body: an Instant as the formatter's string */
    public static com.mojang.serialization.Codec<java.time.Instant> method_43532(java.time.format.DateTimeFormatter formatter) {
        return com.mojang.serialization.Codec.STRING.comapFlatMap(s -> {
            try {
                return com.mojang.serialization.DataResult.success(java.time.Instant.from(formatter.parse(s)));
            } catch (Exception e) {
                return com.mojang.serialization.DataResult.error(e::getMessage);
            }
        }, formatter::format);
    }

    // ---- DecoratedPotRecipe.createDecoratedPotItem(decorations): moved to DecoratedPotBlockEntity in 1.20.2, same body

    /** createDecoratedPotItem(decorations) */
    public static net.minecraft.class_1799 method_51509(net.minecraft.class_8172.class_8526 decorations) {
        return net.minecraft.class_8172.method_52577(decorations);
    }

    // ---- ShapedRecipe.itemFromJson(json): gone in 1.20.2 (recipes decode through codecs)

    /** itemFromJson(json), 1.20.1 body: the "item" id, which must name an item other than air */
    public static net.minecraft.class_1792 method_8155(com.google.gson.JsonObject json) {
        String id = net.minecraft.class_3518.method_15265(json, "item");
        net.minecraft.class_1792 item = net.minecraft.class_7923.field_41178.method_17966(net.minecraft.class_2960.method_12829(id))
            .orElseThrow(() -> new com.google.gson.JsonSyntaxException("Unknown item '" + id + "'"));
        if (item == net.minecraft.class_1802.field_8162) {
            throw new com.google.gson.JsonSyntaxException("Empty ingredient not allowed here");
        }
        return item;
    }

    // ---- PlayerMap.updatePlayer(oldSection, newSection, player): 1.20.1's body was empty; 1.20.2 removed it

    /** updatePlayer(oldSection, newSection, player): nothing, as in 1.20.1 */
    public static void method_14081(net.minecraft.class_3210 map, long oldSection, long newSection, net.minecraft.class_3222 player) {
    }

    // ---- DataFixTypes.SAVED_DATA / References.SAVED_DATA: 1.20.1 updated every saved-data file through one
    // generic "saved_data" type, whose fixes rewrite only vanilla structures in it (raids, map ids, scoreboard
    // objectives and teams, structure features). 1.20.2 split it into one type per vanilla kind and dropped
    // "saved_data" from its schemas. A mod's own saved data matched none of those structures, so 1.20.1's
    // generic update left it as it was; the 1.20.2 type that does exactly that for any data is
    // saved_data/command_storage, a remainder type no fix targets.

    /** DataFixTypes.SAVED_DATA */
    public static net.minecraft.class_4284 field_19219() {
        return net.minecraft.class_4284.field_45077;
    }

    /** References.SAVED_DATA */
    public static com.mojang.datafixers.DSL.TypeReference field_5732() {
        return net.minecraft.class_1208.field_45090;
    }

    private CbMisc() {
    }
}

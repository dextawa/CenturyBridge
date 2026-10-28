package cb.smx1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Runs once the server's world is loaded: triggers the vanilla methods the test mixins name and checks that each
 * one changed or observed what vanilla did, as on 1.20.1. Logs "CB-SMX1 ok|FAIL <case> <detail>" per case and
 * "CB-SMX1 summary ok=N fail=M" at the end.
 */
public final class Driver {
    private static final Map<String, Integer> COUNT = new ConcurrentHashMap<>();
    public static final ThreadLocal<Boolean> ON_DRIVER = ThreadLocal.withInitial(() -> false);
    public static volatile boolean forceLuck;
    public static volatile boolean forceLiquid;
    public static volatile boolean forceWater;
    public static final String PROBE_SHAPED = "cb_smx1:route_probe";
    public static final String PROBE_SMELT = "cb_smx1:route_probe_smelt";
    public static volatile int defaultCookingTime = -1;
    /** the recipe FurnaceCanBurnMixin's handler was handed while the driver ticked its furnace */
    public static volatile Object burnRecipe;
    /** -Dcenturybridge.legacyJsonRoute=false: the layer reads recipes with 1.20.2's codecs; the reader mixins never run */
    private static final boolean ROUTE = !"false".equals(System.getProperty("centurybridge.legacyJsonRoute"));
    public static final String PROBE_STAT_KEY = "stat_type.minecraft.killed";
    /** the modifier EffectAttributesMixin puts on the attack damage with Strength */
    public static final java.util.UUID EFFECT_MODIFIER = java.util.UUID.fromString("6c1f9a52-3f0e-4c1e-9d55-0c2b5e0a1b02");
    /** the modifier cb-smx1n (a native 1.20.2 mod) puts on the movement speed with Speed */
    private static final java.util.UUID NATIVE_MODIFIER = java.util.UUID.fromString("6c1f9a52-3f0e-4c1e-9d55-0c2b5e0a1b01");
    /** -Dcenturybridge.legacyEffectAttributeRoute=false: the restored attribute hooks are never consulted */
    private static final boolean EFFECT_ROUTE = !"false".equals(System.getProperty("centurybridge.legacyEffectAttributeRoute"));
    /** -Dcenturybridge.legacyStatNameRoute=false: getDisplayName() answers 1.20.2's name, never asking getTranslationKey() */
    private static final boolean STAT_ROUTE = !"false".equals(System.getProperty("centurybridge.legacyStatNameRoute"));
    private static int ok;
    private static int fail;

    private Driver() {
    }

    public static void count(String c) {
        COUNT.merge(c, 1, Integer::sum);
    }

    private static void check(String c, boolean cond, String detail) {
        if (cond) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-SMX1 " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    private static net.minecraft.class_1860<?> recipe(net.minecraft.server.MinecraftServer server, String id) {
        for (net.minecraft.class_1860<?> r : server.method_3772().method_8126()) {
            if (id.equals(r.method_8114().toString())) {
                return r;
            }
        }
        return null;
    }

    private static void checkRecipeProbes(net.minecraft.server.MinecraftServer server, String phase) {
        net.minecraft.class_1860<?> shaped = recipe(server, PROBE_SHAPED);
        if (!(shaped instanceof net.minecraft.class_1869 s)) {
            check("recipe-reader-shaped-" + phase, false, PROBE_SHAPED + " loaded as " + shaped);
        } else {
            int count = s.method_8110(server.method_30611()).method_7947();
            String group = s.method_8112();
            boolean expected = ROUTE ? count == 42 && "cbsmx1-group".equals(group) : count == 1 && "".equals(group);
            check("recipe-reader-shaped-" + phase, expected, "route " + ROUTE + ": result count " + count + ", group '" + group
                + "' (RETURN handler ran " + COUNT.getOrDefault("shaped-reader-return", 0) + ", group redirect "
                + COUNT.getOrDefault("shaped-reader-group-redirect", 0) + " time(s))");
        }
        net.minecraft.class_1860<?> smelt = recipe(server, PROBE_SMELT);
        if (!(smelt instanceof net.minecraft.class_1874 c)) {
            check("recipe-reader-cooking-" + phase, false, PROBE_SMELT + " loaded as " + smelt);
        } else {
            int time = c.method_8167();
            boolean expected = ROUTE ? time == 7 && defaultCookingTime == 200 : time == 200;
            check("recipe-reader-cooking-" + phase, expected, "route " + ROUTE + ": cooking time " + time
                + ", default seen by the redirect " + defaultCookingTime + ", experience " + c.method_8171()
                + " (HEAD handler ran " + COUNT.getOrDefault("cooking-reader-head", 0) + ", time redirect "
                + COUNT.getOrDefault("cooking-reader-time-redirect", 0) + " time(s))");
        }
    }

    /** an advancement's criteria as they write themselves (Criterion.serializeToJson), sorted by name */
    private static String criteriaJson(net.minecraft.class_161 a) {
        if (a == null) {
            return "missing";
        }
        java.util.TreeMap<String, String> out = new java.util.TreeMap<>();
        for (java.util.Map.Entry<String, net.minecraft.class_175> e : a.method_682().entrySet()) {
            out.put(e.getKey(), String.valueOf(e.getValue().method_773()));
        }
        return out.toString();
    }

    /** sha-256 over every loaded recipe outside this mod: id, serializer, network form, sorted by id */
    private static String recipeFingerprint(net.minecraft.server.MinecraftServer server) throws Exception {
        java.util.TreeMap<String, byte[]> forms = new java.util.TreeMap<>();
        for (net.minecraft.class_1860<?> r : server.method_3772().method_8126()) {
            String id = r.method_8114().toString();
            if (id.startsWith("cb_smx1:")) {
                continue;
            }
            net.minecraft.class_2540 buf = new net.minecraft.class_2540(io.netty.buffer.Unpooled.buffer());
            @SuppressWarnings("unchecked")
            net.minecraft.class_1865<net.minecraft.class_1860<?>> ser = (net.minecraft.class_1865<net.minecraft.class_1860<?>>) r.method_8119();
            ser.method_8124(buf, r);
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            forms.put(id + "|" + net.minecraft.class_7923.field_41189.method_10221(ser), bytes);
        }
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        for (java.util.Map.Entry<String, byte[]> e : forms.entrySet()) {
            md.update(e.getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            md.update(e.getValue());
        }
        StringBuilder hex = new StringBuilder();
        for (byte b : md.digest()) {
            hex.append(String.format("%02x", b));
        }
        return "n=" + forms.size() + " sha=" + hex;
    }

    public static void run(net.minecraft.server.MinecraftServer server) {
        System.out.println("CB-SMX1 start");
        // ---- a RETURN injector on LootPool's constructor: every pool the server loaded went through it
        int pools = COUNT.getOrDefault("lootpool-init", 0);
        check("lootpool-ctor-return", pools > 100, "LootPool constructor handler ran " + pools + " time(s) while loot tables loaded");
        // ---- a RETURN injector on AdvancementList.add: the advancement manager adds every loaded advancement once
        int adds = COUNT.getOrDefault("advancements-add", 0);
        check("advancements-add-return", adds >= 1, "AdvancementList.add handler ran " + adds + " time(s)");
        // ---- a cancelling HEAD injector on getValidEffectById: a beacon loaded from its own save answers Luck
        try {
            net.minecraft.class_2580 beacon = new net.minecraft.class_2580(net.minecraft.class_2338.field_10980,
                net.minecraft.class_2246.field_10327.method_9564());
            ((cb.smx1.mixin.BeaconAccess) beacon).cbsmx1$setPrimary(net.minecraft.class_1294.field_5904);
            net.minecraft.class_2487 tag = beacon.method_38244();
            net.minecraft.class_2580 loaded = new net.minecraft.class_2580(net.minecraft.class_2338.field_10980,
                net.minecraft.class_2246.field_10327.method_9564());
            forceLuck = true;
            try {
                loaded.method_11014(tag);
            } finally {
                forceLuck = false;
            }
            net.minecraft.class_1291 primary = ((cb.smx1.mixin.BeaconAccess) loaded).cbsmx1$primary();
            check("beacon-effect-head", primary == net.minecraft.class_1294.field_5926,
                "saved Speed, loaded " + primary + " (handler ran " + COUNT.getOrDefault("beacon-effect-head", 0) + " time(s))");
        } catch (Throwable t) {
            check("beacon-effect-head", false, "threw " + t);
        }
        // ---- a HEAD injector capturing canBurn's arguments (CASE 18): a furnace with raw iron and coal, ticked once,
        // asks canBurn with the smelting recipe; the 1.20.1 handler must be handed that Recipe (1.20.2 passes the holder)
        try {
            net.minecraft.class_3218 level = server.method_30002();
            // a fixed height inside every overworld's build range (-64..319): spawn + 200 is above it on a high spawn
            net.minecraft.class_2338 spawn = level.method_43126();
            net.minecraft.class_2338 pos = new net.minecraft.class_2338(spawn.method_10263(), 300, spawn.method_10260());
            net.minecraft.class_2680 before = level.method_8320(pos);
            level.method_8501(pos, net.minecraft.class_2246.field_10181.method_9564());
            try {
                net.minecraft.class_2609 furnace = (net.minecraft.class_2609) level.method_8321(pos);
                furnace.method_5447(0, new net.minecraft.class_1799(net.minecraft.class_1802.field_33400));
                furnace.method_5447(1, new net.minecraft.class_1799(net.minecraft.class_1802.field_8713));
                burnRecipe = null;
                ON_DRIVER.set(true);
                try {
                    net.minecraft.class_2609.method_31651(level, pos, level.method_8320(pos), furnace);
                } finally {
                    ON_DRIVER.set(false);
                }
                Object got = burnRecipe;
                net.minecraft.class_1792 result = got instanceof net.minecraft.class_1860<?> r
                    ? r.method_8110(server.method_30611()).method_7909() : null;
                check("furnace-canburn-head-wrap", result == net.minecraft.class_1802.field_8620,
                    "handler ran " + COUNT.getOrDefault("furnace-canburn-head", 0) + " time(s), handed "
                        + (got == null ? "null" : got.getClass().getName()) + " making " + result);
            } finally {
                level.method_8501(pos, before);
            }
        } catch (Throwable t) {
            check("furnace-canburn-head-wrap", false, "threw " + t);
        }
        // ---- an accessor on a field 1.20.2 made a record component
        try {
            net.minecraft.class_5341 cond = net.minecraft.class_219.method_932(0.25f).build();
            float p = ((cb.smx1.mixin.RandomChanceAccess) cond).cbsmx1$probability();
            check("record-field-accessor", p == 0.25f, "randomChance(0.25).probability -> " + p);
        } catch (Throwable t) {
            check("record-field-accessor", false, "threw " + t);
        }
        // ---- a HEAD injector on NbtIo.readUnnamedTag: one read through NbtIo.read(DataInput)
        try {
            net.minecraft.class_2487 tag = new net.minecraft.class_2487();
            tag.method_10569("cb", 7);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            net.minecraft.class_2507.method_10628(tag, new DataOutputStream(bytes));
            ON_DRIVER.set(true);
            net.minecraft.class_2487 back;
            try {
                back = net.minecraft.class_2507.method_10627(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())));
            } finally {
                ON_DRIVER.set(false);
            }
            int reads = COUNT.getOrDefault("nbt-read-head", 0);
            check("nbt-read-head", reads == 1 && back.method_10550("cb") == 7, "handler ran " + reads + " time(s), read back cb=" + back.method_10550("cb"));
        } catch (Throwable t) {
            check("nbt-read-head", false, "threw " + t);
        }
        // ---- a cancelling HEAD injector on PathNavigation.isInLiquid: a mob in mid-air may update its path
        try {
            net.minecraft.class_1308 zombie = (net.minecraft.class_1308) net.minecraft.class_1299.field_6051.method_5883(server.method_30002());
            zombie.method_5814(0.5, 300.0, 0.5);
            cb.smx1.mixin.NavigationInvoker nav = (cb.smx1.mixin.NavigationInvoker) zombie.method_5942();
            boolean plain = nav.cbsmx1$canUpdatePath();
            forceLiquid = true;
            boolean forced;
            try {
                forced = nav.cbsmx1$canUpdatePath();
            } finally {
                forceLiquid = false;
            }
            check("navigation-in-liquid-head", !plain && forced, "zombie in mid-air canUpdatePath " + plain + ", with isInLiquid forced " + forced
                + " (handler ran " + COUNT.getOrDefault("navigation-in-liquid-head", 0) + " time(s))");
            forceWater = true;
            boolean redirected;
            try {
                redirected = nav.cbsmx1$canUpdatePath();
            } finally {
                forceWater = false;
            }
            check("navigation-water-redirect", !plain && redirected, "with the isInWaterOrBubble call inside isInLiquid redirected: canUpdatePath "
                + redirected + " (redirect ran " + COUNT.getOrDefault("navigation-water-redirect", 0) + " time(s))");
        } catch (Throwable t) {
            check("navigation-in-liquid-head", false, "threw " + t);
        }
        // ---- recipe readers: what vanilla loaded went through the 1.20.1 readers (and their mixins) when the route is on
        try {
            String start = recipeFingerprint(server);
            checkRecipeProbes(server, "start");
            // every vanilla shaped / cooking recipe went through the restored readers (route on), none did (route off)
            int shapedReads = COUNT.getOrDefault("shaped-reader-return-all", 0);
            int cookingReads = COUNT.getOrDefault("cooking-reader-head-all", 0);
            check("recipe-readers-all", ROUTE ? shapedReads > 500 && cookingReads > 100 : shapedReads == 0 && cookingReads == 0,
                "route " + ROUTE + ": shaped reader returned " + shapedReads + " time(s), cooking reader entered " + cookingReads + " time(s)");
            java.util.concurrent.CompletableFuture<Void> reload = server.method_29439(server.method_3836().method_29210());
            server.method_18857(reload::isDone);
            reload.join();
            String after = recipeFingerprint(server);
            checkRecipeProbes(server, "reload");
            System.out.println("CB-SMX1 fingerprint recipes route=" + ROUTE + " start " + start);
            System.out.println("CB-SMX1 fingerprint recipes route=" + ROUTE + " reload " + after);
            check("recipe-fingerprint-reload", start.equals(after), "start " + start + ", after /reload " + after);
            // the restored private static ShapedRecipe.keyFromJson and Ingredient.fromJson, mixed by this mod
            net.minecraft.class_1860<?> statics = recipe(server, "cb_smx1:static_probe");
            boolean diamond = statics != null && statics.method_8117().size() == 2
                && statics.method_8117().get(1).method_8093(new net.minecraft.class_1799(net.minecraft.class_1802.field_8477));
            boolean stick = statics != null && statics.method_8117().size() == 2
                && statics.method_8117().get(1).method_8093(new net.minecraft.class_1799(net.minecraft.class_1802.field_8600));
            check("recipe-static-key", ROUTE ? diamond && !stick : stick && !diamond, "route " + ROUTE + ": cb_smx1:static_probe's second slot takes diamond "
                + diamond + ", stick " + stick + " (keyFromJson handler ran " + COUNT.getOrDefault("shaped-key-return-all", 0)
                + " time(s), probe " + COUNT.getOrDefault("shaped-key-probe", 0) + ")");
            int ingredientReads = COUNT.getOrDefault("ingredient-from-json-all", 0);
            check("recipe-static-ingredient", ROUTE ? ingredientReads > 1000 : ingredientReads == 0,
                "route " + ROUTE + ": Ingredient.fromJson handler ran " + ingredientReads + " time(s)");
        } catch (Throwable t) {
            t.printStackTrace();
            check("recipe-readers", false, "threw " + t);
        }
        // ---- stat type names: getDisplayName() is the translation of getTranslationKey() (route on), so the key
        // mixin renames "minecraft:killed" only; every other stat type's name is the same with the route on and off
        try {
            StringBuilder names = new StringBuilder();
            String probe = null;
            for (net.minecraft.class_3448<?> type : net.minecraft.class_7923.field_41193) {
                net.minecraft.class_2561 name = type.method_30739();
                String key = name.method_10851() instanceof net.minecraft.class_2588 tr ? tr.method_11022() : "<" + name.method_10851() + ">";
                String id = String.valueOf(net.minecraft.class_7923.field_41193.method_10221(type));
                if ("minecraft:killed".equals(id)) {
                    probe = key;
                } else {
                    names.append(id).append('=').append(key).append('|').append(name.method_10855().size())
                        .append('|').append(name.method_10866().method_10967()).append(';');
                }
            }
            String expected = STAT_ROUTE ? PROBE_STAT_KEY + ".cbsmx1" : PROBE_STAT_KEY;
            check("stat-type-name-probe", expected.equals(probe), "route " + STAT_ROUTE + ": minecraft:killed is named '" + probe
                + "' (key handler ran " + COUNT.getOrDefault("stat-type-key-all", 0) + " time(s), renamed "
                + COUNT.getOrDefault("stat-type-key", 0) + ")");
            System.out.println("CB-SMX1 fingerprint stat-types route=" + STAT_ROUTE + " " + names);
        } catch (Throwable t) {
            t.printStackTrace();
            check("stat-type-name-probe", false, "threw " + t);
        }
        // ---- advancements: the converted mixin on 1.20.1's Advancement.Builder.fromJson decides its probe (route on), the
        // native mixin on 1.20.2's Advancement.fromJson decides its own either way, every other advancement is fingerprinted
        try {
            for (String phase : new String[] {"start", "reload"}) {
                if (phase.equals("reload")) {
                    java.util.concurrent.CompletableFuture<Void> reload = server.method_29439(server.method_3836().method_29210());
                    server.method_18857(reload::isDone);
                    reload.join();
                }
                net.minecraft.class_2989 advancements = server.method_3851();
                net.minecraft.class_161 probe = advancements.method_12896(new net.minecraft.class_2960("cb_smx1", "route_probe"));
                String rewards = probe == null ? "missing" : String.valueOf(probe.method_691().method_747());
                boolean seven = rewards.contains("\"experience\":7");
                check("advancement-converted-mixin-" + phase, ROUTE ? seven : !seven && probe != null, "route " + ROUTE
                    + ": cb_smx1:route_probe rewards " + rewards + " (Builder.fromJson handler returned "
                    + COUNT.getOrDefault("advancement-builder-return-all", 0) + " time(s))");
                net.minecraft.class_161 nat = advancements.method_12896(new net.minecraft.class_2960("cb_smx1n", "native_probe"));
                check("advancement-native-mixin-" + phase, nat != null && nat.method_51697(), "route " + ROUTE
                    + ": cb_smx1n:native_probe sends telemetry " + (nat != null && nat.method_51697()) + " (true = the native mixin acted)");
                // the item conditions of the probes' inventory_changed criteria, as the criteria write them
                String itemProbe = criteriaJson(advancements.method_12896(new net.minecraft.class_2960("cb_smx1", "item_probe")));
                check("predicate-item-converted-mixin-" + phase, ROUTE ? itemProbe.contains("minecraft:diamond") && !itemProbe.contains("minecraft:stick")
                    : itemProbe.contains("minecraft:stick") && !itemProbe.contains("minecraft:diamond"), "route " + ROUTE
                    + ": cb_smx1:item_probe criteria " + itemProbe + " (ItemPredicate.fromJson handler returned "
                    + COUNT.getOrDefault("item-predicate-return-all", 0) + " time(s))");
                String itemProbe2 = criteriaJson(advancements.method_12896(new net.minecraft.class_2960("cb_smx1", "item_probe2")));
                check("predicate-item-converted-mixin-single-" + phase, ROUTE ? itemProbe2.contains("minecraft:diamond") && !itemProbe2.contains("minecraft:stick")
                    : itemProbe2.contains("minecraft:stick") && !itemProbe2.contains("minecraft:diamond"), "route " + ROUTE
                    + ": cb_smx1:item_probe2 (enchanted_item) criteria " + itemProbe2);
                for (String n : new String[] {"item_probe", "items_probe"}) {
                    String nativeItem = criteriaJson(advancements.method_12896(new net.minecraft.class_2960("cb_smx1n", n)));
                    check("predicate-item-native-mixin-" + n + "-" + phase, nativeItem.contains("minecraft:emerald") && !nativeItem.contains("minecraft:stick"),
                        "route " + ROUTE + ": cb_smx1n:" + n + " criteria " + nativeItem + " (emerald = the native mixin acted)");
                }
                // location / state conditions: fall_from_height's start_position (LocationPredicate.fromJson, and through it
                // Block / StatePropertiesPredicate), enter_block's state (StatePropertiesPredicate.fromJson)
                String locProbe = criteriaJson(advancements.method_12896(new net.minecraft.class_2960("cb_smx1", "location_probe")));
                check("predicate-location-converted-mixin-" + phase, ROUTE ? locProbe.contains("minecraft:the_nether") && !locProbe.contains("minecraft:overworld")
                    : locProbe.contains("minecraft:overworld") && !locProbe.contains("minecraft:the_nether"), "route " + ROUTE
                    + ": cb_smx1:location_probe criteria " + locProbe + " (LocationPredicate.fromJson handler returned "
                    + COUNT.getOrDefault("location-predicate-return-all", 0) + " time(s))");
                for (String n : new String[] {"state_probe", "state_probe2"}) {
                    String stateProbe = criteriaJson(advancements.method_12896(new net.minecraft.class_2960("cb_smx1", n)));
                    check("predicate-state-converted-mixin-" + n + "-" + phase, ROUTE ? stateProbe.contains("\"axis\":\"x\"") && !stateProbe.contains("cb_smx1_state_probe")
                        : stateProbe.contains("cb_smx1_state_probe") && !stateProbe.contains("\"axis\":\"x\""), "route " + ROUTE
                        + ": cb_smx1:" + n + " criteria " + stateProbe + " (StatePropertiesPredicate.fromJson handler returned "
                        + COUNT.getOrDefault("state-predicate-return-all", 0) + " time(s))");
                }
                String nativeLoc = criteriaJson(advancements.method_12896(new net.minecraft.class_2960("cb_smx1n", "location_probe")));
                check("predicate-location-native-mixin-" + phase, nativeLoc.contains("minecraft:the_end") && !nativeLoc.contains("minecraft:overworld"),
                    "route " + ROUTE + ": cb_smx1n:location_probe criteria " + nativeLoc + " (the_end = the native mixin acted)");
                String nativeState = criteriaJson(advancements.method_12896(new net.minecraft.class_2960("cb_smx1n", "state_probe")));
                check("predicate-state-native-mixin-" + phase, nativeState.contains("\"axis\":\"z\"") && !nativeState.contains("cbsmx1n_state_probe"),
                    "route " + ROUTE + ": cb_smx1n:state_probe criteria " + nativeState + " (axis z = the native mixin acted)");
                // entity family: player_killed_entity's entity (EntityPredicate.fromJson(json, name, context), and through it
                // fromJson(json) and EntityFlagsPredicate), levitation's distance, effects_changed's effects
                String[][] converted = {
                    {"entity_probe", "minecraft:creeper", "minecraft:zombie", "entity-predicate-return-all"},
                    {"flags_probe", "\"is_baby\":true", "\"is_on_fire\":true", "flags-predicate-return-all"},
                    {"distance_probe", "7.0", "\"y\"", "distance-predicate-return-all"},
                    {"effects_probe", "minecraft:glowing", "minecraft:speed", "effects-predicate-return-all"},
                    {"dsource_probe", "minecraft:is_fire", "minecraft:is_explosion", "dsource-predicate-return-all"},
                    {"damage_probe", "11.0", "\"min\":2.0", "damage-predicate-return-all"},
                    {"tag_probe", "minecraft:is_freezing", "minecraft:is_fall", "tag-predicate-return-all"},
                    {"player_probe", "creative", "survival", "player-predicate-return-all"},
                    {"lightning_probe", "\"min\":3", "\"min\":1", "lightning-predicate-return-all"},
                    {"fishing_probe", "\"in_open_water\":false", "\"in_open_water\":true", "fishing-predicate-return-all"},
                    {"slime_probe", "\"min\":4", "\"min\":1", "slime-predicate-return-all"}};
                for (String[] c : converted) {
                    String crit = criteriaJson(advancements.method_12896(new net.minecraft.class_2960("cb_smx1", c[0])));
                    check("predicate-" + c[0] + "-converted-mixin-" + phase, ROUTE ? crit.contains(c[1]) && !crit.contains(c[2])
                        : crit.contains(c[2]) && !crit.contains(c[1]), "route " + ROUTE + ": cb_smx1:" + c[0] + " criteria " + crit
                        + " (handler returned " + COUNT.getOrDefault(c[3], 0) + " time(s))");
                }
                String[][] natives = {
                    {"entity_probe", "minecraft:skeleton", "minecraft:zombie"},
                    {"context_probe", "minecraft:witch", "minecraft:zombie"},
                    {"distance_probe", "9.0", "\"y\""},
                    {"effects_probe", "minecraft:glowing", "minecraft:speed"},
                    {"dsource_probe", "minecraft:is_lightning", "minecraft:is_explosion"},
                    {"damage_probe", "13.0", "\"min\":2.0"}};
                for (String[] c : natives) {
                    String crit = criteriaJson(advancements.method_12896(new net.minecraft.class_2960("cb_smx1n", c[0])));
                    check("predicate-" + c[0] + "-native-mixin-" + phase, crit.contains(c[1]) && !crit.contains(c[2]),
                        "route " + ROUTE + ": cb_smx1n:" + c[0] + " criteria " + crit + " (" + c[1] + " = the native mixin acted)");
                }
                java.util.TreeMap<String, String> forms = new java.util.TreeMap<>();
                for (net.minecraft.class_161 a : advancements.method_12893()) {
                    String id = a.method_688().toString();
                    if (id.startsWith("cb_smx1")) {
                        continue;
                    }
                    forms.put(id, (a.method_687() == null ? "-" : a.method_687().method_688().toString()) + "|" + a.method_691().method_747() + "|"
                        + new java.util.TreeSet<>(a.method_682().keySet()) + "|" + java.util.Arrays.deepToString(a.method_680()) + "|"
                        + a.method_51697() + "|" + (a.method_686() == null ? "-" : a.method_686().method_811().getString()) + "|" + criteriaJson(a));
                }
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
                StringBuilder hex = new StringBuilder();
                for (byte b : md.digest(forms.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                    hex.append(String.format("%02x", b));
                }
                System.out.println("CB-SMX1 fingerprint advancements route=" + ROUTE + " " + phase + " n=" + forms.size() + " sha=" + hex);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            check("advancement-converted-mixin", false, "threw " + t);
        }
        // ---- the native 1.20.2 mod (cb-smx1n): its mixin on ShapedRecipe.Serializer.codec() still decides its recipe
        try {
            net.minecraft.class_1860<?> nat = recipe(server, "cb_smx1n:native_probe");
            int count = nat == null ? -1 : nat.method_8110(server.method_30611()).method_7947();
            check("native-codec-mixin", count == 9, "route " + ROUTE + ": cb_smx1n:native_probe makes " + count + " (9 = the native codec mixin acted)");
        } catch (Throwable t) {
            t.printStackTrace();
            check("native-codec-mixin", false, "threw " + t);
        }
        // ---- effect attributes: the converted mixin on 1.20.1's add/removeAttributeModifiers acts (route on), the native
        // mixin on 1.20.2's add/remove acts either way, every other effect leaves the attributes as with the route off
        try {
            net.minecraft.class_3218 level = server.method_30002();
            net.minecraft.class_1642 zombie = new net.minecraft.class_1642(level);
            zombie.method_6092(new net.minecraft.class_1293(net.minecraft.class_1294.field_5910, 600, 1));
            net.minecraft.class_1324 damage = zombie.method_5996(net.minecraft.class_5134.field_23721);
            boolean vAdded = damage.method_6199(EFFECT_MODIFIER) != null;
            int strength = damage.method_6195().size();
            zombie.method_6016(net.minecraft.class_1294.field_5910);
            boolean vRemoved = damage.method_6199(EFFECT_MODIFIER) == null;
            check("effect-converted-mixin", (EFFECT_ROUTE ? vAdded : !vAdded) && vRemoved, "route " + EFFECT_ROUTE + ": Strength put cbsmx1-v on "
                + vAdded + " (" + strength + " attack damage modifier(s)), took it off " + vRemoved + " (restored add ran "
                + COUNT.getOrDefault("effect-add-all", 0) + ", remove " + COUNT.getOrDefault("effect-remove-all", 0) + " time(s))");
            zombie.method_6092(new net.minecraft.class_1293(net.minecraft.class_1294.field_5904, 600, 0));
            net.minecraft.class_1324 speed = zombie.method_5996(net.minecraft.class_5134.field_23719);
            boolean nAdded = speed.method_6199(NATIVE_MODIFIER) != null;
            int speeds = speed.method_6195().size();
            zombie.method_6016(net.minecraft.class_1294.field_5904);
            boolean nRemoved = speed.method_6199(NATIVE_MODIFIER) == null;
            check("effect-native-mixin", nAdded && nRemoved && speeds == 2, "route " + EFFECT_ROUTE + ": Speed put cbsmx1-native on " + nAdded
                + " (" + speeds + " movement speed modifier(s)), took it off " + nRemoved);
            // every other effect, amplifiers 0 and 2, on a fresh zombie: the attributes after adding and after removing it
            StringBuilder states = new StringBuilder();
            for (net.minecraft.class_1291 effect : net.minecraft.class_7923.field_41174) {
                if (effect == net.minecraft.class_1294.field_5910 || effect == net.minecraft.class_1294.field_5904 || effect.method_5561()) {
                    continue;
                }
                for (int amp : new int[] {0, 2}) {
                    net.minecraft.class_1642 z = new net.minecraft.class_1642(level);
                    z.method_6092(new net.minecraft.class_1293(effect, 600, amp));
                    states.append(net.minecraft.class_7923.field_41174.method_10221(effect)).append('/').append(amp).append(' ')
                        .append(z.method_6127().method_26855()).append(" -> ");
                    z.method_6016(effect);
                    states.append(z.method_6127().method_26855()).append(';');
                }
            }
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            StringBuilder hex = new StringBuilder();
            for (byte b : md.digest(states.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                hex.append(String.format("%02x", b));
            }
            System.out.println("CB-SMX1 fingerprint effect-attributes route=" + EFFECT_ROUTE + " sha=" + hex + " length=" + states.length());
        } catch (Throwable t) {
            t.printStackTrace();
            check("effect-converted-mixin", false, "threw " + t);
        }
        // the routes' load-time cost: /reload eight times (recipes, advancements, loot... all reread), each timed; compare
        // the line of a boot with the routes on to one with -Dcenturybridge.legacyJsonRoute=false
        try {
            long[] ms = new long[8];
            for (int i = 0; i < ms.length; ++i) {
                long t0 = System.nanoTime();
                java.util.concurrent.CompletableFuture<Void> reload = server.method_29439(server.method_3836().method_29210());
                server.method_18857(reload::isDone);
                reload.join();
                ms[i] = (System.nanoTime() - t0) / 1_000_000L;
            }
            long[] sorted = ms.clone();
            java.util.Arrays.sort(sorted);
            System.out.println("CB-SMX1 timing reload route=" + ROUTE + " ms=" + java.util.Arrays.toString(ms) + " median="
                + (sorted[3] + sorted[4]) / 2 + " min=" + sorted[0]);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.out.println("CB-SMX1 summary ok=" + ok + " fail=" + fail);
    }
}

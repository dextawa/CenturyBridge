import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import top.dext.centurybridge.engine.res.ResourceTranslator;
import top.dext.centurybridge.res.LegacyItems;
import top.dext.centurybridge.res.LegacyLoot;
import top.dext.centurybridge.res.Snbt;

/**
 * Value-level checks for the 1.20.5 item-component resource translation. Compile
 * against core/build/classes and the engine's gson jar, then run with that classpath.
 */
public final class ResourceSemanticCheck {
    private static int checks;

    public static void main(String[] args) {
        decoratedPotIcon();
        bannerIconAndResidualNbt();
        containerLootAndLock();
        beeOccupants();
        hideFlags();
        copyNbtComponentsAndCustomData();
        invalidNativeCopiesStayVisible();
        translatorEntryPoints();
        System.out.println("ResourceSemanticCheck: " + checks + " checks passed");
    }

    private static void decoratedPotIcon() {
        List<String> losses = new ArrayList<>();
        JsonObject got = stack("minecraft:decorated_pot", "{BlockEntityTag:{id:\"minecraft:decorated_pot\","
            + "sherds:[\"minecraft:brick\",\"minecraft:heart_pottery_sherd\",\"minecraft:brick\","
            + "\"minecraft:explorer_pottery_sherd\"]}}", losses);
        JsonObject expected = json("{\"item\":\"minecraft:decorated_pot\"}").getAsJsonObject();
        expected.remove("item");
        expected.addProperty("id", "minecraft:decorated_pot");
        expected.addProperty("count", 1);
        JsonObject components = new JsonObject();
        components.add("minecraft:pot_decorations", json(
            "[\"minecraft:brick\",\"minecraft:heart_pottery_sherd\",\"minecraft:brick\","
                + "\"minecraft:explorer_pottery_sherd\"]"));
        expected.add("components", components);
        equal(expected, got, "decorated pot icon uses pot_decorations, not residual block entity NBT");
        equal(List.of(), losses, "decorated pot conversion is lossless");
    }

    private static void bannerIconAndResidualNbt() {
        List<String> losses = new ArrayList<>();
        JsonObject got = stack("{BlockEntityTag:{id:\"minecraft:banner\","
            + "Patterns:[{Color:9,Pattern:\"mr\"},{Color:8,Pattern:\"bs\"},{Color:15,Pattern:\"bo\"}]},"
            + "HideFlags:32}", losses).getAsJsonObject("components");
        equal(json("[{\"color\":\"cyan\",\"pattern\":\"minecraft:rhombus\"},"
                + "{\"color\":\"light_gray\",\"pattern\":\"minecraft:stripe_bottom\"},"
                + "{\"color\":\"black\",\"pattern\":\"minecraft:border\"}]"),
            got.get("minecraft:banner_patterns"), "banner hashes and dye ids become registry values");
        equal(new JsonObject(), got.get("minecraft:hide_additional_tooltip"), "HideFlags 32 hides the extra tooltip");
        yes(!got.has("minecraft:block_entity_data"), "fully componentized banner has no residual NBT");
        equal(List.of(), losses, "banner conversion is lossless");

        JsonObject residual = stack("{BlockEntityTag:{id:\"minecraft:banner\","
            + "Patterns:[{Color:15,Pattern:\"bo\"}],Custom:{Deep:[1b,2s,3L],Text:\"hello world\"}}}",
            losses).getAsJsonObject("components");
        String snbt = residual.get("minecraft:block_entity_data").getAsString();
        Map<String, Object> rest = Snbt.compound(snbt);
        equal("minecraft:banner", rest.get("id"), "residual block entity data keeps its type id");
        @SuppressWarnings("unchecked")
        Map<String, Object> custom = (Map<String, Object>) rest.get("Custom");
        @SuppressWarnings("unchecked")
        List<Object> deep = (List<Object>) custom.get("Deep");
        equal('b', ((Snbt.Num) deep.get(0)).type(), "nested byte type survives residual SNBT");
        equal('s', ((Snbt.Num) deep.get(1)).type(), "nested short type survives residual SNBT");
        equal('l', ((Snbt.Num) deep.get(2)).type(), "nested long type survives residual SNBT");
        equal("hello world", custom.get("Text"), "nested string survives residual SNBT");
    }

    private static void containerLootAndLock() {
        List<String> losses = new ArrayList<>();
        JsonObject zero = stack("{BlockEntityTag:{id:\"minecraft:chest\",Lock:\"secret\","
            + "LootTable:\"minecraft:chests/test\",LootTableSeed:0L,Custom:{Keep:7b}}}", losses)
            .getAsJsonObject("components");
        equal("secret", zero.get("minecraft:lock").getAsString(), "lock becomes a native string component");
        JsonObject loot = zero.getAsJsonObject("minecraft:container_loot");
        equal("minecraft:chests/test", loot.get("loot_table").getAsString(), "loot table id is preserved");
        yes(!loot.has("seed"), "zero loot seed uses the native default and is omitted");
        Map<String, Object> rest = Snbt.compound(zero.get("minecraft:block_entity_data").getAsString());
        equal("minecraft:chest", rest.get("id"), "residual container data retains id");
        yes(rest.containsKey("Custom"), "non-vanilla container NBT remains in block_entity_data");

        JsonObject nonzero = stack("{BlockEntityTag:{id:\"minecraft:chest\","
            + "LootTable:\"minecraft:chests/test\",LootTableSeed:9007199254740991L}}", losses)
            .getAsJsonObject("components").getAsJsonObject("minecraft:container_loot");
        equal(9007199254740991L, nonzero.get("seed").getAsLong(), "nonzero 64-bit loot seed is not truncated");
        equal(List.of(), losses, "container component conversion is lossless");
    }

    private static void beeOccupants() {
        List<String> losses = new ArrayList<>();
        JsonArray bees = stack("{BlockEntityTag:{id:\"minecraft:beehive\",Bees:[{EntityData:{"
            + "id:\"minecraft:bee\",HasNectar:1b},TicksInHive:12,MinTicksInHive:600}]}}", losses)
            .getAsJsonObject("components").getAsJsonArray("minecraft:bees");
        equal(1, bees.size(), "one bee occupant is carried");
        JsonObject bee = bees.get(0).getAsJsonObject();
        equal(12, bee.get("ticks_in_hive").getAsInt(), "bee ticks field is renamed without changing value");
        equal(600, bee.get("min_ticks_in_hive").getAsInt(), "bee minimum ticks field is renamed");
        Map<String, Object> entity = Snbt.compound(bee.get("entity_data").getAsString());
        equal("minecraft:bee", entity.get("id"), "bee entity id remains in custom entity SNBT");
        equal(1, ((Snbt.Num) entity.get("HasNectar")).value().intValue(), "bee entity NBT keeps its byte value");
        equal(List.of(), losses, "bee component conversion is lossless");
    }

    private static void hideFlags() {
        List<String> losses = new ArrayList<>();
        JsonObject hidden = stack("{Unbreakable:1b,HideFlags:5}", losses).getAsJsonObject("components");
        equal(false, hidden.getAsJsonObject("minecraft:unbreakable").get("show_in_tooltip").getAsBoolean(),
            "HideFlags 4 qualifies the unbreakable component");
        yes(!hidden.has("minecraft:hide_additional_tooltip"), "HideFlags 4 does not set the bit-32 component");

        JsonObject defaults = stack("{HideFlags:66}", losses).getAsJsonObject("components");
        equal(false, defaults.getAsJsonObject("minecraft:attribute_modifiers").get("show_in_tooltip").getAsBoolean(),
            "HideFlags 2 creates the native hidden empty modifier list");
        JsonObject dyed = defaults.getAsJsonObject("minecraft:dyed_color");
        equal(10511680, dyed.get("rgb").getAsInt(), "HideFlags 64 uses the game's default undyed color");
        equal(false, dyed.get("show_in_tooltip").getAsBoolean(), "HideFlags 64 hides dyed color");
        equal(List.of(), losses, "supported hide flags are lossless");
    }

    private static void copyNbtComponentsAndCustomData() {
        List<String> losses = new ArrayList<>();
        JsonObject old = json("{function:\"minecraft:copy_nbt\",source:\"block_entity\","
            + "conditions:[{condition:\"minecraft:survives_explosion\"}],ops:["
            + "{op:\"replace\",source:\"Bees\",target:\"BlockEntityTag.Bees\"},"
            + "{op:\"replace\",source:\"Lock\",target:\"BlockEntityTag.Lock\"},"
            + "{op:\"replace\",source:\"LootTable\",target:\"BlockEntityTag.LootTable\"},"
            + "{op:\"replace\",source:\"LootTableSeed\",target:\"BlockEntityTag.LootTableSeed\"},"
            + "{op:\"replace\",source:\"Patterns\",target:\"BlockEntityTag.Patterns\"},"
            + "{op:\"replace\",source:\"note_block_sound\",target:\"BlockEntityTag.note_block_sound\"},"
            + "{op:\"replace\",source:\"sherds\",target:\"BlockEntityTag.sherds\"},"
            + "{op:\"replace\",source:\"SkullOwner\",target:\"SkullOwner\"},"
            + "{op:\"merge\",source:\"Origin.Deep\",target:\"Custom.Deep\"}]}" ).getAsJsonObject();
        JsonArray one = new JsonArray();
        one.add(old);
        JsonArray out = LegacyLoot.functions(one, "1.20.4", "1.20.5", losses::add);
        equal(2, out.size(), "one old copy splits into native and custom copies");
        JsonObject nativeCopy = out.get(0).getAsJsonObject();
        equal("minecraft:copy_components", nativeCopy.get("function").getAsString(), "native function type");
        equal(json("[\"minecraft:bees\",\"minecraft:lock\",\"minecraft:container_loot\","
                + "\"minecraft:banner_patterns\",\"minecraft:note_block_sound\",\"minecraft:pot_decorations\","
                + "\"minecraft:profile\"]"), nativeCopy.get("include"),
            "all native targets are included once and container loot is folded");
        equal("block_entity", nativeCopy.get("source").getAsString(), "native source is preserved");
        equal(old.get("conditions"), nativeCopy.get("conditions"), "native function keeps its conditions");

        JsonObject custom = out.get(1).getAsJsonObject();
        equal("minecraft:copy_custom_data", custom.get("function").getAsString(), "custom function type");
        equal(old.getAsJsonArray("ops").get(8), custom.getAsJsonArray("ops").get(0),
            "custom merge operation keeps source, target, nesting and op exactly");
        equal(old.get("conditions"), custom.get("conditions"), "custom function keeps its conditions");
        equal(List.of(), losses, "exact native and custom copies are lossless");
    }

    private static void invalidNativeCopiesStayVisible() {
        List<String> losses = new ArrayList<>();
        JsonArray partial = functions("{function:\"minecraft:copy_nbt\",source:\"block_entity\",ops:["
            + "{op:\"replace\",source:\"LootTable\",target:\"BlockEntityTag.LootTable\"}]}", losses);
        equal(0, partial.size(), "an unpaired LootTable is not broadened to a whole container_loot copy");
        equal(1, losses.size(), "the unpaired native copy remains an explicit loss");

        losses.clear();
        JsonArray append = functions("{function:\"minecraft:copy_nbt\",source:\"block_entity\",ops:["
            + "{op:\"append\",source:\"Bees\",target:\"BlockEntityTag.Bees\"}]}", losses);
        equal(0, append.size(), "append is not mislabeled as a replacing component copy");
        equal(1, losses.size(), "unsupported native append remains an explicit loss");

        losses.clear();
        JsonArray custom = functions("{function:\"minecraft:copy_nbt\",source:\"this\",ops:["
            + "{op:\"merge\",source:\"Origin.Deep\",target:\"Custom.Deep\"}]}", losses);
        equal(1, custom.size(), "an entity-sourced custom NBT merge remains representable");
        equal("minecraft:copy_custom_data", custom.get(0).getAsJsonObject().get("function").getAsString(),
            "custom merge remains copy_custom_data");
        equal("Custom.Deep", custom.get(0).getAsJsonObject().getAsJsonArray("ops").get(0)
            .getAsJsonObject().get("target").getAsString(), "custom nested target is not flattened");
        equal(List.of(), losses, "custom nested merge is lossless");
    }

    private static void translatorEntryPoints() {
        Map<String, byte[]> entries = new LinkedHashMap<>();
        JsonObject icon = new JsonObject();
        icon.addProperty("item", "minecraft:decorated_pot");
        icon.addProperty("nbt", "{BlockEntityTag:{id:\"minecraft:decorated_pot\",sherds:[\"minecraft:brick\"]}}");
        JsonObject display = new JsonObject();
        display.add("icon", icon);
        JsonObject advancementInput = new JsonObject();
        advancementInput.add("display", display);
        entries.put("data/test/advancements/icon.json", bytes(advancementInput.toString()));
        entries.put("data/test/loot_tables/beehive.json", bytes("{pools:[{entries:[{type:\"minecraft:item\","
            + "name:\"minecraft:beehive\",functions:[{function:\"minecraft:copy_nbt\",source:\"block_entity\","
            + "ops:[{op:\"replace\",source:\"Bees\",target:\"BlockEntityTag.Bees\"}]}]}]}]}"));
        ResourceTranslator.Outcome outcome = ResourceTranslator.apply(entries, "1.20.4", "1.20.5");
        equal(Map.of(), outcome.losses, "resource translator entry points report no losses for native fixtures");
        JsonObject advancement = JsonParser.parseString(new String(entries.get("data/test/advancements/icon.json"),
            StandardCharsets.UTF_8)).getAsJsonObject();
        equal("minecraft:brick", advancement.getAsJsonObject("display").getAsJsonObject("icon")
            .getAsJsonObject("components").getAsJsonArray("minecraft:pot_decorations").get(0).getAsString(),
            "advancement translation writes the component value");
        JsonObject loot = JsonParser.parseString(new String(entries.get("data/test/loot_tables/beehive.json"),
            StandardCharsets.UTF_8)).getAsJsonObject();
        JsonObject fn = loot.getAsJsonArray("pools").get(0).getAsJsonObject().getAsJsonArray("entries").get(0)
            .getAsJsonObject().getAsJsonArray("functions").get(0).getAsJsonObject();
        equal("minecraft:copy_components", fn.get("function").getAsString(), "loot translation writes native copy");
        equal("minecraft:bees", fn.getAsJsonArray("include").get(0).getAsString(), "loot include has native bees id");
    }

    private static JsonArray functions(String json, List<String> losses) {
        JsonArray one = new JsonArray();
        one.add(json(json));
        return LegacyLoot.functions(one, "1.20.4", "1.20.5", losses::add);
    }

    private static JsonObject stack(String nbt, List<String> losses) {
        return stack("minecraft:stone", nbt, losses);
    }

    private static JsonObject stack(String item, String nbt, List<String> losses) {
        JsonObject old = new JsonObject();
        old.addProperty("item", item);
        old.addProperty("nbt", nbt);
        return LegacyItems.stack(old, "1.20.5", losses::add);
    }

    private static byte[] bytes(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    private static JsonElement json(String value) {
        return JsonParser.parseString(value);
    }

    private static void equal(Object expected, Object actual, String what) {
        checks++;
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(what + "\nexpected: " + expected + "\nactual:   " + actual);
        }
    }

    private static void yes(boolean value, String what) {
        checks++;
        if (!value) {
            throw new AssertionError(what);
        }
    }

    private ResourceSemanticCheck() {
    }
}

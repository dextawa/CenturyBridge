package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import top.dext.centurybridge.step.v1_20_3.facade.DeserializationContext;

/**
 * The 1.20.2 JSON form of advancements and their parts -- Advancement, DisplayInfo,
 * AdvancementRewards, AdvancementRequirements, FrameType (1.20.3's AdvancementType) -- on 1.20.3,
 * where codecs replaced every one of these methods. Each bridge is the 1.20.2 body ported onto the
 * 1.20.3 API it used (GsonHelper, Component's serializer, the registries, ...), so a 1.20.2 caller
 * reads and writes exactly the JSON 1.20.2 did, errors included; and the 1.20.2 shapes of the
 * constructors and accessors whose types changed (arrays became lists, a nullable background an
 * Optional, the command function its 1.20.3 class).
 */
public final class CbAdvJson {

    /** DisplayInfo's 1.20.2 serializeToJson(), put back on the class by CbAdvDisplayInfoMixin (a cover) */
    public interface DisplayJson {
        JsonElement method_814();
    }

    /** AdvancementRewards' 1.20.2 serializeToJson() and getRecipes(), put back by CbAdvAdvancementRewardsMixin */
    public interface RewardsJson {
        JsonElement method_747();

        net.minecraft.class_2960[] method_34898();
    }

    // ---------------------------------------------------------------- Advancement (class_161)

    /** Advancement.serializeToJson() */
    public static JsonObject advancementToJson(net.minecraft.class_161 advancement) {
        JsonObject json = new JsonObject();
        advancement.comp_1912().ifPresent(parent -> json.addProperty("parent", parent.toString()));
        advancement.comp_1913().ifPresent(display -> json.add("display", ((DisplayJson) display).method_814()));
        json.add("rewards", ((RewardsJson) (Object) advancement.comp_1914()).method_747());
        JsonObject criteria = new JsonObject();
        for (Map.Entry<String, net.minecraft.class_175<?>> e : advancement.comp_1915().entrySet()) {
            criteria.add(e.getKey(), CbAdvCriteria.criterionToJson(e.getValue()));
        }
        json.add("criteria", criteria);
        json.add("requirements", requirementsToJson(advancement.comp_1916()));
        json.addProperty("sends_telemetry_event", advancement.comp_1917());
        return json;
    }

    /** Advancement.fromJson(json, context) */
    public static net.minecraft.class_161 advancementFromJson(JsonObject json, DeserializationContext context) {
        Optional<net.minecraft.class_2960> parent = json.has("parent")
            ? Optional.of(new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, "parent"))) : Optional.empty();
        Optional<net.minecraft.class_185> display = json.has("display")
            ? Optional.of(displayFromJson(net.minecraft.class_3518.method_15296(json, "display"))) : Optional.empty();
        net.minecraft.class_170 rewards = json.has("rewards")
            ? rewardsFromJson(net.minecraft.class_3518.method_15296(json, "rewards")) : net.minecraft.class_170.field_1167;
        Map<String, net.minecraft.class_175<?>> criteria = CbAdvCriteria.criteriaFromJson(
            net.minecraft.class_3518.method_15296(json, "criteria"), context);
        if (criteria.isEmpty()) {
            throw new JsonSyntaxException("Advancement criteria cannot be empty");
        }
        JsonArray requirementsJson = net.minecraft.class_3518.method_15292(json, "requirements", new JsonArray());
        net.minecraft.class_8782 requirements = requirementsJson.isEmpty()
            ? net.minecraft.class_8782.method_53668(criteria.keySet())
            : requirementsFromJson(requirementsJson, criteria.keySet());
        boolean telemetry = net.minecraft.class_3518.method_15258(json, "sends_telemetry_event", false);
        return new net.minecraft.class_161(parent, display, rewards, criteria, requirements, telemetry);
    }

    // ---------------------------------------------------------------- AdvancementRequirements (class_8782)

    /** new AdvancementRequirements(String[][]): the arrays as the lists 1.20.3 holds */
    public static List<List<String>> requirementsList(String[][] requirements) {
        List<List<String>> out = new ArrayList<>(requirements.length);
        for (String[] alternatives : requirements) {
            out.add(List.of(alternatives));
        }
        return out;
    }

    /**
     * AdvancementRequirements.requirements() as 1.20.2's String[][] (1.20.3 holds immutable lists:
     * the array is a copy of them, not the record's own storage).
     */
    public static String[][] requirementsArray(List<List<String>> requirements) {
        String[][] out = new String[requirements.size()][];
        for (int i = 0; i < out.length; i++) {
            out[i] = requirements.get(i).toArray(new String[0]);
        }
        return out;
    }

    /** AdvancementRequirements.fromJson(json, criteria) */
    public static net.minecraft.class_8782 requirementsFromJson(JsonArray json, Set<String> criteria) {
        String[][] requirements = new String[json.size()][];
        Set<String> seen = new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<>();
        for (int i = 0; i < json.size(); i++) {
            JsonArray alternatives = net.minecraft.class_3518.method_15252(json.get(i), "requirements[" + i + "]");
            if (alternatives.isEmpty() && criteria.isEmpty()) {
                throw new JsonSyntaxException("Requirement entry cannot be empty");
            }
            requirements[i] = new String[alternatives.size()];
            for (int j = 0; j < alternatives.size(); j++) {
                String name = net.minecraft.class_3518.method_15287(alternatives.get(j), "requirements[" + i + "][" + j + "]");
                requirements[i][j] = name;
                seen.add(name);
            }
        }
        if (!criteria.equals(seen)) {
            Set<String> missing = com.google.common.collect.Sets.difference(criteria, seen);
            Set<String> unknown = com.google.common.collect.Sets.difference(seen, criteria);
            throw new JsonSyntaxException("Advancement completion requirements did not exactly match specified criteria. Missing: "
                + missing + ". Unknown: " + unknown);
        }
        return new net.minecraft.class_8782(requirementsList(requirements));
    }

    /** AdvancementRequirements.toJson() */
    public static JsonArray requirementsToJson(net.minecraft.class_8782 requirements) {
        JsonArray json = new JsonArray();
        for (List<String> alternatives : requirements.comp_1922()) {
            JsonArray array = new JsonArray();
            alternatives.forEach(array::add);
            json.add(array);
        }
        return json;
    }

    // ---------------------------------------------------------------- AdvancementRewards (class_170)

    /** new AdvancementRewards(experience, loot[], recipes[], function): the arrays as lists, the function as 1.20.3's */
    public static List<net.minecraft.class_2960> locations(net.minecraft.class_2960[] ids) {
        return List.of(ids);
    }

    /**
     * A 1.20.2 CacheableFunction as 1.20.3's AdvancementRewards holds its function: absent for
     * NONE (no id) or null, else a 1.20.3 CacheableFunction of the same id -- resolved by id against
     * the server's functions when the reward runs, as 1.20.2's resolved it.
     */
    public static Optional<net.minecraft.class_2159> function(
            top.dext.centurybridge.step.v1_20_3.facade.class_2158$class_2159 function) {
        net.minecraft.class_2960 id = function == null ? null : function.method_9197();
        return id == null ? Optional.empty() : Optional.of(new net.minecraft.class_2159(id));
    }

    /** AdvancementRewards.getRecipes(), the cover's body */
    public static net.minecraft.class_2960[] recipes(net.minecraft.class_170 rewards) {
        return rewards.comp_2027().toArray(new net.minecraft.class_2960[0]);
    }

    /** AdvancementRewards.deserialize(json) */
    public static net.minecraft.class_170 rewardsFromJson(JsonObject json) throws JsonParseException {
        int experience = net.minecraft.class_3518.method_15282(json, "experience", 0);
        JsonArray lootJson = net.minecraft.class_3518.method_15292(json, "loot", new JsonArray());
        net.minecraft.class_2960[] loot = new net.minecraft.class_2960[lootJson.size()];
        for (int i = 0; i < loot.length; i++) {
            loot[i] = new net.minecraft.class_2960(net.minecraft.class_3518.method_15287(lootJson.get(i), "loot[" + i + "]"));
        }
        JsonArray recipesJson = net.minecraft.class_3518.method_15292(json, "recipes", new JsonArray());
        net.minecraft.class_2960[] recipes = new net.minecraft.class_2960[recipesJson.size()];
        for (int i = 0; i < recipes.length; i++) {
            recipes[i] = new net.minecraft.class_2960(net.minecraft.class_3518.method_15287(recipesJson.get(i), "recipes[" + i + "]"));
        }
        Optional<net.minecraft.class_2159> function = json.has("function")
            ? Optional.of(new net.minecraft.class_2159(new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, "function"))))
            : Optional.empty();
        return new net.minecraft.class_170(experience, locations(loot), locations(recipes), function);
    }

    /** AdvancementRewards.serializeToJson(), the cover's body: JSON null for EMPTY, else the non-default parts */
    public static JsonElement rewardsToJson(net.minecraft.class_170 rewards) {
        if (rewards == net.minecraft.class_170.field_1167) {
            return JsonNull.INSTANCE;
        }
        JsonObject json = new JsonObject();
        if (rewards.comp_2025() != 0) {
            json.addProperty("experience", rewards.comp_2025());
        }
        if (!rewards.comp_2026().isEmpty()) {
            JsonArray loot = new JsonArray();
            rewards.comp_2026().forEach(id -> loot.add(id.toString()));
            json.add("loot", loot);
        }
        if (!rewards.comp_2027().isEmpty()) {
            JsonArray recipes = new JsonArray();
            rewards.comp_2027().forEach(id -> recipes.add(id.toString()));
            json.add("recipes", recipes);
        }
        rewards.comp_2028().ifPresent(f -> json.addProperty("function", f.method_9197().toString()));
        return json;
    }

    /**
     * AdvancementRewards' codec, encoding a subclass the way 1.20.2 wrote rewards: through the
     * instance's serializeToJson (a virtual call, so a subclass's override wrote its own form).
     * Vanilla's own instances keep 1.20.3's encoding; decoding is unchanged.
     */
    public static Codec<net.minecraft.class_170> rewardsCodec(Codec<net.minecraft.class_170> codec) {
        return overridable(codec, net.minecraft.class_170.class, r -> ((RewardsJson) (Object) r).method_747());
    }

    // ---------------------------------------------------------------- DisplayInfo (class_185)

    /** new DisplayInfo(.., background, ..): 1.20.2's nullable background as 1.20.3's Optional */
    public static Optional<net.minecraft.class_2960> background(net.minecraft.class_2960 background) {
        return Optional.ofNullable(background);
    }

    /** DisplayInfo.getBackground(): 1.20.3's Optional as 1.20.2's nullable background */
    public static net.minecraft.class_2960 backgroundOrNull(Optional<net.minecraft.class_2960> background) {
        return background.orElse(null);
    }

    /** DisplayInfo.fromJson(json) */
    public static net.minecraft.class_185 displayFromJson(JsonObject json) {
        net.minecraft.class_2561 title = net.minecraft.class_2561.class_2562.method_10872(json.get("title"));
        net.minecraft.class_2561 description = net.minecraft.class_2561.class_2562.method_10872(json.get("description"));
        if (title == null || description == null) {
            throw new JsonSyntaxException("Both title and description must be set");
        }
        net.minecraft.class_1799 icon = icon(net.minecraft.class_3518.method_15296(json, "icon"));
        net.minecraft.class_2960 background = json.has("background")
            ? new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, "background")) : null;
        net.minecraft.class_189 frame = json.has("frame")
            ? frameByName(net.minecraft.class_3518.method_15265(json, "frame")) : net.minecraft.class_189.field_1254;
        boolean showToast = net.minecraft.class_3518.method_15258(json, "show_toast", true);
        boolean announceChat = net.minecraft.class_3518.method_15258(json, "announce_to_chat", true);
        boolean hidden = net.minecraft.class_3518.method_15258(json, "hidden", false);
        return new net.minecraft.class_185(icon, title, description, Optional.ofNullable(background), frame,
            showToast, announceChat, hidden);
    }

    /** 1.20.2 DisplayInfo.getIcon(json) (private): an item, its NBT from a string, no "data" */
    private static net.minecraft.class_1799 icon(JsonObject json) {
        if (!json.has("item")) {
            throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
        }
        net.minecraft.class_6880<net.minecraft.class_1792> item = net.minecraft.class_3518.method_15288(json, "item");
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        net.minecraft.class_1799 stack = new net.minecraft.class_1799(item);
        if (json.has("nbt")) {
            try {
                stack.method_7980(net.minecraft.class_2522.method_10718(net.minecraft.class_3518.method_15287(json.get("nbt"), "nbt")));
            } catch (CommandSyntaxException e) {
                throw new JsonSyntaxException("Invalid nbt tag: " + e.getMessage());
            }
        }
        return stack;
    }

    /** DisplayInfo.serializeToJson(), the cover's body */
    public static JsonElement displayToJson(net.minecraft.class_185 display) {
        JsonObject json = new JsonObject();
        JsonObject icon = new JsonObject();
        icon.addProperty("item", net.minecraft.class_7923.field_41178.method_10221(display.method_821().method_7909()).toString());
        if (display.method_821().method_7985()) {
            icon.addProperty("nbt", display.method_821().method_7969().toString());
        }
        json.add("icon", icon);
        json.add("title", net.minecraft.class_2561.class_2562.method_10868(display.method_811()));
        json.add("description", net.minecraft.class_2561.class_2562.method_10868(display.method_817()));
        json.addProperty("frame", frameName(display.method_815()));
        json.addProperty("show_toast", display.method_823());
        json.addProperty("announce_to_chat", display.method_808());
        json.addProperty("hidden", display.method_824());
        display.method_812().ifPresent(background -> json.addProperty("background", background.toString()));
        return json;
    }

    /** DisplayInfo's codec, encoding a subclass through its serializeToJson, as 1.20.2 wrote displays (see rewardsCodec) */
    public static Codec<net.minecraft.class_185> displayCodec(Codec<net.minecraft.class_185> codec) {
        return overridable(codec, net.minecraft.class_185.class, d -> ((DisplayJson) d).method_814());
    }

    // ---------------------------------------------------------------- FrameType / AdvancementType (class_189)

    /** FrameType.getName(): 1.20.3's serialized name, the same string */
    public static String frameName(net.minecraft.class_189 frame) {
        return frame.method_15434();
    }

    /** FrameType.byName(name) */
    public static net.minecraft.class_189 frameByName(String name) {
        for (net.minecraft.class_189 frame : net.minecraft.class_189.values()) {
            if (frame.method_15434().equals(name)) {
                return frame;
            }
        }
        throw new IllegalArgumentException("Unknown frame type '" + name + "'");
    }

    // ----------------------------------------------------------------

    /** encode instances of a subclass of {@code type} through their own 1.20.2 JSON method, the rest as {@code codec} does */
    private static <T> Codec<T> overridable(Codec<T> codec, Class<T> type, java.util.function.Function<T, JsonElement> legacy) {
        return Codec.of(new com.mojang.serialization.Encoder<T>() {
            @Override
            public <O> DataResult<O> encode(T value, DynamicOps<O> ops, O prefix) {
                if (value.getClass() != type) {
                    return net.minecraft.class_5699.field_40721.encode(legacy.apply(value), ops, prefix);
                }
                return codec.encode(value, ops, prefix);
            }
        }, new com.mojang.serialization.Decoder<T>() {
            @Override
            public <I> DataResult<Pair<T, I>> decode(DynamicOps<I> ops, I input) {
                return codec.decode(ops, input);
            }
        });
    }

    private CbAdvJson() {
    }
}

package top.dext.centurybridge.step.v1_20_5.rt;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.class_1208;
import net.minecraft.class_2561;
import net.minecraft.class_3579;
import net.minecraft.class_5250;

/**
 * Datafixer pieces 1.20.5 removed or reshaped, for 1.20.4 mods that build their own fixers from
 * vanilla's (area s4fin). Bodies follow 1.20.4's bytecode; where 1.20.5 folded a 1.20.4 class into
 * another the call lands on the class that now does the work.
 */
public final class CbFinDatafix {

    private CbFinDatafix() {
    }

    // ------------------------------------------------------------------ BlockEntitySignTextStrictJsonFix.GSON

    private static volatile Gson signGson;

    /**
     * BlockEntitySignTextStrictJsonFix.GSON (1.20.5 dropped the field): 1.20.4's lenient Component
     * reader -- a JSON primitive is a literal of its string, an array the first element with the
     * rest appended, anything else a parse error.
     */
    public static Gson field_15827() {
        Gson g = signGson;
        if (g == null) {
            g = new GsonBuilder().registerTypeAdapter(class_2561.class, new LenientComponent()).create();
            signGson = g;
        }
        return g;
    }

    private static final class LenientComponent implements JsonDeserializer<class_2561> {
        @Override
        public class_5250 deserialize(JsonElement json, java.lang.reflect.Type type,
                com.google.gson.JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return class_2561.method_43470(json.getAsString());
            }
            if (json.isJsonArray()) {
                class_5250 first = null;
                for (JsonElement e : json.getAsJsonArray()) {
                    class_5250 part = deserialize(e, e.getClass(), context);
                    if (first == null) {
                        first = part;
                    } else {
                        first.method_10852(part);
                    }
                }
                return first;
            }
            throw new JsonParseException("Don't know how to turn " + json + " into a Component");
        }
    }

    // ------------------------------------------------------------------ AttributesRename

    /** 1.20.4's AttributesRename table (keys and values without a namespace, as 1.20.4 read and wrote them) */
    private static final Map<String, String> ATTRIBUTE_RENAMES_1204 = ImmutableMap.<String, String>builder()
        .put("generic.maxHealth", "generic.max_health")
        .put("Max Health", "generic.max_health")
        .put("zombie.spawnReinforcements", "zombie.spawn_reinforcements")
        .put("Spawn Reinforcements Chance", "zombie.spawn_reinforcements")
        .put("horse.jumpStrength", "horse.jump_strength")
        .put("Jump Strength", "horse.jump_strength")
        .put("generic.followRange", "generic.follow_range")
        .put("Follow Range", "generic.follow_range")
        .put("generic.knockbackResistance", "generic.knockback_resistance")
        .put("Knockback Resistance", "generic.knockback_resistance")
        .put("generic.movementSpeed", "generic.movement_speed")
        .put("Movement Speed", "generic.movement_speed")
        .put("generic.flyingSpeed", "generic.flying_speed")
        .put("Flying Speed", "generic.flying_speed")
        .put("generic.attackDamage", "generic.attack_damage")
        .put("generic.attackKnockback", "generic.attack_knockback")
        .put("generic.attackSpeed", "generic.attack_speed")
        .put("generic.armorToughness", "generic.armor_toughness")
        .build();

    /** new AttributesRename(schema): 1.20.5 takes the fix's name (1.20.5's own for this fix) ... */
    public static String cbAttributeRenamesName() {
        return "Attribute renames";
    }

    /** ... and the renamer: 1.20.4's table, a name not in it unchanged (1.20.4's getOrDefault(name, name)) */
    public static UnaryOperator<String> cbAttributeRenames1204() {
        return name -> ATTRIBUTE_RENAMES_1204.getOrDefault(name, name);
    }

    // ------------------------------------------------------------------ BlockRenameFixWithJigsaw

    /**
     * BlockRenameFixWithJigsaw.create(schema, name, renamer): 1.20.4's subclass of BlockRenameFix that
     * also renamed the block in a jigsaw's final_state. 1.20.5's BlockRenameFix renames flat block
     * states (the type 1.20.5's schemas give final_state) itself, so the class is folded into it
     * (classRenames) and its factory is BlockRenameFix.create.
     */
    public static DataFix method_31446(Schema schema, String name, Function<String, String> renamer) {
        return class_3579.method_15589(schema, name, renamer);
    }

    // ------------------------------------------------------------------ schema statics

    /** V1928.equine(schema), 1.20.4's body */
    public static TypeTemplate method_17997(Schema schema) {
        return DSL.optionalFields("ArmorItems", DSL.list(class_1208.field_5712.in(schema)),
            "HandItems", DSL.list(class_1208.field_5712.in(schema)));
    }

    /** V3083.registerMob(schema, map, name), 1.20.4's body (equipment plus the vibration listener's game event) */
    public static void method_42645(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> DSL.optionalFields(
            "ArmorItems", DSL.list(class_1208.field_5712.in(schema)),
            "HandItems", DSL.list(class_1208.field_5712.in(schema)),
            "listener", DSL.optionalFields("event", DSL.optionalFields("game_event", class_1208.field_38380.in(schema)))));
    }

    /** V702.registerMob(schema, map, name), 1.20.4's body (V99's equipment as 1.20.4 had it) */
    public static void method_5292(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> DSL.optionalFields("ArmorItems", DSL.list(class_1208.field_5712.in(schema)),
            "HandItems", DSL.list(class_1208.field_5712.in(schema))));
    }

    /**
     * V99.addNames(dynamic, blockEntityIds, armorStandName): 1.20.4 gave an armor stand item's
     * EntityTag the one entity id passed; 1.20.5 takes a map from item id to entity id, and this is
     * that map with 1.20.4's single entry.
     */
    public static Map<String, String> cbArmorStandEntityIds(String armorStandName) {
        return Map.of("minecraft:armor_stand", armorStandName);
    }

    // ------------------------------------------------------------------ EntityZombieSplitFix

    /** a 1.20.4 EntityZombieSplitFix subclass's getNewNameAndTag (the adapter the override hook adds) */
    public interface CbLegacyZombieSplit {
        Pair<String, Dynamic<?>> method_5164(String name, Dynamic<?> tag);
    }

    /**
     * 1.20.5 renames an entity through getNewNameAndTyped(name, typed); a 1.20.4 subclass answered
     * getNewNameAndTag(name, tag). Bridged the way 1.20.5's own SimpleEntityRenameFix bridges the
     * two: the typed's remainder is the tag, and the answer's tag becomes its remainder.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Pair<String, Typed<?>> cbZombieSplit(CbLegacyZombieSplit fix, String name, Typed<?> typed) {
        Dynamic<?> tag = (Dynamic<?>) typed.getOrCreate(DSL.remainderFinder());
        Pair<String, Dynamic<?>> answer = fix.method_5164(name, tag);
        return Pair.of(answer.getFirst(), ((Typed) typed).set(DSL.remainderFinder(), answer.getSecond()));
    }

    // ------------------------------------------------------------------ VillagerTradeFix

    /** a 1.20.4 VillagerTradeFix subclass's fix(typed) */
    public interface CbLegacyVillagerTrade {
        Typed<?> method_5105(Typed<?> typed);
    }

    /** VillagerTradeFix's 1.20.4 rule shape (CbFinVillagerTradeMixin): NamedEntityFix's over a fix function */
    public interface CbVillagerTradeRule {
        TypeRewriteRule cb$namedEntityRule(Function<Typed<?>, Typed<?>> fix);
    }

    /**
     * 1.20.5's VillagerTradeFix fixes every villager trade type-wide (makeRule); 1.20.4's was a
     * NamedEntityFix on minecraft:villager whose fix(typed) a subclass overrode. A 1.20.4 subclass's
     * makeRule is that NamedEntityFix rule over its own fix.
     */
    public static TypeRewriteRule cbVillagerTradeRule(CbLegacyVillagerTrade fix) {
        return ((CbVillagerTradeRule) fix).cb$namedEntityRule(fix::method_5105);
    }
}

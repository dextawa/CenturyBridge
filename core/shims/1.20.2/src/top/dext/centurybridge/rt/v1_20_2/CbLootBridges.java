package top.dext.centurybridge.rt.v1_20_2;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The 1.20.1 loot API calls 1.20.2 changed in place, answered on the 1.20.2 API. Static redirects
 * and instance redirects land on the same-named methods here; the call / constructor reshapes and
 * override hooks of the s1loot wiring take their converters from here (each named for what it
 * converts).
 *
 * Most of 1.20.2's change in this area is mechanical: every array of conditions, functions,
 * entries became a List, and a nullable argument became an Optional. 1.20.1 handed arrays around
 * that the callee kept (a function's conditions, an entry's children); the conversions below copy
 * them into the immutable lists 1.20.2's own codecs produce, and back into fresh arrays for a
 * 1.20.1 reader. A 1.20.1 caller mutating such an array after the call was not reading anything
 * the game looked at again (the 1.20.1 classes compiled their composite predicate / function at
 * construction), so the copy loses nothing it saw.
 */
public final class CbLootBridges {

    private CbLootBridges() {
    }

    // ------------------------------------------------------------------ arrays <-> lists (reshape converters)

    public static List<net.minecraft.class_5341> conditionList(net.minecraft.class_5341[] conditions) {
        return List.of(conditions);
    }

    public static List<net.minecraft.class_117> functionList(net.minecraft.class_117[] functions) {
        return List.of(functions);
    }

    public static List<net.minecraft.class_79> entryList(net.minecraft.class_79[] entries) {
        return List.of(entries);
    }

    /** LootConditionTypes.and / or (Predicate<T>... -> List): any predicates, as 1.20.1 took */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List predicateList(Predicate[] predicates) {
        return Arrays.asList(predicates);
    }

    /** LootFunctionTypes.compose (BiFunction... -> List) */
    @SuppressWarnings("rawtypes")
    public static List biFunctionList(BiFunction[] functions) {
        return Arrays.asList(functions);
    }

    public static net.minecraft.class_5341[] conditionArray(List<net.minecraft.class_5341> conditions) {
        return conditions.toArray(new net.minecraft.class_5341[0]);
    }

    public static net.minecraft.class_117[] functionArray(List<net.minecraft.class_117> functions) {
        return functions.toArray(new net.minecraft.class_117[0]);
    }

    /**
     * CompositeEntryBase.compose: 1.20.2 hands an override the List of the entry's children, a
     * 1.20.1 override takes them as EntryCombiner[]. 1.20.1 passed the entry's own LootPoolEntry[]
     * (an EntryCombiner[] by array covariance); this passes the same: the children are
     * LootPoolEntries in both releases. (EntryCombiner, class_64, is package-private -- not
     * nameable here, nor needed: the verifier takes a LootPoolEntry[] where an array of an
     * interface type it implements is expected.)
     */
    public static net.minecraft.class_79[] combinerArray(List<?> children) {
        return children.toArray(new net.minecraft.class_79[0]);
    }

    /** a 1.20.1 getConditions() / getFunctions() answer, as the List 1.20.2's builders read */
    public static List<net.minecraft.class_5341> conditionListOf(net.minecraft.class_5341[] conditions) {
        return List.of(conditions);
    }

    // ------------------------------------------------------------------ nullable -> Optional

    /** LootContext.Builder.build(@Nullable Identifier randomSequence) */
    public static Optional<net.minecraft.class_2960> optionalId(net.minecraft.class_2960 id) {
        return Optional.ofNullable(id);
    }

    /** a 1.20.1 override of build(Identifier) handed what vanilla now passes */
    public static net.minecraft.class_2960 idOrNull(Optional<net.minecraft.class_2960> id) {
        return id.orElse(null);
    }

    /** SetLoreFunction(..., @Nullable EntityTarget entity) */
    public static Optional<net.minecraft.class_47.class_50> optionalTarget(net.minecraft.class_47.class_50 target) {
        return Optional.ofNullable(target);
    }

    /**
     * MatchTool(ItemPredicate): 1.20.1 had no "absent" predicate but ItemPredicate.ANY, whose
     * test() passes everything; 1.20.2 says the same with an empty Optional. A predicate equal to
     * the match-all one (an empty builder's) is that ANY.
     */
    public static Optional<net.minecraft.class_2073> optionalItemPredicate(net.minecraft.class_2073 predicate) {
        if (predicate == null || predicate.equals(net.minecraft.class_2073.class_2074.method_8973().method_8976())) {
            return Optional.empty();
        }
        return Optional.of(predicate);
    }

    // ------------------------------------------------------------------ Attribute -> Holder<Attribute>

    /** SetAttributesFunction.modifier / ModifierBuilder(name, Attribute, ...): the attribute's registry entry */
    public static net.minecraft.class_6880<net.minecraft.class_1320> attributeHolder(net.minecraft.class_1320 attribute) {
        return net.minecraft.class_7923.field_41190.method_47983(attribute);
    }

    // ------------------------------------------------------------------ the generic builder

    /**
     * LootItemConditionalFunction.simpleBuilder(Function<LootCondition[], LootFunction>): the
     * descriptor is unchanged (a Function), the argument is not -- 1.20.2's builder applies the
     * function to a List. The 1.20.1 function gets the array it was written for.
     */
    public static Function<List<net.minecraft.class_5341>, net.minecraft.class_117> conditionsFunction(
            Function<net.minecraft.class_5341[], net.minecraft.class_117> legacy) {
        return conditions -> legacy.apply(conditions.toArray(new net.minecraft.class_5341[0]));
    }

    // ------------------------------------------------------------------ dead statics

    /** LootConditionTypes.createGsonSerializer() */
    public static Object method_29326() {
        return CbLootGson.conditions();
    }

    /** LootFunctionTypes.createGsonSerializer() */
    public static Object method_29322() {
        return CbLootGson.functions();
    }

    /** LootPoolEntryTypes.createGsonSerializer() */
    public static Object method_29316() {
        return CbLootGson.entries();
    }

    /** LootNumberProviderTypes.createGsonSerializer() */
    public static Object method_32455() {
        return CbLootGson.numberProviders();
    }

    /** LootNbtProviderTypes.createGsonSerializer() */
    public static Object method_32442() {
        return CbLootGson.nbtProviders();
    }

    /** LootScoreProviderTypes.createGsonSerializer() */
    public static Object method_32478() {
        return CbLootGson.scoreProviders();
    }

    /** LootContextTypes.get(id): the id -> type map 1.20.2 still keeps, private */
    public static net.minecraft.class_176 method_757(net.minecraft.class_2960 id) {
        return top.dext.centurybridge.shims.v1_20_2.CbLootContextTypesAccessor.cbRegistry().get(id);
    }

    /** CopyNameFunction.Source.byName(name), as 1.20.1 looked it up */
    public static net.minecraft.class_101.class_102 method_475(String name) {
        for (net.minecraft.class_101.class_102 source : net.minecraft.class_101.class_102.values()) {
            if (source.method_15434().equals(name)) {
                return source;
            }
        }
        throw new IllegalArgumentException("Invalid name source " + name);
    }

    /** CopyNbtFunction.MergeStrategy.byName(name), as 1.20.1 looked it up */
    public static net.minecraft.class_3837.class_3841 method_16865(String name) {
        for (net.minecraft.class_3837.class_3841 strategy : net.minecraft.class_3837.class_3841.values()) {
            if (strategy.method_15434().equals(name)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Invalid merge strategy" + name);
    }

    /** LootDataManager.createComposite(LootCondition[]) */
    public static net.minecraft.class_5341 method_51201(net.minecraft.class_5341[] terms) {
        return CbLootComposites.method_51201(terms);
    }

    /** LootDataManager.createComposite(LootFunction[]) */
    public static net.minecraft.class_117 method_51200(net.minecraft.class_117[] functions) {
        return CbLootComposites.method_51200(functions);
    }

    /**
     * CompositeEntryBase.createSerializer(factory): the 1.20.1 serializer of a composite entry,
     * ported (its anonymous class): "children" written from the entry, read as LootPoolEntry[]
     * through the caller's context and handed with the conditions to the factory.
     */
    public static <T extends net.minecraft.class_69> top.dext.centurybridge.facade.v1_20_2.class_79$class_5337<T> method_395(
            top.dext.centurybridge.facade.v1_20_2.class_69$class_70<T> factory) {
        return new top.dext.centurybridge.facade.v1_20_2.class_79$class_5337<T>() {
            @Override
            public void method_422(com.google.gson.JsonObject json, T entry, com.google.gson.JsonSerializationContext context) {
                List<net.minecraft.class_79> children =
                    ((top.dext.centurybridge.shims.v1_20_2.CbLootCompositeEntryChildren) entry).cb$children();
                json.add("children", context.serialize(children.toArray(new net.minecraft.class_79[0])));
            }

            @Override
            public T method_424(com.google.gson.JsonObject json, com.google.gson.JsonDeserializationContext context,
                    net.minecraft.class_5341[] conditions) {
                net.minecraft.class_79[] children = (net.minecraft.class_79[]) net.minecraft.class_3518.method_15272(json,
                    "children", context, net.minecraft.class_79[].class);
                return factory.create(children, conditions);
            }
        };
    }

    /** LeafEntry.builder(factory) with a 1.20.1 factory (arrays) -- see CbLootLeafAccess */
    public static net.minecraft.class_85.class_86<?> method_434(top.dext.centurybridge.facade.v1_20_2.class_85$class_89 factory) {
        return CbLootLeafAccess.builder(factory);
    }

    /** CompositeEntryBase.children (an array in 1.20.1), read by a mod's subclass */
    public static net.minecraft.class_79[] field_982(net.minecraft.class_69 self) {
        return ((top.dext.centurybridge.shims.v1_20_2.CbLootCompositeEntryChildren) self).cb$children()
            .toArray(new net.minecraft.class_79[0]);
    }

    /** LeafEntry.functions (an array in 1.20.1) */
    public static net.minecraft.class_117[] field_996(net.minecraft.class_85 self) {
        return ((top.dext.centurybridge.shims.v1_20_2.LeafEntryAccessor) self).cb$functions()
            .toArray(new net.minecraft.class_117[0]);
    }

    /** ConditionalLootFunction.conditions (an array in 1.20.1) */
    public static net.minecraft.class_5341[] field_1047(net.minecraft.class_120 self) {
        return ((top.dext.centurybridge.shims.v1_20_2.LootFunctionAccessor) self).cb$conditions()
            .toArray(new net.minecraft.class_5341[0]);
    }

    /** CompositeLootItemCondition.terms (an array in 1.20.1) */
    public static net.minecraft.class_5341[] field_1246(net.minecraft.class_186 self) {
        return ((top.dext.centurybridge.shims.v1_20_2.CbLootCompositeConditionTerms) self).cb$terms()
            .toArray(new net.minecraft.class_5341[0]);
    }

    // ------------------------------------------------------------------ WeatherCheck.Builder

    /**
     * setRaining(Boolean): 1.20.1 stored the Boolean, null meaning "either"; 1.20.2 takes a
     * boolean. A value goes through the 1.20.2 setter (so a mod subclass's override, reconnected
     * by the override hook, still sees the call); null leaves the check unset as 1.20.1 did.
     */
    public static net.minecraft.class_227.class_6165 method_35565(net.minecraft.class_227.class_6165 self, Boolean raining) {
        if (raining != null) {
            return self.method_35565(raining);
        }
        ((top.dext.centurybridge.shims.v1_20_2.CbWeatherCheckBuilderFields) self).cb$setRaining(Optional.empty());
        return self;
    }

    /** setThundering(Boolean), as setRaining */
    public static net.minecraft.class_227.class_6165 method_35567(net.minecraft.class_227.class_6165 self, Boolean thundering) {
        if (thundering != null) {
            return self.method_35567(thundering);
        }
        ((top.dext.centurybridge.shims.v1_20_2.CbWeatherCheckBuilderFields) self).cb$setThundering(Optional.empty());
        return self;
    }

    /** what a 1.20.1 override of setRaining(Boolean) receives from the 1.20.2 setter vanilla calls */
    public static Boolean boxed(boolean value) {
        return value;
    }

    // ------------------------------------------------------------------ constants

    /** ExplorationMapFunction.DEFAULT_DECORATION_NAME: removed in 1.20.2, the 1.20.1 constant */
    public static final String field_31850 = "mansion";
}

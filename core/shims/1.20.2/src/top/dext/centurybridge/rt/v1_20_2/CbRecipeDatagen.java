package top.dext.centurybridge.rt.v1_20_2;

import com.google.gson.JsonObject;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import top.dext.centurybridge.res.LegacyAdvancements;
import top.dext.centurybridge.res.LegacyItems;

/**
 * 1.20.1 data generation (net/minecraft/data) on 1.20.2's.
 *
 * <ul>
 * <li>Recipes left the Consumer&lt;RecipeJsonProvider&gt; for a RecipeExporter (accept + the
 * advancement builder every recipe's unlock advancement starts from). The two are converted into
 * each other, each conversion unwrapping the other so a value that crosses twice is the original.
 * The exporter made from a 1.20.1 consumer starts its advancements as 1.20.1's builders did and
 * 1.20.2's RecipeProvider does: untelemetered, parent recipes/root.</li>
 * <li>A recipe's unlock criterion is an AdvancementCriterion (trigger + conditions) instead of bare
 * conditions, whose trigger 1.20.1 read off the conditions' own id. A criterion this layer took
 * apart is put back together from what it remembered; otherwise the trigger is the one criterion
 * registered for the conditions' class.</li>
 * <li>RecipeJsonProvider's getters became the record-style id() / type() / advancement(): old calls
 * read the new ones, and a 1.20.1 implementation answers the new ones from its own (the hooks).</li>
 * <li>Advancements are handed around as AdvancementEntry (id + value) instead of Advancement: the
 * shared Trackers map records which entry an advancement value belongs to.</li>
 * </ul>
 */
public final class CbRecipeDatagen {

    private CbRecipeDatagen() {
    }

    // ------------------------------------------------------------------ Consumer<RecipeJsonProvider> <-> RecipeExporter

    /** a RecipeExporter over a 1.20.1 consumer */
    static final class ExporterOfConsumer implements net.minecraft.class_8790 {
        final Consumer<net.minecraft.class_2444> consumer;

        ExporterOfConsumer(Consumer<net.minecraft.class_2444> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void method_53819(net.minecraft.class_2444 recipe) {
            consumer.accept(recipe);
        }

        /** what 1.20.1's recipe builders started their advancement from, and 1.20.2's RecipeProvider hands out */
        @Override
        public net.minecraft.class_161.class_162 method_53818() {
            return net.minecraft.class_161.class_162.method_51698().method_708(net.minecraft.class_5797.field_39377);
        }
    }

    /** a 1.20.1 consumer over a RecipeExporter */
    static final class ConsumerOfExporter implements Consumer<net.minecraft.class_2444> {
        final net.minecraft.class_8790 exporter;

        ConsumerOfExporter(net.minecraft.class_8790 exporter) {
            this.exporter = exporter;
        }

        @Override
        public void accept(net.minecraft.class_2444 recipe) {
            exporter.method_53819(recipe);
        }
    }

    @SuppressWarnings("unchecked")
    public static net.minecraft.class_8790 exporterOf(Consumer<?> consumer) {
        if (consumer == null) {
            return null;
        }
        if (((Object) consumer) instanceof ConsumerOfExporter c) {
            return c.exporter;
        }
        return new ExporterOfConsumer((Consumer<net.minecraft.class_2444>) consumer);
    }

    public static Consumer<?> consumerOf(net.minecraft.class_8790 exporter) {
        if (exporter == null) {
            return null;
        }
        if (exporter instanceof ExporterOfConsumer e) {
            return e.consumer;
        }
        return new ConsumerOfExporter(exporter);
    }

    // ------------------------------------------------------------------ criteria

    /** criteria this layer took apart, by their conditions: putting one back together is exact */
    private static final Map<Object, net.minecraft.class_175<?>> CRITERIA =
        Collections.synchronizedMap(new IdentityHashMap<>());

    /** a 1.20.2 criterion handed to 1.20.1 code as bare conditions; remembered for the way back */
    public static net.minecraft.class_184 conditionsOf(net.minecraft.class_175<?> criterion) {
        if (criterion == null) {
            return null;
        }
        net.minecraft.class_184 conditions = criterion.comp_1924();
        CRITERIA.put(conditions, criterion);
        return conditions;
    }

    /** RecipeProvider.has(...): 1.20.1 returned the inventory-changed conditions themselves */
    public static net.minecraft.class_2066.class_2068 inventoryConditionsOf(net.minecraft.class_175<?> criterion) {
        return (net.minecraft.class_2066.class_2068) conditionsOf(criterion);
    }

    /**
     * 1.20.1 conditions as the criterion 1.20.2 wants. 1.20.1 named the trigger through the
     * conditions' getId(); 1.20.2 conditions no longer know it. A criterion this layer took apart
     * comes back whole; otherwise the trigger is the registered criterion whose conditions type is
     * the conditions' class -- unambiguous unless several registered criteria share one conditions
     * class (1.20.2's TickCriterion conditions serve five), which is reported, not guessed.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_175<?> criterionOf(net.minecraft.class_184 conditions) {
        if (conditions == null) {
            return null;
        }
        net.minecraft.class_175<?> known = CRITERIA.get(conditions);
        if (known != null) {
            return known;
        }
        // the registered trigger(s) taking the most specific conditions type the conditions are
        net.minecraft.class_179 best = null;
        Class<?> bestType = null;
        int ties = 0;
        for (net.minecraft.class_179<?> trigger : net.minecraft.class_174.method_766()) {
            Class<?> type = conditionsType(trigger.getClass());
            if (type == null || !type.isInstance(conditions)) {
                continue;
            }
            if (best == null || (type != bestType && bestType.isAssignableFrom(type))) {
                best = trigger;
                bestType = type;
                ties = 1;
            } else if (type == bestType) {
                ties++;
            }
        }
        if (best == null || ties != 1) {
            throw new IllegalStateException("CenturyBridge: no single criterion trigger for 1.20.1 conditions "
                + conditions.getClass().getName() + (best == null ? "" : " (" + ties + " registered triggers take them)"));
        }
        return new net.minecraft.class_175(best, conditions);
    }

    /** the conditions class a trigger takes: AbstractCriterion<T> / Criterion<T>'s argument */
    private static Class<?> conditionsType(Class<?> c) {
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
            Class<?> t = argOf(k.getGenericSuperclass(), net.minecraft.class_4558.class);
            if (t != null) {
                return t;
            }
            for (Type i : k.getGenericInterfaces()) {
                t = argOf(i, net.minecraft.class_179.class);
                if (t != null) {
                    return t;
                }
            }
        }
        return null;
    }

    private static Class<?> argOf(Type t, Class<?> raw) {
        if (t instanceof ParameterizedType p && p.getRawType() == raw && p.getActualTypeArguments().length == 1) {
            Type a = p.getActualTypeArguments()[0];
            if (a instanceof Class<?> k) {
                return k;
            }
            if (a instanceof ParameterizedType pa && pa.getRawType() instanceof Class<?> k) {
                return k;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ RecipeJsonProvider's 1.20.1 getters

    /** getRecipeId() */
    public static net.minecraft.class_2960 method_10417(net.minecraft.class_2444 self) {
        return self.comp_1230();
    }

    /** getSerializer() */
    public static net.minecraft.class_1865<?> method_17800(net.minecraft.class_2444 self) {
        return self.comp_1231();
    }

    /** toAdvancementJson(): null when the recipe has no advancement, as in 1.20.1 */
    public static JsonObject method_10415(net.minecraft.class_2444 self) {
        net.minecraft.class_8779 a = self.comp_1235();
        return a == null ? null : a.comp_1920().method_53621();
    }

    /** getAdvancementId() */
    public static net.minecraft.class_2960 method_10418(net.minecraft.class_2444 self) {
        net.minecraft.class_8779 a = self.comp_1235();
        return a == null ? null : a.comp_1919();
    }

    /**
     * A 1.20.1 RecipeJsonProvider implementation's advancement(), from its toAdvancementJson() and
     * getAdvancementId(): the JSON (1.20.1's, upgraded by the shared data-format rules) read back
     * into the advancement 1.20.2 writes out. A loot table reference inside it is not resolved here
     * (there is no loaded loot data at generation time) -- only reported, as 1.20.2's reader does.
     */
    public static net.minecraft.class_8779 advancement(CbLegacyRecipeAdvancement recipe) {
        JsonObject json = recipe.method_10415();
        if (json == null) {
            return null;
        }
        net.minecraft.class_2960 id = recipe.method_10418();
        JsonObject upgraded = LegacyAdvancements.upgrade(json, "1.20.1", "1.20.2", LegacyItems.Losses.NONE);
        net.minecraft.class_161 value = net.minecraft.class_161.method_53626(upgraded,
            new net.minecraft.class_5257(id, new net.minecraft.class_60()));
        return track(new net.minecraft.class_8779(id, value));
    }

    // ------------------------------------------------------------------ the recipe results' 1.20.1 constructors / accessors

    /** a result's (advancement builder, advancement id) as the AdvancementEntry 1.20.2 takes */
    public static net.minecraft.class_8779 advancementEntry(net.minecraft.class_161.class_162 builder, net.minecraft.class_2960 id) {
        return builder == null ? null : track(builder.method_695(id));
    }

    /**
     * The advancement builder an advancement was built from, as 1.20.1's accessors handed it out:
     * every component of the 1.20.2 record put back (parent, display, rewards, criteria in order,
     * requirements, the telemetry flag).
     */
    public static net.minecraft.class_161.class_162 builderOf(net.minecraft.class_161 advancement) {
        if (advancement == null) {
            return null;
        }
        net.minecraft.class_161.class_162 b = net.minecraft.class_161.class_162.method_51698();
        advancement.comp_1912().ifPresent(b::method_708);
        advancement.comp_1913().ifPresent(b::method_693);
        b.method_706(advancement.comp_1914());
        for (var e : advancement.comp_1915().entrySet()) {
            b.method_705(e.getKey(), e.getValue());
        }
        b.method_34884(advancement.comp_1916());
        if (advancement.comp_1917()) {
            b.method_53634();
        }
        return b;
    }

    /** SmithingTransformRecipeBuilder.Result.advancement() (the builder, 1.20.1) */
    public static net.minecraft.class_161.class_162 comp_1228(net.minecraft.class_8074.class_8075 self) {
        net.minecraft.class_8779 a = self.comp_1235();
        return a == null ? null : builderOf(a.comp_1920());
    }

    /** SmithingTransformRecipeBuilder.Result.advancementId() */
    public static net.minecraft.class_2960 comp_1229(net.minecraft.class_8074.class_8075 self) {
        net.minecraft.class_8779 a = self.comp_1235();
        return a == null ? null : a.comp_1919();
    }

    /** SmithingTrimRecipeBuilder.Result.advancement() (the builder, 1.20.1) */
    public static net.minecraft.class_161.class_162 comp_1235(net.minecraft.class_8076.class_8077 self) {
        net.minecraft.class_8779 a = self.comp_1235();
        return a == null ? null : builderOf(a.comp_1920());
    }

    /** SmithingTrimRecipeBuilder.Result.advancementId() */
    public static net.minecraft.class_2960 comp_1236(net.minecraft.class_8076.class_8077 self) {
        net.minecraft.class_8779 a = self.comp_1235();
        return a == null ? null : a.comp_1919();
    }

    /** SpecialRecipeBuilder.save(consumer, String id): 1.20.1 made the id with new Identifier(id) */
    public static net.minecraft.class_2960 idOf(String id) {
        return new net.minecraft.class_2960(id);
    }

    /** a 1.20.1 RecipeProvider.buildAdvancement override, handed what 1.20.2 passes */
    public static java.util.concurrent.CompletableFuture<?> buildAdvancement(CbLegacyRecipeAdvancementSaver self,
            net.minecraft.class_7403 output, net.minecraft.class_8779 entry) {
        return self.method_46206(output, entry.comp_1919(), builderOf(entry.comp_1920()));
    }

    /** a 1.20.1 RecipeProvider.buildAdvancement call: the builder built under the id, as 1.20.2 takes it */
    public static net.minecraft.class_8779 advancementEntryOf(net.minecraft.class_2960 id, net.minecraft.class_161.class_162 builder) {
        return builder == null ? null : track(builder.method_695(id));
    }

    /** a 1.20.1 SpecialRecipeBuilder.save(consumer, String) override, handed what 1.20.2 passes */
    public static void specialSave(CbLegacySpecialRecipeSave self, net.minecraft.class_8790 exporter, net.minecraft.class_2960 id) {
        self.method_10475(consumerOf(exporter), id.toString());
    }

    // ------------------------------------------------------------------ advancements: Advancement <-> AdvancementEntry

    /** remember which entry an advancement value belongs to (the shared Trackers map) */
    public static net.minecraft.class_8779 track(net.minecraft.class_8779 entry) {
        if (entry != null) {
            Trackers.ADVANCEMENT_ENTRIES.put(entry.comp_1920(), entry);
        }
        return entry;
    }

    /** an advancement value as its entry: only an advancement whose entry was seen can be one */
    public static net.minecraft.class_8779 entryOf(net.minecraft.class_161 advancement) {
        if (advancement == null) {
            return null;
        }
        net.minecraft.class_8779 entry = Trackers.ADVANCEMENT_ENTRIES.get(advancement);
        if (entry == null) {
            throw new IllegalStateException("CenturyBridge: this advancement has no known id -- 1.20.2 hands advancements"
                + " around as AdvancementEntry, and this one was not built or loaded where its id was recorded");
        }
        return entry;
    }

    /** an entry handed back to 1.20.1 code, which wants the advancement value */
    public static net.minecraft.class_161 valueOf(net.minecraft.class_8779 entry) {
        return entry == null ? null : track(entry).comp_1920();
    }

    /**
     * The advancement consumer a 1.20.1 generator passes to a 1.20.2 helper that wants a
     * Consumer&lt;AdvancementEntry&gt;. A consumer that came from 1.20.2 (the generator's own, see
     * CbAdvancementConsumers) already takes entries; a 1.20.1 one is handed the advancement value.
     */
    @SuppressWarnings("unchecked")
    public static Consumer<?> entryConsumer(Consumer<?> consumer) {
        if (consumer == null || ((Object) consumer) instanceof CbAdvancementConsumers.Dual) {
            return consumer;
        }
        Consumer<Object> legacy = (Consumer<Object>) consumer;
        return (Consumer<net.minecraft.class_8779>) entry -> legacy.accept(valueOf(entry));
    }

    /** AdvancementSubProvider.createPlaceholder(id), as 1.20.1 made it */
    public static net.minecraft.class_161 method_48496(String id) {
        return valueOf(net.minecraft.class_161.class_162.method_707().method_695(new net.minecraft.class_2960(id)));
    }
}

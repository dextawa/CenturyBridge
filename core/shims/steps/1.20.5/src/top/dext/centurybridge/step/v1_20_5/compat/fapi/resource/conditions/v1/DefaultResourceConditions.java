package top.dext.centurybridge.step.v1_20_5.compat.fapi.resource.conditions.v1;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * Fabric API 0.97.3's (1.20.4) DefaultResourceConditions, removed in 1.20.5: the JSON of Fabric's default
 * conditions, for data generators (bodies as in 1.20.4, Fabric API, Apache-2.0). The ids it writes are
 * 1.20.4's; those 1.20.5 renamed or dropped (any_mod_loaded, block/fluid/item_tags_populated) are read on
 * 1.20.5 by this layer's condition types of the same ids (impl/resource/conditions/CbLegacyConditions), the
 * others by Fabric API's own, which read the same parameters.
 */
public final class DefaultResourceConditions {

    private static final net.minecraft.class_2960 NOT = new net.minecraft.class_2960("fabric:not");
    private static final net.minecraft.class_2960 AND = new net.minecraft.class_2960("fabric:and");
    private static final net.minecraft.class_2960 OR = new net.minecraft.class_2960("fabric:or");
    private static final net.minecraft.class_2960 ALL_MODS_LOADED = new net.minecraft.class_2960("fabric:all_mods_loaded");
    private static final net.minecraft.class_2960 ANY_MOD_LOADED = new net.minecraft.class_2960("fabric:any_mod_loaded");
    private static final net.minecraft.class_2960 BLOCK_TAGS_POPULATED = new net.minecraft.class_2960("fabric:block_tags_populated");
    private static final net.minecraft.class_2960 FLUID_TAGS_POPULATED = new net.minecraft.class_2960("fabric:fluid_tags_populated");
    private static final net.minecraft.class_2960 ITEM_TAGS_POPULATED = new net.minecraft.class_2960("fabric:item_tags_populated");
    private static final net.minecraft.class_2960 TAGS_POPULATED = new net.minecraft.class_2960("fabric:tags_populated");
    private static final net.minecraft.class_2960 FEATURES_ENABLED = new net.minecraft.class_2960("fabric:features_enabled");
    private static final net.minecraft.class_2960 REGISTRY_CONTAINS = new net.minecraft.class_2960("fabric:registry_contains");

    private DefaultResourceConditions() {
    }

    public static ConditionJsonProvider not(ConditionJsonProvider value) {
        return new ConditionJsonProvider() {
            @Override
            public void writeParameters(JsonObject object) {
                object.add("value", value.toJson());
            }

            @Override
            public net.minecraft.class_2960 getConditionId() {
                return NOT;
            }
        };
    }

    public static ConditionJsonProvider and(ConditionJsonProvider... values) {
        return array(AND, values);
    }

    public static ConditionJsonProvider or(ConditionJsonProvider... values) {
        return array(OR, values);
    }

    public static ConditionJsonProvider allModsLoaded(String... modIds) {
        return mods(ALL_MODS_LOADED, modIds);
    }

    public static ConditionJsonProvider anyModLoaded(String... modIds) {
        return mods(ANY_MOD_LOADED, modIds);
    }

    @SafeVarargs
    @Deprecated
    public static ConditionJsonProvider blockTagsPopulated(net.minecraft.class_6862<net.minecraft.class_2248>... tags) {
        return tagsPopulated(BLOCK_TAGS_POPULATED, false, tags);
    }

    @SafeVarargs
    @Deprecated
    public static ConditionJsonProvider fluidTagsPopulated(net.minecraft.class_6862<net.minecraft.class_3611>... tags) {
        return tagsPopulated(FLUID_TAGS_POPULATED, false, tags);
    }

    @SafeVarargs
    @Deprecated
    public static ConditionJsonProvider itemTagsPopulated(net.minecraft.class_6862<net.minecraft.class_1792>... tags) {
        return tagsPopulated(ITEM_TAGS_POPULATED, false, tags);
    }

    @SafeVarargs
    public static <T> ConditionJsonProvider tagsPopulated(net.minecraft.class_6862<T>... tags) {
        return tagsPopulated(TAGS_POPULATED, true, tags);
    }

    public static ConditionJsonProvider featuresEnabled(net.minecraft.class_7696... features) {
        final Set<net.minecraft.class_2960> ids = new TreeSet<>(net.minecraft.class_7701.field_40180.method_45392(
            net.minecraft.class_7701.field_40180.method_45390(features)));
        return new ConditionJsonProvider() {
            @Override
            public net.minecraft.class_2960 getConditionId() {
                return FEATURES_ENABLED;
            }

            @Override
            public void writeParameters(JsonObject object) {
                JsonArray array = new JsonArray();
                for (net.minecraft.class_2960 id : ids) {
                    array.add(id.toString());
                }
                object.add("features", array);
            }
        };
    }

    public static ConditionJsonProvider itemsRegistered(net.minecraft.class_1935... items) {
        return registryContains(net.minecraft.class_7923.field_41178, transform(items, net.minecraft.class_1935::method_8389));
    }

    @SafeVarargs
    public static <T> ConditionJsonProvider registryContains(net.minecraft.class_2378<T> registry, T... entries) {
        return registryContains(transform(entries,
            e -> registry.method_29113(e).orElseThrow(() -> new IllegalArgumentException("Entry is not registered"))));
    }

    @SafeVarargs
    public static <T> ConditionJsonProvider registryContains(net.minecraft.class_5321<T>... entries) {
        Preconditions.checkArgument(entries.length > 0, "Must register at least one entry.");
        return registryContains(net.minecraft.class_5321.method_29180(entries[0].method_41185()),
            transform(entries, net.minecraft.class_5321::method_29177));
    }

    public static <T> ConditionJsonProvider registryContains(net.minecraft.class_5321<net.minecraft.class_2378<T>> registry,
                                                            net.minecraft.class_2960... entries) {
        Preconditions.checkArgument(entries.length > 0, "Must register at least one entry.");
        net.minecraft.class_2960 registryId = registry.method_29177();
        return new ConditionJsonProvider() {
            @Override
            public net.minecraft.class_2960 getConditionId() {
                return REGISTRY_CONTAINS;
            }

            @Override
            public void writeParameters(JsonObject object) {
                JsonArray array = new JsonArray();
                for (net.minecraft.class_2960 entry : entries) {
                    array.add(entry.toString());
                }
                object.add("values", array);
                if (!net.minecraft.class_7924.field_41197.method_29177().equals(registryId)) {
                    object.addProperty("registry", registryId.toString());
                }
            }
        };
    }

    // 1.20.4's ResourceConditionsImpl providers

    private static ConditionJsonProvider array(net.minecraft.class_2960 id, ConditionJsonProvider... values) {
        Preconditions.checkArgument(values.length > 0, "Must register at least one value.");
        return new ConditionJsonProvider() {
            @Override
            public net.minecraft.class_2960 getConditionId() {
                return id;
            }

            @Override
            public void writeParameters(JsonObject object) {
                JsonArray array = new JsonArray();
                for (ConditionJsonProvider provider : values) {
                    array.add(provider.toJson());
                }
                object.add("values", array);
            }
        };
    }

    private static ConditionJsonProvider mods(net.minecraft.class_2960 id, String... modIds) {
        Preconditions.checkArgument(modIds.length > 0, "Must register at least one mod id.");
        return new ConditionJsonProvider() {
            @Override
            public net.minecraft.class_2960 getConditionId() {
                return id;
            }

            @Override
            public void writeParameters(JsonObject object) {
                JsonArray array = new JsonArray();
                for (String modId : modIds) {
                    array.add(modId);
                }
                object.add("values", array);
            }
        };
    }

    @SafeVarargs
    private static <T> ConditionJsonProvider tagsPopulated(net.minecraft.class_2960 id, boolean includeRegistry, net.minecraft.class_6862<T>... tags) {
        Preconditions.checkArgument(tags.length > 0, "Must register at least one tag.");
        final net.minecraft.class_5321<? extends net.minecraft.class_2378<?>> registryRef = tags[0].comp_326();
        return new ConditionJsonProvider() {
            @Override
            public net.minecraft.class_2960 getConditionId() {
                return id;
            }

            @Override
            public void writeParameters(JsonObject object) {
                JsonArray array = new JsonArray();
                for (net.minecraft.class_6862<T> tag : tags) {
                    array.add(tag.comp_327().toString());
                }
                object.add("values", array);
                if (includeRegistry && registryRef != net.minecraft.class_7924.field_41197) {
                    object.addProperty("registry", registryRef.method_29177().toString());
                }
            }
        };
    }

    @SafeVarargs
    private static <A, B> B[] transform(A[] input, Function<A, B> mapper, B... outputType) {
        B[] output = Arrays.copyOf(outputType, input.length);
        for (int i = 0; i < input.length; i++) {
            output[i] = mapper.apply(input[i]);
        }
        return output;
    }
}

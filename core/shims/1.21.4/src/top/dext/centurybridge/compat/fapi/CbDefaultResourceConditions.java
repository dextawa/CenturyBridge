package top.dext.centurybridge.compat.fapi;

import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.class_1792;
import net.minecraft.class_1935;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3611;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import net.minecraft.class_7696;
import net.minecraft.class_7701;
import net.minecraft.class_7923;
import net.minecraft.class_7924;

/**
 * The 1.20.1 DefaultResourceConditions factories, emitting the JSON the 1.21.4 loader's built-in condition
 * types parse (fabric:not/and/or/all_mods_loaded/any_mods_loaded/tags_populated/features_enabled/
 * registry_contains). The per-registry tag shortcuts (block/fluid/item_tags_populated) were folded into
 * fabric:tags_populated with a "registry" field in 1.20.5, so they are emitted in that form.
 * classRename: net/fabricmc/fabric/api/resource/conditions/v1/DefaultResourceConditions -> this class.
 */
public final class CbDefaultResourceConditions {

    private static CbConditionJsonProvider of(String id, Consumer<JsonObject> params) {
        class_2960 cid = class_2960.method_60655("fabric", id);
        return new CbConditionJsonProvider() {
            @Override
            public class_2960 getConditionId() {
                return cid;
            }

            @Override
            public void writeParameters(JsonObject object) {
                params.accept(object);
            }
        };
    }

    private static JsonArray strings(String... values) {
        JsonArray array = new JsonArray();
        for (String v : values) {
            array.add(v);
        }
        return array;
    }

    private static JsonArray conditions(CbConditionJsonProvider... values) {
        JsonArray array = new JsonArray();
        for (CbConditionJsonProvider v : values) {
            array.add(v.toJson());
        }
        return array;
    }

    public static CbConditionJsonProvider not(CbConditionJsonProvider value) {
        return of("not", o -> o.add("value", value.toJson()));
    }

    public static CbConditionJsonProvider and(CbConditionJsonProvider... values) {
        return of("and", o -> o.add("values", conditions(values)));
    }

    public static CbConditionJsonProvider or(CbConditionJsonProvider... values) {
        return of("or", o -> o.add("values", conditions(values)));
    }

    public static CbConditionJsonProvider allModsLoaded(String... modIds) {
        return of("all_mods_loaded", o -> o.add("values", strings(modIds)));
    }

    public static CbConditionJsonProvider anyModLoaded(String... modIds) {
        return of("any_mods_loaded", o -> o.add("values", strings(modIds)));
    }

    @SafeVarargs
    private static <T> CbConditionJsonProvider tags(class_5321<? extends class_2378<T>> registry, class_6862<T>... tags) {
        return of("tags_populated", o -> {
            o.addProperty("registry", registry.method_29177().toString());
            JsonArray array = new JsonArray();
            for (class_6862<T> tag : tags) {
                array.add(tag.comp_327().toString());
            }
            o.add("values", array);
        });
    }

    @SafeVarargs
    public static CbConditionJsonProvider blockTagsPopulated(class_6862<class_2248>... tags) {
        return tags(class_7924.field_41254, tags);
    }

    @SafeVarargs
    public static CbConditionJsonProvider fluidTagsPopulated(class_6862<class_3611>... tags) {
        return tags(class_7924.field_41270, tags);
    }

    @SafeVarargs
    public static CbConditionJsonProvider itemTagsPopulated(class_6862<class_1792>... tags) {
        return tags(class_7924.field_41197, tags);
    }

    @SafeVarargs
    public static <T> CbConditionJsonProvider tagsPopulated(class_6862<T>... tags) {
        if (tags.length == 0) {
            return of("tags_populated", o -> o.add("values", new JsonArray()));
        }
        return tags(tags[0].comp_326(), tags);
    }

    public static CbConditionJsonProvider featuresEnabled(class_7696... features) {
        return of("features_enabled", o -> {
            JsonArray array = new JsonArray();
            for (class_2960 id : class_7701.field_40180.method_45392(class_7701.field_40180.method_45390(features))) {
                array.add(id.toString());
            }
            o.add("features", array);
        });
    }

    public static CbConditionJsonProvider itemsRegistered(class_1935... items) {
        return registryContains(class_7923.field_41178, transform(items, class_1935::method_8389, new class_1792[0]));
    }

    @SafeVarargs
    public static <T> CbConditionJsonProvider registryContains(class_2378<T> registry, T... entries) {
        return registryContains(registry.method_46765(), transform(entries, registry::method_10221, new class_2960[0]));
    }

    @SafeVarargs
    public static <T> CbConditionJsonProvider registryContains(class_5321<T>... entries) {
        if (entries.length == 0) {
            return of("registry_contains", o -> o.add("values", new JsonArray()));
        }
        return registryContains(class_5321.method_29180(entries[0].method_41185()), transform(entries, class_5321::method_29177, new class_2960[0]));
    }

    public static <T> CbConditionJsonProvider registryContains(class_5321<? extends class_2378<T>> registry, class_2960... entries) {
        return of("registry_contains", o -> {
            o.addProperty("registry", registry.method_29177().toString());
            JsonArray array = new JsonArray();
            for (class_2960 id : entries) {
                array.add(id.toString());
            }
            o.add("values", array);
        });
    }

    private static <A, B> B[] transform(A[] in, Function<A, B> f, B[] proto) {
        B[] out = java.util.Arrays.copyOf(proto, in.length);
        for (int i = 0; i < in.length; i++) {
            out[i] = f.apply(in[i]);
        }
        return out;
    }

    private CbDefaultResourceConditions() {
    }
}

package top.dext.centurybridge.step.v1_21_2.rt;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import top.dext.centurybridge.step.v1_21_2.facade.ArmorMaterial;

/**
 * Converters for members whose erased descriptor 1.21.2 kept while their type arguments changed (the step's generic
 * drift, s7item's area). Each is adaptive -- a value already of the wanted kind passes through -- and each pair undoes
 * the other, because erasure cannot tell which side a value came from.
 * <ul>
 * <li>recipe lists / maps: 1.21.1 named recipes by id, 1.21.2 by ResourceKey in the recipe registry
 *     (AdvancementRewards.recipes, PlayerPredicate.recipes);</li>
 * <li>TagLoader.build: 1.21.1's map valued Collection, 1.21.2 reads Lists;</li>
 * <li>TrimMaterial's armour overrides: 1.21.1 keyed them by ArmorMaterial holder, 1.21.2 by equipment model id
 *     (the id CbItemArmor gives a 1.21.1 material).</li>
 * </ul>
 */
public final class CbItemGeneric {
    private CbItemGeneric() {
    }

    // ---- recipe ids <-> keys ----

    /** a 1.21.1 list of recipe ids as 1.21.2's keys */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List recipeKeys(List list) {
        if (list == null || list.stream().noneMatch(e -> e instanceof class_2960)) {
            return list;
        }
        List out = new ArrayList(list.size());
        for (Object e : list) {
            out.add(e instanceof class_2960 id ? CbItemRecipeBook.key(id) : e);
        }
        return out;
    }

    /** 1.21.2's list of recipe keys as 1.21.1's ids */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List recipeIds(List list) {
        if (list == null || list.stream().noneMatch(e -> e instanceof class_5321)) {
            return list;
        }
        List out = new ArrayList(list.size());
        for (Object e : list) {
            out.add(e instanceof class_5321<?> key ? key.method_29177() : e);
        }
        return out;
    }

    /** 1.21.2's recipe key -> unlocked map as 1.21.1's id -> unlocked map (PlayerPredicate.recipes) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object2BooleanMap recipeIdMap(Object2BooleanMap map) {
        if (map == null || map.keySet().stream().noneMatch(e -> e instanceof class_5321)) {
            return map;
        }
        Object2BooleanMap out = new Object2BooleanOpenHashMap(map.size());
        for (Object o : map.object2BooleanEntrySet()) {
            Object2BooleanMap.Entry e = (Object2BooleanMap.Entry) o;
            Object k = e.getKey();
            out.put(k instanceof class_5321<?> key ? key.method_29177() : k, e.getBooleanValue());
        }
        return out;
    }

    // ---- TagLoader.build ----

    /** a 1.21.1 build(..) override's map (Collection values) as 1.21.2's (List values) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map tagLists(Map map) {
        if (map == null || map.values().stream().allMatch(v -> v == null || v instanceof List)) {
            return map;
        }
        Map out = new LinkedHashMap(map.size());
        for (Object o : map.entrySet()) {
            Map.Entry e = (Map.Entry) o;
            Object v = e.getValue();
            out.put(e.getKey(), v instanceof Collection c && !(v instanceof List) ? new ArrayList(c) : v);
        }
        return out;
    }

    // ---- TrimMaterial's armour overrides ----

    /** 1.21.1's ArmorMaterial-keyed overrides as 1.21.2's equipment-model-keyed ones */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map overridesByModel(Map map) {
        if (map == null || map.keySet().stream().noneMatch(k -> k instanceof class_6880)) {
            return map;
        }
        Map out = new LinkedHashMap(map.size());
        for (Object o : map.entrySet()) {
            Map.Entry e = (Map.Entry) o;
            Object k = e.getKey();
            out.put(k instanceof class_6880 h ? CbItemArmor.equipmentModelId((class_6880<ArmorMaterial>) h) : k, e.getValue());
        }
        return out;
    }

    /**
     * 1.21.2's equipment-model-keyed overrides as 1.21.1's ArmorMaterial-keyed ones: each model id stands for the
     * 1.21.1 materials whose equipment model it is (CbItemArmor.equipmentModelId: vanilla's own for vanilla's
     * materials); an id no 1.21.1 material has has no ArmorMaterial to key it by in 1.21.1's map
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map overridesByMaterial(Map map) {
        if (map == null || map.keySet().stream().noneMatch(k -> k instanceof class_2960)) {
            return map;
        }
        Map out = new LinkedHashMap(map.size());
        for (Object o : map.entrySet()) {
            Map.Entry e = (Map.Entry) o;
            if (e.getKey() instanceof class_2960 model) {
                CbItemArmor.registry().method_42017().forEach(h -> {
                    if (model.equals(CbItemArmor.equipmentModelId(h))) {
                        out.put(h, e.getValue());
                    }
                });
            } else {
                out.put(e.getKey(), e.getValue());
            }
        }
        return out;
    }
}

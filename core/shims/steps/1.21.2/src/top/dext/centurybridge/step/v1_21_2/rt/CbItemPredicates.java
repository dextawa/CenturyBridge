package top.dext.centurybridge.step.v1_21_2.rt;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Optional;
import net.minecraft.class_1299;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1860;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7871;
import net.minecraft.class_7923;
import net.minecraft.class_7924;

/**
 * The arguments 1.21.2's advancement predicate builders and triggers take where 1.21.1's did without (callReshapes
 * in s7item-predicates-gen.json):
 * <ul>
 * <li>a HolderGetter for the items / blocks / entity types named: the built-in registries' bootstrap lookups, which
 *     resolve exactly what 1.21.1's builders held (holders of those registries; tags bound when first tested);</li>
 * <li>recipe ids as recipe keys (1.21.2 keys recipes in a registry of their own);</li>
 * <li>KilledByArrowTrigger's firing weapon: a crossbow -- 1.21.1's trigger was "killed by crossbow" (fired from
 *     CrossbowItem only), and 1.21.2's crossbowKilled predicates ask for a crossbow;</li>
 * <li>PlayerPredicate's input predicate: none (1.21.1 had none).</li>
 * </ul>
 */
public final class CbItemPredicates {

    public static class_7871<class_1792> items() {
        return top.dext.centurybridge.step.v1_21_2.rt.CbItemTags.lookup(class_7923.field_41178);
    }

    public static class_7871<class_2248> blocks() {
        return top.dext.centurybridge.step.v1_21_2.rt.CbItemTags.lookup(class_7923.field_41175);
    }

    public static class_7871<class_1299<?>> entityTypes() {
        return top.dext.centurybridge.step.v1_21_2.rt.CbItemTags.lookup(class_7923.field_41177);
    }

    /** a recipe id as the recipe key */
    public static class_5321<class_1860<?>> recipe(class_2960 id) {
        return class_5321.method_29179(class_7924.field_52178, id);
    }

    /** a recipe key as the id 1.21.1's override took (overrideHooks wrap) */
    public static class_2960 id(class_5321<class_1860<?>> key) {
        return key.method_29177();
    }

    /** PlayerPredicate's recipes (id -> unlocked) as 1.21.2's (recipe key -> unlocked) */
    public static Object2BooleanMap<class_5321<class_1860<?>>> recipes(Object2BooleanMap<class_2960> byId) {
        Object2BooleanMap<class_5321<class_1860<?>>> out = new Object2BooleanOpenHashMap<>();
        for (Object2BooleanMap.Entry<class_2960> e : byId.object2BooleanEntrySet()) {
            out.put(recipe(e.getKey()), e.getBooleanValue());
        }
        return out;
    }

    /** the weapon 1.21.1's KilledByCrossbow trigger implied */
    public static class_1799 crossbow() {
        return new class_1799(class_1802.field_8399);
    }

    public static <T> Optional<T> empty() {
        return Optional.empty();
    }

    private CbItemPredicates() {
    }
}

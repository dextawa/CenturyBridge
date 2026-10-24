package cb.stags;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1860;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_3481;
import net.minecraft.class_52;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import net.minecraft.class_7924;
import net.minecraft.class_8786;
import net.minecraft.server.MinecraftServer;

/**
 * Real-server check of the tag-drift bridge for step 1.21.3 -> 1.21.4 (written against 1.21.3, converted one step).
 *
 * Code side: BlockTags constants whose vanilla tag 1.21.4 changed keep 1.21.3's members -- SWORD_EFFICIENT (1.21.4
 * dropped the flowers) and REPLACEABLE_BY_TREES (1.21.4 added them).
 *
 * Data side (this mod's own json, rewritten by the resource translator): a recipe and a loot table naming the item
 * tag minecraft:flowers (1.21.4 has none; it comes back in 1.21.5 with its own members), a placed feature and a
 * configured feature naming replaceable_by_trees / sword_efficient, a tag file including #minecraft:sword_efficient,
 * and this mod's own file adding stone to minecraft:replaceable_by_trees (a widened tag: mirrored into the compat
 * tag). Prints "CBTEST PASS|FAIL name" lines and a summary.
 */
public class StagsTestMod implements ModInitializer {

    private static final List<String> FAILS = new ArrayList<>();
    private static int passed;

    private static void check(String name, boolean ok, Object detail) {
        if (ok) {
            passed++;
            System.out.println("CBTEST PASS " + name + " : " + detail);
        } else {
            FAILS.add(name);
            System.out.println("CBTEST FAIL " + name + " : " + detail);
        }
    }

    private static boolean in(class_2248 block, class_6862<class_2248> tag) {
        return block.method_9564().method_26164(tag);
    }

    private static class_2960 id(String ns, String path) {
        return class_2960.method_60655(ns, path);
    }

    /** every TagKey reachable from o through instance fields, collections, maps, optionals and arrays */
    private static List<class_6862<?>> tagKeys(Object o) {
        List<class_6862<?>> out = new ArrayList<>();
        walk(o, out, java.util.Collections.newSetFromMap(new IdentityHashMap<>()), 0);
        return out;
    }

    private static void walk(Object o, List<class_6862<?>> out, Set<Object> seen, int depth) {
        if (o == null || depth > 10 || !seen.add(o)) {
            return;
        }
        if (o instanceof class_6862<?> k) {
            out.add(k);
            return;
        }
        if (o instanceof Collection<?> c) {
            for (Object x : c) walk(x, out, seen, depth + 1);
            return;
        }
        if (o instanceof Map<?, ?> m) {
            for (Object x : m.values()) walk(x, out, seen, depth + 1);
            return;
        }
        if (o instanceof Optional<?> opt) {
            opt.ifPresent(x -> walk(x, out, seen, depth + 1));
            return;
        }
        if (o.getClass().isArray()) {
            if (!o.getClass().getComponentType().isPrimitive()) {
                for (Object x : (Object[]) o) walk(x, out, seen, depth + 1);
            }
            return;
        }
        String cn = o.getClass().getName();
        if (cn.startsWith("java.") || o instanceof String || o instanceof Number || o.getClass().isEnum()) {
            return;
        }
        // a registry entry stands for itself, not for what it holds (a referenced feature, a block's own tags), and a
        // tag's owner (the registry) is not part of the predicate
        if (o instanceof net.minecraft.class_6880<?> || o instanceof class_2248
                || o instanceof net.minecraft.class_4970.class_4971 || o instanceof class_1792
                || o instanceof net.minecraft.class_7876<?> || o instanceof net.minecraft.class_2378<?>) {
            return;
        }
        for (Class<?> c = o.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers()) || f.getType().isPrimitive()) {
                    continue;
                }
                try {
                    f.setAccessible(true);
                    walk(f.get(o), out, seen, depth + 1);
                } catch (RuntimeException | IllegalAccessException e) {
                    // a module-closed JDK field: not where tags live
                }
            }
        }
    }

    private static String names(List<class_6862<?>> keys) {
        Set<String> s = new TreeSet<>();
        for (class_6862<?> k : keys) s.add(String.valueOf(k.comp_327()));
        return s.toString();
    }

    @SuppressWarnings("unchecked")
    private static void dataChecks(MinecraftServer server) {
        String flowers = "centurybridge:compat/1_21_3/flowers";
        // recipe: ingredient "#minecraft:flowers" -> the compat tag, holding 1.21.3's flowers
        Optional<class_8786<?>> recipe = server.method_3772().method_8130(
            class_5321.method_29179(class_7924.field_52178, id("cb_stags", "flower_diamond")));
        check("recipe loaded", recipe.isPresent(), "cb_stags:flower_diamond");
        if (recipe.isPresent()) {
            class_1860<?> r = recipe.get().comp_1933();
            List<class_6862<?>> keys = tagKeys(r);
            check("recipe ingredient names the compat tag", names(keys).contains(flowers), names(keys));
            boolean poppy = false;
            boolean rose = false;
            List<Object> preds = new ArrayList<>();
            collectPredicates(r, preds, java.util.Collections.newSetFromMap(new IdentityHashMap<>()), 0);
            for (Object p : preds) {
                poppy |= ((Predicate<Object>) p).test(new class_1799(class_1802.field_8880));
                rose |= ((Predicate<Object>) p).test(new class_1799(class_1802.field_17527));
            }
            check("recipe accepts poppy (1.21.3 flowers)", poppy, preds.size() + " ingredient(s)");
            check("recipe accepts rose_bush (1.21.3 flowers held the tall ones)", rose, preds.size() + " ingredient(s)");
        }
        // loot: a "minecraft:tag" entry naming minecraft:flowers
        class_52 loot = server.method_58576().method_58295(
            class_5321.method_29179(class_7924.field_50079, id("cb_stags", "flowers")));
        List<class_6862<?>> lk = tagKeys(loot);
        check("loot tag entry names the compat tag", names(lk).contains(flowers), names(lk));
        for (class_6862<?> k : lk) {
            if (String.valueOf(k.comp_327()).equals(flowers)) {
                check("loot tag holds poppy", new class_1799(class_1802.field_8880).method_31573((class_6862<class_1792>) k), k.comp_327());
            }
        }
        // worldgen: matching_block_tag (bare id) and matching_blocks "#..." of replaceable_by_trees; tag_match of
        // sword_efficient
        Object placed = server.method_30611().method_30530(class_7924.field_41245).method_63535(id("cb_stags", "tree_spot"));
        List<class_6862<?>> pk = tagKeys(placed);
        check("placed feature names the compat replaceable_by_trees",
            // both predicates name it (TagKey.create interns: one instance); a predicate left on the vanilla tag
            // would show minecraft:replaceable_by_trees here
            names(pk).equals("[centurybridge:compat/1_21_3/replaceable_by_trees]"), names(pk));
        Object ore = server.method_30611().method_30530(class_7924.field_41239).method_63535(id("cb_stags", "sword_ore"));
        List<class_6862<?>> ok = tagKeys(ore);
        check("configured feature tag_match names the compat sword_efficient",
            names(ok).equals("[centurybridge:compat/1_21_3/sword_efficient]"), names(ok));
        // a tag file of this mod including #minecraft:sword_efficient
        class_6862<class_2248> mine = class_6862.method_40092(class_7924.field_41254, id("cb_stags", "my_tag"));
        check("own tag including #sword_efficient holds poppy (1.21.3 member)", in(class_2246.field_10449, mine), "cb_stags:my_tag");
        // this mod's own minecraft:replaceable_by_trees file adds stone: mirrored into the compat tag
        check("REPLACEABLE_BY_TREES holds this mod's stone (mirrored)", in(class_2246.field_10340, class_3481.field_44470), "stone");
        class_6862<class_2248> vanilla = class_6862.method_40092(class_7924.field_41254, id("minecraft", "replaceable_by_trees"));
        check("vanilla replaceable_by_trees still holds this mod's stone", in(class_2246.field_10340, vanilla), "stone");
    }

    private static void collectPredicates(Object o, List<Object> out, Set<Object> seen, int depth) {
        if (o == null || depth > 6 || !seen.add(o)) {
            return;
        }
        if (o instanceof Predicate<?> && o.getClass().getName().startsWith("net.minecraft")) {
            out.add(o);
            return;
        }
        if (o instanceof Collection<?> c) {
            for (Object x : c) collectPredicates(x, out, seen, depth + 1);
            return;
        }
        if (o instanceof Optional<?> opt) {
            opt.ifPresent(x -> collectPredicates(x, out, seen, depth + 1));
            return;
        }
        if (o.getClass().getName().startsWith("java.")) {
            return;
        }
        for (Class<?> c = o.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers()) || f.getType().isPrimitive()) {
                    continue;
                }
                try {
                    f.setAccessible(true);
                    collectPredicates(f.get(o), out, seen, depth + 1);
                } catch (RuntimeException | IllegalAccessException e) {
                    // not where ingredients live
                }
            }
        }
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            class_6862<class_2248> sword = class_3481.field_44469;
            class_6862<class_2248> trees = class_3481.field_44470;
            check("sword_efficient key", true, sword.comp_327());
            check("replaceable_by_trees key", true, trees.comp_327());
            // 1.21.3: poppy and oak_leaves sword-efficient; 1.21.4 vanilla keeps only oak_leaves (via #leaves)
            check("sword_efficient holds poppy (1.21.3 member, gone in 1.21.4)", in(class_2246.field_10449, sword), "poppy");
            check("sword_efficient holds oak_leaves (member in both)", in(class_2246.field_10503, sword), "oak_leaves");
            // 1.21.3: short_grass replaceable by trees, dandelion not; 1.21.4 vanilla adds dandelion
            check("replaceable_by_trees lacks dandelion (added in 1.21.4)", !in(class_2246.field_10182, trees), "dandelion");
            check("replaceable_by_trees holds short_grass (member in both)", in(class_2246.field_10479, trees), "short_grass");
            try {
                dataChecks(server);
            } catch (RuntimeException e) {
                check("data checks ran", false, e);
            }
            System.out.println("CBTEST SUMMARY passed=" + passed + " failed=" + FAILS.size() + " " + FAILS);
        });
    }
}

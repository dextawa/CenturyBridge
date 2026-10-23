package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4 world/item members whose classes live on in 1.20.5 with another shape (area s4fin).
 *
 * <ul>
 * <li>AdventureModePredicate: 1.20.4's was made with the tag key it reads ("CanDestroy" / "CanPlaceOn") and
 *     tested a stack against a block: the stack's string list under that key, each parsed as a block predicate
 *     against the block registry, the last answer cached for the same block (state, and block entity NBT when
 *     a matching predicate asked for it). 1.20.5's holds its block predicates and tests the block only. A
 *     1.20.4 predicate is a 1.20.5 one with no predicates (never matching on its own) remembered with its key
 *     ({@link #adventure}); its test(stack, registry, block) is 1.20.4's body, reading the stack's 1.20.4 NBT
 *     view (which can_break / can_place_on components are mirrored into).</li>
 * <li>CrossbowItem.performShooting(level, shooter, hand, crossbow, velocity, inaccuracy): 1.20.4's static,
 *     shooting a crossbow mob's projectiles at its target; 1.20.5's is an instance method with the target
 *     (a CrossbowAttackMob's getTarget(), as 1.20.5's own caller passes), else none.</li>
 * <li>RecipeManager(): 1.20.4's had no registries; 1.20.5's takes the world's (CbRegistries.current()).</li>
 * <li>SmithingTemplateItem's 1.20.4 constructor: 1.20.5's with no required features, as 1.20.4's
 *     Item.Properties had (vanilla only).</li>
 * </ul>
 */
public final class CbFinItemMisc {

    private CbFinItemMisc() {
    }

    // ---------------------------------------------------------------- AdventureModePredicate

    /** what a 1.20.4 AdventureModePredicate remembers between tests */
    private static final class Adventure {
        final String key;
        net.minecraft.class_2694 last;
        boolean lastResult;
        boolean checksBlockEntity;

        Adventure(String key) {
            this.key = key;
        }
    }

    private static final java.util.Map<net.minecraft.class_6538, Adventure> ADVENTURE =
        java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    /** new AdventureModePredicate(tagName) */
    public static net.minecraft.class_6538 adventure(String tagName) {
        net.minecraft.class_6538 p = new net.minecraft.class_6538(java.util.List.of(), true);
        ADVENTURE.put(p, new Adventure(tagName));
        return p;
    }

    /** a 1.20.4 subclass's super(tagName): 1.20.5's constructor arguments (no predicates, shown in tooltips) */
    public static java.util.List<net.minecraft.class_4550> noPredicates(String tagName) {
        return java.util.List.of();
    }

    public static boolean showInTooltip() {
        return true;
    }

    /** a 1.20.4 subclass after super(tagName): its key */
    public static void adventurePost(net.minecraft.class_6538 p, String tagName) {
        ADVENTURE.put(p, new Adventure(tagName));
    }

    private static boolean sameBlock(net.minecraft.class_2694 a, net.minecraft.class_2694 b, boolean checkNbt) {
        if (a == null || a.method_11681() != b.method_11681()) {
            return false;
        }
        if (!checkNbt) {
            return true;
        }
        if (a.method_11680() == null && b.method_11680() == null) {
            return true;
        }
        if (a.method_11680() == null || b.method_11680() == null) {
            return false;
        }
        net.minecraft.class_7225.class_7874 r = CbRegistries.current();
        return java.util.Objects.equals(a.method_11680().method_38243(r), b.method_11680().method_38243(r));
    }

    /** a 1.20.4 AdventureModePredicate subclass declaring test(stack, blocks, block) */
    public interface LegacyTest {
        boolean method_38070(net.minecraft.class_1799 stack, net.minecraft.class_2378<net.minecraft.class_2248> blocks, net.minecraft.class_2694 block);
    }

    /** predicate.test(stack, blocks, block): the class's own when it declares one (1.20.4's virtual call), else 1.20.4's body */
    public static boolean method_38070(net.minecraft.class_6538 p, net.minecraft.class_1799 stack,
                                       net.minecraft.class_2378<net.minecraft.class_2248> blocks, net.minecraft.class_2694 block) {
        return p instanceof LegacyTest own ? own.method_38070(stack, blocks, block) : superTest(p, stack, blocks, block);
    }

    /**
     * AdventureModePredicate.test(stack, blocks, block)'s body: 1.20.4's for a predicate made the 1.20.4 way; a
     * 1.20.5 predicate (made by 1.20.5 code) tests the block as 1.20.5 does, the stack being whatever carries it.
     */
    public static boolean superTest(net.minecraft.class_6538 p, net.minecraft.class_1799 stack,
                                    net.minecraft.class_2378<net.minecraft.class_2248> blocks, net.minecraft.class_2694 block) {
        Adventure a = ADVENTURE.get(p);
        if (a == null) {
            return p.method_38070(block);
        }
        synchronized (a) {
            if (sameBlock(a.last, block, a.checksBlockEntity)) {
                return a.lastResult;
            }
            a.last = block;
            a.checksBlockEntity = false;
            net.minecraft.class_2487 tag = CbItemTags.getTag(stack);
            if (tag != null && tag.method_10573(a.key, 9)) {
                net.minecraft.class_2499 list = tag.method_10554(a.key, 8);
                for (int i = 0; i < list.size(); i++) {
                    String s = list.method_10608(i);
                    try {
                        net.minecraft.class_2252.class_2254 pred = net.minecraft.class_2252.method_41953(blocks.method_46771(),
                            new com.mojang.brigadier.StringReader(s));
                        a.checksBlockEntity |= pred.method_38559();
                        if (pred.test(block)) {
                            a.lastResult = true;
                            return true;
                        }
                    } catch (com.mojang.brigadier.exceptions.CommandSyntaxException ignored) {
                        // 1.20.4 skipped an unparsable entry
                    }
                }
            }
            a.lastResult = false;
            return false;
        }
    }

    // ---------------------------------------------------------------- RecipeCache.get (generic drift)

    /** RecipeCache.get: 1.20.4 answered the crafting recipe, 1.20.5 its holder -- the holder's recipe (adaptive) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.Optional recipeValue(java.util.Optional result) {
        return result == null ? null : result.map(r -> r instanceof net.minecraft.class_8786 h ? h.comp_1933() : r);
    }

    /**
     * The reverse, for a 1.20.4 RecipeCache subclass's override answered to 1.20.5: the recipe's holder, found
     * by identity among the world's recipes (every crafting recipe a cache answers is one of the manager's),
     * else held under the empty id (a recipe the manager never had has none).
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.Optional recipeHolder(java.util.Optional result) {
        if (result == null || result.isEmpty() || result.get() instanceof net.minecraft.class_8786) {
            return result;
        }
        Object recipe = result.get();
        net.minecraft.server.MinecraftServer server = CbRegistries.server();
        if (server != null) {
            for (net.minecraft.class_8786<?> h : server.method_3772().method_8126()) {
                if (h.comp_1933() == recipe) {
                    return java.util.Optional.of(h);
                }
            }
        }
        return java.util.Optional.of(new net.minecraft.class_8786(new net.minecraft.class_2960("minecraft", "unknown"), (net.minecraft.class_1860) recipe));
    }

    // ---------------------------------------------------------------- SmithingTemplateItem

    /** the required features of a 1.20.4 smithing template: none */
    public static net.minecraft.class_7696[] noFeatures() {
        return new net.minecraft.class_7696[0];
    }

    // ---------------------------------------------------------------- CrossbowItem

    /** CrossbowItem.performShooting(level, shooter, hand, crossbow, velocity, inaccuracy) */
    public static void method_7777(net.minecraft.class_1937 level, net.minecraft.class_1309 shooter, net.minecraft.class_1268 hand,
                                   net.minecraft.class_1799 crossbow, float velocity, float inaccuracy) {
        if (crossbow.method_7909() instanceof net.minecraft.class_1764 item) {
            net.minecraft.class_1309 target = shooter instanceof net.minecraft.class_3745 mob ? mob.method_5968() : null;
            item.method_7777(level, shooter, hand, crossbow, velocity, inaccuracy, target);
        }
    }
}

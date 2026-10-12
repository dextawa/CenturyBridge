package cb.recipetest;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1263;
import net.minecraft.class_1277;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1860;
import net.minecraft.class_1863;
import net.minecraft.class_1874;
import net.minecraft.class_1937;
import net.minecraft.class_2540;
import net.minecraft.class_2788;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3441;
import net.minecraft.class_3861;
import net.minecraft.class_3956;
import net.minecraft.class_5455;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.20.1-era server mod (intermediary names, Fabric Loader API only) for the generic-drift bridges of
 * RecipeManager and its neighbours: 1.20.2 hands RecipeHolders where 1.20.1 handed recipes under the
 * same erased types, so an unconverted mod's first cast of an element is a ClassCastException. Once the
 * server ticks, on the server thread, it takes the raw-iron smelting recipe through every lookup
 * (getRecipeFor, getRecipeFor with the last id, CachedCheck, getAllRecipesFor, getRecipesFor, byKey,
 * getRecipes), casts what it gets to SmeltingRecipe / AbstractCookingRecipe and calls their methods;
 * hands recipes back (replaceRecipes, the recipe-sync packet and its write/read, ServerRecipeBook's
 * addRecipes / removeRecipes on a stand-in player); and overrides RecipeManager, the packet and
 * ServerRecipeBook with super calls, called both by this mod and the way vanilla calls them
 * (reflection on the vanilla Method, which no converter touches: what it sees is what 1.20.2 code sees).
 * Every check prints one "CB-RECIPETEST ok|FAIL ..." line; the last line is "CB-RECIPETEST DONE".
 *
 * <p>Build against 1.20.1 (any fabric-loader jar for the API):
 * <pre>
 *   javac --release 17 -cp "client-1.20.1-intermediary.jar:fabric-loader.jar:&lt;1.20.1 libraries&gt;" -d out \
 *         core/testmods/cb-recipetest/src/cb/recipetest/*.java
 *   cp core/testmods/cb-recipetest/res/fabric.mod.json out/ &amp;&amp; (cd out &amp;&amp; jar cf cb-recipetest.jar .)
 * </pre>
 */
public class RecipeTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-RECIPETEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    static boolean holderValue(Object o) {
        return o != null && o.getClass().getName().equals("net.minecraft.class_8786");
    }

    @Override
    public void onInitialize() {
        Thread t = new Thread(() -> {
            for (int i = 0; i < 900; i++) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    return;
                }
                Object game = FabricLoader.getInstance().getGameInstance();
                if (game instanceof MinecraftServer server && server.method_3780() > 40) {
                    server.execute(() -> run(server));
                    return;
                }
            }
            System.out.println("CB-RECIPETEST DONE no server ticked");
        }, "cb-recipetest");
        t.setDaemon(true);
        t.start();
    }

    static void run(MinecraftServer server) {
        try {
            lookups(server);
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "lookups threw " + e);
        }
        try {
            overrides(server);
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "overrides threw " + e);
        }
        try {
            packets(server);
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "packets threw " + e);
        }
        try {
            recipeBook(server);
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "recipe book threw " + e);
        }
        System.out.println("CB-RECIPETEST DONE " + checks + " checks, " + FAILS.size() + " failed" + (FAILS.isEmpty() ? "" : ": " + FAILS));
    }

    static class_1277 rawIron() {
        return new class_1277(new class_1799(class_1802.field_33400));
    }

    static class_3861 smelting(MinecraftServer server) {
        return server.method_3772().method_8132(class_3956.field_17546, rawIron(), server.method_30002()).get();
    }

    // ---------------------------------------------------------------- the lookups

    static void lookups(MinecraftServer server) {
        class_1863 rm = server.method_3772();
        class_3218 level = server.method_30002();
        class_5455 regs = server.method_30611();
        class_1277 inv = rawIron();

        Optional<class_3861> found = rm.method_8132(class_3956.field_17546, inv, level);
        check(found.isPresent(), "getRecipeFor(SMELTING, raw iron) finds a recipe");
        class_3861 smelt = found.get();
        class_2960 id = smelt.method_8114();
        check(smelt.method_8110(regs).method_7909() == class_1802.field_8620, "its result is an iron ingot (" + id + ")");
        check(smelt.method_8167() == 200, "its cooking time is 200, got " + smelt.method_8167());
        check(smelt.method_8115(inv, level), "it matches the raw iron container");
        check(smelt.method_17716() == class_3956.field_17546, "its type is SMELTING");

        Optional<Pair<class_2960, class_3861>> pair = rm.method_42299(class_3956.field_17546, inv, level, null);
        check(pair.isPresent() && pair.get().getSecond() == smelt, "getRecipeFor(.., lastId null) pairs the same recipe");
        check(pair.isPresent() && id.equals(pair.get().getFirst()), "the pair's id is the recipe's id");
        Optional<Pair<class_2960, class_3861>> again = rm.method_42299(class_3956.field_17546, inv, level, id);
        check(again.isPresent() && again.get().getSecond() == smelt, "getRecipeFor(.., lastId) finds it by its id");

        class_1863.class_7266<class_1263, class_3861> cached = class_1863.method_42302(class_3956.field_17546);
        Optional<class_3861> viaCheck = cached.method_42303(inv, level);
        check(viaCheck.isPresent() && viaCheck.get() == smelt, "CachedCheck.getRecipeFor gives the same recipe");
        check(cached.method_42303(inv, level).get().method_8171() == smelt.method_8171(), "the cached second lookup too");

        List<class_3861> all = rm.method_30027(class_3956.field_17546);
        int n = 0;
        float xp = 0;
        for (class_3861 r : all) {
            n++;
            xp += r.method_8171();
        }
        check(n > 50 && n == all.size(), "getAllRecipesFor(SMELTING): " + n + " smelting recipes, cast and read (xp sum " + xp + ")");
        check(all.contains(smelt), "getAllRecipesFor contains the recipe getRecipeFor found");
        boolean immutable;
        try {
            all.add(smelt);
            immutable = false;
        } catch (UnsupportedOperationException e) {
            immutable = true;
        }
        check(immutable, "getAllRecipesFor's list is unmodifiable, as 1.20.1's List.copyOf was");

        List<class_3861> matching = rm.method_17877(class_3956.field_17546, inv, level);
        check(matching.size() == 1 && matching.get(0) == smelt, "getRecipesFor(SMELTING, raw iron) is that one recipe, got " + matching.size());
        List<class_3861> sorted = rm.method_17877(class_3956.field_17546, new class_1277(1), level);
        check(sorted.isEmpty(), "getRecipesFor with an empty container finds nothing");

        Optional<? extends class_1860<?>> byKey = rm.method_8130(id);
        check(byKey.isPresent() && byKey.get() == smelt, "byKey(id) gives the same recipe");
        class_1860<?> keyed = byKey.get();
        check(keyed instanceof class_1874 cooking && cooking.method_8167() == 200, "byKey's recipe casts to AbstractCookingRecipe");
        check(!rm.method_8130(new class_2960("cb_recipetest", "absent")).isPresent(), "byKey of an unknown id is empty");

        Collection<class_1860<?>> recipes = rm.method_8126();
        int total = 0;
        int cooking = 0;
        for (class_1860<?> r : recipes) {
            total++;
            if (r instanceof class_1874) {
                cooking++;
            }
        }
        check(total > 1000 && total == recipes.size(), "getRecipes(): " + total + " recipes, " + cooking + " of them cooking recipes");
        check(recipes instanceof Set, "getRecipes() is a Set, as 1.20.1's Collectors.toSet() was");
        check(recipes.contains(smelt), "getRecipes() contains the recipe");
        List<class_1860<?>> copy = new ArrayList<>(recipes);
        copy.sort(Comparator.comparing(r -> r.method_8114().toString()));
        check(copy.size() == total && copy.get(0).method_8114().toString().compareTo(copy.get(total - 1).method_8114().toString()) < 0,
            "a copy of getRecipes() sorts by the recipes' ids");

        // method references to the same members: what the lambda answers converts like a call's answer
        java.util.function.Function<class_2960, Optional<? extends class_1860<?>>> byKeyRef = rm::method_8130;
        Optional<? extends class_1860<?>> viaRef = byKeyRef.apply(id);
        check(viaRef.isPresent() && viaRef.get() == smelt, "a method reference rm::byKey answers the recipe");
        java.util.function.Function<class_1863, Collection<class_1860<?>>> unbound = class_1863::method_8126;
        check(unbound.apply(rm).contains(smelt), "an unbound method reference RecipeManager::getRecipes answers recipes");
        java.util.function.Function<class_3956<class_3861>, List<class_3861>> ofType = rm::method_30027;
        class_3861 firstRef = ofType.apply(class_3956.field_17546).get(0);
        check(firstRef.method_8167() > 0, "a method reference rm::getAllRecipesFor answers SmeltingRecipes");

        // the manager's own recipes handed back: the same recipes afterwards
        rm.method_20702(recipes);
        check(rm.method_8126().size() == total, "replaceRecipes(getRecipes()) keeps " + total + " recipes");
        check(rm.method_8132(class_3956.field_17546, inv, level).get() == smelt, "after replaceRecipes getRecipeFor gives the same recipe object");
        // and a plain 1.20.1 list of recipes
        rm.method_20702(copy);
        check(rm.method_8126().size() == total, "replaceRecipes(ArrayList of recipes) keeps " + total + " recipes");
        check(rm.method_8130(id).get() == smelt, "byKey after replaceRecipes(list) gives the same recipe object");
    }

    // ---------------------------------------------------------------- overrides with super calls

    /** a 1.20.1 RecipeManager subclass: the bodies see 1.20.1's values, their super calls reach vanilla */
    static final class TestManager extends class_1863 {
        final List<String> log = new ArrayList<>();

        @Override
        public <C extends class_1263, T extends class_1860<C>> Optional<T> method_8132(class_3956<T> type, C inv, class_1937 level) {
            Optional<T> r = super.method_8132(type, inv, level);
            log.add("getRecipeFor " + r.map(x -> x.method_8114().toString()).orElse("none"));
            return r;
        }

        @Override
        public Collection<class_1860<?>> method_8126() {
            Collection<class_1860<?>> all = super.method_8126();
            int n = 0;
            for (class_1860<?> r : all) {
                if (r.method_8114() != null) {
                    n++;
                }
            }
            log.add("getRecipes " + n);
            return all;
        }

        @Override
        public void method_20702(Iterable<class_1860<?>> recipes) {
            int n = 0;
            for (class_1860<?> r : recipes) {
                if (r.method_17716() != null) {
                    n++;
                }
            }
            log.add("replaceRecipes " + n);
            super.method_20702(recipes);
        }

        @Override
        public Optional<? extends class_1860<?>> method_8130(class_2960 id) {
            Optional<? extends class_1860<?>> r = super.method_8130(id);
            log.add("byKey " + r.map(x -> x.method_8114().toString()).orElse("none"));
            return r;
        }

        @Override
        public <C extends class_1263, T extends class_1860<C>> List<T> method_30027(class_3956<T> type) {
            List<T> r = super.method_30027(type);
            log.add("getAllRecipesFor " + r.size() + " " + (r.isEmpty() ? "" : r.get(0).method_17716() == type));
            return r;
        }
    }

    static void overrides(MinecraftServer server) throws Exception {
        class_1863 rm = server.method_3772();
        class_3218 level = server.method_30002();
        class_1277 inv = rawIron();
        class_3861 smelt = smelting(server);
        class_2960 id = smelt.method_8114();
        int total = rm.method_8126().size();

        TestManager tm = new TestManager();
        List<class_1860<?>> mine = new ArrayList<>(rm.method_8126());
        tm.method_20702(mine);
        check(tm.log.contains("replaceRecipes " + total), "our replaceRecipes override saw the " + total + " recipes we handed it: " + tm.log);
        Optional<class_3861> viaOverride = tm.method_8132(class_3956.field_17546, inv, level);
        check(viaOverride.isPresent() && viaOverride.get() == smelt, "our getRecipeFor override answers the recipe (body called super)");
        check(tm.log.contains("getRecipeFor " + id), "its body saw the recipe from super: " + tm.log);
        check(tm.method_8130(id).get() == smelt, "our byKey override answers the recipe");
        check(tm.method_8126().size() == total, "our getRecipes override answers " + total);
        check(tm.method_30027(class_3956.field_17546).contains(smelt), "our getAllRecipesFor override answers a list holding the recipe");

        // what vanilla sees calling the same overrides: the 1.20.2 shape, RecipeHolders
        Method getRecipeFor = class_1863.class.getMethod("method_8132", class_3956.class, class_1263.class, class_1937.class);
        Object w = getRecipeFor.invoke(tm, class_3956.field_17546, inv, level);
        check(w instanceof Optional<?> o && holderValue(o.orElse(null)), "vanilla calling our getRecipeFor gets a RecipeHolder: " + w);
        Method byKey = class_1863.class.getMethod("method_8130", class_2960.class);
        Object wk = byKey.invoke(tm, id);
        check(wk instanceof Optional<?> o && holderValue(o.orElse(null)), "vanilla calling our byKey gets a RecipeHolder");
        Method getRecipes = class_1863.class.getMethod("method_8126");
        Object wc = getRecipes.invoke(tm);
        boolean allHolders = wc instanceof Collection<?> c && !c.isEmpty();
        if (allHolders) {
            for (Object o : (Collection<?>) wc) {
                allHolders &= holderValue(o);
            }
        }
        check(allHolders, "vanilla calling our getRecipes gets RecipeHolders only");
        Method getAll = class_1863.class.getMethod("method_30027", class_3956.class);
        Object wa = getAll.invoke(tm, class_3956.field_17546);
        check(wa instanceof List<?> l && !l.isEmpty() && holderValue(l.get(0)), "vanilla calling our getAllRecipesFor gets RecipeHolders");
        // vanilla handing our replaceRecipes 1.20.2 holders (its own manager's): the body sees recipes
        Object vanillaHolders = getRecipes.invoke(rm);
        tm.log.clear();
        class_1863.class.getMethod("method_20702", Iterable.class).invoke(tm, vanillaHolders);
        check(tm.log.contains("replaceRecipes " + total), "vanilla handing our replaceRecipes RecipeHolders: the body read recipes: " + tm.log);
        check(tm.method_8130(id).get() == smelt, "and the manager holds the recipe after that");

        // lambdas implementing CachedCheck (a functional interface): called by this mod, and the way vanilla calls one
        class_1863.class_7266<class_1263, class_3861> fixed = (container, lvl) -> Optional.of(smelt);
        check(fixed.method_42303(inv, level).get() == smelt, "our CachedCheck lambda answers the recipe to us");
        Method checkGet = class_1863.class_7266.class.getMethod("method_42303", class_1263.class, class_1937.class);
        Object wl = checkGet.invoke(fixed, inv, level);
        check(wl instanceof Optional<?> o && holderValue(o.orElse(null)), "vanilla calling our CachedCheck lambda gets a RecipeHolder: " + wl);
        class_1863.class_7266<class_1263, class_3861> lookingUp = (container, lvl) -> rm.method_8132(class_3956.field_17546, container, lvl);
        check(lookingUp.method_42303(inv, level).get() == smelt, "a CachedCheck lambda that asks the manager answers the recipe to us");
        Object wl2 = checkGet.invoke(lookingUp, inv, level);
        check(wl2 instanceof Optional<?> o && holderValue(o.orElse(null)), "vanilla calling that lambda gets a RecipeHolder");
        class_1863.class_7266<class_1263, class_3861> ref = RecipeTestMod::ownCheck;
        OWN = smelt;
        check(ref.method_42303(inv, level).get() == smelt, "a CachedCheck method reference to our own method answers the recipe to us");
        Object wl3 = checkGet.invoke(ref, inv, level);
        check(wl3 instanceof Optional<?> o && holderValue(o.orElse(null)), "vanilla calling that method reference gets a RecipeHolder");
    }

    static class_3861 OWN;

    /** a CachedCheck-shaped method of our own, written for 1.20.1: it answers the recipe */
    static Optional<class_3861> ownCheck(class_1263 container, class_1937 level) {
        return Optional.ofNullable(OWN);
    }

    // ---------------------------------------------------------------- the recipe-sync packet

    /** a 1.20.1 packet subclass: its constructor passes recipes to super, its getRecipes override calls super */
    static final class TestPacket extends class_2788 {
        int reads;

        TestPacket(Collection<class_1860<?>> recipes) {
            super(recipes);
        }

        @Override
        public List<class_1860<?>> method_11998() {
            List<class_1860<?>> r = super.method_11998();
            for (class_1860<?> x : r) {
                if (x.method_8114() != null) {
                    reads++;
                }
            }
            return r;
        }
    }

    static void packets(MinecraftServer server) throws Exception {
        class_1863 rm = server.method_3772();
        class_3861 smelt = smelting(server);
        class_2960 id = smelt.method_8114();
        Collection<class_1860<?>> recipes = rm.method_8126();
        int total = recipes.size();

        class_2788 pkt = new class_2788(recipes);
        List<class_1860<?>> pr = pkt.method_11998();
        check(pr.size() == total && pr.contains(smelt), "the packet built from getRecipes() lists " + pr.size() + " recipes, ours among them");
        pr.add(smelt);
        check(pkt.method_11998().size() == total + 1, "a recipe added to getRecipes() is in the packet's own list");
        class_2540 buf = new class_2540(io.netty.buffer.Unpooled.buffer());
        pkt.method_11052(buf);
        class_2788 back = new class_2788(buf);
        List<class_1860<?>> br = back.method_11998();
        check(br.size() == total + 1, "written and read back: " + br.size() + " recipes");
        class_1860<?> last = br.get(br.size() - 1);
        check(last instanceof class_3861 && id.equals(last.method_8114()), "the added recipe travelled with its id: " + last.method_8114());
        check(((class_3861) last).method_8167() == 200, "and its cooking time");
        Object raw = class_2788.class.getMethod("method_11998").invoke(back);
        check(raw instanceof List<?> l && holderValue(l.get(0)), "vanilla reads the packet's list as RecipeHolders");

        TestPacket tp = new TestPacket(recipes);
        check(tp.method_11998().size() == total && tp.reads == total, "our packet subclass: super(recipes), and its getRecipes override read " + tp.reads);
        Object rawSub = class_2788.class.getMethod("method_11998").invoke(tp);
        check(rawSub instanceof List<?> l && l.size() == total && holderValue(l.get(0)), "vanilla calling our getRecipes override gets RecipeHolders");
    }

    // ---------------------------------------------------------------- ServerRecipeBook

    /** a 1.20.1 ServerRecipeBook subclass whose removeRecipes override reads recipes, then calls super */
    static final class TestBook extends class_3441 {
        final List<String> log = new ArrayList<>();

        @Override
        public int method_14900(Collection<class_1860<?>> recipes, class_3222 player) {
            for (class_1860<?> r : recipes) {
                log.add("remove " + r.method_8114());
            }
            return super.method_14900(recipes, player);
        }
    }

    static void recipeBook(MinecraftServer server) throws Exception {
        class_1863 rm = server.method_3772();
        class_3861 smelt = smelting(server);
        class_2960 id = smelt.method_8114();
        class_1860<?> special = null;
        for (class_1860<?> r : rm.method_8126()) {
            if (r.method_8118()) {
                special = r;
                break;
            }
        }
        // a stand-in player that is never added to the world. It has no connection, so the recipe packet the
        // book sends after its loop -- removeRecipes always, addRecipes when it added any -- is a
        // NullPointerException on player.connection, in 1.20.1 as in 1.20.2; the book has changed by then,
        // so what the calls did is read off the book
        class_3222 player = new class_3222(server, server.method_30002(), new GameProfile(UUID.fromString("0c5b5d2e-6a39-4b51-9d7e-2f1fd0c0ffee"), "cb_recipetest"));
        class_3441 book = player.method_14253();
        check(!book.method_22845(id), "a fresh recipe book does not know " + id);
        check(special != null && book.method_14903(List.of(special), player) == 0,
            "addRecipes of a special recipe adds none (and sends nothing): " + (special == null ? null : special.method_8114()));
        check(special != null && !book.method_22845(special.method_8114()), "the special recipe stays unknown");
        sendless("addRecipes", () -> book.method_14903(List.of(smelt), player));
        check(book.method_22845(id), "addRecipes([recipe], player) made the recipe known");
        sendless("removeRecipes", () -> book.method_14900(new ArrayList<>(List.of(smelt)), player));
        check(!book.method_22845(id), "removeRecipes([recipe], player) made it unknown again");

        TestBook tb = new TestBook();
        Object holder = ((Optional<?>) class_1863.class.getMethod("method_8130", class_2960.class).invoke(rm, id)).get();
        check(holderValue(holder), "vanilla's byKey answers a RecipeHolder: " + holder);
        Method remove = class_3441.class.getMethod("method_14900", Collection.class, class_3222.class);
        sendless("our removeRecipes override", () -> {
            try {
                remove.invoke(tb, List.of(holder), player);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause() instanceof RuntimeException r ? r : new RuntimeException(e.getCause());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        check(tb.log.contains("remove " + id), "vanilla handing our removeRecipes override RecipeHolders: the body read recipes: " + tb.log);
    }

    /** a recipe-book call whose packet send (after the book changed) has no connection to go to */
    static void sendless(String what, Runnable call) {
        try {
            call.run();
            System.out.println("CB-RECIPETEST note: " + what + " sent nothing");
        } catch (NullPointerException expected) {
            System.out.println("CB-RECIPETEST note: " + what + " reached the packet send (no connection): " + expected.getMessage());
        }
    }
}

package cb.recipeinput;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1263;
import net.minecraft.class_1277;
import net.minecraft.class_1715;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1856;
import net.minecraft.class_1860;
import net.minecraft.class_1863;
import net.minecraft.class_1865;
import net.minecraft.class_1869;
import net.minecraft.class_1937;
import net.minecraft.class_2371;
import net.minecraft.class_2952;
import net.minecraft.class_3218;
import net.minecraft.class_3861;
import net.minecraft.class_3955;
import net.minecraft.class_3956;
import net.minecraft.class_7225;
import net.minecraft.class_7709;
import net.minecraft.class_7710;
import net.minecraft.class_8566;
import net.minecraft.class_8786;
import net.minecraft.class_8957;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.20.6-era server mod (intermediary names, Fabric Loader API only) for the recipe-input bridge of the
 * 1.20.6 -> 1.21 step. 1.20.6 handed a recipe its container (Recipe&lt;C extends Container&gt;); 1.21 hands
 * it a RecipeInput. The mod declares recipes the 1.20.6 way -- a SmeltingRecipe subclass whose matches and
 * assemble call super, a CraftingRecipe of its own with per-slot leftovers, a ShapedRecipe subclass whose
 * matches and getRemainingItems call super -- and a PlaceRecipe (a class and a lambda). Once the server
 * runs it checks, on the server thread, 1.21's own entry points -- reached by reflection on 1.21's
 * methods, which no converter touches: what they see is what 1.21 code sees -- answering with the mod's
 * methods and the mod's super calls reaching vanilla's 1.21 implementations; and the mod's own 1.20.6
 * calls (Recipe, RecipeManager, CachedCheck) answering as 1.20.6 did. Every check prints one
 * "CB-INPUTTEST ok|FAIL ..." line; the last line is "CB-INPUTTEST DONE &lt;passed&gt;/&lt;checks&gt;".
 */
public class RecipeInputTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-INPUTTEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    /** iron ore smelts only two at a time; assemble makes five ingots */
    public static class TSmelt extends class_3861 {
        static class_1263 lastContainer;
        static int matchesCalls;

        public TSmelt() {
            super("", class_7709.field_40244, class_1856.method_8091(class_1802.field_8599), new class_1799(class_1802.field_8620), 0.7F, 200);
        }

        @Override
        public boolean method_8115(class_1263 container, class_1937 level) {
            matchesCalls++;
            lastContainer = container;
            return super.method_8115(container, level) && container.method_5438(0).method_7947() >= 2;
        }

        @Override
        public class_1799 method_8116(class_1263 container, class_7225.class_7874 registries) {
            class_1799 out = super.method_8116(container, registries);
            out.method_7939(5);
            return out;
        }
    }

    /** a crafting recipe of the mod's own: a stick in the first slot makes a diamond, a bucket is left in slot 0 */
    public static class TCraft implements class_3955 {
        static class_8566 lastGrid;

        @Override
        public boolean method_8115(class_8566 grid, class_1937 level) {
            lastGrid = grid;
            return grid.method_5438(0).method_31574(class_1802.field_8600) || grid.method_5438(4).method_31574(class_1802.field_8600);
        }

        @Override
        public class_1799 method_8116(class_8566 grid, class_7225.class_7874 registries) {
            return new class_1799(class_1802.field_8477);
        }

        @Override
        public boolean method_8113(int w, int h) {
            return true;
        }

        @Override
        public class_1799 method_8110(class_7225.class_7874 registries) {
            return new class_1799(class_1802.field_8477);
        }

        @Override
        public class_1865<?> method_8119() {
            return null;
        }

        @Override
        public class_7710 method_45441() {
            return class_7710.field_40251;
        }

        @Override
        public class_2371<class_1799> method_8111(class_8566 grid) {
            class_2371<class_1799> out = class_2371.method_10213(grid.method_5439(), class_1799.field_8037);
            out.set(0, new class_1799(class_1802.field_8550));
            return out;
        }
    }

    /** a one-stick shaped recipe that also takes a diamond; leftovers as vanilla's with a bowl in slot 0 */
    public static class TShaped extends class_1869 {
        public TShaped() {
            super("", class_7710.field_40251, class_8957.method_55086(Map.of('S', class_1856.method_8091(class_1802.field_8600)), "S"),
                new class_1799(class_1802.field_8477));
        }

        @Override
        public boolean method_17728(class_8566 grid, class_1937 level) {
            return super.method_17728(grid, level) || grid.method_5438(0).method_31574(class_1802.field_8477);
        }

        @Override
        public class_2371<class_1799> method_8111(class_8566 grid) {
            class_2371<class_1799> out = super.method_8111(grid);
            out.set(0, new class_1799(class_1802.field_8428));
            return out;
        }
    }

    /** a placer recording what 1.20.6's addItemToSlot was handed */
    public static class TPlace implements class_2952<Integer> {
        static String seen;

        @Override
        public void method_12815(Iterator<Integer> ingredients, int slot, int maxAmount, int y, int x) {
            seen = ingredients.next() + "," + slot + "," + maxAmount + ",y=" + y + ",x=" + x;
        }
    }

    static String lambdaSeen;

    @Override
    public void onInitialize() {
        Thread t = new Thread(() -> {
            for (int i = 0; i < 600; i++) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    return;
                }
                Object game = FabricLoader.getInstance().getGameInstance();
                if (game instanceof MinecraftServer server && server.method_3806() && server.method_30002() != null) {
                    server.execute(() -> run(server));
                    return;
                }
            }
        }, "cb-recipeinput");
        t.setDaemon(true);
        t.start();
    }

    static Method method(Class<?> c, String name, Class<?>... params) throws Exception {
        Method m = c.getMethod(name, params);
        m.setAccessible(true);
        return m;
    }

    static class_8566 grid(class_1799... items) {
        class_2371<class_1799> list = class_2371.method_10213(9, class_1799.field_8037);
        for (int i = 0; i < items.length; i++) {
            list.set(i, items[i]);
        }
        return new class_1715(null, 3, 3, list);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void run(MinecraftServer server) {
        try {
            class_3218 level = server.method_30002();
            Object registries = server.method_30611();
            Class<?> recipeInput = Class.forName("net.minecraft.class_9695");
            Class<?> craftingInput = Class.forName("net.minecraft.class_9694");
            Class<?> singleInput = Class.forName("net.minecraft.class_9696");
            Class<?> provider = Class.forName("net.minecraft.class_7225$class_7874");
            Method matches = method(class_1860.class, "method_8115", recipeInput, class_1937.class);
            Method assemble = method(class_1860.class, "method_8116", recipeInput, provider);
            Method remaining = method(class_1860.class, "method_8111", recipeInput);
            Method craftingOf = method(craftingInput, "method_59986", int.class, int.class, List.class);
            Constructor<?> single = singleInput.getConstructor(class_1799.class);

            // ---- a SmeltingRecipe subclass, asked by 1.21 ----
            TSmelt smelt = new TSmelt();
            Object two = single.newInstance(new class_1799(class_1802.field_8599, 2));
            Object one = single.newInstance(new class_1799(class_1802.field_8599, 1));
            check((boolean) matches.invoke(smelt, two, level), "1.21 Recipe.matches(two iron ore) reaches the mod's matches and its super");
            check(!(boolean) matches.invoke(smelt, one, level), "1.21 Recipe.matches(one iron ore) is the mod's answer (false)");
            check(!(boolean) matches.invoke(smelt, single.newInstance(new class_1799(class_1802.field_8477, 2)), level),
                "1.21 Recipe.matches(two diamonds): the mod's super call answers vanilla's ingredient test (false)");
            Method typedMatches = method(Class.forName("net.minecraft.class_1874"), "method_59981", singleInput, class_1937.class);
            int before = TSmelt.matchesCalls;
            check((boolean) typedMatches.invoke(smelt, two, level) && TSmelt.matchesCalls == before + 1,
                "1.21 AbstractCookingRecipe.matches(SingleRecipeInput) reaches the mod's matches once");
            class_1799 out = (class_1799) assemble.invoke(smelt, two, registries);
            check(out.method_31574(class_1802.field_8620) && out.method_7947() == 5,
                "1.21 Recipe.assemble: the mod's assemble over its super's (5 iron ingots): " + out);
            class_1277 furnace = new class_1277(new class_1799(class_1802.field_8599, 3), class_1799.field_8037, class_1799.field_8037);
            check(((class_1860) smelt).method_8115(furnace, level) && TSmelt.lastContainer == furnace,
                "the mod's own Recipe.matches(container) call: the mod sees its very container");

            // ---- a CraftingRecipe of the mod's own ----
            TCraft craft = new TCraft();
            Object stick = craftingOf.invoke(null, 1, 1, List.of(new class_1799(class_1802.field_8600)));
            check((boolean) matches.invoke(craft, stick, level), "1.21 Recipe.matches(CraftingInput stick) reaches the mod's crafting recipe");
            check(!(boolean) matches.invoke(craft, craftingOf.invoke(null, 1, 1, List.of(new class_1799(class_1802.field_8477))), level),
                "1.21 Recipe.matches(CraftingInput diamond) is false");
            Object pair = craftingOf.invoke(null, 2, 1, List.of(new class_1799(class_1802.field_8600), new class_1799(class_1802.field_8600)));
            class_2371<class_1799> left = (class_2371<class_1799>) remaining.invoke(craft, pair);
            check(left.size() == 2 && left.get(0).method_31574(class_1802.field_8550),
                "1.21 Recipe.getRemainingItems: the mod's leftovers, one per input slot: " + left);
            class_8566 g = grid(class_1799.field_8037, class_1799.field_8037, class_1799.field_8037, class_1799.field_8037,
                new class_1799(class_1802.field_8600));
            check(((class_1860) craft).method_8115(g, level) && TCraft.lastGrid == g, "the mod's own Recipe.matches(grid) call: the mod sees its grid");
            class_2371<class_1799> left9 = ((class_1860) craft).method_8111(g);
            check(left9.size() == 9 && left9.get(0).method_31574(class_1802.field_8550), "the mod's own Recipe.getRemainingItems(grid): 9 slots, as 1.20.6");

            // ---- a ShapedRecipe subclass ----
            TShaped shaped = new TShaped();
            check((boolean) matches.invoke(shaped, stick, level), "1.21 Recipe.matches(stick) on the shaped subclass: its super call matches");
            Object diamond = craftingOf.invoke(null, 1, 1, List.of(new class_1799(class_1802.field_8477)));
            check((boolean) matches.invoke(shaped, diamond, level), "1.21 Recipe.matches(diamond): the subclass's own answer");
            Method shapedTyped = method(class_1869.class, "method_17728", craftingInput, class_1937.class);
            check((boolean) shapedTyped.invoke(shaped, diamond, level), "1.21 ShapedRecipe.matches(CraftingInput) reaches the subclass");
            check(!(boolean) matches.invoke(shaped, craftingOf.invoke(null, 1, 1, List.of(new class_1799(class_1802.field_8428))), level),
                "1.21 Recipe.matches(bowl) is false");
            Object buckets = craftingOf.invoke(null, 2, 1, List.of(new class_1799(class_1802.field_8705), new class_1799(class_1802.field_8705)));
            class_2371<class_1799> sl = (class_2371<class_1799>) remaining.invoke(shaped, buckets);
            check(sl.size() == 2 && sl.get(0).method_31574(class_1802.field_8428) && sl.get(1).method_31574(class_1802.field_8550),
                "1.21 getRemainingItems: the subclass's bowl over its super's buckets: " + sl);
            class_2371<class_1799> sl9 = ((class_1860) shaped).method_8111(grid(new class_1799(class_1802.field_8705), new class_1799(class_1802.field_8705)));
            check(sl9.size() == 9 && sl9.get(0).method_31574(class_1802.field_8428) && sl9.get(1).method_31574(class_1802.field_8550),
                "the mod's own getRemainingItems(grid) on the subclass: 9 slots, super's bucket in slot 1");

            // ---- PlaceRecipe ----
            Method place = method(class_2952.class, "method_12815", Object.class, int.class, int.class, int.class, int.class);
            place.invoke(new TPlace(), 7, 3, 64, 1, 2);
            check("7,3,64,y=2,x=1".equals(TPlace.seen), "1.21 PlaceRecipe.addItemToSlot(item, slot, max, x, y) reaches the mod's (iterator, .., y, x): " + TPlace.seen);
            class_2952<Integer> lam = (ingredients, slot, maxAmount, y, x) -> lambdaSeen = ingredients.next() + ",y=" + y + ",x=" + x;
            place.invoke(lam, 9, 0, 1, 4, 5);
            check("9,y=5,x=4".equals(lambdaSeen), "1.21 PlaceRecipe on the mod's lambda: " + lambdaSeen);

            // ---- the mod's 1.20.6 calls into the recipe manager ----
            class_1863 manager = server.method_3772();
            Optional<class_8786<?>> found = (Optional) manager.method_8132(class_3956.field_17546, furnace, level);
            check(found.isPresent() && ((class_1860) found.get().comp_1933()).method_8110((class_7225.class_7874) registries).method_31574(class_1802.field_8620),
                "RecipeManager.getRecipeFor(SMELTING, furnace container) finds iron ore smelting");
            class_1863.class_7266 cached = class_1863.method_42302(class_3956.field_17546);
            Optional<class_8786<?>> viaCheck = (Optional) cached.method_42303(furnace, level);
            check(viaCheck.isPresent(), "RecipeManager.createCheck(SMELTING).getRecipeFor(container) finds it");
            class_2371<class_1799> rest = manager.method_8128(class_3956.field_17545, grid(new class_1799(class_1802.field_8705)), level);
            check(rest.size() == 9, "RecipeManager.getRemainingItemsFor(CRAFTING, grid): 9 slots: " + rest.size());
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            check(false, "threw " + e);
        }
        System.out.println("CB-INPUTTEST DONE " + (checks - FAILS.size()) + "/" + checks);
    }
}

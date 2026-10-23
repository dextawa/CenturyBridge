package top.dext.centurybridge.step.v1_21_2.rt;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1863;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2371;
import net.minecraft.class_2609;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_3751;
import net.minecraft.class_3920;
import net.minecraft.class_3924;
import net.minecraft.class_3956;
import net.minecraft.class_5712;
import net.minecraft.class_8174;
import net.minecraft.class_8786;
import net.minecraft.class_9696;
import net.minecraft.class_9895;
import net.minecraft.server.MinecraftServer;

/**
 * 1.21.1's block entity API on 1.21.2 (furnaces, campfires, brushable blocks, trial spawners, jigsaws).
 * <ul>
 * <li>Fuel: 1.21.1 kept one static fuel map (AbstractFurnaceBlockEntity.getFuel, built from the item tags and
 *     dropped by invalidateCache when they reloaded); 1.21.2 keeps FuelValues per server (rebuilt with its
 *     registries) and per client connection, built from the same list. The static API answers from the FuelValues
 *     of the side asking -- the running server's on its thread, else the client level's -- and invalidateCache has
 *     nothing left to drop (1.21.2 rebuilds FuelValues when the data reloads). getBurnDuration(stack) asks 1.21.2's
 *     getBurnDuration with its level's FuelValues.</li>
 * <li>Ticks and other server-side calls take the server level since 1.21.2: 1.21.1 ran them on server levels.</li>
 * <li>Campfire: 1.21.1's block entity kept its recipe check; 1.21.2's block hands one to cookTick. Each campfire keeps
 *     one here, made as 1.21.1's was (RecipeManager.createCheck(CAMPFIRE_COOKING)). getCookableRecipe and
 *     placeFood(entity, stack, cookTime) are 1.21.1's bodies; 1.21.2's placeFood looks the cook time up itself, so a
 *     1.21.1 campfire's placeFood override is asked with the cook time of the recipe 1.21.2 finds (1.21.1's
 *     CampfireBlock passed that one).</li>
 * <li>Brushing: 1.21.2 passes the brush stack (the loot context's tool) where 1.21.1 had the player: the item the
 *     player is using -- what 1.21.2's BrushItem passes.</li>
 * </ul>
 */
public final class CbLevelBlockEntities {

    // ---- the sides' fuel values ----

    private static volatile WeakReference<MinecraftServer> server = new WeakReference<>(null);
    private static volatile WeakReference<class_1937> clientLevel = new WeakReference<>(null);

    /** a server starting (CbLevelFuelServerMixin) */
    public static void server(MinecraftServer s) {
        server = new WeakReference<>(s);
    }

    /** a client level made (CbLevelFuelClientMixin) */
    public static void clientLevel(class_1937 level) {
        clientLevel = new WeakReference<>(level);
    }

    private static class_9895 fuel() {
        MinecraftServer s = server.get();
        if (s != null && s.method_18854()) {
            return s.method_62735();
        }
        class_1937 c = clientLevel.get();
        if (c != null) {
            return c.method_61269();
        }
        if (s != null) {
            return s.method_62735();
        }
        throw new IllegalStateException("1.21.1's furnace fuel API asked before any server or client level exists");
    }

    /** AbstractFurnaceBlockEntity.invalidateCache(): 1.21.2 rebuilds its FuelValues with the data; nothing is cached here */
    public static void method_56120() {
    }

    /** AbstractFurnaceBlockEntity.getFuel(): item to burn time, in 1.21.2's fuel order */
    public static Map<class_1792, Integer> method_11196() {
        class_9895 values = fuel();
        Map<class_1792, Integer> map = new LinkedHashMap<>();
        for (class_1792 item : values.method_61751()) {
            map.put(item, values.method_61755(new class_1799(item)));
        }
        return map;
    }

    /** AbstractFurnaceBlockEntity.isFuel(stack) */
    public static boolean method_11195(class_1799 stack) {
        return fuel().method_61752(stack);
    }

    /** AbstractFurnaceBlockEntity's protected getBurnDuration(fuelValues, stack) (CbLevelBlockEntityInvoker) */
    public interface FurnaceAccess {
        int cb$burnDuration(class_9895 fuel, class_1799 stack);
    }

    /** furnace.getBurnDuration(stack) */
    public static int method_11200(class_2609 self, class_1799 stack) {
        return ((FurnaceAccess) self).cb$burnDuration(fuelValues(self, stack), stack);
    }

    /** the fuel values a furnace burns by: its level's (also a 1.21.1 override's super call, frameDefaults) */
    public static class_9895 fuelValues(class_2609 self, class_1799 stack) {
        class_1937 level = self.method_10997();
        return level != null ? level.method_61269() : fuel();
    }

    /** a 1.21.1 server-side block entity call's level, as the server level 1.21.2 takes */
    public static class_3218 server(class_1937 level) {
        if (level instanceof class_3218 s) {
            return s;
        }
        throw new UnsupportedOperationException("1.21.1's block entity server tick or action called on a client level: "
            + "1.21.1 ran it on server levels, 1.21.2 only can");
    }

    // ---- campfire ----

    private static final Map<class_3924, class_1863.class_7266<class_9696, class_3920>> CHECKS = new WeakHashMap<>();

    /** a campfire's recipe check, made as 1.21.1's campfire block entity made its own */
    public static class_1863.class_7266<class_9696, class_3920> check(class_3924 campfire) {
        synchronized (CHECKS) {
            return CHECKS.computeIfAbsent(campfire, c -> class_1863.method_42302(class_3956.field_17549));
        }
    }

    /** CampfireBlockEntity.cookTick(level, pos, state, campfire) */
    public static void method_31666(class_1937 level, class_2338 pos, class_2680 state, class_3924 campfire) {
        class_3924.method_31666(server(level), pos, state, campfire, check(campfire));
    }

    /** CampfireBlockEntity's fields and markUpdated (CbLevelCampfireAccess) */
    public interface CampfireAccess {
        int[] cb$cookingProgress();

        int[] cb$cookingTime();

        void cb$markUpdated();
    }

    public interface LegacyCookableRecipe {
        Optional<class_8786<class_3920>> method_17502(class_1799 stack);
    }

    public interface LegacyPlaceFood {
        boolean method_17503(class_1309 entity, class_1799 stack, int cookTime);
    }

    public interface SuperPlaceFood {
        boolean cb$super$method_17503(class_3218 level, class_1309 entity, class_1799 stack);
    }

    /** campfire.getCookableRecipe(stack) */
    public static Optional<class_8786<class_3920>> method_17502(class_3924 self, class_1799 stack) {
        return self instanceof LegacyCookableRecipe l ? l.method_17502(stack) : cookableRecipe(self, stack);
    }

    /** 1.21.1's body: none when every slot is full, else the campfire's check for the stack */
    public static Optional<class_8786<class_3920>> cookableRecipe(class_3924 self, class_1799 stack) {
        class_2371<class_1799> items = self.method_17505();
        if (items.stream().noneMatch(class_1799::method_7960)) {
            return Optional.empty();
        }
        if (!(self.method_10997() instanceof class_3218 level)) {
            throw new UnsupportedOperationException("1.21.1's CampfireBlockEntity.getCookableRecipe on a client level: "
                + "a 1.21.2 client holds no recipes");
        }
        return check(self).method_42303(new class_9696(stack), level);
    }

    /** campfire.placeFood(entity, stack, cookTime) */
    public static boolean method_17503(class_3924 self, class_1309 entity, class_1799 stack, int cookTime) {
        return self instanceof LegacyPlaceFood l ? l.method_17503(entity, stack, cookTime) : placeFood(self, entity, stack, cookTime);
    }

    /** 1.21.1's placeFood body (also its super call) */
    public static boolean placeFood(Object campfire, class_1309 entity, class_1799 stack, int cookTime) {
        class_3924 self = (class_3924) campfire;
        class_2371<class_1799> items = self.method_17505();
        CampfireAccess access = (CampfireAccess) self;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).method_7960()) {
                access.cb$cookingTime()[i] = cookTime;
                access.cb$cookingProgress()[i] = 0;
                items.set(i, stack.method_60504(1, entity));
                self.method_10997().method_43276(class_5712.field_28733, self.method_11016(),
                    class_5712.class_7397.method_43286(entity, self.method_11010()));
                access.cb$markUpdated();
                return true;
            }
        }
        return false;
    }

    /** vanilla's placeFood(level, entity, stack) of a 1.21.1 campfire: its override, with the recipe's cook time */
    public static boolean placeFoodIn(LegacyPlaceFood self, class_3218 level, class_1309 entity, class_1799 stack) {
        Optional<class_8786<class_3920>> recipe = check((class_3924) self).method_42303(new class_9696(stack), level);
        return recipe.isPresent() && self.method_17503(entity, stack, recipe.get().comp_1933().method_8167());
    }

    public static boolean superPlaceFood(SuperPlaceFood self, class_1309 entity, class_1799 stack, int cookTime) {
        return placeFood(self, entity, stack, cookTime);
    }

    // ---- brushable ----

    /** the brush a 1.21.1 brushing player brushes with: the item being used */
    public static class_1799 brushOf(class_1657 player) {
        return player.method_6030();
    }

    /** brushable.brush(gameTime, player, direction) */
    public static boolean method_49215(class_8174 self, long gameTime, class_1657 player, class_2350 direction) {
        return self.method_49215(gameTime, server(self.method_10997()), player, direction, brushOf(player));
    }

    /** brushable.checkReset() */
    public static void method_49219(class_8174 self) {
        self.method_49219(server(self.method_10997()));
    }

    /** BrushableBlockEntity's private unpackLootTable (CbLevelBrushableMixin) */
    public interface BrushableAccess {
        void cb$unpackLootTable(class_3218 level, class_1657 player, class_1799 brush);
    }

    public interface LegacyUnpackLootTable {
        void method_49217(class_1657 player);
    }

    /** brushable.unpackLootTable(player) */
    public static void method_49217(class_8174 self, class_1657 player) {
        if (self instanceof LegacyUnpackLootTable l) {
            l.method_49217(player);
        } else {
            unpackLootTable(self, player);
        }
    }

    /** 1.21.1's unpackLootTable: 1.21.2's, on the block entity's level, with the player's brush */
    public static void unpackLootTable(class_8174 self, class_1657 player) {
        ((BrushableAccess) self).cb$unpackLootTable(server(self.method_10997()), player, brushOf(player));
    }

    /** frameDefaults: brush's level and brush, checkReset's level, for a 1.21.1 override entered directly */
    public static class_3218 brushLevel(class_8174 self, long gameTime, class_1657 player, class_2350 direction) {
        return server(self.method_10997());
    }

    public static class_1799 brushStack(class_8174 self, long gameTime, class_1657 player, class_2350 direction) {
        return brushOf(player);
    }

    public static class_3218 resetLevel(class_8174 self) {
        return server(self.method_10997());
    }

    // ---- trial spawner ----

    /** a 1.21.1 trial spawner config, as the holder 1.21.2's TrialSpawner takes (a direct holder: 1.21.1 held the value) */
    public static net.minecraft.class_6880<net.minecraft.class_8965> configHolder(net.minecraft.class_8965 config) {
        return net.minecraft.class_6880.method_40223(config);
    }

    // ---- jigsaw ----

    /** JigsawBlockEntity.JointType.byName(name) */
    public static Optional<class_3751.class_4991> method_26401(String name) {
        return Arrays.stream(class_3751.class_4991.values()).filter(t -> t.method_15434().equals(name)).findFirst();
    }

    private CbLevelBlockEntities() {
    }
}

package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import net.minecraft.class_10209;
import net.minecraft.class_10286;
import net.minecraft.class_1863;
import net.minecraft.class_1928;
import net.minecraft.class_1937;
import net.minecraft.class_3218;
import net.minecraft.class_3695;
import net.minecraft.class_5217;
import net.minecraft.class_5268;
import net.minecraft.class_7701;
import net.minecraft.class_9895;

/**
 * 1.21.1's Level-wide API on 1.21.2.
 * <ul>
 * <li>getGameRules(): CbLevelGameRules (ServerLevel's; a client level's default rules, as 1.21.1's ClientLevelData
 *     made them). LevelData.getGameRules(): ServerLevelData's, which 1.21.2 keeps; 1.21.2's client level data holds
 *     no rules, so asked of it the call throws explicitly (Level.getGameRules() answers a client level's).</li>
 * <li>getProfiler() / getProfilerSupplier(): 1.21.1's Level kept the profiler supplier its constructor was given
 *     (the server's or the client's); 1.21.2 has the thread's profiler, Profiler.get(), which is what those suppliers
 *     answered for vanilla levels. A 1.21.1 level subclass's own supplier is kept from its constructor call and
 *     answered as 1.21.1 did.</li>
 * <li>getRecipeManager(): a server level's RecipeManager is its 1.21.2 recipeAccess(). A 1.21.2 client holds no
 *     recipe manager (the server stopped sending recipes), so 1.21.1's client-side getRecipeManager has nothing to
 *     answer: an explicit UnsupportedOperationException.</li>
 * <li>GameRules: 1.21.1 had no feature-gated rules; a 1.21.1 GameRules() is 1.21.2's for the default feature set,
 *     copy() keeps the rules' own features, and the static visitGameRuleTypes visits every type (all features).</li>
 * <li>a 1.21.1 Level subclass is asked fuelValues(), which 1.21.1 did not have: the vanilla burn times for its
 *     registries and features, what 1.21.2 builds for a level.</li>
 * </ul>
 */
public final class CbLevelLevels {

    private static final Map<class_1937, Supplier<class_3695>> PROFILERS = new WeakHashMap<>();

    // ---- game rules ----

    /** level.getGameRules() */
    public static class_1928 method_8450(class_1937 self) {
        if (self instanceof LegacyGameRules l) {
            return l.method_8450();
        }
        return CbLevelGameRules.of(self);
    }

    /** super.getGameRules(): Level's */
    public static class_1928 gameRules(class_1937 self) {
        return CbLevelGameRules.of(self);
    }

    public interface LegacyGameRules {
        class_1928 method_8450();
    }

    /** levelData.getGameRules() */
    public static class_1928 method_146(class_5217 self) {
        if (self instanceof LegacyDataGameRules l) {
            return l.method_146();
        }
        if (self instanceof class_5268 server) {
            return server.method_146();
        }
        throw new UnsupportedOperationException("1.21.1's LevelData.getGameRules() asked of " + self.getClass().getName()
            + ": 1.21.2 keeps game rules on server level data only (a client level's are Level.getGameRules())");
    }

    public interface LegacyDataGameRules {
        class_1928 method_146();
    }

    /** the feature set a 1.21.1 GameRules is made for: the default one (1.21.1 had no feature-gated rules) */
    public static net.minecraft.class_7699 defaultFeatures() {
        return class_7701.field_40183;
    }

    /** new GameRules() */
    public static class_1928 rules() {
        return new class_1928(class_7701.field_40183);
    }

    /** gameRules.copy(): the same features */
    public static class_1928 method_27325(class_1928 self) {
        return self.method_27325(((RulesState) self).cb$features());
    }

    /** frameDefaults of a 1.21.1 copy() override entered directly: the rules' own features, as copy() keeps them */
    public static net.minecraft.class_7699 features(class_1928 self) {
        return ((RulesState) self).cb$features();
    }

    /** GameRules' feature set (CbLevelGameRulesAccess) */
    public interface RulesState {
        net.minecraft.class_7699 cb$features();
    }

    /** the static GameRules.visitGameRuleTypes(visitor): every type */
    public static void method_20744(class_1928.class_4311 visitor) {
        new class_1928(class_7701.field_40180.method_45383()).method_20744(visitor);
    }

    // ---- profiler ----

    /** the profiler supplier a 1.21.1 level subclass's constructor call handed (superCtorReshapes post) */
    public static void constructed(class_1937 level, Supplier<class_3695> profiler) {
        synchronized (PROFILERS) {
            PROFILERS.put(level, profiler);
        }
    }

    /** level.getProfiler() */
    public static class_3695 method_16107(class_1937 self) {
        if (self instanceof LegacyProfiler l) {
            return l.method_16107();
        }
        return profiler(self);
    }

    /** super.getProfiler(): Level's */
    public static class_3695 profiler(class_1937 self) {
        return method_24367(self).get();
    }

    /** level.getProfilerSupplier() */
    public static Supplier<class_3695> method_24367(class_1937 self) {
        if (self instanceof LegacyProfilerSupplier l) {
            return l.method_24367();
        }
        return profilerSupplier(self);
    }

    /** super.getProfilerSupplier(): Level's */
    public static Supplier<class_3695> profilerSupplier(class_1937 self) {
        Supplier<class_3695> own;
        synchronized (PROFILERS) {
            own = PROFILERS.get(self);
        }
        return own != null ? own : class_10209::method_64146;
    }

    public interface LegacyProfiler {
        class_3695 method_16107();
    }

    public interface LegacyProfilerSupplier {
        Supplier<class_3695> method_24367();
    }

    // ---- recipes, fuel ----

    /** level.getRecipeManager() */
    public static class_1863 method_8433(class_1937 self) {
        if (self instanceof LegacyRecipeManager l) {
            return l.method_8433();
        }
        class_10286 access = self.method_8433();
        if (access instanceof class_1863 manager) {
            return manager;
        }
        throw new UnsupportedOperationException("1.21.1's Level.getRecipeManager() on " + self.getClass().getName()
            + ": a 1.21.2 client receives no recipes from the server and holds no RecipeManager");
    }

    public interface LegacyRecipeManager {
        class_1863 method_8433();
    }

    /** a 1.21.1 level's recipe manager, as 1.21.2's recipe access (override hook unwrap) */
    public static class_10286 recipeAccess(class_1863 manager) {
        return manager;
    }

    /** fuelValues() of a 1.21.1 level subclass (abstractFills) */
    public static class_9895 fuelValues(class_1937 self) {
        return class_9895.method_61753(self.method_30349(), self.method_45162());
    }

    private CbLevelLevels() {
    }
}

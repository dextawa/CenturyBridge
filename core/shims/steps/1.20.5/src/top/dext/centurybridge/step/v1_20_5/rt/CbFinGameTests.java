package top.dext.centurybridge.step.v1_20_5.rt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.class_1657;
import net.minecraft.class_1934;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2470;
import net.minecraft.class_2633;
import net.minecraft.class_3218;
import net.minecraft.class_3341;
import net.minecraft.class_4514;
import net.minecraft.class_4517;
import net.minecraft.class_4518;
import net.minecraft.class_4519;
import net.minecraft.class_4520;
import net.minecraft.class_4521;
import net.minecraft.class_4525;
import net.minecraft.class_4529;
import net.minecraft.class_5624;
import net.minecraft.class_6306;
import net.minecraft.class_9113;
import net.minecraft.class_9118;
import top.dext.centurybridge.step.v1_20_5.facade.GameTestBatchRunner;
import top.dext.centurybridge.step.v1_20_5.facade.LegacyGameTestRunner;
import top.dext.centurybridge.step.v1_20_5.mixin.CbFinGameTestRegistryAccessor;
import top.dext.centurybridge.step.v1_20_5.mixin.CbGameTestTickerAccessor;

/**
 * 1.20.4's game test framework on 1.20.5. 1.20.5 rewrote it around a GameTestRunner instance: batches
 * became records holding GameTestInfos (1.20.4's held TestFunctions, made into infos by the batch runner
 * with its rotation and level), GameTestBatchRunner and GameTestRunner's static run methods are gone,
 * and GameTestInfo gained RetryOptions.
 *
 * <p>Batches: a batch a 1.20.4 mod builds holds TestFunctions, as in 1.20.4 (the record's erased
 * Collection takes them). It becomes runnable where 1.20.4 made infos of it -- the batch runner
 * (facade/GameTestBatchRunner), runTestBatches and GameTestServer.create -- with that runner's rotation
 * and level, and getTestFunctions() answers TestFunctions for either kind (mixin/CbFinGameTestBatchMixin).
 */
public final class CbFinGameTests {

    private CbFinGameTests() {
    }

    /** a 1.20.4 GameTestBatch's own methods, given back by mixin/CbFinGameTestBatchMixin */
    public interface LegacyBatch {
        /** getTestFunctions(): the TestFunctions, whether the batch holds them (1.20.4) or infos (1.20.5) */
        Collection<class_4529> method_22154();

        /** runBeforeBatchFunction(level) */
        void method_22153(class_3218 level);

        /** runAfterBatchFunction(level) */
        void method_32237(class_3218 level);
    }

    /** a 1.20.4 GameTestInfo's own methods, given back by mixin/CbFinGameTestInfoMixin */
    public interface LegacyInfo {
        /** spawnStructure(northWestCorner) */
        void method_22166(class_2338 pos);
    }

    /** GameTestServer's 1.20.4 batches, run instead of the ones 1.20.5 makes of their test functions */
    public interface LegacyBatchServer {
        void cb$setLegacyBatches(List<class_4514> batches);
    }

    /** RetryOptions.noRetries(): a 1.20.4 test ran once (reruns were the flaky/rerun-until-failed paths of its report listener) */
    public static class_9118 noRetries() {
        return class_9118.method_56245();
    }

    /** new GameTestInfo(function, rotation, level): 1.20.5's with no retries */
    public static class_4517 newInfo(class_4529 function, class_2470 rotation, class_3218 level) {
        return new class_4517(function, rotation, level, noRetries());
    }

    /**
     * new TestFunction(batch, test, structure, rotation, maxTicks, setupTicks, required, requiredSuccesses,
     * maxAttempts, function): 1.20.5's record takes (... required, manualOnly, maxAttempts, requiredSuccesses,
     * skyAccess, function). 1.20.4 had neither manual-only tests nor sky access: false, as 1.20.5's own
     * shorter constructors pass.
     */
    public static class_4529 newTestFunction(String batchName, String testName, String structureName, class_2470 rotation,
            int maxTicks, long setupTicks, boolean required, int requiredSuccesses, int maxAttempts, Consumer<net.minecraft.class_4516> function) {
        return new class_4529(batchName, testName, structureName, rotation, maxTicks, setupTicks, required, false,
            maxAttempts, requiredSuccesses, false, function);
    }

    /** the TestFunctions of a 1.20.4 batch (through its own, possibly overridden, getTestFunctions) */
    public static Collection<class_4529> functions(class_4514 batch) {
        return ((LegacyBatch) (Object) batch).method_22154();
    }

    /**
     * The batches as 1.20.5 runs them: each one's TestFunctions made into GameTestInfos with the given rotation
     * and level (1.20.4 GameTestBatchRunner: new GameTestInfo(function, rotation, level)); the before/after
     * functions are the batch's own run methods (an override of either is what runs).
     */
    public static List<class_4514> runnable(Collection<class_4514> batches, class_2470 rotation, class_3218 level) {
        List<class_4514> out = new ArrayList<>(batches.size());
        for (class_4514 batch : batches) {
            LegacyBatch legacy = (LegacyBatch) (Object) batch;
            List<class_4517> infos = new ArrayList<>();
            for (class_4529 function : legacy.method_22154()) {
                infos.add(newInfo(function, rotation, level));
            }
            out.add(new class_4514(batch.comp_2209(), ImmutableList.copyOf(infos), legacy::method_22153, legacy::method_32237));
        }
        return out;
    }

    /**
     * GameTestRunner.runTest(info, northWestCorner, ticker), 1.20.4's body: the ticker takes the info, a report
     * listener reports it, its structure is spawned at the corner. 1.20.5's report listener reruns flaky tests
     * through the runner its ticker hands it; a ticker no runner has started yet gets one with no batches, so a
     * rerun is spawned in place as 1.20.5 reruns any test.
     */
    public static void runTest(class_4517 info, class_2338 pos, class_4521 ticker) {
        ensureRunner(ticker, info.method_22176());
        ticker.method_22227(info);
        info.method_22167(new class_5624());
        ((LegacyInfo) info).method_22166(pos);
    }

    /** a runner for a ticker that has none (1.20.5's constructor makes itself the ticker's runner) */
    static void ensureRunner(class_4521 ticker, class_3218 level) {
        if (((CbGameTestTickerAccessor) (Object) ticker).cb$runner() == null) {
            new class_4520(class_9113.method_56188(), List.of(), level, ticker,
                net.minecraft.class_4520.class_9117.field_48491, net.minecraft.class_4520.class_9117.field_48492);
        }
    }

    /** GameTestRunner.runTestBatches(batches, corner, rotation, level, ticker, testsPerRow), 1.20.4's body */
    public static Collection<class_4517> runTestBatches(Collection<class_4514> batches, class_2338 pos, class_2470 rotation,
            class_3218 level, class_4521 ticker, int testsPerRow) {
        GameTestBatchRunner runner = new GameTestBatchRunner(batches, pos, rotation, level, ticker, testsPerRow);
        runner.method_22160();
        return runner.method_22155();
    }

    /** GameTestRunner.runTests(functions, corner, rotation, level, ticker, testsPerRow), 1.20.4's body */
    public static Collection<class_4517> runTests(Collection<class_4529> functions, class_2338 pos, class_2470 rotation,
            class_3218 level, class_4521 ticker, int testsPerRow) {
        return runTestBatches(groupTestsIntoBatches(functions), pos, rotation, level, ticker, testsPerRow);
    }

    /**
     * GameTestRunner.groupTestsIntoBatches(functions), 1.20.4's body: by batch name in first-seen order, 50 tests
     * a batch named "name:n", with the registry's before/after functions of the batch name. The batches hold
     * the TestFunctions, as 1.20.4's did.
     */
    public static Collection<class_4514> groupTestsIntoBatches(Collection<class_4529> functions) {
        Map<String, List<class_4529>> byBatch = functions.stream()
            .collect(Collectors.groupingBy(class_4529::comp_2218, LinkedHashMap::new, Collectors.toList()));
        ImmutableList.Builder<class_4514> out = ImmutableList.builder();
        for (Map.Entry<String, List<class_4529>> entry : byBatch.entrySet()) {
            String name = entry.getKey();
            Consumer<class_3218> before = class_4519.method_22198(name);
            Consumer<class_3218> after = class_4519.method_32244(name);
            int n = 0;
            for (List<class_4529> part : Iterables.partition(entry.getValue(), 50)) {
                n++;
                @SuppressWarnings({"unchecked", "rawtypes"})
                Collection<class_4517> held = (Collection) ImmutableList.copyOf(part);
                out.add(new class_4514(name + ":" + n, held, before, after));
            }
        }
        return out.build();
    }

    /** GameTestRunner.clearAllTests(level, center, ticker, radius), 1.20.4's body */
    public static void clearAllTests(class_3218 level, class_2338 pos, class_4521 ticker, int radius) {
        ticker.method_22226();
        class_2338.method_20437(pos.method_10069(-radius, 0, -radius), pos.method_10069(radius, 0, radius))
            .filter(p -> level.method_8320(p).method_27852(class_2246.field_10465))
            .forEach(p -> {
                class_2633 block = (class_2633) level.method_8321(p);
                class_4525.method_22246(class_4525.method_29410(block), level);
            });
    }

    /** new GameTestRunner(): 1.20.4's was a holder of static methods with no state; an inert 1.20.5 runner */
    public static class_4520 newRunner() {
        return new LegacyGameTestRunner();
    }

    /** GameTestRegistry.getTestFunctionsForClassName(name), 1.20.4's body: the matching functions as a list */
    public static Collection<class_4529> testFunctionsForClassName(String className) {
        return class_4519.method_22193(className).collect(Collectors.toList());
    }

    /** GameTestRegistry.getLastFailedTests(): 1.20.4 answered the live collection */
    public static Collection<class_4529> lastFailedTests() {
        return CbFinGameTestRegistryAccessor.cb$lastFailedTests();
    }

    /** StructureUtils.findStructureBlocks(center, radius, level), 1.20.4's body */
    public static Collection<class_2338> findStructureBlocks(class_2338 pos, int radius, class_3218 level) {
        Collection<class_2338> found = new ArrayList<>();
        class_3341 box = new class_3341(pos).method_35410(radius);
        class_2338.method_23627(box).forEach(p -> {
            if (level.method_8320(p).method_27852(class_2246.field_10465)) {
                found.add(p.method_10062());
            }
        });
        return found;
    }

    /**
     * GameTestServer.create(thread, storage, packs, batches, corner): 1.20.5's takes test functions and batches
     * them itself; it gets the batches' functions, and the 1.20.4 batches themselves run instead
     * (mixin/CbFinGameTestServerMixin), as 1.20.4's server ran exactly the batches it was given.
     */
    public static class_6306 createServer(Thread thread, net.minecraft.class_32.class_5143 storage, net.minecraft.class_3283 packs,
            Collection<class_4514> batches, class_2338 pos) {
        List<class_4529> functions = new ArrayList<>();
        for (class_4514 batch : batches) {
            functions.addAll(functions(batch));
        }
        class_6306 server = class_6306.method_40378(thread, storage, packs, functions, pos);
        ((LegacyBatchServer) server).cb$setLegacyBatches(List.copyOf(batches));
        return server;
    }

    /**
     * GameTestListener.testAddedForRerun(old, new, runner), which 1.20.4 listeners lack: a listener follows a
     * rerun only by adding itself to the new info (1.20.5's report listener does). In 1.20.4 a rerun info got
     * the report listener alone (ReportGameListener.rerunTest: info.addListener(this)), so a 1.20.4 listener
     * stays with the test it was added to.
     */
    public static void testAddedForRerun(class_4518 listener, class_4517 original, class_4517 rerun, class_4520 runner) {
        // a 1.20.4 listener does not follow reruns (see above)
    }

    /** GameTestHelper.makeMockSurvivalPlayer(), 1.20.4's body */
    public static class_1657 mockSurvivalPlayer(class_3218 level) {
        return new MockSurvivalPlayer(level);
    }

    /**
     * GameType.CREATIVE for GameTestHelper.makeMockPlayer(): 1.20.4's mock player answered isCreative() true
     * and isSpectator() false (its anonymous Player), as 1.20.5's mock player of game type CREATIVE does
     * (isSpectator: type == SPECTATOR, isCreative: type.isCreative())
     */
    public static class_1934 mockPlayerGameType() {
        return class_1934.field_9220;
    }

    /** 1.20.4's makeMockSurvivalPlayer() player: neither spectator nor creative (Player.isLocalPlayer() left false) */
    static final class MockSurvivalPlayer extends class_1657 {
        MockSurvivalPlayer(class_3218 level) {
            super(level, class_2338.field_10980, 0.0F, new GameProfile(UUID.randomUUID(), "test-mock-player"));
        }

        @Override
        public boolean method_7325() {
            return false;
        }

        @Override
        public boolean method_7337() {
            return false;
        }
    }
}

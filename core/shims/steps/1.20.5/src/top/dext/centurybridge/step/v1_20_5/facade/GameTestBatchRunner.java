package top.dext.centurybridge.step.v1_20_5.facade;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1923;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2470;
import net.minecraft.class_2633;
import net.minecraft.class_3218;
import net.minecraft.class_4514;
import net.minecraft.class_4517;
import net.minecraft.class_4518;
import net.minecraft.class_4520;
import net.minecraft.class_4521;
import net.minecraft.class_4524;
import net.minecraft.class_4525;
import net.minecraft.class_4529;
import org.slf4j.Logger;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinGameTests;

/**
 * 1.20.4's GameTestBatchRunner (removed in 1.20.5, whose GameTestRunner instance runs batches of
 * GameTestInfos) with 1.20.4's bodies on 1.20.5's API: the infos are made of each batch's TestFunctions with
 * the runner's rotation and level when it is built; start() runs the batches one after another (each one's
 * before function, its structures laid out testsPerRow to a row from the corner, a tracker whose completion
 * runs the after function, releases the forced chunks and starts the next batch), every test through
 * GameTestRunner.runTest(info, corner, ticker).
 */
public class GameTestBatchRunner {

    private static final Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    private final class_2338 firstTestNorthWestCorner;
    final class_3218 level;
    private final class_4521 testTicker;
    private final int testsPerRow;
    private final List<class_4517> allTestInfos;
    private final List<Pair<class_4514, Collection<class_4517>>> batches;
    private int count;
    private class_238 rowBounds;
    private final class_2338.class_2339 nextTestNorthWestCorner;

    public GameTestBatchRunner(Collection<class_4514> batches, class_2338 corner, class_2470 rotation, class_3218 level,
            class_4521 ticker, int testsPerRow) {
        this.nextTestNorthWestCorner = corner.method_25503();
        this.rowBounds = new class_238(this.nextTestNorthWestCorner);
        this.firstTestNorthWestCorner = corner;
        this.level = level;
        this.testTicker = ticker;
        this.testsPerRow = testsPerRow;
        ImmutableList.Builder<Pair<class_4514, Collection<class_4517>>> pairs = ImmutableList.builder();
        for (class_4514 batch : batches) {
            ImmutableList.Builder<class_4517> infos = ImmutableList.builder();
            for (class_4529 function : CbFinGameTests.functions(batch)) {
                infos.add(CbFinGameTests.newInfo(function, rotation, level));
            }
            pairs.add(Pair.of(batch, infos.build()));
        }
        this.batches = pairs.build();
        this.allTestInfos = this.batches.stream().flatMap(p -> p.getSecond().stream()).collect(ImmutableList.toImmutableList());
    }

    /** getTestInfos() */
    public List<class_4517> method_22155() {
        return this.allTestInfos;
    }

    /** start() */
    public void method_22160() {
        this.cbRunBatch(0);
    }

    /** runBatch(index), package-private in 1.20.4 */
    void cbRunBatch(int index) {
        if (index >= this.batches.size()) {
            return;
        }
        Pair<class_4514, Collection<class_4517>> pair = this.batches.get(index);
        class_4514 batch = pair.getFirst();
        Collection<class_4517> infos = pair.getSecond();
        Map<class_4517, class_2338> corners = this.cbCreateStructuresForBatch(infos);
        String name = batch.comp_2209();
        LOGGER.info("Running test batch '{}' ({} tests)...", name, infos.size());
        ((CbFinGameTests.LegacyBatch) (Object) batch).method_22153(this.level);
        class_4524 tracker = new class_4524();
        infos.forEach(tracker::method_22230);
        tracker.method_22231(new BatchListener(tracker, batch, index));
        infos.forEach(info -> CbFinGameTests.runTest(info, corners.get(info), this.testTicker));
    }

    private Map<class_4517, class_2338> cbCreateStructuresForBatch(Collection<class_4517> infos) {
        Map<class_4517, class_2338> corners = new HashMap<>();
        for (class_4517 info : infos) {
            class_2338 corner = new class_2338(this.nextTestNorthWestCorner);
            class_2633 block = class_4525.method_54903(info, corner, info.method_29402(), this.level);
            class_238 bounds = class_4525.method_22242(block);
            info.method_23635(block.method_11016());
            corners.put(info, new class_2338(this.nextTestNorthWestCorner));
            this.rowBounds = this.rowBounds.method_991(bounds);
            this.nextTestNorthWestCorner.method_10100((int) bounds.method_17939() + 5, 0, 0);
            if (this.count++ % this.testsPerRow == this.testsPerRow - 1) {
                this.nextTestNorthWestCorner.method_10100(0, 0, (int) this.rowBounds.method_17941() + 6);
                this.nextTestNorthWestCorner.method_33097(this.firstTestNorthWestCorner.method_10263());
                this.rowBounds = new class_238(this.nextTestNorthWestCorner);
            }
        }
        return corners;
    }

    /** 1.20.4's batch listener: when the batch's tracker is done, the after function, the forced chunks released, the next batch */
    private final class BatchListener implements class_4518 {
        private final class_4524 tracker;
        private final class_4514 batch;
        private final int index;

        BatchListener(class_4524 tracker, class_4514 batch, int index) {
            this.tracker = tracker;
            this.batch = batch;
            this.index = index;
        }

        private void testCompleted() {
            if (this.tracker.method_22239()) {
                ((CbFinGameTests.LegacyBatch) (Object) this.batch).method_32237(GameTestBatchRunner.this.level);
                LongSet forced = new LongArraySet(GameTestBatchRunner.this.level.method_17984());
                forced.forEach((long chunk) -> GameTestBatchRunner.this.level.method_17988(class_1923.method_8325(chunk), class_1923.method_8332(chunk), false));
                GameTestBatchRunner.this.cbRunBatch(this.index + 1);
            }
        }

        @Override
        public void method_22188(class_4517 info) {
        }

        @Override
        public void method_33317(class_4517 info, class_4520 runner) {
            this.testCompleted();
        }

        @Override
        public void method_22190(class_4517 info, class_4520 runner) {
            this.testCompleted();
        }

        /** 1.20.4's batch listener stayed with the tests it tracked (CbFinGameTests.testAddedForRerun) */
        @Override
        public void method_56216(class_4517 original, class_4517 rerun, class_4520 runner) {
        }
    }
}

package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1936;
import net.minecraft.class_2680;
import net.minecraft.class_4863;
import net.minecraft.class_4865;

/**
 * 1.21.1's GrowingPlantBlock.getStateForPlacement(LevelAccessor) on 1.21.2's getStateForPlacement(RandomSource).
 * 1.21.1 asked it with the level (placing a growing plant, and the body block's updateShape asking its head block);
 * 1.21.2 asks it with the level's random. A 1.21.1 override (LegacyPlacementState, adapterHooks) is asked with the
 * level at both of 1.21.2's call sites (CbLevelGrowingPlantMixin, CbLevelGrowingPlantBodyMixin) and by a 1.21.1 call;
 * a block without one answers 1.21.2's method with the level's random -- 1.21.1's bodies drew from it the same way.
 * A 1.21.1 override's super call is 1.21.1's body on the level's random, 1.21.2's body read off its bytecode: the
 * default state, and the head block's with AGE drawn below 25.
 */
public final class CbLevelGrowingPlants {
    private CbLevelGrowingPlants() {
    }

    /** a 1.21.1 GrowingPlantBlock subclass declaring getStateForPlacement(LevelAccessor) */
    public interface LegacyPlacementState {
        class_2680 method_24948(class_1936 level);
    }

    /** block.getStateForPlacement(level) */
    public static class_2680 method_24948(class_4863 self, class_1936 level) {
        return self instanceof LegacyPlacementState l ? l.method_24948(level) : self.method_24948(level.method_8409());
    }

    /** headBlock.getStateForPlacement(level) */
    public static class_2680 method_24948(class_4865 self, class_1936 level) {
        return method_24948((class_4863) self, level);
    }

    /** super.getStateForPlacement(level) reaching GrowingPlantBlock's body */
    public static class_2680 superPlacementState(class_4863 self, class_1936 level) {
        return self.method_9564();
    }

    /** super.getStateForPlacement(level) reaching GrowingPlantHeadBlock's body */
    public static class_2680 superHeadPlacementState(class_4865 self, class_1936 level) {
        return self.method_9564().method_11657(class_4865.field_22509, level.method_8409().method_43048(25));
    }
}

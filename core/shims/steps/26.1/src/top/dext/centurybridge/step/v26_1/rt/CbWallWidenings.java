package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;

/**
 * Parameters 26.1 widened where 1.21.11's value passes as it is (callReshapes "conv": a reshaped call's operand
 * takes the new parameter's type), and the one it narrowed to what 1.21.11's callers passed.
 */
public final class CbWallWidenings {
    private CbWallWidenings() {
    }

    /** a player where 26.1 takes a living entity (MaceItem.calculateImpactPosition) */
    public static LivingEntity living(ServerPlayer player) {
        return player;
    }

    /** a stack where 26.1 takes an item instance (BundleContents.getWeight) */
    public static ItemInstance instance(ItemStack stack) {
        return stack;
    }

    /**
     * CherryTrunkPlacer.generateBranch's level: 26.1 takes the WorldGenLevel that 1.21.11's tree feature handed its
     * trunk placers as a LevelSimulatedReader (TreeFeature.doPlace); another reader has no 26.1 counterpart
     */
    public static WorldGenLevel worldGenLevel(LevelSimulatedReader level) {
        if (level instanceof WorldGenLevel worldGen) {
            return worldGen;
        }
        throw new IllegalArgumentException("26.1 places cherry branches only in a WorldGenLevel, given " + level);
    }
}

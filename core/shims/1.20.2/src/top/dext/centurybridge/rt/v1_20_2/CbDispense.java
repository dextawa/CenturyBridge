package top.dext.centurybridge.rt.v1_20_2;

/**
 * DispenserBlock.dispenseFrom(level, pos), 1.20.1's shape. 1.20.2 hands it the block state too --
 * dispenseFrom(level, state, pos) -- and builds the dispense source from it (BlockSource(level, pos,
 * state, blockEntity)). 1.20.1's body built BlockSourceImpl(level, pos), whose getBlockState() read
 * level.getBlockState(pos) whenever a behaviour asked: the state 1.20.2 takes is that one.
 */
public final class CbDispense {

    /**
     * this.dispenseFrom(level, pos) in a dispenser or dropper subclass that inherits it (protected: a
     * subclass's own call), reshaped to 1.20.2's: the state at the position, as 1.20.1's source read it
     */
    public static net.minecraft.class_2680 stateAt(net.minecraft.class_3218 level, net.minecraft.class_2338 pos) {
        return level.method_8320(pos);
    }

    /**
     * frameDefaults of the dispenseFrom hook: a 1.20.1 override's super.dispenseFrom(level, pos) when
     * the override was entered directly (no frame of vanilla's call): the same state
     */
    public static net.minecraft.class_2680 stateAt(net.minecraft.class_2315 self, net.minecraft.class_3218 level,
            net.minecraft.class_2338 pos) {
        return level.method_8320(pos);
    }

    private CbDispense() {
    }
}

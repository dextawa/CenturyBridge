package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 block-interface contracts on top of 1.20.2's, reached from the methods the
 * converter synthesizes into a legacy block (adapterHooks).
 */
public final class CbBlocks {

    /**
     * Fertilizable.isFertilizable(world, pos, state): 1.20.2 dropped the isClient flag
     * vanilla always filled from the world itself.
     */
    public static boolean isFertilizable(CbLegacyFertilizable block, net.minecraft.class_4538 world,
            net.minecraft.class_2338 pos, net.minecraft.class_2680 state) {
        return block.method_9651(world, pos, state, world.method_8608());
    }

    /**
     * FluidFillable.canFillWithFluid(player, world, pos, state, fluid): 1.20.2 added the
     * (nullable) player doing the filling; the 1.20.1 answer never depended on one.
     */
    public static boolean canFillWithFluid(CbLegacyFluidFillable block, net.minecraft.class_1657 player,
            net.minecraft.class_1922 world, net.minecraft.class_2338 pos, net.minecraft.class_2680 state,
            net.minecraft.class_3611 fluid) {
        return block.method_10310(world, pos, state, fluid);
    }

    private CbBlocks() {
    }
}

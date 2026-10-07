package top.dext.centurybridge.facade.v26_2;

/** Facade for 1.20.1 net/minecraft/class_2647 (worldgen tree/feature decorator
 *  base), deleted on 26.2. abstract method_11430 (getConfiguredFeature) kept
 *  abstract (mod supplies it); method_11431 (place) default false. Member names
 *  are intermediary tokens. */
public abstract class class_2647 {
    public class_2647() {}

    /** getConfiguredFeature (method_11430) */
    protected abstract net.minecraft.resources.ResourceKey<?> method_11430(
            net.minecraft.util.RandomSource rand, boolean beehive);

    /** place (method_11431) -> no-op false */
    public boolean method_11431(net.minecraft.server.level.ServerLevel level,
            net.minecraft.world.level.chunk.ChunkGenerator gen, net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState state, net.minecraft.util.RandomSource rand) {
        return false;
    }
}

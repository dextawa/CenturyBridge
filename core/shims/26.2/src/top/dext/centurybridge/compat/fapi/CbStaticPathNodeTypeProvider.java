package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.registry.LandPathTypeRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;

/**
 * was LandPathNodeTypesRegistry.StaticPathNodeTypeProvider: the SAM was renamed
 * getPathNodeType -> getPathType in 26.x. The old name stays the single abstract
 * method (1.20.1 lambdas and hephaestus' six block classes implement it) and the
 * new one is derived, so instances are real 26.2 providers.
 */
@FunctionalInterface
public interface CbStaticPathNodeTypeProvider extends LandPathTypeRegistry.StaticPathTypeProvider {

    PathType getPathNodeType(BlockState state, boolean neighbor);

    @Override
    default PathType getPathType(BlockState state, boolean neighbor) {
        return getPathNodeType(state, neighbor);
    }
}

package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.registry.LandPathTypeRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.pathfinder.PathType;

/** fabric-content-registries-v0's LandPathNodeTypesRegistry (5 corpus mods): renamed LandPathTypeRegistry in 26.x, provider SAM renamed (see CbStaticPathNodeTypeProvider). */
public final class CbLandPathNodeTypesRegistry {

    public static void register(Block block, PathType nodeType, PathType nodeTypeIfNeighbor) {
        LandPathTypeRegistry.register(block, nodeType, nodeTypeIfNeighbor);
    }

    public static void register(Block block, CbStaticPathNodeTypeProvider provider) {
        LandPathTypeRegistry.register(block, provider);
    }

    private CbLandPathNodeTypesRegistry() {
    }
}

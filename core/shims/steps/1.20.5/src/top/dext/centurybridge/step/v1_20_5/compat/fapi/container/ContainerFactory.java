package top.dext.centurybridge.step.v1_20_5.compat.fapi.container;

/**
 * Fabric API 0.97.3's (1.20.4) fabric-containers-v0 ContainerFactory, a module 1.20.5's Fabric API no
 * longer ships: builds a screen handler (or, client side, a screen) for a container id.
 */
@Deprecated
@FunctionalInterface
public interface ContainerFactory<T> {

    T create(int syncId, net.minecraft.class_2960 identifier, net.minecraft.class_1657 player, net.minecraft.class_2540 buf);
}

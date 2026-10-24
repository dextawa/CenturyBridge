package top.dext.centurybridge.step.v26_1.facade;

import net.minecraft.world.Container;

/**
 * 1.21.11's ContainerListener: told when a SimpleContainer it listens to changed (setChanged). 26.1 dropped the
 * listeners; SimpleContainer keeps them again (mixin/CbWallSimpleContainerMixin). The method keeps its
 * intermediary name (a mod's lambda or class implements it).
 */
@FunctionalInterface
public interface ContainerListener {

    /** containerChanged(container) */
    void method_5453(Container container);
}

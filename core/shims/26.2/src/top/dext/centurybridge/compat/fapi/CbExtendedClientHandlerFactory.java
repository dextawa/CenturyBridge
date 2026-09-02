package top.dext.centurybridge.compat.fapi;

import net.minecraft.world.inventory.AbstractContainerMenu;

/** was ScreenHandlerRegistry.ExtendedClientHandlerFactory: an empty sub-interface, the SAM is inherited */
@FunctionalInterface
public interface CbExtendedClientHandlerFactory<T extends AbstractContainerMenu> extends CbExtendedFactory<T> {
}

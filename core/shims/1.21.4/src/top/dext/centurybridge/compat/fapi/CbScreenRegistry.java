package top.dext.centurybridge.compat.fapi;

import net.minecraft.class_1703;
import net.minecraft.class_3917;
import net.minecraft.class_3929;
import net.minecraft.class_3936;
import net.minecraft.class_437;
import top.dext.centurybridge.shims.v1_21_4.HandledScreensAccessor;

/**
 * fabric-screen-handler-api-v1's client ScreenRegistry (deleted in 1.20.5; 4 corpus mods).
 * The 1.20.1 ScreenRegistry.Factory SAM ({@code create(handler, inventory, title)}) is exactly
 * vanilla HandledScreens.Provider's, so the converter renames the factory onto the vanilla
 * interface and this registers it through HandledScreens' private register (invoker mixin).
 * Client only: a dedicated server never loads it.
 * classRename: net/fabricmc/fabric/api/client/screenhandler/v1/ScreenRegistry -> this class.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbScreenRegistry {

    public static <H extends class_1703, S extends class_437 & class_3936<H>> void register(
            class_3917<? extends H> type, class_3929.class_3930<H, S> factory) {
        HandledScreensAccessor.cbRegister(type, factory);
    }

    private CbScreenRegistry() {
    }
}

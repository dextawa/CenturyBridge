package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * The 1.20.1 ExtendedScreenHandlerFactory contract (23 corpus implementors): the screen-opening
 * data is written into a PacketByteBuf ({@code writeScreenOpeningData}). The 26.x interface
 * (ExtendedMenuProvider) asks for a typed value ({@code D getScreenOpeningData(player)}), which a
 * 1.20.1 class never implements (AbstractMethodError at container open). The converter renames
 * the interface here: the old abstract method stays abstract, the new one is derived from it,
 * and the data is the raw buffer that {@link CbExtendedScreenHandlerType} transports byte-for-byte.
 */
public interface CbExtendedScreenHandlerFactory extends ExtendedMenuProvider<FriendlyByteBuf> {

    void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf);

    @Override
    default FriendlyByteBuf getScreenOpeningData(ServerPlayer player) {
        FriendlyByteBuf buf = new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), player.registryAccess());
        writeScreenOpeningData(player, buf);
        return buf;
    }
}

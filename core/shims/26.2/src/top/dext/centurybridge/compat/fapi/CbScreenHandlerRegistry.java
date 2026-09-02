package top.dext.centurybridge.compat.fapi;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * fabric-screen-handler-api-v1's ScreenHandlerRegistry (deleted in 1.20.5; 4 corpus mods).
 * registerSimple is a plain vanilla MenuType registration (the 1.20.1 factory SAM is the vanilla
 * MenuSupplier's, the converter renames it there); registerExtended builds a
 * {@link CbExtendedScreenHandlerType} around the buffer-reading factory. The client-side
 * ScreenRegistry needs no compat class: its register(type, factory) and Factory SAM are exactly
 * MenuScreens.register / MenuScreens.ScreenConstructor (public on 26.2), a pure rename.
 */
public final class CbScreenHandlerRegistry {

    /** the 1.20.1 opening data verbatim: a length-prefixed byte array around the buffer, read back registry-aware */
    public static final StreamCodec<RegistryFriendlyByteBuf, FriendlyByteBuf> RAW = StreamCodec.of(
        (buf, data) -> {
            byte[] bytes = new byte[data.readableBytes()];
            data.getBytes(data.readerIndex(), bytes);
            buf.writeByteArray(bytes);
        },
        buf -> new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.wrappedBuffer(buf.readByteArray()), buf.registryAccess()));

    public static <T extends AbstractContainerMenu> MenuType<T> registerSimple(Identifier id, MenuType.MenuSupplier<T> factory) {
        return Registry.register(BuiltInRegistries.MENU, id, new MenuType<>(factory, FeatureFlags.VANILLA_SET));
    }

    public static <T extends AbstractContainerMenu> MenuType<T> registerExtended(Identifier id, CbExtendedClientHandlerFactory<T> factory) {
        return Registry.register(BuiltInRegistries.MENU, id, new CbExtendedScreenHandlerType<T>(factory));
    }

    private CbScreenHandlerRegistry() {
    }
}

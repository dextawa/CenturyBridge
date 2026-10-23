package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.container;

import io.netty.buffer.Unpooled;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.container.ContainerFactory;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.container.ContainerProviderRegistry;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.networking.v1.ServerPlayNetworking;

/**
 * 1.20.4's fabric-containers-v0 ContainerProviderImpl on 1.20.5 (Fabric API, Apache-2.0): opening a
 * container takes the player's next screen handler sync id (CbFapiContainerPlayerMixin, as 1.20.4's
 * ServerPlayerEntitySyncHook), sends the container id, sync id and the mod's extra data to the client on
 * fabric:container/open (1.20.4's channel and layout, through this layer's 1.20.4 networking), and opens
 * the server's screen handler from the same data.
 */
public final class CbContainerProviderImpl implements ContainerProviderRegistry {

    public static final net.minecraft.class_2960 OPEN_CONTAINER = new net.minecraft.class_2960("fabric", "container/open");
    private static final Logger LOGGER = LoggerFactory.getLogger("centurybridge/fabric-containers-v0");
    private static final Map<net.minecraft.class_2960, ContainerFactory<net.minecraft.class_1703>> FACTORIES = new HashMap<>();

    /** a ServerPlayerEntity's next screen handler sync id (CbFapiContainerPlayerMixin) */
    public interface SyncHook {
        int cb$incrementSyncId();

        void cb$onScreenHandlerOpened(net.minecraft.class_1703 handler);
    }

    @Override
    public void registerFactory(net.minecraft.class_2960 identifier, ContainerFactory<net.minecraft.class_1703> factory) {
        if (FACTORIES.containsKey(identifier)) {
            throw new RuntimeException("A factory has already been registered as " + identifier.toString());
        }
        FACTORIES.put(identifier, factory);
    }

    @Override
    public void openContainer(net.minecraft.class_2960 identifier, net.minecraft.class_1657 player, Consumer<net.minecraft.class_2540> writer) {
        if (!(player instanceof net.minecraft.class_3222)) {
            LOGGER.warn("Please only use ContainerProviderRegistry.openContainer() with server-sided player entities!");
            return;
        }
        openContainer(identifier, (net.minecraft.class_3222) player, writer);
    }

    @Override
    public void openContainer(net.minecraft.class_2960 identifier, net.minecraft.class_3222 player, Consumer<net.minecraft.class_2540> writer) {
        SyncHook hook = (SyncHook) player;
        int syncId = hook.cb$incrementSyncId();
        net.minecraft.class_2540 buf = new net.minecraft.class_2540(Unpooled.buffer());
        buf.method_10812(identifier);
        buf.method_52997(syncId);
        writer.accept(buf);
        player.field_13987.method_14364(ServerPlayNetworking.createS2CPacket(OPEN_CONTAINER, buf));
        net.minecraft.class_2540 clonedBuf = new net.minecraft.class_2540(buf.duplicate());
        clonedBuf.method_10810();
        clonedBuf.readUnsignedByte();
        net.minecraft.class_1703 screenHandler = createContainer(syncId, identifier, player, clonedBuf);
        if (screenHandler == null) {
            return;
        }
        player.field_7512 = screenHandler;
        hook.cb$onScreenHandlerOpened(screenHandler);
    }

    @SuppressWarnings("unchecked")
    public <C extends net.minecraft.class_1703> C createContainer(int syncId, net.minecraft.class_2960 identifier, net.minecraft.class_1657 player,
                                                                   net.minecraft.class_2540 buf) {
        ContainerFactory<net.minecraft.class_1703> factory = FACTORIES.get(identifier);
        if (factory == null) {
            LOGGER.error("No container factory found for {}!", identifier.toString());
            return null;
        }
        return (C) factory.create(syncId, identifier, player, buf);
    }
}

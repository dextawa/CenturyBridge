package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.container;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.client.networking.v1.ClientPlayNetworking;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.client.screen.ContainerScreenFactory;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.client.screen.ScreenProviderRegistry;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.container.ContainerFactory;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.container.ContainerProviderRegistry;

/**
 * 1.20.4's fabric-containers-v0 ScreenProviderRegistryImpl on 1.20.5 (Fabric API, Apache-2.0): the client
 * side of fabric:container/open, its receiver registered from this layer's client entrypoint
 * (compat/fapi/CbFapiClientInit) as 1.20.4's module did from its own.
 */
@Environment(EnvType.CLIENT)
public final class CbScreenProviderRegistryImpl implements ScreenProviderRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger("centurybridge/fabric-containers-v0");
    private static final Map<net.minecraft.class_2960, ContainerFactory<net.minecraft.class_465>> FACTORIES = new HashMap<>();

    @Override
    public void registerFactory(net.minecraft.class_2960 identifier, ContainerFactory<net.minecraft.class_465> factory) {
        if (FACTORIES.containsKey(identifier)) {
            throw new RuntimeException("A factory has already been registered as " + identifier + "!");
        }
        FACTORIES.put(identifier, factory);
    }

    @Override
    public <C extends net.minecraft.class_1703> void registerFactory(net.minecraft.class_2960 identifier,
                                                                    ContainerScreenFactory<C> containerScreenFactory) {
        registerFactory(identifier, (syncId, identifier1, player, buf) -> {
            C container = ((CbContainerProviderImpl) ContainerProviderRegistry.INSTANCE).createContainer(syncId, identifier1, player, buf);
            if (container == null) {
                LOGGER.error("Could not open container for {} - a null object was created!", identifier1.toString());
                return null;
            }
            return containerScreenFactory.create(container);
        });
    }

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(CbContainerProviderImpl.OPEN_CONTAINER, (client, handler, buf, responseSender) -> {
            net.minecraft.class_2960 identifier = buf.method_10810();
            int syncId = buf.readUnsignedByte();
            // retained: the screen handler opens with its extra data on the client thread
            buf.retain();
            client.execute(() -> {
                try {
                    ContainerFactory<net.minecraft.class_465> factory = FACTORIES.get(identifier);
                    if (factory == null) {
                        LOGGER.error("No GUI factory found for {}!", identifier.toString());
                        return;
                    }
                    net.minecraft.class_746 player = client.field_1724;
                    net.minecraft.class_465<?> gui = factory.create(syncId, identifier, player, buf);
                    player.field_7512 = gui.method_17577();
                    client.method_1507(gui);
                } finally {
                    buf.release();
                }
            });
        });
    }
}

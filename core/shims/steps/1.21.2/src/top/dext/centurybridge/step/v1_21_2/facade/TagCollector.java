package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * TagCollector (class_9175) of 1.21.1: the client's collector of the tags a server sends, applied to the
 * client's registries once the registries are known. 1.21.2 folded it into RegistryDataCollector; this is
 * 1.21.1's class on 1.21.2's tag binding (a payload resolved against its registry and bound through the
 * registry's prepareTagReload, as 1.21.2's own collector binds it).
 *
 * updateTags(registries, isMemoryConnection), as 1.21.1: over a memory connection (singleplayer, the
 * registries are the server's own) only the payloads of the network-synchronised registries are bound;
 * otherwise every registry that is not network-synchronised has its tags cleared and every payload is
 * bound. 1.21.1 then rebuilt two static caches (Blocks' shape cache and the furnace fuel map); 1.21.2 has
 * neither (shapes and fuel values are computed from the bound tags).
 */
@Environment(EnvType.CLIENT)
public class TagCollector {

    public final Map<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>, net.minecraft.class_6864.class_5748> field_48770 = new HashMap<>();

    public TagCollector() {
    }

    /** append(registry, payload) */
    public void method_56590(net.minecraft.class_5321<? extends net.minecraft.class_2378<?>> registry,
            net.minecraft.class_6864.class_5748 payload) {
        this.field_48770.put(registry, payload);
    }

    /** updateTags(registries, isMemoryConnection) */
    public void method_57053(net.minecraft.class_5455 registries, boolean memoryConnection) {
        if (memoryConnection) {
            this.field_48770.forEach((key, payload) -> {
                if (net.minecraft.class_7782.method_62704(key)) {
                    bind(registries.method_30530(key), payload);
                }
            });
        } else {
            registries.method_40311()
                .filter(entry -> !net.minecraft.class_7782.method_62704(entry.comp_350()))
                .forEach(entry -> clear(entry.comp_351()));
            this.field_48770.forEach((key, payload) -> bind(registries.method_30530(key), payload));
        }
    }

    private static <T> void bind(net.minecraft.class_2378<T> registry, net.minecraft.class_6864.class_5748 payload) {
        registry.method_62683(payload.method_61316(registry)).method_62696();
    }

    /** 1.21.1's Registry.resetTags: no tag left bound */
    private static <T> void clear(net.minecraft.class_2378<T> registry) {
        registry.method_62683(new net.minecraft.class_3503.class_6863<>(registry.method_46765(), Map.of())).method_62696();
    }
}

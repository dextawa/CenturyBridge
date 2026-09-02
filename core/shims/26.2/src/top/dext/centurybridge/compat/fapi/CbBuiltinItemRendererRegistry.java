package top.dext.centurybridge.compat.fapi;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

/**
 * fabric-rendering-v1's BuiltinItemRendererRegistry (10 corpus mods). Gone since
 * 1.21.4: an item's custom renderer is a special model in the item model
 * definition JSON (minecraft:special), not a runtime callback, and the old
 * callback's signature (MultiBufferSource) cannot even be expressed on 26.2. The
 * registry keeps client initialisers alive, records the renderer for get(), and
 * warns once that the item will render its JSON model instead.
 */
public interface CbBuiltinItemRendererRegistry {

    CbBuiltinItemRendererRegistry INSTANCE = new Impl();

    void register(ItemLike item, CbDynamicItemRenderer renderer);

    CbDynamicItemRenderer get(ItemLike item);

    final class Impl implements CbBuiltinItemRendererRegistry {
        private static final AtomicBoolean WARNED = new AtomicBoolean();
        private final Map<Item, CbDynamicItemRenderer> renderers = new IdentityHashMap<>();

        @Override
        public void register(ItemLike item, CbDynamicItemRenderer renderer) {
            synchronized (renderers) {
                renderers.put(item.asItem(), renderer);
            }
            if (WARNED.compareAndSet(false, true)) {
                System.err.println("[CenturyBridge] BuiltinItemRendererRegistry has no runtime equivalent on 26.x (item special models are JSON since 1.21.4); custom item renderers are recorded but not invoked");
            }
        }

        @Override
        public CbDynamicItemRenderer get(ItemLike item) {
            synchronized (renderers) {
                return renderers.get(item.asItem());
            }
        }
    }
}

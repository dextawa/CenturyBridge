package top.dext.centurybridge.rt.v26_2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import top.dext.centurybridge.shims.v26_2.Class1792Accessor;
import top.dext.centurybridge.shims.v26_2.Class1793Accessor;
import top.dext.centurybridge.shims.v26_2.Class2251Accessor;
import top.dext.centurybridge.shims.v26_2.Class4970Accessor;

/**
 * 1.21.3 made Block/Item construction demand a registry key on the Settings
 * ("Block id not set"), but the entire legacy corpus constructs first and
 * registers later. Two-phase repair: constructors falling through without a
 * key get a unique placeholder, and the registry add hook backfills the real
 * key and re-derives the fields the constructor materialized from it (loot
 * table key + translation key for blocks; translation key + name component
 * for items). Pending instances are remembered in identity maps because mods
 * reuse Settings objects across instances and Item does not retain its
 * Settings at all. Instances never registered keep placeholder names.
 */
public final class CbPending {

    public static final String NS = "centurybridge_pending";
    private static final AtomicInteger N = new AtomicInteger();
    private static final Map<Object, net.minecraft.world.level.block.state.BlockBehaviour.Properties> BLOCKS = new IdentityHashMap<>();
    private static final Map<Object, net.minecraft.world.item.Item.Properties> ITEMS = new IdentityHashMap<>();

    private static net.minecraft.resources.Identifier next() {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath(NS, "p" + N.getAndIncrement());
    }

    public static synchronized void ensureBlockKey(Object self, net.minecraft.world.level.block.state.BlockBehaviour.Properties settings) {
        Class2251Accessor acc = (Class2251Accessor) settings;
        if (acc.cbGetKey() == null) {
            acc.cbSetKey(net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.BLOCK, next()));
        }
        if (acc.cbGetKey().identifier().getNamespace().equals(NS)) {
            BLOCKS.put(self, settings);
        }
    }

    public static synchronized void ensureItemKey(Object self, net.minecraft.world.item.Item.Properties settings) {
        Class1793Accessor acc = (Class1793Accessor) settings;
        if (acc.cbGetKey() == null) {
            acc.cbSetKey(net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.ITEM, next()));
        }
        if (acc.cbGetKey().identifier().getNamespace().equals(NS)) {
            ITEMS.put(self, settings);
        }
    }

    /** Called from the registry add hook with the real key. */
    public static synchronized void backfill(net.minecraft.resources.ResourceKey<?> key, Object entry) {
        var bs = BLOCKS.remove(entry);
        if (bs != null && entry instanceof net.minecraft.world.level.block.state.BlockBehaviour block) {
            Class2251Accessor acc = (Class2251Accessor) bs;
            acc.cbSetKey(net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.BLOCK, key.identifier()));
            Class4970Accessor ba = (Class4970Accessor) block;
            ba.cbSetLootTableKey(acc.cbLootTableKey());
            ba.cbSetTranslationKey(acc.cbTranslationKey());
        }
        var is = ITEMS.remove(entry);
        if (is != null && entry instanceof net.minecraft.world.item.Item item) {
            Class1793Accessor acc = (Class1793Accessor) is;
            net.minecraft.resources.ResourceKey<?> placeholder = acc.cbGetKey();
            acc.cbSetKey(net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.ITEM, key.identifier()));
            Class1792Accessor ia = (Class1792Accessor) item;
            String tk = acc.cbTranslationKey();
            ia.cbSetTranslationKey(tk);
            // 26.x: an Item constructor calls
            // DATA_COMPONENT_INITIALIZERS.add(Properties.itemIdOrThrow(), init),
            // keying its component initializer by the id the Properties held AT
            // CONSTRUCTION -- which, for the construct-then-register corpus, is
            // our placeholder. DataComponentInitializers.build() later does
            // getOrThrow(key) against the real ITEM registry for every entry;
            // a placeholder key is not in that registry -> hard crash during
            // datapack load (seen with ironchests, resourcefullib codecs, any
            // mod that attaches a component to a late-registered item). Setting
            // the Item's own key field above is not enough: the initializer
            // entry lives in a separate global list keyed by the stale id.
            // Re-key that entry to the real key now (we are at register HEAD,
            // before build()), so the component is materialized under the key
            // the registry actually holds. Generic over every such item.
            if (placeholder != null
                    && placeholder.identifier().getNamespace().equals(NS)) {
                rekeyInitializer(placeholder, key);
            }
        }
    }

    // --- generic DataComponentInitializers re-key (reflection: cross-package
    // access to a private record + private field in a vanilla class) ---
    private static Field INIT_LIST;      // DataComponentInitializers.initializers
    private static Field ENTRY_KEY;      // InitializerEntry.key
    private static Field ENTRY_INIT;     // InitializerEntry.initializer
    private static Constructor<?> ENTRY_CTOR;
    private static boolean REFLECT_READY;
    private static boolean REFLECT_FAILED;

    private static synchronized void ensureReflect() {
        if (REFLECT_READY || REFLECT_FAILED) {
            return;
        }
        try {
            Class<?> dci = net.minecraft.core.component.DataComponentInitializers.class;
            INIT_LIST = dci.getDeclaredField("initializers");
            INIT_LIST.setAccessible(true);
            Class<?> entry = Class.forName(
                "net.minecraft.core.component.DataComponentInitializers$InitializerEntry");
            ENTRY_KEY = entry.getDeclaredField("key");
            ENTRY_KEY.setAccessible(true);
            ENTRY_INIT = entry.getDeclaredField("initializer");
            ENTRY_INIT.setAccessible(true);
            ENTRY_CTOR = entry.getDeclaredConstructors()[0];
            ENTRY_CTOR.setAccessible(true);
            REFLECT_READY = true;
        } catch (Throwable t) {
            REFLECT_FAILED = true;
            System.err.println("[CB] DataComponentInitializers re-key unavailable: " + t);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void rekeyInitializer(net.minecraft.resources.ResourceKey<?> placeholder,
                                         net.minecraft.resources.ResourceKey<?> real) {
        ensureReflect();
        if (!REFLECT_READY) {
            return;
        }
        try {
            Object dci = net.minecraft.core.registries.BuiltInRegistries.DATA_COMPONENT_INITIALIZERS;
            List list = (List) INIT_LIST.get(dci);
            for (int i = 0; i < list.size(); i++) {
                Object e = list.get(i);
                Object k = ENTRY_KEY.get(e);
                if (placeholder.equals(k)) {
                    Object init = ENTRY_INIT.get(e);
                    list.set(i, ENTRY_CTOR.newInstance(real, init));
                    return;
                }
            }
        } catch (Throwable t) {
            System.err.println("[CB] DataComponentInitializers re-key failed: " + t);
        }
    }

    private CbPending() {
    }
}

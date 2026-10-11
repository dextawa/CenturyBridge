package top.dext.centurybridge.rt.v26_2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import top.dext.centurybridge.shims.v26_2.Class1792Accessor;
import top.dext.centurybridge.shims.v26_2.Class1793Accessor;
import top.dext.centurybridge.shims.v26_2.Class2251Accessor;
import top.dext.centurybridge.shims.v26_2.Class4970Accessor;

/**
 * 1.21.3 made Block/Item construction demand a registry key on the Properties
 * ("Block id not set"), but the entire legacy corpus constructs first and
 * registers later: a 1.20.1 Settings object never carries an id. Two-phase
 * repair. Every construction whose Properties hold no id (or only another
 * pending construction's placeholder: one static Settings object often builds
 * several blocks or items) gets a placeholder key of its own, remembered per
 * instance; the registry add hook then backfills the real key and re-derives
 * everything the constructor took from it, and clears the key from the
 * Properties again, so the next construction from the same object is not
 * handed this one's identity.
 *
 * Re-derived: for blocks the loot table key and the translation key; for
 * items the translation key and the item's default components -- 26.x keys
 * them by the id the Properties held at construction (DATA_COMPONENT_INITIALIZERS)
 * and bakes the name (ITEM_NAME) and the model (ITEM_MODEL) from it, so a
 * placeholder there left every legacy item unnamed and drawn with the missing
 * model. A BlockItem takes its block's translation key, as 1.20.1's
 * BlockItem.getTranslationKey did (26.x would make it item.<id> unless the
 * Properties ask for the block prefix). Instances never registered keep their
 * placeholder names.
 */
public final class CbPending {

    public static final String NS = "centurybridge_pending";
    private static final AtomicInteger N = new AtomicInteger();

    /** one construction: the Properties it was built from and the placeholder its constructor saw */
    private record Pending<P>(P settings, ResourceKey<?> placeholder) {
    }

    private static final Map<Object, Pending<BlockBehaviour.Properties>> BLOCKS = new IdentityHashMap<>();
    private static final Map<Object, Pending<Item.Properties>> ITEMS = new IdentityHashMap<>();
    /** block items registered before their block was: their translation key follows the block's backfill */
    private static final Map<Block, List<Item>> BLOCK_ITEMS_AWAITING_BLOCK = new IdentityHashMap<>();

    private static Identifier next() {
        return Identifier.fromNamespaceAndPath(NS, "p" + N.getAndIncrement());
    }

    private static boolean unset(ResourceKey<?> key) {
        return key == null || key.identifier().getNamespace().equals(NS);
    }

    public static synchronized void ensureBlockKey(Object self, BlockBehaviour.Properties settings) {
        Class2251Accessor acc = (Class2251Accessor) settings;
        Pending<BlockBehaviour.Properties> mine = BLOCKS.get(self);
        if (mine != null) { // the constructor's second key-derived read
            acc.cbSetKey(cast(mine.placeholder()));
            return;
        }
        if (unset(acc.cbGetKey())) {
            ResourceKey<Block> p = ResourceKey.create(Registries.BLOCK, next());
            acc.cbSetKey(p);
            BLOCKS.put(self, new Pending<>(settings, p));
        }
    }

    public static synchronized void ensureItemKey(Object self, Item.Properties settings) {
        Class1793Accessor acc = (Class1793Accessor) settings;
        Pending<Item.Properties> mine = ITEMS.get(self);
        if (mine != null) { // the constructor's second key-derived read
            acc.cbSetKey(cast(mine.placeholder()));
            return;
        }
        if (unset(acc.cbGetKey())) {
            ResourceKey<Item> p = ResourceKey.create(Registries.ITEM, next());
            acc.cbSetKey(p);
            ITEMS.put(self, new Pending<>(settings, p));
        }
    }

    /** Called from the registry add hook with the real key. */
    public static synchronized void backfill(ResourceKey<?> key, Object entry) {
        Pending<BlockBehaviour.Properties> bp = BLOCKS.remove(entry);
        if (bp != null && entry instanceof BlockBehaviour block) {
            Class2251Accessor acc = (Class2251Accessor) bp.settings();
            acc.cbSetKey(ResourceKey.create(Registries.BLOCK, key.identifier()));
            Class4970Accessor ba = (Class4970Accessor) block;
            ba.cbSetLootTableKey(acc.cbLootTableKey());
            ba.cbSetTranslationKey(acc.cbTranslationKey());
            acc.cbSetKey(null);
            List<Item> waiting = BLOCK_ITEMS_AWAITING_BLOCK.remove(block);
            if (waiting != null) {
                for (Item item : waiting) {
                    ((Class1792Accessor) item).cbSetTranslationKey(block.getDescriptionId());
                }
            }
        }
        Pending<Item.Properties> ip = ITEMS.remove(entry);
        if (ip != null && entry instanceof Item item) {
            Class1793Accessor acc = (Class1793Accessor) ip.settings();
            acc.cbSetKey(ResourceKey.create(Registries.ITEM, key.identifier()));
            String translationKey = acc.cbTranslationKey();
            if (item instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                translationKey = block.getDescriptionId();
                if (BLOCKS.containsKey(block)) {
                    BLOCK_ITEMS_AWAITING_BLOCK.computeIfAbsent(block, b -> new ArrayList<>()).add(item);
                }
            }
            ((Class1792Accessor) item).cbSetTranslationKey(translationKey);
            Identifier model = acc.cbModelId();
            acc.cbSetKey(null);
            // the component initializer the constructor queued is keyed by the
            // placeholder, and DataComponentInitializers.build() resolves every
            // key against the real ITEM registry (a placeholder hard-crashes
            // datapack load); re-key it, and set the name and model it baked from
            // the placeholder. The name is read when the components are built
            // (datapack load, after every registration), so a block item whose
            // block registers later still gets the block's key.
            rekeyInitializer(ip.placeholder(), key, (builder, lookup, k) -> builder
                .set(DataComponents.ITEM_NAME, Component.translatable(item.getDescriptionId()))
                .set(DataComponents.ITEM_MODEL, model));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> ResourceKey<T> cast(ResourceKey<?> key) {
        return (ResourceKey<T>) key;
    }

    // --- DataComponentInitializers re-key (reflection: cross-package access to a
    // private record and a private field in a vanilla class) ---
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
            Class<?> dci = DataComponentInitializers.class;
            INIT_LIST = dci.getDeclaredField("initializers");
            INIT_LIST.setAccessible(true);
            Class<?> entry = Class.forName("net.minecraft.core.component.DataComponentInitializers$InitializerEntry");
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
    private static void rekeyInitializer(ResourceKey<?> placeholder, ResourceKey<?> real,
                                         DataComponentInitializers.Initializer<Item> fix) {
        ensureReflect();
        if (!REFLECT_READY) {
            return;
        }
        try {
            Object dci = net.minecraft.core.registries.BuiltInRegistries.DATA_COMPONENT_INITIALIZERS;
            List list = (List) INIT_LIST.get(dci);
            for (int i = 0; i < list.size(); i++) {
                Object e = list.get(i);
                if (placeholder.equals(ENTRY_KEY.get(e))) {
                    DataComponentInitializers.Initializer<Item> init =
                        (DataComponentInitializers.Initializer<Item>) ENTRY_INIT.get(e);
                    list.set(i, ENTRY_CTOR.newInstance(real, init.andThen(fix)));
                    return;
                }
            }
            System.err.println("[CB] no component initializer queued under " + placeholder + " for " + real);
        } catch (Throwable t) {
            System.err.println("[CB] DataComponentInitializers re-key failed: " + t);
        }
    }

    private CbPending() {
    }
}

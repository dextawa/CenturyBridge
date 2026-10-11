package top.dext.centurybridge.rt.v1_21_11;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import top.dext.centurybridge.shims.v1_21_11.Class1792Accessor;
import top.dext.centurybridge.shims.v1_21_11.Class1793Accessor;
import top.dext.centurybridge.shims.v1_21_11.Class2251Accessor;
import top.dext.centurybridge.shims.v1_21_11.Class4970Accessor;

/**
 * 1.21.3 made Block/Item construction demand a registry key on the Settings
 * ("Block id not set"), but the entire legacy corpus constructs first and
 * registers later: a 1.20.1 Settings object never carries an id. Two-phase
 * repair. Every construction whose Settings hold no id (or only another
 * pending construction's placeholder: one static Settings object often builds
 * several blocks or items) gets a placeholder key of its own, remembered per
 * instance; the registry add hook then backfills the real key, re-derives what
 * the constructor took from it, and clears the key from the Settings again, so
 * the next construction from the same object is not handed this one's
 * identity (its loot table, its name).
 *
 * Re-derived: for blocks the loot table key and the translation key; for
 * items the translation key and the two components the constructor baked from
 * the key (ITEM_NAME, ITEM_MODEL) -- set on the components the item was built
 * with, not rebuilt from the Settings, which may have been changed for a later
 * item since. A BlockItem takes its block's translation key, as 1.20.1's
 * BlockItem.getTranslationKey did (1.21.2+ makes it item.<id> unless the
 * Settings ask for the block prefix); one registered before its block follows
 * the block's backfill. Instances never registered keep their placeholder names.
 */
public final class CbPending {

    public static final String NS = "centurybridge_pending";
    private static final AtomicInteger N = new AtomicInteger();

    /** one construction: the Settings it was built from and the placeholder its constructor saw */
    private record Pending<P>(P settings, net.minecraft.class_5321<?> placeholder) {
    }

    private static final Map<Object, Pending<net.minecraft.class_4970.class_2251>> BLOCKS = new IdentityHashMap<>();
    private static final Map<Object, Pending<net.minecraft.class_1792.class_1793>> ITEMS = new IdentityHashMap<>();
    /** block items registered before their block was: their translation key and name follow the block's backfill */
    private static final Map<Object, List<net.minecraft.class_1792>> BLOCK_ITEMS_AWAITING_BLOCK = new IdentityHashMap<>();

    private static net.minecraft.class_2960 next() {
        return net.minecraft.class_2960.method_60655(NS, "p" + N.getAndIncrement());
    }

    private static boolean unset(net.minecraft.class_5321<?> key) {
        return key == null || key.method_29177().method_12836().equals(NS);
    }

    @SuppressWarnings("unchecked")
    private static <T> net.minecraft.class_5321<T> cast(net.minecraft.class_5321<?> key) {
        return (net.minecraft.class_5321<T>) key;
    }

    public static synchronized void ensureBlockKey(Object self, net.minecraft.class_4970.class_2251 settings) {
        Class2251Accessor acc = (Class2251Accessor) settings;
        Pending<net.minecraft.class_4970.class_2251> mine = BLOCKS.get(self);
        if (mine != null) { // the constructor's second key-derived read
            acc.cbSetKey(cast(mine.placeholder()));
            return;
        }
        if (unset(acc.cbGetKey())) {
            net.minecraft.class_5321<net.minecraft.class_2248> p =
                net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41254, next());
            acc.cbSetKey(p);
            BLOCKS.put(self, new Pending<>(settings, p));
        }
    }

    public static synchronized void ensureItemKey(Object self, net.minecraft.class_1792.class_1793 settings) {
        Class1793Accessor acc = (Class1793Accessor) settings;
        Pending<net.minecraft.class_1792.class_1793> mine = ITEMS.get(self);
        if (mine != null) { // the constructor's second key-derived read
            acc.cbSetKey(cast(mine.placeholder()));
            return;
        }
        if (unset(acc.cbGetKey())) {
            net.minecraft.class_5321<net.minecraft.class_1792> p =
                net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41197, next());
            acc.cbSetKey(p);
            ITEMS.put(self, new Pending<>(settings, p));
        }
    }

    /** Called from the registry add hook with the real key. */
    public static synchronized void backfill(net.minecraft.class_5321<?> key, Object entry) {
        Pending<net.minecraft.class_4970.class_2251> bp = BLOCKS.remove(entry);
        if (bp != null && entry instanceof net.minecraft.class_4970 block) {
            Class2251Accessor acc = (Class2251Accessor) bp.settings();
            acc.cbSetKey(net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41254, key.method_29177()));
            Class4970Accessor ba = (Class4970Accessor) block;
            ba.cbSetLootTableKey(acc.cbLootTableKey());
            ba.cbSetTranslationKey(acc.cbTranslationKey());
            acc.cbSetKey(null);
            List<net.minecraft.class_1792> waiting = BLOCK_ITEMS_AWAITING_BLOCK.remove(block);
            if (waiting != null) {
                for (net.minecraft.class_1792 item : waiting) {
                    rename(item, block.method_63499(), null);
                }
            }
        }
        Pending<net.minecraft.class_1792.class_1793> ip = ITEMS.remove(entry);
        if (ip != null && entry instanceof net.minecraft.class_1792 item) {
            Class1793Accessor acc = (Class1793Accessor) ip.settings();
            acc.cbSetKey(net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41197, key.method_29177()));
            String translationKey = acc.cbTranslationKey();
            if (item instanceof net.minecraft.class_1747 blockItem) {
                net.minecraft.class_2248 block = blockItem.method_7711();
                translationKey = block.method_63499();
                if (BLOCKS.containsKey(block)) {
                    BLOCK_ITEMS_AWAITING_BLOCK.computeIfAbsent(block, b -> new ArrayList<>()).add(item);
                }
            }
            net.minecraft.class_2960 model = acc.cbModelId();
            acc.cbSetKey(null);
            rename(item, translationKey, model);
        }
    }

    /** the translation key, and the name (and model, when given) the constructor baked into the item's components */
    private static void rename(net.minecraft.class_1792 item, String translationKey, net.minecraft.class_2960 model) {
        Class1792Accessor ia = (Class1792Accessor) item;
        ia.cbSetTranslationKey(translationKey);
        net.minecraft.class_9323.class_9324 b = net.minecraft.class_9323.method_57827().method_57839(item.method_57347())
            .method_57840(net.minecraft.class_9334.field_50239, net.minecraft.class_2561.method_43471(translationKey));
        if (model != null) {
            b.method_57840(net.minecraft.class_9334.field_54199, model);
        }
        ia.cbSetComponents(b.method_57838());
    }

    private CbPending() {
    }
}

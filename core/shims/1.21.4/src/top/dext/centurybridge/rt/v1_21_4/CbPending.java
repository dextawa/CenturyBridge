package top.dext.centurybridge.rt.v1_21_4;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import top.dext.centurybridge.shims.v1_21_4.Class1299Accessor;
import top.dext.centurybridge.shims.v1_21_4.Class1792Accessor;
import top.dext.centurybridge.shims.v1_21_4.Class1793Accessor;
import top.dext.centurybridge.shims.v1_21_4.Class2251Accessor;
import top.dext.centurybridge.shims.v1_21_4.Class4970Accessor;

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
    private static final Map<Object, net.minecraft.class_4970.class_2251> BLOCKS = new IdentityHashMap<>();
    private static final Map<Object, net.minecraft.class_1792.class_1793> ITEMS = new IdentityHashMap<>();
    private static final Map<Object, Boolean> ENTITY_TYPES = new IdentityHashMap<>();

    private static net.minecraft.class_2960 next() {
        return net.minecraft.class_2960.method_60655(NS, "p" + N.getAndIncrement());
    }

    public static synchronized void ensureBlockKey(Object self, net.minecraft.class_4970.class_2251 settings) {
        Class2251Accessor acc = (Class2251Accessor) settings;
        if (acc.cbGetKey() == null) {
            acc.cbSetKey(net.minecraft.class_5321.method_29179(
                net.minecraft.class_7924.field_41254, next()));
        }
        if (acc.cbGetKey().method_29177().method_12836().equals(NS)) {
            BLOCKS.put(self, settings);
        }
    }

    public static synchronized void ensureItemKey(Object self, net.minecraft.class_1792.class_1793 settings) {
        Class1793Accessor acc = (Class1793Accessor) settings;
        if (acc.cbGetKey() == null) {
            acc.cbSetKey(net.minecraft.class_5321.method_29179(
                net.minecraft.class_7924.field_41197, next()));
        }
        if (acc.cbGetKey().method_29177().method_12836().equals(NS)) {
            ITEMS.put(self, settings);
        }
    }

    /** EntityType.Builder.build wants the key up front since 1.21.2; a fapi builder's no-arg build() gets a placeholder. */
    public static net.minecraft.class_5321<net.minecraft.class_1299<?>> pendingEntityKey() {
        return net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41266, next());
    }

    public static synchronized void rememberEntityType(net.minecraft.class_1299<?> type) {
        ENTITY_TYPES.put(type, Boolean.TRUE);
    }

    /** Called from the registry add hook with the real key. */
    public static synchronized void backfill(net.minecraft.class_5321<?> key, Object entry) {
        if (ENTITY_TYPES.remove(entry) != null && entry instanceof net.minecraft.class_1299<?> type) {
            // what EntityType.Builder.build(key) derives from the key: "entity.<ns>.<path>" and entities/<id>
            net.minecraft.class_2960 id = key.method_29177();
            Class1299Accessor ea = (Class1299Accessor) type;
            ea.cbSetTranslationKey("entity." + id.method_12836() + "." + id.method_12832());
            ea.cbSetLootTableKey(java.util.Optional.of(net.minecraft.class_5321.method_29179(
                net.minecraft.class_7924.field_50079, id.method_45138("entities/"))));
        }
        var bs = BLOCKS.remove(entry);
        if (bs != null && entry instanceof net.minecraft.class_4970 block) {
            Class2251Accessor acc = (Class2251Accessor) bs;
            acc.cbSetKey(net.minecraft.class_5321.method_29179(
                net.minecraft.class_7924.field_41254, key.method_29177()));
            Class4970Accessor ba = (Class4970Accessor) block;
            ba.cbSetLootTableKey(acc.cbLootTableKey());
            ba.cbSetTranslationKey(acc.cbTranslationKey());
        }
        var is = ITEMS.remove(entry);
        if (is != null && entry instanceof net.minecraft.class_1792 item) {
            Class1793Accessor acc = (Class1793Accessor) is;
            acc.cbSetKey(net.minecraft.class_5321.method_29179(
                net.minecraft.class_7924.field_41197, key.method_29177()));
            Class1792Accessor ia = (Class1792Accessor) item;
            String tk = acc.cbTranslationKey();
            ia.cbSetTranslationKey(tk);
            ia.cbSetComponents(acc.cbBuildComponents(
                net.minecraft.class_2561.method_43471(tk), acc.cbModelId()));
        }
    }

    private CbPending() {
    }
}

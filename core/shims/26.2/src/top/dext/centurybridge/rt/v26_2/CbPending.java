package top.dext.centurybridge.rt.v26_2;

import java.util.IdentityHashMap;
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
            acc.cbSetKey(net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.ITEM, key.identifier()));
            Class1792Accessor ia = (Class1792Accessor) item;
            String tk = acc.cbTranslationKey();
            ia.cbSetTranslationKey(tk);
            ia.cbSetComponents(acc.cbBuildComponents(
                net.minecraft.network.chat.Component.translatable(tk), acc.cbModelId()));
        }
    }

    private CbPending() {
    }
}

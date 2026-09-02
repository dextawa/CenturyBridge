package top.dext.centurybridge.compat.fapi;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_6862;
import net.minecraft.class_7225;
import net.minecraft.class_7701;
import net.minecraft.class_7923;
import net.minecraft.class_9895;
import top.dext.centurybridge.shims.v1_21_4.FuelBuilderAccessor;

/**
 * fabric-content-registries-v0's FuelRegistry as 1.20.1 mods call it (15 corpus mods:
 * {@code FuelRegistry.INSTANCE.add(item, 1600)}). 1.21.2 made fuel values a data-driven
 * registry (FuelValues, rebuilt per world/reload) and Fabric API replaced the static table by
 * {@code FuelRegistryEvents.BUILD / EXCLUSIONS}. This keeps the old table and replays it into
 * every build: additions in BUILD, removals in EXCLUSIONS (tags through the builder's own
 * remove, single items straight out of its map -- vanilla only exposes tag removal).
 * {@code get} answers from a private vanilla+replayed FuelValues built on demand, which is
 * what the old static lookup meant before any world existed.
 *
 * Erased signatures ({@code add(ItemConvertible, Object)}) are the 1.20.1 bytecode shape:
 * the interface was generic over Integer.
 * classRename: net/fabricmc/fabric/api/registry/FuelRegistry -> this interface.
 */
public interface CbFuelRegistry {

    CbFuelRegistry INSTANCE = new Impl();

    Object get(class_1935 item);

    void add(class_1935 item, Object cookTime);

    void add(class_6862<class_1792> tag, Object cookTime);

    void remove(class_1935 item);

    void remove(class_6862<class_1792> tag);

    void clear(class_1935 item);

    void clear(class_6862<class_1792> tag);

    final class Impl implements CbFuelRegistry {
        private final Map<class_1792, Integer> items = new LinkedHashMap<>();
        private final Map<class_6862<class_1792>, Integer> tags = new LinkedHashMap<>();
        private final Set<class_1792> removedItems = new LinkedHashSet<>();
        private final Set<class_6862<class_1792>> removedTags = new LinkedHashSet<>();
        private class_9895 snapshot;

        Impl() {
            FuelRegistryEvents.BUILD.register((builder, context) -> {
                synchronized (this) {
                    items.forEach((item, time) -> builder.method_61762(item, time));
                    tags.forEach((tag, time) -> builder.method_61760(tag, time));
                }
            });
            FuelRegistryEvents.EXCLUSIONS.register((builder, context) -> {
                synchronized (this) {
                    removedTags.forEach(builder::method_61759);
                    if (!removedItems.isEmpty()) {
                        ((FuelBuilderAccessor) builder).cbValues().keySet().removeAll(removedItems);
                    }
                }
            });
        }

        private static int time(Object cookTime) {
            return ((Number) cookTime).intValue();
        }

        @Override
        public synchronized Object get(class_1935 item) {
            if (snapshot == null) {
                // the vanilla table plus every registered listener (ours included): fapi fires
                // BUILD/EXCLUSIONS inside vanillaBurnTimes
                snapshot = class_9895.method_61753(
                    class_7225.class_7874.method_46761(Stream.of(class_7923.field_41178)),
                    class_7701.field_40182);
            }
            int t = snapshot.method_61755(new class_1799(item));
            return t > 0 ? Integer.valueOf(t) : null;
        }

        @Override
        public synchronized void add(class_1935 item, Object cookTime) {
            class_1792 i = item.method_8389();
            items.put(i, time(cookTime));
            removedItems.remove(i);
            snapshot = null;
        }

        @Override
        public synchronized void add(class_6862<class_1792> tag, Object cookTime) {
            tags.put(tag, time(cookTime));
            removedTags.remove(tag);
            snapshot = null;
        }

        @Override
        public synchronized void remove(class_1935 item) {
            class_1792 i = item.method_8389();
            items.remove(i);
            removedItems.add(i);
            snapshot = null;
        }

        @Override
        public synchronized void remove(class_6862<class_1792> tag) {
            tags.remove(tag);
            removedTags.add(tag);
            snapshot = null;
        }

        @Override
        public void clear(class_1935 item) {
            remove(item);
        }

        @Override
        public void clear(class_6862<class_1792> tag) {
            remove(tag);
        }
    }
}

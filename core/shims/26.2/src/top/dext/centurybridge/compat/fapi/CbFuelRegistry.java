package top.dext.centurybridge.compat.fapi;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.fabricmc.fabric.api.util.Item2ObjectMap;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.FuelValues;
import top.dext.centurybridge.rt.v26_2.Trackers;

/**
 * fabric-content-registries-v0's FuelRegistry as 1.20.x mods call it (15 corpus
 * mods): a mutable item-to-burn-time map consulted by furnaces. Since 1.21.2
 * burn times are a per-server table (FuelValues) built from vanilla data plus
 * Fabric's FuelValueEvents.BUILD / EXCLUSIONS callbacks. The old registrations
 * are recorded here and replayed into every FuelValues build; get() answers
 * from the running server's table so vanilla fuels are visible too, as they
 * were in 1.20.1's FuelRegistryImpl.
 *
 * <p>Kept an interface (the corpus calls INSTANCE through INVOKEINTERFACE and
 * FuelRegistry extended Item2ObjectMap, which still exists).
 */
public interface CbFuelRegistry extends Item2ObjectMap<Integer> {

    CbFuelRegistry INSTANCE = new Impl();

    final class Impl implements CbFuelRegistry {
        private final Map<Item, Integer> items = new LinkedHashMap<>();
        private final Map<TagKey<Item>, Integer> tags = new LinkedHashMap<>();
        private final Set<TagKey<Item>> removedTags = new LinkedHashSet<>();
        private final AtomicBoolean warnedItemRemove = new AtomicBoolean();
        private volatile FuelValues fallback;

        Impl() {
            FuelValueEvents.BUILD.register((builder, ctx) -> {
                synchronized (this) {
                    tags.forEach(builder::add);
                    items.forEach(builder::add);
                }
            });
            FuelValueEvents.EXCLUSIONS.register((builder, ctx) -> {
                synchronized (this) {
                    removedTags.forEach(builder::remove);
                }
            });
        }

        @Override
        public Integer get(ItemLike item) {
            synchronized (this) {
                Integer own = items.get(item.asItem());
                if (own != null) {
                    return own > 0 ? own : null;
                }
            }
            MinecraftServer server = Trackers.SERVER;
            FuelValues values = server != null ? server.fuelValues() : fallback();
            int t = values.burnDuration(new ItemStack(item));
            return t > 0 ? t : null;
        }

        private FuelValues fallback() {
            FuelValues f = fallback;
            if (f == null) {
                // no server yet (client init): the vanilla table, which the BUILD callback above also fills
                fallback = f = FuelValues.vanillaBurnTimes(VanillaRegistries.createLookup(), FeatureFlags.DEFAULT_FLAGS);
            }
            return f;
        }

        @Override
        public void add(ItemLike item, Integer cookTime) {
            synchronized (this) {
                items.put(item.asItem(), cookTime);
            }
        }

        @Override
        public void add(TagKey<Item> tag, Integer cookTime) {
            synchronized (this) {
                tags.put(tag, cookTime);
                removedTags.remove(tag);
            }
        }

        @Override
        public void remove(ItemLike item) {
            synchronized (this) {
                items.remove(item.asItem());
            }
            if (warnedItemRemove.compareAndSet(false, true)) {
                System.err.println("[CenturyBridge] FuelRegistry.remove(item): 26.x FuelValues.Builder can only exclude item TAGS; a vanilla fuel item cannot be un-fueled");
            }
        }

        @Override
        public void remove(TagKey<Item> tag) {
            synchronized (this) {
                tags.remove(tag);
                removedTags.add(tag);
            }
        }

        @Override
        public void clear(ItemLike item) {
            synchronized (this) {
                items.remove(item.asItem());
            }
        }

        @Override
        public void clear(TagKey<Item> tag) {
            synchronized (this) {
                tags.remove(tag);
                removedTags.remove(tag);
            }
        }
    }
}

package top.dext.centurybridge.rt.v26_2;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
/**
 * Identity maps rebuilt on each datapack (re)load. 1.20.2 moved ids out of
 * Recipe and Advancement into wrapper entries; legacy getId()-style calls are
 * answered from here (populated by RecipeManagerBridge / AdvancementLoaderBridge).
 */
public final class Trackers {

    public static final Map<Object, net.minecraft.resources.Identifier> RECIPE_IDS =
        Collections.synchronizedMap(new IdentityHashMap<>());

    public static final Map<Object, net.minecraft.advancements.AdvancementHolder> ADVANCEMENT_ENTRIES =
        Collections.synchronizedMap(new IdentityHashMap<>());

    /** the running server (set by MinecraftServerBridge); source of the dynamic registries */
    public static volatile net.minecraft.server.MinecraftServer SERVER;

    /**
     * 1.21 enchantments are registry values with Holder identity; a bare
     * Enchantment handed to a 1.20.1 mod (from Enchantments.* getters or a
     * component read) is remembered with its key so it can be turned back into
     * the live registry's Holder even when it came from another registry copy.
     */
    public static final Map<Object, net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>> ENCHANTMENT_KEYS =
        Collections.synchronizedMap(new IdentityHashMap<>());

    private Trackers() {
    }
}

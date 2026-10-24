package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import top.dext.centurybridge.step.v26_1.facade.CauldronInteractionMap;
import top.dext.centurybridge.step.v26_1.mixin.CbWallCauldronInteractionsAccess;
import top.dext.centurybridge.step.v26_1.mixin.CbWallCauldronBlockAccess;
import top.dext.centurybridge.step.v26_1.mixin.CbWallCauldronDispatcherAccess;

/**
 * 1.21.11's CauldronInteraction maps (facade/CauldronInteractionMap) on 26.1's dispatchers.
 * <p>
 * Each dispatcher may carry (mixin/CbWallCauldronDispatcherMixin) the items a 1.21.11 mod put through a map --
 * answered before the dispatcher's tags and items, as a 1.21.11 map entry was the item's interaction -- or,
 * for a map a mod built itself, that map, which then answers alone (1.21.11's cauldron asked only it).
 */
public final class CbWallCauldrons {

    private CbWallCauldrons() {
    }

    /** a dispatcher's 1.21.11 state */
    public interface DispatcherState {
        Map<Item, CauldronInteraction> cb$overrides();

        Map<Item, CauldronInteraction> cb$backing();

        void cb$setBacking(Map<Item, CauldronInteraction> map);
    }

    private static final Map<CauldronInteraction.Dispatcher, CauldronInteractionMap> MAPS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<CauldronInteractionMap, CauldronInteraction.Dispatcher> BOUND = Collections.synchronizedMap(new WeakHashMap<>());

    /** 1.21.11's named maps (CauldronInteraction.INTERACTIONS): the four 26.1 has, and those newInteractionMap made */
    public static final Map<String, CauldronInteractionMap> INTERACTIONS = new LinkedHashMap<>();

    public static final CauldronInteractionMap EMPTY = named("empty", CauldronInteractions.EMPTY);
    public static final CauldronInteractionMap WATER = named("water", CauldronInteractions.WATER);
    public static final CauldronInteractionMap LAVA = named("lava", CauldronInteractions.LAVA);
    public static final CauldronInteractionMap POWDER_SNOW = named("powder_snow", CauldronInteractions.POWDER_SNOW);

    /** CauldronInteraction.CODEC: a map by its name */
    public static final Codec<CauldronInteractionMap> CODEC = Codec.stringResolver(CauldronInteractionMap::comp_1981, INTERACTIONS::get);

    private static CauldronInteractionMap named(String name, CauldronInteraction.Dispatcher dispatcher) {
        CauldronInteractionMap map = CauldronInteractionMap.of(name, dispatcher);
        INTERACTIONS.put(name, map);
        return map;
    }

    /** the facade standing for a dispatcher */
    public static void bind(CauldronInteractionMap map, CauldronInteraction.Dispatcher dispatcher) {
        MAPS.put(dispatcher, map);
        BOUND.put(map, dispatcher);
    }

    /** the 1.21.11 map of a dispatcher (a 26.1 one gets one of its own the first time) */
    public static CauldronInteractionMap map(CauldronInteraction.Dispatcher dispatcher) {
        CauldronInteractionMap map = MAPS.get(dispatcher);
        return map != null ? map : CauldronInteractionMap.of("unnamed", dispatcher);
    }

    /** the dispatcher a 1.21.11 map stands for */
    public static CauldronInteraction.Dispatcher dispatcher(CauldronInteractionMap map) {
        return BOUND.get(map);
    }

    /** new InteractionMap(name, map): a dispatcher answering from the mod's map alone */
    public static CauldronInteraction.Dispatcher adopt(String name, Map<Item, CauldronInteraction> map) {
        CauldronInteraction.Dispatcher dispatcher = new CauldronInteraction.Dispatcher();
        ((DispatcherState) dispatcher).cb$setBacking(map);
        return dispatcher;
    }

    // ---- the map view ----

    public static CauldronInteraction interaction(CauldronInteraction.Dispatcher dispatcher, Item item) {
        return dispatcher.get(new ItemStack(item));
    }

    public static CauldronInteraction put(CauldronInteraction.Dispatcher dispatcher, Item item, CauldronInteraction interaction) {
        CauldronInteraction previous = interaction(dispatcher, item);
        ((DispatcherState) dispatcher).cb$overrides().put(item, interaction);
        return previous;
    }

    /** an item removed from a 1.21.11 map answered the map's default: the dispatcher's DEFAULT */
    public static CauldronInteraction remove(CauldronInteraction.Dispatcher dispatcher, Item item) {
        CauldronInteraction previous = interaction(dispatcher, item);
        ((DispatcherState) dispatcher).cb$overrides().put(item, CauldronInteraction.DEFAULT);
        return previous;
    }

    public static boolean known(CauldronInteraction.Dispatcher dispatcher, Item item) {
        return interaction(dispatcher, item) != CauldronInteraction.DEFAULT;
    }

    public static void forEachKnown(CauldronInteraction.Dispatcher dispatcher, BiConsumer<Item, CauldronInteraction> action) {
        for (Item item : BuiltInRegistries.ITEM) {
            CauldronInteraction interaction = interaction(dispatcher, item);
            if (interaction != CauldronInteraction.DEFAULT) {
                action.accept(item, interaction);
            }
        }
    }

    // ---- 1.21.11 statics ----

    /** CauldronInteraction.newInteractionMap(name): a 26.1 dispatcher registered under the name, and its map */
    public static CauldronInteractionMap newInteractionMap(String name) {
        return named(name, CbWallCauldronInteractionsAccess.cb$newDispatcher(name));
    }

    /** CauldronInteraction.bootStrap() */
    public static void bootStrap() {
        CauldronInteractions.bootStrap();
    }

    /** CauldronInteraction.addDefaultInteractions(map): 26.1's defaults (filling from buckets), put into the map */
    public static void addDefaultInteractions(Map<Item, CauldronInteraction> map) {
        CauldronInteraction.Dispatcher defaults = new CauldronInteraction.Dispatcher();
        CbWallCauldronInteractionsAccess.cb$addDefaultInteractions(defaults);
        map.putAll(((CbWallCauldronDispatcherAccess) defaults).cb$items());
    }

    /** CauldronInteraction.fillBucket(...) */
    public static InteractionResult fillBucket(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack emptyBucket,
        ItemStack filledBucket, Predicate<BlockState> canFill, SoundEvent sound) {
        return CbWallCauldronInteractionsAccess.cb$fillBucket(state, level, pos, player, hand, emptyBucket, filledBucket, canFill, sound);
    }

    /** CauldronInteraction.emptyBucket(...) */
    public static InteractionResult emptyBucket(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack filledBucket, BlockState state,
        SoundEvent sound) {
        return CbWallCauldronInteractionsAccess.cb$emptyBucket(level, pos, player, hand, filledBucket, state, sound);
    }

    // field getters of CauldronInteraction (fieldGetters name the static by the field's intermediary name)

    public static Map<String, CauldronInteractionMap> field_46589() {
        return INTERACTIONS;
    }

    public static Codec<CauldronInteractionMap> field_46590() {
        return CODEC;
    }

    public static CauldronInteractionMap field_27775() {
        return EMPTY;
    }

    public static CauldronInteractionMap field_27776() {
        return WATER;
    }

    public static CauldronInteractionMap field_27777() {
        return LAVA;
    }

    public static CauldronInteractionMap field_28011() {
        return POWDER_SNOW;
    }

    /** a cauldron block's interactions (AbstractCauldronBlock.interactions, 26.1's dispatcher) as 1.21.11's map */
    public static CauldronInteractionMap interactions(AbstractCauldronBlock block) {
        return map(((CbWallCauldronBlockAccess) block).cb$interactions());
    }
}

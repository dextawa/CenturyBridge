package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_10162;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_2248;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_4970;
import net.minecraft.class_5321;
import net.minecraft.class_52;
import net.minecraft.class_7924;
import net.minecraft.class_9323;
import net.minecraft.class_9334;

/**
 * The item and block halves of {@link CbPendingIds}: what 1.21.2's Item and BlockBehaviour constructors
 * take from the key on their Properties, made at construction under a placeholder and remade with the
 * real key when the object registers.
 *
 * <p>1.21.2's Item(Properties) reads Properties.effectiveDescriptionId() -- its descriptionId function
 * (ITEM_DESCRIPTION_ID "item.&lt;ns&gt;.&lt;path&gt;" unless useBlockDescriptionPrefix / overrideDescription
 * changed it) of the id -- into Item.descriptionId, and builds the components with ITEM_NAME =
 * translatable(that key) and ITEM_MODEL = its model function (the id unless overrideModel) of the id.
 * BlockBehaviour(Properties) reads effectiveDrops() -- the drops function ("blocks/" + the id unless
 * noLootTable / overrideLootTable) -- into drops, and effectiveDescriptionId() ("block.&lt;ns&gt;.&lt;path&gt;"
 * unless overrideDescription) into descriptionId. Nothing else in either class, or in Block, or in any
 * subclass of 1.21.2, reads the key.
 *
 * <p>1.21.1 asked the registry lazily instead, and what it answered is what the backfill makes:
 * Item.getDescriptionId "item." + id, and a BlockItem's its block's (BlockItem.getDescriptionId); the
 * name a stack fell back to was translatable(getDescriptionId(stack)); Block.getDescriptionId "block." +
 * id, getLootTable "blocks/" + id unless noLootTable / dropsLike said otherwise. The functions the
 * properties held at construction are kept and applied to the real key, so a 1.21.2-style override on
 * the properties keeps its effect. A component the mod set explicitly on the properties (an ITEM_NAME of
 * its own) is not replaced: only a value that is still the one the placeholder produced is.
 */
public final class CbPendingIdsItems {

    /** what one construction took from its properties: the functions to re-apply to the real key */
    private record ItemDerive(class_10162<class_1792, String> descriptionId, class_10162<class_1792, class_2960> model,
                              class_2561 placeholderName, class_2960 placeholderModel) {
    }

    private record BlockDerive(class_10162<class_2248, Optional<class_5321<class_52>>> drops,
                               class_10162<class_2248, String> descriptionId) {
    }

    private static final Map<Object, ItemDerive> ITEMS = new IdentityHashMap<>();
    private static final Map<Object, BlockDerive> BLOCKS = new IdentityHashMap<>();
    /** block items registered before their block: their description id and name follow the block's backfill */
    private static final Map<Object, List<class_1792>> WAITING_FOR_BLOCK = new IdentityHashMap<>();

    /** the accessors the mixins give the vanilla classes (CbPendingIdsItemMixin, CbPendingIdsBlockMixin) */
    public interface ItemProps {
        class_5321<class_1792> cb$id();

        void cb$setId(class_5321<class_1792> id);

        class_10162<class_1792, String> cb$descriptionIdFn();

        class_10162<class_1792, class_2960> cb$modelFn();

        /** Properties.effectiveDescriptionId(), the read the constructor makes */
        String cb$effectiveDescriptionId();

        /** Properties.effectiveModel() */
        class_2960 cb$effectiveModel();
    }

    public interface ItemFields {
        void cb$setDescriptionId(String id);

        /** the description id field (1.21.2's final getDescriptionId() answers it) */
        String cb$descriptionId();

        void cb$setComponents(class_9323 components);
    }

    public interface BlockProps {
        class_5321<class_2248> cb$id();

        void cb$setId(class_5321<class_2248> id);

        class_10162<class_2248, Optional<class_5321<class_52>>> cb$dropsFn();

        class_10162<class_2248, String> cb$descriptionIdFn();

        /** Properties.effectiveDrops() */
        Optional<class_5321<class_52>> cb$effectiveDrops();

        /** Properties.effectiveDescriptionId() */
        String cb$effectiveDescriptionId();
    }

    public interface BlockFields {
        void cb$setDrops(Optional<class_5321<class_52>> drops);

        void cb$setDescriptionId(String id);
    }

    static {
        CbPendingIds.listen(class_7924.field_41197, CbPendingIdsItems::backfillItem);
        CbPendingIds.listen(class_7924.field_41254, CbPendingIdsItems::backfillBlock);
    }

    /** the key each construction's properties held before it: put back when the constructor ends */
    private static final Map<Object, Optional<class_5321<?>>> HAD = new IdentityHashMap<>();

    /**
     * Item constructor, at each key-derived read: the first one gives the properties -- holding no id of
     * their own -- a placeholder for this construction (a later read of the same construction finds it);
     * properties with an id of their own (1.21.2's setId) are left alone
     */
    public static synchronized void itemReading(Object item, ItemProps props) {
        if (HAD.containsKey(item)) {
            return;
        }
        class_5321<class_1792> had = props.cb$id();
        HAD.put(item, Optional.ofNullable(had));
        if (had == null || CbPendingIds.isPlaceholder(had)) {
            class_5321<class_1792> p = CbPendingIds.placeholder(class_7924.field_41197);
            props.cb$setId(p);
            CbPendingIds.pending(item, p);
        }
    }

    /** Item constructor's end: records what to re-derive and gives the properties back the key they had */
    @SuppressWarnings("unchecked")
    public static synchronized void itemConstructed(class_1792 item, ItemProps props) {
        Optional<class_5321<?>> had = HAD.remove(item);
        class_5321<?> p = CbPendingIds.placeholderOf(item);
        if (had != null && p != null) {
            class_5321<class_1792> key = (class_5321<class_1792>) p;
            ITEMS.put(item, new ItemDerive(props.cb$descriptionIdFn(), props.cb$modelFn(),
                item.method_57347().method_57829(class_9334.field_50239), props.cb$modelFn().get(key)));
            props.cb$setId((class_5321<class_1792>) had.orElse(null));
        }
    }

    /** Block constructor, at each key-derived read (as for items) */
    public static synchronized void blockReading(Object block, BlockProps props) {
        if (HAD.containsKey(block)) {
            return;
        }
        class_5321<class_2248> had = props.cb$id();
        HAD.put(block, Optional.ofNullable(had));
        if (had == null || CbPendingIds.isPlaceholder(had)) {
            class_5321<class_2248> p = CbPendingIds.placeholder(class_7924.field_41254);
            props.cb$setId(p);
            CbPendingIds.pending(block, p);
        }
    }

    @SuppressWarnings("unchecked")
    public static synchronized void blockConstructed(class_4970 block, BlockProps props) {
        Optional<class_5321<?>> had = HAD.remove(block);
        class_5321<?> p = CbPendingIds.placeholderOf(block);
        if (had != null && p != null) {
            BLOCKS.put(block, new BlockDerive(props.cb$dropsFn(), props.cb$descriptionIdFn()));
            props.cb$setId((class_5321<class_2248>) had.orElse(null));
        }
    }

    private static synchronized void backfillItem(Object value, class_5321<?> realKey) {
        ItemDerive d = ITEMS.remove(value);
        if (d == null || !(value instanceof class_1792 item)) {
            return;
        }
        @SuppressWarnings("unchecked")
        class_5321<class_1792> key = (class_5321<class_1792>) realKey;
        String descriptionId = d.descriptionId().get(key);
        if (item instanceof class_1747 blockItem) {
            // 1.21.1's BlockItem.getDescriptionId: its block's
            class_2248 block = blockItem.method_7711();
            if (CbPendingIds.isPending(block)) {
                WAITING_FOR_BLOCK.computeIfAbsent(block, b -> new ArrayList<>()).add(item);
            }
            descriptionId = block.method_63499();
        }
        rename(item, descriptionId, d.placeholderName(), d.placeholderModel(), d.model().get(key));
    }

    private static synchronized void backfillBlock(Object value, class_5321<?> realKey) {
        BlockDerive d = BLOCKS.remove(value);
        if (d == null || !(value instanceof class_4970 block)) {
            return;
        }
        @SuppressWarnings("unchecked")
        class_5321<class_2248> key = (class_5321<class_2248>) realKey;
        BlockFields f = (BlockFields) block;
        f.cb$setDrops(d.drops().get(key));
        f.cb$setDescriptionId(d.descriptionId().get(key));
        List<class_1792> waiting = WAITING_FOR_BLOCK.remove(block);
        if (waiting != null && block instanceof class_2248 b) {
            for (class_1792 item : waiting) {
                ItemFields fi = (ItemFields) item;
                String id = b.method_63499();
                fi.cb$setDescriptionId(id);
                class_2561 name = CbItemMembers.built(item).method_57829(class_9334.field_50239);
                class_9323.class_9324 comps = class_9323.method_57827().method_57839(CbItemMembers.built(item));
                // the name the item's backfill gave it from the block's placeholder description id
                if (name != null && name.method_10851() instanceof net.minecraft.class_2588 tr
                        && tr.method_11022().startsWith("block." + CbPendingIds.NS + ".")) {
                    comps.method_57840(class_9334.field_50239, class_2561.method_43471(id));
                }
                CbItemMemberComponents.setBuilt(item, comps.method_57838());
            }
        }
    }

    /**
     * the description id, and the name and model the constructor baked into the item's components --
     * replaced only where they are still what the placeholder made (a mod's own ITEM_NAME stays)
     */
    private static void rename(class_1792 item, String descriptionId, class_2561 placeholderName, class_2960 placeholderModel,
                               class_2960 model) {
        ItemFields f = (ItemFields) item;
        f.cb$setDescriptionId(descriptionId);
        class_9323 now = CbItemMembers.built(item);
        class_9323.class_9324 b = class_9323.method_57827().method_57839(now);
        if (java.util.Objects.equals(now.method_57829(class_9334.field_50239), placeholderName)) {
            b.method_57840(class_9334.field_50239, class_2561.method_43471(descriptionId));
        }
        if (java.util.Objects.equals(now.method_57829(class_9334.field_54199), placeholderModel)) {
            b.method_57840(class_9334.field_54199, model);
        }
        CbItemMemberComponents.setBuilt(item, b.method_57838());
    }

    private CbPendingIdsItems() {
    }
}

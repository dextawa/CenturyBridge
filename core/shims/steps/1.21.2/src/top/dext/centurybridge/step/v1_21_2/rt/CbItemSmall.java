package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1657;
import net.minecraft.class_1745;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1806;
import net.minecraft.class_1937;
import net.minecraft.class_22;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2960;
import net.minecraft.class_5250;
import net.minecraft.class_9209;
import net.minecraft.class_9334;

/**
 * Small 1.21.1 item members 1.21.2 dropped, on 1.21.2:
 * <ul>
 * <li>ComplexItem / MapItem.getUpdatePacket(stack, level, player): the packet 1.21.1's ServerPlayer sent each tick
 *     for a complex item in the inventory (CbItemComplexMixin); MapItem's: its map's update for the player;</li>
 * <li>BannerPatternItem.getDisplayName(): translatable(getDescriptionId() + ".desc");</li>
 * <li>BundleItem(properties): 1.21.2's bundle takes the models of its open state; a 1.21.1 bundle has the plain
 *     bundle's.</li>
 * </ul>
 */
public final class CbItemSmall {
    private CbItemSmall() {
    }

    // ---- getUpdatePacket ----

    /** getUpdatePacket(stack, level, player), declared by a 1.21.1 item (a ComplexItem facade subclass, a MapItem subclass) */
    public interface Legacy_getUpdatePacket {
        class_2596<?> method_7757(class_1799 stack, class_1937 level, class_1657 player);
    }

    /** MapItem.getUpdatePacket(stack, level, player), called */
    public static class_2596<?> getUpdatePacket(class_1806 item, class_1799 stack, class_1937 level, class_1657 player) {
        if (item instanceof Legacy_getUpdatePacket legacy) {
            return legacy.method_7757(stack, level, player);
        }
        return superGetUpdatePacket(item, stack, level, player);
    }

    /** 1.21.1 MapItem's: the update of the stack's map for the player, null without one */
    public static class_2596<?> superGetUpdatePacket(class_1806 item, class_1799 stack, class_1937 level, class_1657 player) {
        class_9209 id = stack.method_57824(class_9334.field_49646);
        class_22 data = class_1806.method_7997(id, level);
        return data != null ? data.method_100(id, player) : null;
    }

    /**
     * Whether 1.21.1's ServerPlayer synchronised the stack's item with its own getUpdatePacket: a ComplexItem whose
     * isComplex() is true, or a MapItem subclass declaring getUpdatePacket (MapItem was a ComplexItem)
     */
    public static Legacy_getUpdatePacket complex(class_1792 item) {
        if (item instanceof Legacy_getUpdatePacket legacy
                && (item instanceof class_1806 || CbItemMembers.method_16698(item))) {
            return legacy;
        }
        return null;
    }

    // ---- BannerPatternItem.getDisplayName ----

    /** getDisplayName(), declared by a 1.21.1 BannerPatternItem subclass */
    public interface Legacy_getDisplayName {
        class_5250 method_7703();
    }

    public static class_5250 getDisplayName(class_1745 item) {
        if (item instanceof Legacy_getDisplayName legacy) {
            return legacy.method_7703();
        }
        return superGetDisplayName(item);
    }

    /** 1.21.1: translatable(getDescriptionId() + ".desc") */
    public static class_5250 superGetDisplayName(class_1745 item) {
        return class_2561.method_43471(item.method_7876() + ".desc");
    }

    // ---- BundleItem ----

    /** the plain bundle's open front model (1.21.2's Items.BUNDLE) */
    public static class_2960 bundleOpenFront() {
        return class_2960.method_60656("bundle_open_front");
    }

    /** the plain bundle's open back model */
    public static class_2960 bundleOpenBack() {
        return class_2960.method_60656("bundle_open_back");
    }

    // ---- boats and minecarts by entity type ----

    /** BoatItem(hasChest, type, properties): 1.21.2 takes the boat's entity type */
    public static net.minecraft.class_1299<?> boatEntity(boolean chest, top.dext.centurybridge.step.v1_21_2.facade.BoatType type) {
        return type.entityType(chest);
    }

    /** BoatDispenseItemBehavior(type): a boat without a chest */
    public static net.minecraft.class_1299<?> boatEntity(top.dext.centurybridge.step.v1_21_2.facade.BoatType type) {
        return type.entityType(false);
    }

    /** BoatDispenseItemBehavior(type, hasChest) */
    public static net.minecraft.class_1299<?> boatEntity(top.dext.centurybridge.step.v1_21_2.facade.BoatType type, boolean chest) {
        return type.entityType(chest);
    }

    /** MinecartItem(type, properties): 1.21.2 takes the minecart's entity type */
    public static net.minecraft.class_1299<?> minecartEntity(top.dext.centurybridge.step.v1_21_2.facade.MinecartType type) {
        return type.entityType();
    }

    // ---- ItemCombinerMenuSlotDefinition ----

    /** hasSlot(slot): 1.21.1's body, the number of input slots at least the slot */
    public static boolean hasSlot(net.minecraft.class_8047 definition, int slot) {
        return definition.method_48368().size() >= slot;
    }

    /** getInputSlotIndexes(): the input slots' indices, in order */
    public static java.util.List<Integer> getInputSlotIndexes(net.minecraft.class_8047 definition) {
        java.util.List<Integer> out = new java.util.ArrayList<>();
        for (net.minecraft.class_8047.class_8049 slot : definition.method_48368()) {
            out.add(slot.comp_1204());
        }
        return out;
    }

    // ---- collision contexts ----

    /** EntityCollisionContext(entity): 1.21.1 had no "always collide with fluid" */
    public static boolean noFluidCollision() {
        return false;
    }

    /**
     * 1.21.2's CollisionContext.getCollisionShape(state, level, pos), which a 1.21.1 implementation lacks: what every
     * 1.21.1 caller asked -- the state's collision shape in this context (1.21.2's EntityCollisionContext answers so)
     */
    public static net.minecraft.class_265 collisionShape(net.minecraft.class_3726 context, net.minecraft.class_2680 state,
            net.minecraft.class_1941 level, net.minecraft.class_2338 pos) {
        return state.method_26194(level, pos, context);
    }


    // ---- JukeboxPlayable.tryInsertIntoJukebox ----

    /**
     * 1.21.1 answered an ItemInteractionResult: PASS_TO_DEFAULT_BLOCK_INTERACTION where nothing was inserted, else
     * sidedSuccess(isClientSide) -- 1.21.2 answers TRY_WITH_EMPTY_HAND / SUCCESS for the same two outcomes
     */
    public static top.dext.centurybridge.step.v1_21_2.facade.ItemInteractionResult tryInsertIntoJukebox(net.minecraft.class_1937 level,
            net.minecraft.class_2338 pos, class_1799 stack, class_1657 player) {
        net.minecraft.class_1269 result = net.minecraft.class_9792.method_60747(level, pos, stack, player);
        return result instanceof net.minecraft.class_1269.class_9862
            ? top.dext.centurybridge.step.v1_21_2.facade.ItemInteractionResult.field_47731
            : top.dext.centurybridge.step.v1_21_2.facade.ItemInteractionResult.method_55644(level.method_8608());
    }

    // ---- static fields 1.21.2 dropped ----

    /**
     * FeatureFlags.BUNDLE, the experimental flag 1.21.1's bundle needed: 1.21.2 released the bundle, so what needed it
     * needs only the vanilla feature now
     */
    public static net.minecraft.class_7696 field_40178() {
        return net.minecraft.class_7701.field_40177;
    }

    /** ItemStack.OPTIONAL_LIST_STREAM_CODEC: 1.21.1's, the optional stack codec over a NonNullList */
    public static net.minecraft.class_9139<net.minecraft.class_9129, java.util.List<class_1799>> field_48350() {
        return OptionalList.CODEC;
    }

    private static final class OptionalList {
        static final net.minecraft.class_9139<net.minecraft.class_9129, java.util.List<class_1799>> CODEC =
            class_1799.field_48349.method_56433(net.minecraft.class_9135.method_56374(net.minecraft.class_2371::method_37434));
    }

}

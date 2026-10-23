package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.function.Predicate;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3414;
import net.minecraft.class_5620;
import top.dext.centurybridge.step.v1_21_2.facade.ItemInteractionResult;

/**
 * 1.21.1's CauldronInteraction on 1.21.2, where interact answers an InteractionResult instead of an
 * ItemInteractionResult (facade ItemInteractionResult converts both ways, see its toResult / fromResult).
 * <ul>
 * <li>a call of 1.21.1's interact (and of fillBucket / emptyBucket, whose answer changed alike): 1.21.2's,
 *     answered as 1.21.1's result;</li>
 * <li>a 1.21.1 lambda or class implementing it: 1.21.2's interact asks it and converts its answer (lambdaAdapters
 *     through {@link Legacy}, overrideHooks for classes);</li>
 * <li>FILL_WATER / FILL_LAVA / FILL_POWDER_SNOW / SHULKER_BOX / BANNER / DYED_ITEM, constants 1.21.2 turned into
 *     private methods: the interactions 1.21.2's own maps hold for the items 1.21.1 put them under (EMPTY's water,
 *     lava and powder snow buckets; WATER's shulker boxes, banners and dyeable armor).</li>
 * </ul>
 */
public final class CbItemCauldron {

    /** a 1.21.1 CauldronInteraction lambda: 1.21.1's shape */
    public interface Legacy {
        ItemInteractionResult interact(class_2680 state, class_1937 level, class_2338 pos, class_1657 player, class_1268 hand,
                                       class_1799 stack);
    }

    /** a 1.21.1 lambda as 1.21.2's interaction */
    public static class_5620 fromLambda(Legacy legacy) {
        return (state, level, pos, player, hand, stack) ->
            ItemInteractionResult.toResult(legacy.interact(state, level, pos, player, hand, stack));
    }

    /** interact(..) called with 1.21.1's shape */
    public static ItemInteractionResult interact(class_5620 interaction, class_2680 state, class_1937 level, class_2338 pos,
                                                 class_1657 player, class_1268 hand, class_1799 stack) {
        return ItemInteractionResult.fromResult(interaction.interact(state, level, pos, player, hand, stack));
    }

    /** fillBucket(..) */
    public static ItemInteractionResult method_32210(class_2680 state, class_1937 level, class_2338 pos, class_1657 player,
                                                     class_1268 hand, class_1799 empty, class_1799 filled,
                                                     Predicate<class_2680> fullEnough, class_3414 sound) {
        return ItemInteractionResult.fromResult(class_5620.method_32210(state, level, pos, player, hand, empty, filled, fullEnough, sound));
    }

    /** emptyBucket(..) */
    public static ItemInteractionResult method_32207(class_1937 level, class_2338 pos, class_1657 player, class_1268 hand,
                                                     class_1799 filled, class_2680 state, class_3414 sound) {
        return ItemInteractionResult.fromResult(class_5620.method_32207(level, pos, player, hand, filled, state, sound));
    }

    /** the interaction 1.21.2's map holds for the item (the maps are filled by CauldronInteraction.bootStrap) */
    private static class_5620 in(class_5620.class_8821 map, net.minecraft.class_1792 item) {
        class_5620 i = map.comp_1982().get(item);
        if (i == null) {
            throw new IllegalStateException("cauldron interactions not bootstrapped yet: " + map.comp_1981());
        }
        return i;
    }

    /** FILL_WATER */
    public static class_5620 field_27778() {
        return in(class_5620.field_27775, class_1802.field_8705);
    }

    /** FILL_LAVA */
    public static class_5620 field_27779() {
        return in(class_5620.field_27775, class_1802.field_8187);
    }

    /** FILL_POWDER_SNOW */
    public static class_5620 field_28012() {
        return in(class_5620.field_27775, class_1802.field_27876);
    }

    /** SHULKER_BOX */
    public static class_5620 field_27780() {
        return in(class_5620.field_27776, class_1802.field_8722);
    }

    /** BANNER */
    public static class_5620 field_27781() {
        return in(class_5620.field_27776, class_1802.field_8539);
    }

    /** DYED_ITEM */
    public static class_5620 field_27782() {
        return in(class_5620.field_27776, class_1802.field_8267);
    }

    private CbItemCauldron() {
    }
}

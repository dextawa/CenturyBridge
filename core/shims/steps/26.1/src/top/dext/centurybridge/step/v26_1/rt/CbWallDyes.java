package top.dext.centurybridge.step.v26_1.rt;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignApplicator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

/**
 * 1.21.11 DyeItem and SignApplicator members on 26.1's.
 * <p>
 * 1.21.11's DyeItem took its color in the constructor, kept it (getDyeColor()) and recorded itself as the dye
 * of that color (byColor: the last one constructed). 26.1's dye is a plain DyeItem whose color is its default
 * DYE component (Items registers each dye with it). A 1.21.11 constructor gives the item that component and
 * records it; byColor answers the recorded one, else the first registered dye item of that color -- 26.1's
 * vanilla dye, constructed first in 1.21.11 too.
 * <p>
 * SignApplicator's methods gained the applied stack: 1.21.11's sign interaction applied the stack in the hand
 * holding the applicator (CbWallFrames.applicatorStack).
 */
public final class CbWallDyes {

    private CbWallDyes() {
    }

    private static final Map<DyeColor, DyeItem> BY_COLOR = new HashMap<>();

    /** the properties 26.1's DyeItem takes: 1.21.11's, with the color as the default DYE component */
    public static Item.Properties withDye(DyeColor color, Item.Properties properties) {
        return properties.component(DataComponents.DYE, color);
    }

    /** post of 1.21.11's DyeItem(color, properties): the dye of that color */
    public static void constructed(DyeItem item, DyeColor color) {
        synchronized (BY_COLOR) {
            BY_COLOR.put(color, item);
        }
    }

    /** 1.21.11 DyeItem.byColor(color) */
    public static DyeItem byColor(DyeColor color) {
        synchronized (BY_COLOR) {
            DyeItem recorded = BY_COLOR.get(color);
            if (recorded != null) {
                return recorded;
            }
        }
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof DyeItem dye && dye.components().get(DataComponents.DYE) == color) {
                return dye;
            }
        }
        return null;
    }

    /** 1.21.11 DyeItem.getDyeColor(): its color, the default DYE component 26.1 keeps it as */
    public static DyeColor dyeColor(Item item) {
        return item.components().get(DataComponents.DYE);
    }

    /** 1.21.11 DyeItem.dyeColor field (class_1769.field_7969, opened by access wideners): the same color getDyeColor() answers */
    public static DyeColor dyeColorField(DyeItem item) {
        return item.components().get(DataComponents.DYE);
    }

    /** 1.21.11 SignApplicator.tryApplyToSign(level, sign, front, player) */
    public static boolean tryApplyToSign(SignApplicator applicator, Level level, SignBlockEntity sign, boolean front, Player player) {
        return applicator.tryApplyToSign(level, sign, front, CbWallFrames.applicatorStack(applicator, level, sign, front, player), player);
    }

    /** 1.21.11 SignApplicator.canApplyToSign(text, player) */
    public static boolean canApplyToSign(SignApplicator applicator, SignText text, Player player) {
        return applicator.canApplyToSign(text, CbWallFrames.applicatorStack(applicator, text, player), player);
    }
}

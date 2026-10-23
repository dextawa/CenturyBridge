package top.dext.centurybridge.step.v1_21_4.rt;

import com.mojang.datafixers.util.Pair;
import net.minecraft.class_1735;
import net.minecraft.class_2960;

/**
 * 1.21.3's Slot.getNoItemIcon(): a pair (atlas, sprite) where 1.21.4 answers the sprite id in the GUI sprite
 * atlas (minecraft:textures/atlas/gui.png). A 1.21.3 call gets the pair that names 1.21.4's sprite where it
 * lives (the GUI atlas); a 1.21.3 override's pair is handed to 1.21.4 as its sprite id, and the slot renderer
 * draws a 1.21.3 slot's icon from the atlas its pair names, as 1.21.3's renderer did (CbRestSlotIconMixin;
 * 1.21.3's vanilla icons lived in the block atlas). A mod's super call answers 1.21.3's Slot body, null.
 */
public final class CbRestSlots {

    private CbRestSlots() {
    }

    /** a 1.21.3 slot overriding getNoItemIcon (adapterHooks): its own (atlas, sprite), drawn from that atlas (CbRestSlotIconMixin) */
    public interface LegacyIcon {
        Pair<class_2960, class_2960> method_7679();
    }

    public static final class_2960 GUI_ATLAS = class_2960.method_60656("textures/atlas/gui.png");

    public static Pair<class_2960, class_2960> method_7679(class_1735 slot) {
        class_2960 sprite = slot.method_7679();
        return sprite == null ? null : Pair.of(GUI_ATLAS, sprite);
    }

    public static Pair<class_2960, class_2960> superGetNoItemIcon(class_1735 slot) {
        return null;
    }

    /** a 1.21.3 override's (atlas, sprite) as 1.21.4's sprite id */
    public static class_2960 sprite(Pair<class_2960, class_2960> icon) {
        return icon == null ? null : icon.getSecond();
    }
}

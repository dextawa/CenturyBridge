package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * 1.21.11's Gui.setChatDisabledByPlayerShown / isShowingChatDisabledByPlayer, given back (covers): a flag the
 * overlay message's display time bounds, 1.21.11's bodies (26.1 reworked chat restrictions and keeps no such flag;
 * only a mod's own calls set it).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = Gui.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallGuiChatDisabledMixin {

    @Shadow
    private int overlayMessageTime;

    @Unique
    private boolean cb$chatDisabledByPlayerShown;

    public void setChatDisabledByPlayerShown(boolean shown) {
        this.cb$chatDisabledByPlayerShown = shown;
    }

    public boolean isShowingChatDisabledByPlayer() {
        return this.cb$chatDisabledByPlayerShown && this.overlayMessageTime > 0;
    }
}

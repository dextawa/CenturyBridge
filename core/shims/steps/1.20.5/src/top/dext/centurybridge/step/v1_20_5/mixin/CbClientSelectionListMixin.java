package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * AbstractSelectionList.setRenderBackground(boolean): 1.20.4's list drew its background (the darkened dirt
 * under the entries, and the top/bottom shadows) unless told not to. 1.20.5 always draws its own backdrop and
 * separators (renderListBackground / renderListSeparators); a list 1.20.4 code switched off skips them, as
 * 1.20.5's own list without a background does (SocialInteractionsPlayerList overrides both to nothing;
 * CbClientSelectionListMixinHooks, default priority).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_350.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientSelectionListMixin implements CbPrioritySplitClient.ListBackground {

    /** 1.20.4's renderBackground field, true by default */
    @Unique
    private boolean cb$renderBackground = true;

    /** 1.20.4 AbstractSelectionList.setRenderBackground(renderBackground) */
    public void method_31322(boolean renderBackground) {
        this.cb$renderBackground = renderBackground;
    }

    @Override
    public boolean cb$v1_20_5$renderBackground() {
        return this.cb$renderBackground;
    }
}

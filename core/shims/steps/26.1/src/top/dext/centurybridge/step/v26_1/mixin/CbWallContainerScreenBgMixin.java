package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallContainerBackground;

/**
 * 1.21.11's AbstractContainerScreen.renderBg, and the backdrop-then-renderBg background 1.21.11's
 * AbstractContainerScreen.renderBackground drew (rt/CbWallContainerBackground).
 */
@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class CbWallContainerScreenBgMixin extends Screen implements CbWallContainerBackground.Screen {

    protected CbWallContainerScreenBgMixin(Component title) {
        super(title);
    }

    protected void renderBg(GuiGraphicsExtractor graphics, float partialTick, int mouseX, int mouseY) {
        CbWallContainerBackground.vanillaTexture((AbstractContainerScreen<?>) (Object) this, graphics, partialTick, mouseX, mouseY);
    }

    @Override
    public void cb$renderBg(GuiGraphicsExtractor graphics, float partialTick, int mouseX, int mouseY) {
        renderBg(graphics, partialTick, mouseX, mouseY);
    }

    /**
     * 1.21.11 AbstractContainerScreen.renderBackground (class_465.method_25420): 26.1's AbstractContainerScreen declares
     * no extractBackground, so this override sits between Screen's and every container screen's: vanilla's
     * Screen.extractRenderStateWithTooltipAndSubtitles calls extractBackground, and 26.1's container screens
     * (ContainerScreen, AbstractRecipeBookScreen's subclasses, ...) call super.extractBackground first.
     */
    @CbVanillaReads("CbWallContainerScreenBgMixin: Screen.extractRenderStateWithTooltipAndSubtitles calls extractBackground, "
        + "and 26.1's container screens' own extractBackground call super into this override before drawing their texture")
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (CbWallContainerBackground.textureOnly(this)) {
            return;
        }
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        if (CbWallContainerBackground.overridesRenderBg(this) && !CbWallContainerBackground.vanillaBackgroundBelow(this)) {
            renderBg(graphics, partialTick, mouseX, mouseY);
        }
    }
}

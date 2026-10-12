package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;

/**
 * OptionsSubScreen.basicListRender(GuiGraphics, OptionsList, int, int, float) (method_45626), gone in
 * 1.20.3, 1.20.2's body: the screen (Screen.render), then the list, then the title centred at y 20.
 */
@Mixin(net.minecraft.class_4667.class)
public abstract class CbOptionsSubScreenMixin extends net.minecraft.class_437 {

    private CbOptionsSubScreenMixin() {
        super(null);
    }

    protected void method_45626(net.minecraft.class_332 graphics, net.minecraft.class_353 list, int mouseX, int mouseY,
                                float partialTick) {
        super.method_25394(graphics, mouseX, mouseY, partialTick);
        list.method_25394(graphics, mouseX, mouseY, partialTick);
        graphics.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 20, 16777215);
    }
}

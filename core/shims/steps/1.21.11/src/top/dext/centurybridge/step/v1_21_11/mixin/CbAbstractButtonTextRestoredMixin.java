package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12225;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4264;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_11.rt.CbTextDrawing;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10 AbstractButton.renderString(graphics, font, color) (method_48589), gone from 1.21.11: 1.21.10's body,
 * renderScrollingString with an inset of 2. 1.21.11 never calls it; a 1.21.10 button overriding it is reached from
 * 1.21.11's renderDefaultLabel (override hook, rt/CbTextDrawing.renderStringBridge). Also lets the layer reach
 * renderDefaultSprite / renderDefaultLabel (CbTextDrawing.ButtonText).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = class_4264.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAbstractButtonTextRestoredMixin implements CbTextDrawing.ButtonText {
    @Shadow
    protected abstract void method_75794(class_332 graphics);

    @Shadow
    protected abstract void method_75793(class_12225 collector);

    /**
     * AbstractButton.renderString(graphics, font, color). With the arguments 1.21.10's renderWidget passed (the client
     * font, ARGB.color(alpha, active ? white : 0xFFA0A0A0)) 1.21.11's renderDefaultLabel over the graphics' collector
     * draws the same thing (the message, inactive ones in 0xFFA0A0A0, at the widget's alpha, scrolling with inset 2 by
     * 1.21.10's arithmetic), so the call takes vanilla's path and its mixins run; a mod's own font or colour gets
     * 1.21.10's body (renderScrollingString with an inset of 2). In a class whose renderString override the hook
     * reaches from renderDefaultLabel (CbTextDrawing.LegacyRenderString), the virtual renderDefaultLabel lands back in
     * that override unless the hook's guard is already held for the object (the override's super call made from the
     * hook): only then is vanilla's path taken, so the mod's renderString never runs twice for one call.
     */
    public void method_48589(class_332 graphics, class_327 font, int color) {
        class_4264 self = (class_4264) (Object) this;
        boolean viaVanilla = !(this instanceof CbTextDrawing.LegacyRenderString);
        if (!viaVanilla) {
            if (top.dext.centurybridge.step.v1_21_11.rt.Guard.cbEnter(self)) {
                top.dext.centurybridge.step.v1_21_11.rt.Guard.cbExit(self); // not inside the hook
            } else {
                viaVanilla = true; // inside the hook: renderDefaultLabel falls through to vanilla's
            }
        }
        if (viaVanilla && font == net.minecraft.class_310.method_1551().field_1772 && color == CbTextDrawing.legacyColor(self)) {
            this.method_75793(graphics.method_75787(self, class_332.class_12228.field_63850));
            return;
        }
        ((CbTextDrawing.WidgetText) this).cb$v1_21_11$scrollingString(graphics, font, 2, color);
    }

    @Override
    public void cb$v1_21_11$sprite(class_332 graphics) {
        this.method_75794(graphics);
    }

    @Override
    public void cb$v1_21_11$label(class_12225 collector) {
        this.method_75793(collector);
    }
}

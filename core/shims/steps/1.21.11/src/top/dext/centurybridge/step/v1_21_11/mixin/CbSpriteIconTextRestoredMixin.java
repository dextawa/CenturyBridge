package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_8662;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/** 1.21.10 SpriteIconButton.CenteredIcon.renderString(graphics, font, color): an icon-only button drew no text (javap: return) */
@Environment(EnvType.CLIENT)
@Mixin(value = class_8662.class_8664.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbSpriteIconTextRestoredMixin {

    /** SpriteIconButton.CenteredIcon.renderString(graphics, font, color) */
    public void method_48589(class_332 graphics, class_327 font, int color) {
    }
}

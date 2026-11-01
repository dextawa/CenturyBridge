package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import net.minecraft.class_8662;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_11.rt.CbTextDrawing;

/** SpriteIconButton's sprite size and renderSprite(graphics, x, y), for 1.21.10's icon-button text (rt/CbTextDrawing) */
@Environment(EnvType.CLIENT)
@Mixin(class_8662.class)
public abstract class CbSpriteIconButtonAccess implements CbTextDrawing.SpriteIcon {
    @Shadow
    @Final
    protected int field_45380;

    @Shadow
    @Final
    protected int field_45381;

    @Shadow
    protected abstract void method_75817(class_332 graphics, int x, int y);

    @Override
    public int cb$v1_21_11$spriteWidth() {
        return this.field_45380;
    }

    @Override
    public int cb$v1_21_11$spriteHeight() {
        return this.field_45381;
    }

    @Override
    public void cb$v1_21_11$icon(class_332 graphics, int x, int y) {
        this.method_75817(graphics, x, y);
    }
}

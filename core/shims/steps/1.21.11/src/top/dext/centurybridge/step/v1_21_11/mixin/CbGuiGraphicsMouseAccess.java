package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_11.rt.CbTextDrawing;

/** GuiGraphics' mouse position (package-private final fields), which its text collectors hit-test styles with */
@Environment(EnvType.CLIENT)
@Mixin(class_332.class)
public abstract class CbGuiGraphicsMouseAccess implements CbTextDrawing.GraphicsMouse {
    @Shadow
    @Final
    int field_63846;

    @Shadow
    @Final
    int field_63847;

    @Override
    public int cb$v1_21_11$mouseX() {
        return this.field_63846;
    }

    @Override
    public int cb$v1_21_11$mouseY() {
        return this.field_63847;
    }
}

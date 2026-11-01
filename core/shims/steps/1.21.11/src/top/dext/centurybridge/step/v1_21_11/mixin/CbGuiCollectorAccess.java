package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_11.rt.CbTextDrawing;

/** The ActiveTextCollector of a GuiGraphics (inner class, package-private): the GuiGraphics it draws into, its outer instance */
@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.class_332$class_12229")
public abstract class CbGuiCollectorAccess implements CbTextDrawing.CollectorGraphics {
    @Shadow
    @Final
    class_332 field_63856;

    @Override
    public class_332 cb$v1_21_11$graphics() {
        return this.field_63856;
    }
}

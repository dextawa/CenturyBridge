package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderTail;

/** KeyboardInput's options (private): 1.21.3's tick body reads its keys (CbRenderTail.superTick) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_743.class)
public interface CbTailKeyboardInputAccess extends CbRenderTail.CbKeyboardInput {
    @Override
    @Accessor("field_3902")
    net.minecraft.class_315 cb$options();
}

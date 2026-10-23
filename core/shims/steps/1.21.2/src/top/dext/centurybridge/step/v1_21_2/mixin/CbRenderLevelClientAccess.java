package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ClientLevel's LevelEventHandler (1.21.2 moved LevelRenderer's level events there), for CbRenderLevel */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_638.class)
public interface CbRenderLevelClientAccess {

    @Accessor("field_53028")
    net.minecraft.class_9959 cb$levelEvents();
}

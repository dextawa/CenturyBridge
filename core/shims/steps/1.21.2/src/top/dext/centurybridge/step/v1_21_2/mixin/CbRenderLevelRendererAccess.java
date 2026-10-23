package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** LevelRenderer's level, for CbRenderLevel */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_761.class)
public interface CbRenderLevelRendererAccess {

    @Accessor("field_4085")
    net.minecraft.class_638 cb$level();
}

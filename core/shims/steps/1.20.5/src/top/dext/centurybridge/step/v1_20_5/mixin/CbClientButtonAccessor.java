package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Button.DEFAULT_NARRATION, what 1.20.4's SpriteIconButton narrated with (rt/CbClientGui) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_4185.class)
public interface CbClientButtonAccessor {

    @Accessor("field_40754")
    static net.minecraft.class_4185.class_7841 cb$defaultNarration() {
        throw new AssertionError();
    }
}

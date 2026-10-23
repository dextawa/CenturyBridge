package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Screen.CUBE_MAP, which is 1.20.4's TitleScreen.CUBE_MAP since 1.20.5 (rt/CbClientGui) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_437.class)
public interface CbClientScreenAccessor {

    @Accessor("field_49508")
    static net.minecraft.class_751 cb$cubeMap() {
        throw new AssertionError();
    }
}

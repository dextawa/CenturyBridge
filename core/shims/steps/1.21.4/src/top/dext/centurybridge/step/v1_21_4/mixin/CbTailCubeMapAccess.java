package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderTail;

/** CubeMap's face images (private): 1.21.3's preload loaded each (CbRenderTail) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_751.class)
public interface CbTailCubeMapAccess extends CbRenderTail.CbCubeMap {
    @Override
    @Accessor("field_55505")
    List<net.minecraft.class_2960> cb$faces();
}

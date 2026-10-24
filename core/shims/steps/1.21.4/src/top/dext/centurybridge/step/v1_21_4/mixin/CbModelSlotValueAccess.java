package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelBlockModel;

/** TextureSlots.Value (a package-private record): a texture slot naming a material (1.21.3's Either.left) */
@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.class_10419$class_10425")
public interface CbModelSlotValueAccess extends CbModelBlockModel.CbSlotValue {
    @Override
    @Accessor("comp_3378")
    net.minecraft.class_4730 cb$material();
}

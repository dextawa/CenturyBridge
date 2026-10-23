package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelBlockModel;

/** TextureSlots.Reference (a package-private record): a texture slot naming another slot (1.21.3's Either.right) */
@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.class_10419$class_10422")
public interface CbModelSlotReferenceAccess extends CbModelBlockModel.CbSlotReference {
    @Override
    @Accessor("comp_3377")
    String cb$target();
}

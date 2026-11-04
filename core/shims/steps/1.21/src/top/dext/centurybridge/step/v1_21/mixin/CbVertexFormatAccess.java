package top.dext.centurybridge.step.v1_21.mixin;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_293;
import net.minecraft.class_296;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(class_293.class)
public interface CbVertexFormatAccess {
    @Accessor("field_1602")
    List<class_296> cb$elements();
}

package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** EntityRenderer's protected culling questions, asked from CbEntityCullingMixin's redirects. */
@Environment(EnvType.CLIENT)
@Mixin(class_897.class)
public interface CbEntityCullingInvoker {

    @Invoker("method_62406")
    boolean cb$affectedByCulling(class_1297 entity);

    @Invoker("method_62358")
    class_238 cb$getBoundingBoxForCulling(class_1297 entity);
}

package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21.1 ModelBaker.getModel(id), gone from the 1.21.2 interface. ModelBakery's baker keeps it (class_1088$class_7778,
 * private, made public by the layer's access widener): the discovered model, or the missing model with a warning --
 * 1.21.1's answered the bakery's model of that id. That implementation overrides this default; another 1.21.2 baker
 * never had the method.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_7775.class)
public interface CbRenderBakeBakerMixin extends top.dext.centurybridge.step.v1_21_2.rt.CbRenderBake.Baker {

    /** 1.21.1 ModelBaker.getModel(id) */
    @Override
    default net.minecraft.class_1100 method_45872(net.minecraft.class_2960 id) {
        throw new AbstractMethodError(this.getClass().getName() + ".getModel(ResourceLocation): abstract in 1.21.1");
    }
}

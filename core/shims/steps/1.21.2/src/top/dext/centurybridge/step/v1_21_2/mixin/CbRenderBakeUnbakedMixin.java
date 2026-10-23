package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Collection;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBake;

/**
 * 1.21.1 UnbakedModel.resolveParents(getter) and getDependencies() on 1.21.2's UnbakedModel (see CbRenderBake).
 * resolveParents is 1.21.2's resolveDependencies with the getter as resolver -- both link the model to the models it
 * names, through what the caller hands in. getDependencies has no generic 1.21.2 form (asking resolveDependencies with
 * a recording resolver would relink the model): the vanilla models declare it themselves (CbRenderBakeBlockModelMixin),
 * a 1.21.1 class declares its own, and any other 1.21.2 class never had it.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1100.class)
public interface CbRenderBakeUnbakedMixin extends CbRenderBake.Legacy {

    /** 1.21.1 UnbakedModel.resolveParents(getter) */
    @Override
    default void method_45785(Function<net.minecraft.class_2960, net.minecraft.class_1100> getter) {
        ((net.minecraft.class_1100) this).method_62326(getter::apply);
    }

    /** 1.21.1 UnbakedModel.getDependencies() */
    @Override
    default Collection<net.minecraft.class_2960> method_4755() {
        throw new AbstractMethodError(this.getClass().getName() + ".getDependencies(): abstract in 1.21.1, and this model did not exist there");
    }
}

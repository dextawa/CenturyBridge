package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21.1 MultiPart.getDependencies(): every selector's variant models (class_816.method_4755). 1.21.2's
 * resolveDependencies asks the resolver for exactly those (each selector's MultiVariant, each variant's model location)
 * and keeps nothing it answers (class_816.method_62337 / class_807.method_62333 discard the result), so recording what
 * it asks for gives 1.21.1's set without touching the model.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_816.class)
public abstract class CbRenderBakeMultiPartMixin {

    /** 1.21.1 MultiPart.getDependencies() */
    public Collection<net.minecraft.class_2960> method_4755() {
        Set<net.minecraft.class_2960> ids = new LinkedHashSet<>();
        ((net.minecraft.class_816) (Object) this).method_62326(id -> {
            ids.add(id);
            return null;
        });
        return ids;
    }
}

package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Collection;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** 1.21.1 MultiVariant.getDependencies(): its variants' model locations (class_807.method_4755), on the 1.21.2 record */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_807.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderBakeMultiVariantMixin {

    /** 1.21.1 MultiVariant.getDependencies() */
    public Collection<net.minecraft.class_2960> method_4755() {
        return ((net.minecraft.class_807) (Object) this).comp_3049().stream().map(net.minecraft.class_813::method_3510).collect(Collectors.toSet());
    }
}

package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * 1.21.3's EntityModelSet was a resource reload listener: built empty, it took the vanilla layer definitions' roots on each
 * reload. 1.21.4's is an immutable set made from them; a 1.21.3 set is again a listener with that reload.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_5599.class)
public abstract class CbRenderEntityModelSetMixin implements net.minecraft.class_4013 {

    @Shadow
    @Final
    @Mutable
    private Map<net.minecraft.class_5601, net.minecraft.class_5607> field_27542;

    /**
     * was onResourceManagerReload(resourceManager) (method_14491): 1.21.3's body. 1.21.4's model manager reload builds its
     * set through it (CbEntityModelSetReloadMixin): an empty set, then this reload, the roots 1.21.4's EntityModelSet.vanilla()
     * takes, so with no mod mixin the set is 1.21.4's.
     */
    @CbVanillaReads("CbEntityModelSetReloadMixin: 1.21.4's ModelManager reload builds the entity model set through it, with the reload's resource manager")
    @Override
    public void method_14491(net.minecraft.class_3300 resourceManager) {
        this.field_27542 = com.google.common.collect.ImmutableMap.copyOf(net.minecraft.class_5600.method_32073());
    }
}

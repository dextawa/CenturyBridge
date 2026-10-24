package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** 1.21.1 BlockModel.getDependencies(): its overrides' models and its parent (class_793.method_4755), on 1.21.2's fields */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_793.class)
public abstract class CbRenderBakeBlockModelMixin {

    @Shadow
    @Final
    private List<net.minecraft.class_799> field_4255;

    @Shadow
    protected net.minecraft.class_2960 field_4247;

    /** 1.21.1 BlockModel.getDependencies() */
    public Collection<net.minecraft.class_2960> method_4755() {
        Set<net.minecraft.class_2960> ids = new HashSet<>();
        for (net.minecraft.class_799 o : this.field_4255) {
            ids.add(o.comp_3193());
        }
        if (this.field_4247 != null) {
            ids.add(this.field_4247);
        }
        return ids;
    }
}
